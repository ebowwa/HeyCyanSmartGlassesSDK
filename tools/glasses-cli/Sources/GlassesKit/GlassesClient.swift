import CoreBluetooth
import Foundation

/// Callback-based BLE client for the glasses. Drive it from a run loop
/// (`RunLoop.main.run()`); all callbacks are delivered on the main queue.
///
/// Typical use:
/// ```
/// let g = GlassesClient()
/// g.connect { result in
///     guard case .success = result else { … }
///     g.mediaCount { … }
/// }
/// RunLoop.main.run()
/// ```
public final class GlassesClient: NSObject {
    public private(set) var deviceName = ""

    private var central: CBCentralManager!
    private var peripheral: CBPeripheral?
    private var bigWrite: CBCharacteristic?
    private var bigNotify: CBCharacteristic?
    private var uartWrite: CBCharacteristic?

    private var connectCompletion: ((Result<Void, Error>) -> Void)?
    private var connectTimeout: DispatchWorkItem?
    private var ready = false

    private var dis: [CBUUID: String] = [:]

    // Single outstanding bigData request (CLI usage is sequential).
    private struct Waiter {
        let action: UInt8
        let predicate: ([UInt8]) -> Bool
        let done: (Result<[UInt8], Error>) -> Void
        let timeout: DispatchWorkItem
    }
    private var waiter: Waiter?
    private var rx = [UInt8]()

    // Thumbnail collection mode.
    private var collectingImage = false
    private var imageBuf = [UInt8]()
    private var thumbDone: ((Data?) -> Void)?
    private var thumbIndex = 0

    // UART write ack (MusicSwitch).
    private var uartWriteDone: ((Result<Void, Error>) -> Void)?

    public override init() { super.init() }

    // MARK: - Connection

    public func connect(timeout: TimeInterval = 20, _ completion: @escaping (Result<Void, Error>) -> Void) {
        connectCompletion = completion
        let to = DispatchWorkItem { [weak self] in
            self?.finishConnect(.failure(GlassesError.timeout("connection")))
        }
        connectTimeout = to
        DispatchQueue.main.asyncAfter(deadline: .now() + timeout, execute: to)
        central = CBCentralManager(delegate: self, queue: nil)
    }

    public func disconnect() {
        if let p = peripheral { central?.cancelPeripheralConnection(p) }
    }

    private func finishConnect(_ r: Result<Void, Error>) {
        connectTimeout?.cancel(); connectTimeout = nil
        let c = connectCompletion; connectCompletion = nil
        c?(r)
    }

    // MARK: - Public commands

    public func deviceInfo(_ done: @escaping (Result<DeviceInfo, Error>) -> Void) {
        request(action: BigAction.deviceInfo, payload: [0x00, 0x00], awaitAction: BigAction.deviceInfo) { r in
            done(r.map { d in
                let l1 = Wire.le16(d, 7), l2 = Wire.le16(d, 9), l3 = Wire.le16(d, 11), l4 = Wire.le16(d, 13)
                func str(_ start: Int, _ n: Int) -> String {
                    guard n > 0, start + n <= d.count else { return "" }
                    return String(bytes: d[start..<(start + n)], encoding: .utf8) ?? ""
                }
                var info = DeviceInfo()
                info.firmware = str(15, l1)
                info.hardware = str(15 + l1, l2)
                info.wifiFirmware = str(15 + l1 + l2, l3)
                info.wifiHardware = str(15 + l1 + l2 + l3, l4)
                info.serial = self.dis[GlassesUUID.disSerial] ?? ""
                return info
            })
        }
    }

    public func battery(_ done: @escaping (Result<BatteryStatus, Error>) -> Void) {
        request(action: BigAction.battery, payload: [0x00, 0x00], awaitAction: BigAction.battery) { r in
            done(r.map { d in
                BatteryStatus(percent: d.count > 6 ? Int(d[6]) : -1,
                              charging: d.count > 7 && d[7] == 1)
            })
        }
    }

    public func mediaCount(_ done: @escaping (Result<MediaCount, Error>) -> Void) {
        request(action: BigAction.glassesControl, payload: [0x02, 0x04],
                awaitAction: BigAction.glassesControl,
                where: { $0.count > 7 && $0[7] == 4 }) { r in
            done(r.map { d in
                MediaCount(images: Wire.le16(d, 8), videos: Wire.le16(d, 10), audio: Wire.le16(d, 12))
            })
        }
    }

    /// Sends a "set work mode" control command (photo/video/record/AI-photo/transfer).
    public func setMode(_ mode: WorkMode, _ done: @escaping (Result<Void, Error>) -> Void) {
        request(action: BigAction.glassesControl, payload: [0x02, 0x01, mode.rawValue],
                awaitAction: BigAction.glassesControl,
                where: { $0.count > 7 && $0[7] == 1 }, timeout: 6) { r in
            done(r.map { _ in () })
        }
    }

    /// Triggers an AI photo; the glasses then push a thumbnail (see `thumbnail`).
    public func aiPhoto(size: UInt8 = 2, _ done: @escaping (Result<Void, Error>) -> Void) {
        request(action: BigAction.glassesControl, payload: [0x02, 0x01, 0x06, size, size, 0x02],
                awaitAction: BigAction.glassesControl, timeout: 6) { r in done(r.map { _ in () }) }
    }

    /// Full "point-and-ask" capture: trigger an AI photo, wait for the glasses'
    /// "photo ready" push (device-report action 0x73, subtype 0x02), then pull
    /// the JPEG thumbnail over BLE. Returns the JPEG.
    public func captureAIPhoto(size: UInt8 = 2, timeout: TimeInterval = 12,
                               _ done: @escaping (Result<Data, Error>) -> Void) {
        guard let wc = bigWrite, let p = peripheral else { return done(.failure(GlassesError.disconnected)) }
        // Arm the "ready" waiter *before* triggering, to avoid missing a fast event.
        awaitFrame(action: 0x73, where: { $0.count > 6 && $0[6] == 0x02 }, timeout: timeout) { [weak self] r in
            guard let self = self else { return }
            if case .failure(let e) = r { return done(.failure(e)) }
            self.thumbnail(index: 0, timeout: 12) { data in
                if let d = data { done(.success(d)) }
                else { done(.failure(GlassesError.timeout("thumbnail after AI photo"))) }
            }
        }
        p.writeValue(Wire.bigFrame(action: BigAction.glassesControl, payload: [0x02, 0x01, 0x06, size, size, 0x02]),
                     for: wc, type: .withResponse)
    }

    /// Enters Wi-Fi transfer mode and returns the glasses' P2P IP (best effort).
    public func enterTransfer(_ done: @escaping (Result<String, Error>) -> Void) {
        request(action: BigAction.glassesControl, payload: [0x02, 0x01, WorkMode.transfer.rawValue],
                awaitAction: BigAction.glassesControl,
                where: { $0.count > 7 && $0[7] == 3 }, timeout: 15) { r in
            done(r.map { d in
                guard d.count > 13 else { return "" }
                return "\(d[10]).\(d[11]).\(d[12]).\(d[13])"
            })
        }
    }

    /// Toggles the classic-Bluetooth (A2DP audio) radio via the UART channel.
    /// The firmware acks the write but sends no notification, so success is the write itself.
    public func setAudioBluetooth(on: Bool, _ done: @escaping (Result<Void, Error>) -> Void) {
        guard let wc = uartWrite, let p = peripheral else { return done(.failure(GlassesError.disconnected)) }
        uartWriteDone = done
        let pkt = Wire.uartFrame(opcode: 0x1C, payload: [0x02, on ? 0x01 : 0x02])
        p.writeValue(pkt, for: wc, type: .withResponse)
    }

    /// Fetches a thumbnail JPEG over BLE. The image is delivered in `total`
    /// chunks (the response header carries `total` and `index`); each chunk is
    /// requested in turn (`getPictureThumbnails(index)`) and its JPEG bytes are
    /// concatenated. Re-requests the current chunk periodically in case a fresh
    /// AI photo isn't ready yet.
    public func thumbnail(index: Int = 0, timeout: TimeInterval = 15, _ done: @escaping (Data?) -> Void) {
        guard bigWrite != nil, peripheral != nil else { return done(nil) }
        imageBuf.removeAll(); collectingImage = true; thumbIndex = index
        thumbDone = done
        sendThumbRequest(index)
        // Nudge the current chunk if the device hasn't answered (image not ready yet).
        for t in stride(from: 3.0, to: timeout, by: 3.0) {
            DispatchQueue.main.asyncAfter(deadline: .now() + t) { [weak self] in
                guard let self = self, self.collectingImage else { return }
                self.sendThumbRequest(self.thumbIndex)
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + timeout) { [weak self] in
            self?.finishThumb(force: true)
        }
    }

    private func sendThumbRequest(_ index: Int) {
        guard let wc = bigWrite, let p = peripheral else { return }
        let sub: [UInt8] = [0x01, UInt8(index & 0xFF), UInt8((index >> 8) & 0xFF)]
        p.writeValue(Wire.bigFrame(action: BigAction.thumbnail, payload: sub), for: wc, type: .withResponse)
    }

    /// Handles one 0xFD thumbnail chunk: append its JPEG bytes and drive the next request.
    private func onThumbnailChunk(_ frame: [UInt8]) {
        let total = Wire.le16(frame, 7)
        let index = Wire.le16(frame, 9)
        if total <= 0 { return }                 // image not available (e.g. stored gallery)
        guard index == thumbIndex else { return } // ignore duplicate/stale chunk
        if frame.count > 11 { imageBuf.append(contentsOf: Array(frame[11...])) }
        if index + 1 >= total {
            finishThumb(force: true)             // last chunk — buffer is the whole JPEG
        } else {
            thumbIndex = index + 1
            sendThumbRequest(thumbIndex)
        }
    }

    private func finishThumb(force: Bool = false) {
        guard collectingImage else { return }
        collectingImage = false
        let d = thumbDone; thumbDone = nil
        d?(extractJPEG(imageBuf))
    }

    // MARK: - Internals

    /// Arms a one-shot waiter for the next matching bigData frame (no write).
    private func awaitFrame(action: UInt8, where predicate: @escaping ([UInt8]) -> Bool = { _ in true },
                            timeout: TimeInterval,
                            _ done: @escaping (Result<[UInt8], Error>) -> Void) {
        let to = DispatchWorkItem { [weak self] in
            self?.waiter = nil
            done(.failure(GlassesError.timeout("action 0x\(String(format: "%02x", action))")))
        }
        waiter = Waiter(action: action, predicate: predicate, done: done, timeout: to)
        DispatchQueue.main.asyncAfter(deadline: .now() + timeout, execute: to)
    }

    private func request(action: UInt8, payload: [UInt8], awaitAction: UInt8,
                         where predicate: @escaping ([UInt8]) -> Bool = { _ in true },
                         timeout: TimeInterval = 8,
                         _ done: @escaping (Result<[UInt8], Error>) -> Void) {
        guard let wc = bigWrite, let p = peripheral else { return done(.failure(GlassesError.disconnected)) }
        awaitFrame(action: awaitAction, where: predicate, timeout: timeout, done)
        p.writeValue(Wire.bigFrame(action: action, payload: payload), for: wc, type: .withResponse)
    }

    private static let debug = ProcessInfo.processInfo.environment["GLASSES_DEBUG"] != nil

    private func handleBig(_ bytes: [UInt8]) {
        rx.append(contentsOf: bytes)
        while let s = rx.firstIndex(of: 0xBC), rx.count >= s + 6 {
            let action = rx[s + 1]
            let len = Int(rx[s + 2]) | (Int(rx[s + 3]) << 8)
            let total = s + 6 + len
            guard rx.count >= total else { return }        // wait for more chunks
            let frame = Array(rx[s..<total])
            rx.removeSubrange(0..<total)
            if GlassesClient.debug {
                let head = frame.prefix(10).map { String(format: "%02x", $0) }.joined(separator: " ")
                FileHandle.standardError.write(Data("[dbg] frame action=0x\(String(format: "%02x", action)) len=\(len) head=[\(head)] collecting=\(collectingImage)\n".utf8))
            }
            if collectingImage && action == BigAction.thumbnail {
                onThumbnailChunk(frame)
            }
            if let w = waiter, w.action == action, w.predicate(frame) {
                w.timeout.cancel(); waiter = nil; w.done(.success(frame))
            }
        }
    }

    private func extractJPEG(_ buf: [UInt8]) -> Data? {
        var i = 0
        while i + 1 < buf.count {
            if buf[i] == 0xFF && buf[i + 1] == 0xD8 {
                var j = i + 2
                while j + 1 < buf.count && !(buf[j] == 0xFF && buf[j + 1] == 0xD9) { j += 1 }
                if j + 1 < buf.count { return Data(buf[i...(j + 1)]) }
            }
            i += 1
        }
        return nil
    }

    private func markReadyIfPossible() {
        guard !ready, bigWrite != nil, bigNotify != nil else { return }
        ready = true
        finishConnect(.success(()))
    }
}

// MARK: - CoreBluetooth delegates

extension GlassesClient: CBCentralManagerDelegate, CBPeripheralDelegate {
    public func centralManagerDidUpdateState(_ c: CBCentralManager) {
        switch c.state {
        case .poweredOn: c.scanForPeripherals(withServices: nil, options: nil)
        case .unauthorized: finishConnect(.failure(GlassesError.bluetoothUnavailable("unauthorized (grant Bluetooth in System Settings)")))
        case .poweredOff: finishConnect(.failure(GlassesError.bluetoothUnavailable("powered off")))
        default: break
        }
    }

    public func centralManager(_ c: CBCentralManager, didDiscover p: CBPeripheral,
                               advertisementData a: [String: Any], rssi: NSNumber) {
        let name = (a[CBAdvertisementDataLocalNameKey] as? String) ?? p.name ?? ""
        guard peripheral == nil, name.contains("M02S") || name.contains("M01") else { return }
        deviceName = name
        peripheral = p; p.delegate = self
        c.stopScan(); c.connect(p, options: nil)
    }

    public func centralManager(_ c: CBCentralManager, didConnect p: CBPeripheral) {
        p.discoverServices([GlassesUUID.bigService, GlassesUUID.uartService, GlassesUUID.disService])
    }

    public func centralManager(_ c: CBCentralManager, didFailToConnect p: CBPeripheral, error: Error?) {
        finishConnect(.failure(error ?? GlassesError.disconnected))
    }

    public func centralManager(_ c: CBCentralManager, didDisconnectPeripheral p: CBPeripheral, error: Error?) {
        if !ready { finishConnect(.failure(GlassesError.disconnected)) }
    }

    public func peripheral(_ p: CBPeripheral, didDiscoverServices error: Error?) {
        for s in p.services ?? [] {
            switch s.uuid {
            case GlassesUUID.bigService:  p.discoverCharacteristics([GlassesUUID.bigWrite, GlassesUUID.bigNotify], for: s)
            case GlassesUUID.uartService: p.discoverCharacteristics([GlassesUUID.uartWrite], for: s)
            case GlassesUUID.disService:  p.discoverCharacteristics([GlassesUUID.disFirmware, GlassesUUID.disHardware, GlassesUUID.disSerial], for: s)
            default: break
            }
        }
    }

    public func peripheral(_ p: CBPeripheral, didDiscoverCharacteristicsFor s: CBService, error: Error?) {
        for ch in s.characteristics ?? [] {
            switch ch.uuid {
            case GlassesUUID.bigWrite:  bigWrite = ch
            case GlassesUUID.bigNotify: bigNotify = ch; p.setNotifyValue(true, for: ch)
            case GlassesUUID.uartWrite: uartWrite = ch
            case GlassesUUID.disFirmware, GlassesUUID.disHardware, GlassesUUID.disSerial:
                p.readValue(for: ch)
            default: break
            }
        }
        markReadyIfPossible()
    }

    public func peripheral(_ p: CBPeripheral, didUpdateNotificationStateFor ch: CBCharacteristic, error: Error?) {
        markReadyIfPossible()
    }

    public func peripheral(_ p: CBPeripheral, didUpdateValueFor ch: CBCharacteristic, error: Error?) {
        guard let v = ch.value else { return }
        if ch.uuid == GlassesUUID.bigNotify {
            handleBig([UInt8](v))
        } else if [GlassesUUID.disFirmware, GlassesUUID.disHardware, GlassesUUID.disSerial].contains(ch.uuid) {
            dis[ch.uuid] = String(data: v, encoding: .utf8)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        }
    }

    public func peripheral(_ p: CBPeripheral, didWriteValueFor ch: CBCharacteristic, error: Error?) {
        if ch.uuid == GlassesUUID.uartWrite, let d = uartWriteDone {
            uartWriteDone = nil
            d(error.map { .failure($0) } ?? .success(()))
        }
    }
}
