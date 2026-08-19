import CoreBluetooth
import Foundation

/// Low-level wire protocol for HeyCyan / oudmon (A02S / "M02S") smart glasses.
///
/// The glasses expose two command channels over BLE plus the standard GATT
/// Device Information service:
///
///  * **UART small-command channel** (`6e40fff0…`): fixed 16-byte frames,
///    `[opcode, payload…, additiveChecksum]`. Used here for the audio
///    (classic-Bluetooth) enable toggle.
///  * **bigData / serial-port channel** (`de5bf728…`): length+CRC framed
///    packets `[0xBC, action, len(LE16), crc16(LE16), payload…]`, reassembled
///    across notifications. Carries device info, battery, media control and
///    image thumbnails.
public enum GlassesUUID {
    // bigData / serial port
    public static let bigService  = CBUUID(string: "de5bf728-d711-4e47-af26-65e3012a5dc7")
    public static let bigNotify   = CBUUID(string: "de5bf729-d711-4e47-af26-65e3012a5dc7")
    public static let bigWrite    = CBUUID(string: "de5bf72a-d711-4e47-af26-65e3012a5dc7")
    // UART small command
    public static let uartService = CBUUID(string: "6e40fff0-b5a3-f393-e0a9-e50e24dcca9e")
    public static let uartWrite   = CBUUID(string: "6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    public static let uartNotify  = CBUUID(string: "6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    // Standard Device Information Service
    public static let disService  = CBUUID(string: "180A")
    public static let disFirmware = CBUUID(string: "2A26")
    public static let disHardware = CBUUID(string: "2A27")
    public static let disSerial   = CBUUID(string: "2A25")
}

/// bigData channel action bytes (from `LargeDataHandler.ACTION_*`).
public enum BigAction {
    public static let glassesControl: UInt8 = 0x41   // media control / status
    public static let battery: UInt8        = 0x42
    public static let deviceInfo: UInt8     = 0x43
    public static let thumbnail: UInt8      = 0xFD
}

/// Glasses work modes (`QCOperatorDeviceMode`), used as the 3rd sub-data byte
/// of a `glassesControl` "set work mode" command.
public enum WorkMode: UInt8 {
    case photo      = 0x01
    case videoStart = 0x02
    case videoStop  = 0x03
    case transfer   = 0x04
    case aiPhoto    = 0x06
    case audioStart = 0x08
    case audioStop  = 0x0C
}

public enum GlassesError: Error, CustomStringConvertible {
    case notFound
    case timeout(String)
    case bluetoothUnavailable(String)
    case disconnected

    public var description: String {
        switch self {
        case .notFound: return "No glasses found (advertising name containing M02S/M01)."
        case .timeout(let what): return "Timed out waiting for \(what)."
        case .bluetoothUnavailable(let s): return "Bluetooth unavailable: \(s)."
        case .disconnected: return "Glasses disconnected."
        }
    }
}

// MARK: - Framing helpers

public enum Wire {
    /// CRC16 as used by the SDK (`CRC16.calcCrc16`): init 0xFFFF, poly 0xA001.
    public static func crc16(_ bytes: [UInt8]) -> UInt16 {
        if bytes.isEmpty { return 0xFFFF }
        var crc: UInt16 = 0xFFFF
        for b in bytes {
            crc ^= UInt16(b)
            for _ in 0..<8 { crc = (crc & 1 != 0) ? (crc >> 1) ^ 0xA001 : crc >> 1 }
        }
        return crc
    }

    /// bigData frame: `[0xBC, action, lenLo, lenHi, crcLo, crcHi, payload…]`.
    public static func bigFrame(action: UInt8, payload: [UInt8]) -> Data {
        var out: [UInt8] = [0xBC, action,
                            UInt8(payload.count & 0xFF), UInt8((payload.count >> 8) & 0xFF)]
        let c = crc16(payload)
        out.append(UInt8(c & 0xFF)); out.append(UInt8((c >> 8) & 0xFF))
        out.append(contentsOf: payload)
        return Data(out)
    }

    /// UART fixed 16-byte frame with trailing additive checksum.
    public static func uartFrame(opcode: UInt8, payload: [UInt8]) -> Data {
        var buf = [UInt8](repeating: 0, count: 16)
        buf[0] = opcode
        for (i, b) in payload.enumerated() where 1 + i < 15 { buf[1 + i] = b }
        var sum = 0
        for i in 0..<15 { sum += Int(buf[i]) }
        buf[15] = UInt8(sum & 0xFF)
        return Data(buf)
    }

    public static func le16(_ d: [UInt8], _ o: Int) -> Int {
        guard o + 1 < d.count else { return 0 }
        return Int(d[o]) | (Int(d[o + 1]) << 8)
    }

    public static func hex(_ d: [UInt8]) -> String {
        d.map { String(format: "%02x", $0) }.joined(separator: " ")
    }
}

// MARK: - Decoded result types

public struct DeviceInfo {
    public var firmware = ""
    public var hardware = ""
    public var wifiFirmware = ""
    public var wifiHardware = ""
    public var serial = ""
}

public struct MediaCount {
    public let images: Int
    public let videos: Int
    public let audio: Int
    public var total: Int { images + videos + audio }
}

public struct BatteryStatus {
    public let percent: Int
    public let charging: Bool
}
