//
//  BluetoothManager.swift
//  HeyCyanSwift
//
//  Created on 2025/8/14.
//

import Foundation
import CoreBluetooth
import Combine

// Device action types matching the Objective-C demo
enum DeviceActionType: Int, CaseIterable {
    case getVersion = 0
    case setTime
    case getBattery
    case getMediaInfo
    case takePhoto
    case toggleVideoRecording
    case toggleAudioRecording
    case takeAIImage
    
    var title: String {
        switch self {
        case .getVersion:
            return "Get Version Info"
        case .setTime:
            return "Set Device Time"
        case .getBattery:
            return "Get Battery Status"
        case .getMediaInfo:
            return "Get Media Info"
        case .takePhoto:
            return "Take Photo"
        case .toggleVideoRecording:
            return "Toggle Video Recording"
        case .toggleAudioRecording:
            return "Toggle Audio Recording"
        case .takeAIImage:
            return "Take AI Image"
        }
    }
}

// Device information model
struct DeviceInfo {
    var hardwareVersion: String = ""
    var firmwareVersion: String = ""
    var hardwareWiFiVersion: String = ""
    var firmwareWiFiVersion: String = ""
    var macAddress: String = ""
    var batteryLevel: Int = 0
    var isCharging: Bool = false
    var photoCount: Int = 0
    var videoCount: Int = 0
    var audioCount: Int = 0
    var isRecordingVideo: Bool = false
    var isRecordingAudio: Bool = false
    var aiImageData: Data?
}

// Discovered device model
struct DiscoveredDevice: Identifiable {
    let id = UUID()
    let peripheral: CBPeripheral
    let name: String
    let macAddress: String
}

class BluetoothManager: NSObject, ObservableObject {
    static let shared = BluetoothManager()
    
    @Published var isConnected = false
    @Published var isScanning = false
    @Published var discoveredDevices: [DiscoveredDevice] = []
    @Published var connectedDeviceName: String = ""
    @Published var deviceInfo = DeviceInfo()
    @Published var connectionState: QCState = .unbind
    
    private var centralManager: QCCentralManager?
    private var sdkManager: QCSDKManager?
    
    private override init() {
        super.init()
        setupManagers()
    }
    
    private func setupManagers() {
        centralManager = QCCentralManager.shared()
        centralManager?.delegate = self
        
        sdkManager = QCSDKManager.shareInstance()
        sdkManager?.delegate = self
    }
    
    // MARK: - Scanning
    func startScanning() {
        print("BluetoothManager: Starting scan...")
        DispatchQueue.main.async { [weak self] in
            self?.isScanning = true
            self?.discoveredDevices.removeAll()
        }
        centralManager?.scan()
        
        // Force check for devices after a delay
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { [weak self] in
            self?.forceUpdateDevices()
        }
    }
    
    private func forceUpdateDevices() {
        guard let peripherals = centralManager?.value(forKey: "peripherals") as? NSMutableArray else { 
            print("Could not get peripherals array")
            return 
        }
        
        print("Force updating with \(peripherals.count) devices")
        
        var devices: [DiscoveredDevice] = []
        for obj in peripherals {
            if let qcPeripheral = obj as? QCBlePeripheral,
               let name = qcPeripheral.peripheral.name,
               !name.isEmpty {
                // Only show M01 devices (the glasses)
                if name.contains("M01") {
                    let device = DiscoveredDevice(
                        peripheral: qcPeripheral.peripheral,
                        name: name,
                        macAddress: qcPeripheral.mac ?? ""
                    )
                    devices.append(device)
                    print("Force added device: \(name) - \(qcPeripheral.peripheral.identifier)")
                }
            }
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.discoveredDevices = devices
            print("Force updated UI with \(devices.count) devices")
        }
    }
    
    func stopScanning() {
        print("BluetoothManager: Stopping scan...")
        DispatchQueue.main.async { [weak self] in
            self?.isScanning = false
        }
        centralManager?.stopScan()
    }
    
    // MARK: - Connection
    func connect(to device: DiscoveredDevice) {
        print("Attempting to connect to: \(device.name) - \(device.peripheral.identifier)")
        
        // For reconnection, we need to get a fresh peripheral reference
        if device.peripheral.state == .disconnected {
            centralManager?.connect(device.peripheral)
        } else {
            print("Peripheral not in disconnected state: \(device.peripheral.state.rawValue)")
        }
    }
    
    func disconnect() {
        centralManager?.remove()
        // Clear the discovered devices after disconnect to force a fresh scan
        DispatchQueue.main.async { [weak self] in
            self?.discoveredDevices.removeAll()
            self?.isConnected = false
            self?.connectedDeviceName = ""
        }
    }
    
    // MARK: - Device Actions
    func getVersionInfo() {
        QCSDKCmdCreator.getDeviceVersionInfoSuccess({ [weak self] (hdVersion: String?, firmVersion: String?, hdWifiVersion: String?, firmWifiVersion: String?) in
            self?.deviceInfo.hardwareVersion = hdVersion ?? ""
            self?.deviceInfo.firmwareVersion = firmVersion ?? ""
            self?.deviceInfo.hardwareWiFiVersion = hdWifiVersion ?? ""
            self?.deviceInfo.firmwareWiFiVersion = firmWifiVersion ?? ""
        }, fail: {
            print("Failed to get version info")
        })
    }
    
    func getMacAddress() {
        QCSDKCmdCreator.getDeviceMacAddressSuccess({ [weak self] macAddress in
            self?.deviceInfo.macAddress = macAddress ?? ""
        }, fail: {
            print("Failed to get MAC address")
        })
    }
    
    func setDeviceTime() {
        QCSDKCmdCreator.setupDeviceDateTime { isSuccess, error in
            if let error = error {
                print("Failed to set time: \(error)")
            } else {
                print("Time set successfully")
            }
        }
    }
    
    func getBatteryStatus() {
        QCSDKCmdCreator.getDeviceBattery({ [weak self] battery, charging in
            self?.deviceInfo.batteryLevel = battery
            self?.deviceInfo.isCharging = charging
        }, fail: {
            print("Failed to get battery status")
        })
    }
    
    func getMediaInfo() {
        QCSDKCmdCreator.getDeviceMedia({ [weak self] photo, video, audio, type in
            self?.deviceInfo.photoCount = photo
            self?.deviceInfo.videoCount = video
            self?.deviceInfo.audioCount = audio
        }, fail: {
            print("Failed to get media info")
        })
    }
    
    func takePhoto() {
        QCSDKCmdCreator.setDeviceMode(.photo, success: {
            print("Photo taken")
        }, fail: { mode in
            print("Failed to take photo, current mode: \(mode)")
        })
    }
    
    func toggleVideoRecording() {
        let mode: QCOperatorDeviceMode = deviceInfo.isRecordingVideo ? .videoStop : .video
        QCSDKCmdCreator.setDeviceMode(mode, success: { [weak self] in
            self?.deviceInfo.isRecordingVideo.toggle()
        }, fail: { mode in
            print("Failed to toggle video, current mode: \(mode)")
        })
    }
    
    func toggleAudioRecording() {
        let mode: QCOperatorDeviceMode = deviceInfo.isRecordingAudio ? .audioStop : .audio
        QCSDKCmdCreator.setDeviceMode(mode, success: { [weak self] in
            self?.deviceInfo.isRecordingAudio.toggle()
        }, fail: { mode in
            print("Failed to toggle audio, current mode: \(mode)")
        })
    }
    
    func takeAIImage() {
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("AI photo requested")
        }, fail: { mode in
            print("Failed to take AI photo, current mode: \(mode)")
        })
    }
}

// MARK: - QCCentralManagerDelegate
extension BluetoothManager: QCCentralManagerDelegate {
    func didScanPeripherals(_ peripheralArr: [Any]?) {
        print("didScanPeripherals called with \(peripheralArr?.count ?? 0) devices")
        guard let peripherals = peripheralArr as? [QCBlePeripheral] else { 
            print("Failed to cast peripherals")
            return 
        }
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            // Convert QCBlePeripheral array to DiscoveredDevice array
            let newDevices = peripherals.compactMap { peripheral -> DiscoveredDevice? in
                // Skip devices without names
                guard let name = peripheral.peripheral.name, !name.isEmpty else {
                    return nil
                }
                
                return DiscoveredDevice(
                    peripheral: peripheral.peripheral,
                    name: name,
                    macAddress: peripheral.mac ?? ""
                )
            }
            
            // Update the discovered devices array by replacing with new devices
            // The Objective-C side already handles deduplication in its peripherals array
            self.discoveredDevices = newDevices
            
            print("Updated discovered devices:")
            for device in self.discoveredDevices {
                print("  - \(device.name) (\(device.macAddress))")
            }
            print("Total discovered devices: \(self.discoveredDevices.count)")
        }
    }
    
    func didState(_ state: QCState) {
        connectionState = state
        
        switch state {
        case .unbind:
            isConnected = false
            connectedDeviceName = ""
        case .connecting:
            print("Connecting...")
        case .connected:
            isConnected = true
            if let peripheral = centralManager?.connectedPeripheral {
                connectedDeviceName = peripheral.name ?? "Unknown Device"
            }
        case .disconnecting, .disconnected:
            isConnected = false
            connectedDeviceName = ""
        default:
            break
        }
    }
    
    func didConnected(_ peripheral: CBPeripheral) {
        print("Connected to: \(peripheral.name ?? "Unknown")")
        isConnected = true
        connectedDeviceName = peripheral.name ?? "Unknown Device"
    }
    
    func didDisconnecte(_ peripheral: CBPeripheral) {
        print("Disconnected from: \(peripheral.name ?? "Unknown")")
        isConnected = false
        connectedDeviceName = ""
    }
    
    func didFailConnected(_ peripheral: CBPeripheral, error: Error?) {
        print("Failed to connect to: \(peripheral.name ?? "Unknown"), error: \(error?.localizedDescription ?? "Unknown error")")
    }
    
    func didBluetoothState(_ state: QCBluetoothState) {
        print("Bluetooth state: \(state.rawValue)")
    }
}

// MARK: - QCSDKManagerDelegate
extension BluetoothManager: QCSDKManagerDelegate {
    func didUpdateBatteryLevel(_ battery: Int, charging: Bool) {
        deviceInfo.batteryLevel = battery
        deviceInfo.isCharging = charging
    }
    
    func didUpdateMedia(withPhotoCount photo: Int, videoCount video: Int, audioCount audio: Int, type: Int) {
        deviceInfo.photoCount = photo
        deviceInfo.videoCount = video
        deviceInfo.audioCount = audio
    }
    
    func didReceiveAIChatImageData(_ imageData: Data) {
        print("🎨 AI image received: \(imageData.count) bytes")
        deviceInfo.aiImageData = imageData
        
        // Verify it's a valid image
        if let image = UIImage(data: imageData) {
            print("✅ Valid image: \(image.size.width)x\(image.size.height)")
            // Post notification for gallery
            DispatchQueue.main.async {
                NotificationCenter.default.post(name: .aiImageReceived, object: imageData)
            }
        } else {
            print("❌ Invalid image data")
        }
    }
}