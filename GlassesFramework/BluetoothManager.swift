//
//  BluetoothManager.swift
//  HeyCyanSwift
//
//  Created on 2025/8/14.
//

import Foundation
import CoreBluetooth
import Combine
import UIKit
import QCSDK

// Device action types matching the Objective-C demo
public enum DeviceActionType: Int, CaseIterable {
    case getVersion = 0
    case setTime
    case getBattery
    case getMediaInfo
    case takePhoto
    case toggleVideoRecording
    case toggleAudioRecording
    case takeAIImage
    
    public var title: String {
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
public struct DeviceInfo {
    public var hardwareVersion: String = ""
    public var firmwareVersion: String = ""
    public var hardwareWiFiVersion: String = ""
    public var firmwareWiFiVersion: String = ""
    public var macAddress: String = ""
    public var batteryLevel: Int = 0
    public var isCharging: Bool = false
    public var photoCount: Int = 0
    public var videoCount: Int = 0
    public var audioCount: Int = 0
    public var isRecordingVideo: Bool = false
    public var isRecordingAudio: Bool = false
    public var aiImageData: Data?
    
    public init() {}
}

// Discovered device model
public struct DiscoveredDevice: Identifiable {
    public let id = UUID()
    public let peripheral: CBPeripheral
    public let name: String
    public let macAddress: String
}

public class BluetoothManager: NSObject, ObservableObject {
    public static let shared = BluetoothManager()
    
    @Published public var isConnected = false
    @Published public var isScanning = false
    @Published public var discoveredDevices: [DiscoveredDevice] = []
    @Published public var connectedDeviceName: String = ""
    @Published public var deviceInfo = DeviceInfo()
    @Published public var connectionState: QCState = .unbind
    
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
    public func startScanning() {
        print("🔍 BluetoothManager: Starting scan...")
        print("🔍 Central manager exists: \(centralManager != nil)")
        DispatchQueue.main.async { [weak self] in
            self?.isScanning = true
            self?.discoveredDevices.removeAll()
        }
        centralManager?.scan()
        
        // Force check for devices after a delay
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { [weak self] in
            print("🔍 Force checking for devices...")
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
                        macAddress: qcPeripheral.mac
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
    
    public func stopScanning() {
        print("BluetoothManager: Stopping scan...")
        DispatchQueue.main.async { [weak self] in
            self?.isScanning = false
        }
        centralManager?.stopScan()
    }
    
    // MARK: - Connection
    public func connect(to device: DiscoveredDevice) {
        print("Attempting to connect to: \(device.name) - \(device.peripheral.identifier)")
        
        // For reconnection, we need to get a fresh peripheral reference
        if device.peripheral.state == .disconnected {
            centralManager?.connect(device.peripheral)
        } else {
            print("Peripheral not in disconnected state: \(device.peripheral.state.rawValue)")
        }
    }
    
    public func disconnect() {
        centralManager?.remove()
        // Clear the discovered devices after disconnect to force a fresh scan
        DispatchQueue.main.async { [weak self] in
            self?.discoveredDevices.removeAll()
            self?.isConnected = false
            self?.connectedDeviceName = ""
        }
    }
    
    // MARK: - Device Actions
    public func getVersionInfo() {
        QCSDKCmdCreator.getDeviceVersionInfoSuccess({ [weak self] (hdVersion: String?, firmVersion: String?, hdWifiVersion: String?, firmWifiVersion: String?) in
            self?.deviceInfo.hardwareVersion = hdVersion ?? ""
            self?.deviceInfo.firmwareVersion = firmVersion ?? ""
            self?.deviceInfo.hardwareWiFiVersion = hdWifiVersion ?? ""
            self?.deviceInfo.firmwareWiFiVersion = firmWifiVersion ?? ""
        }, fail: {
            print("Failed to get version info")
        })
    }
    
    public func getMacAddress() {
        QCSDKCmdCreator.getDeviceMacAddressSuccess({ [weak self] macAddress in
            self?.deviceInfo.macAddress = macAddress ?? ""
        }, fail: {
            print("Failed to get MAC address")
        })
    }
    
    public func setDeviceTime() {
        QCSDKCmdCreator.setupDeviceDateTime { isSuccess, error in
            if let error = error {
                print("Failed to set time: \(error)")
            } else {
                print("Time set successfully")
            }
        }
    }
    
    public func getBatteryStatus() {
        QCSDKCmdCreator.getDeviceBattery({ [weak self] battery, charging in
            self?.deviceInfo.batteryLevel = battery
            self?.deviceInfo.isCharging = charging
        }, fail: {
            print("Failed to get battery status")
        })
    }
    
    public func getMediaInfo() {
        QCSDKCmdCreator.getDeviceMedia({ [weak self] photo, video, audio, type in
            self?.deviceInfo.photoCount = photo
            self?.deviceInfo.videoCount = video
            self?.deviceInfo.audioCount = audio
        }, fail: {
            print("Failed to get media info")
        })
    }
    
    public func takePhoto() {
        QCSDKCmdCreator.setDeviceMode(.photo, success: {
            print("Photo taken")
        }, fail: { mode in
            print("Failed to take photo, current mode: \(mode)")
        })
    }
    
    public func toggleVideoRecording() {
        let mode: QCOperatorDeviceMode = deviceInfo.isRecordingVideo ? .videoStop : .video
        QCSDKCmdCreator.setDeviceMode(mode, success: { [weak self] in
            self?.deviceInfo.isRecordingVideo.toggle()
        }, fail: { mode in
            print("Failed to toggle video, current mode: \(mode)")
        })
    }
    
    public func toggleAudioRecording() {
        let mode: QCOperatorDeviceMode = deviceInfo.isRecordingAudio ? .audioStop : .audio
        QCSDKCmdCreator.setDeviceMode(mode, success: { [weak self] in
            self?.deviceInfo.isRecordingAudio.toggle()
        }, fail: { mode in
            print("Failed to toggle audio, current mode: \(mode)")
        })
    }
    
    public func takeAIImage() {
        print("📸 Requesting AI image capture...")
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("✅ AI photo request sent successfully")
            // The image will be received via didReceiveAIChatImageData delegate
        }, fail: { mode in
            print("❌ Failed to take AI photo, current mode: \(mode)")
            // Post failure notification
            DispatchQueue.main.async {
                NotificationCenter.default.post(
                    name: Notification.Name("AIImageCaptureFailed"),
                    object: nil,
                    userInfo: ["error": "Device is in mode \(mode)"]
                )
            }
        })
    }
}

// MARK: - QCCentralManagerDelegate
extension BluetoothManager: QCCentralManagerDelegate {
    public func didScanPeripherals(_ peripheralArr: [QCBlePeripheral]?) {
        print("didScanPeripherals called with \(peripheralArr?.count ?? 0) devices")
        guard let peripherals = peripheralArr else { 
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
                    macAddress: peripheral.mac
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
    
    public func didState(_ state: QCState) {
        print("🔵 Connection state changed to: \(state.rawValue)")
        connectionState = state
        
        switch state {
        case .unbind:
            print("📱 Device unbound")
            isConnected = false
            connectedDeviceName = ""
        case .connecting:
            print("🔄 Connecting...")
        case .connected:
            print("✅ Connected!")
            isConnected = true
            if let peripheral = centralManager?.connectedPeripheral {
                connectedDeviceName = peripheral.name ?? "Unknown Device"
                print("✅ Connected to: \(connectedDeviceName)")
            }
        case .disconnecting:
            print("🔄 Disconnecting...")
            isConnected = false
            connectedDeviceName = ""
        case .disconnected:
            print("❌ Disconnected")
            isConnected = false
            connectedDeviceName = ""
        default:
            print("⚠️ Unknown state: \(state.rawValue)")
            break
        }
    }
    
    public func didConnected(_ peripheral: CBPeripheral) {
        print("Connected to: \(peripheral.name ?? "Unknown")")
        isConnected = true
        connectedDeviceName = peripheral.name ?? "Unknown Device"
    }
    
    public func didDisconnecte(_ peripheral: CBPeripheral) {
        print("Disconnected from: \(peripheral.name ?? "Unknown")")
        isConnected = false
        connectedDeviceName = ""
    }
    
    public func didFailConnected(_ peripheral: CBPeripheral, error: Error?) {
        print("Failed to connect to: \(peripheral.name ?? "Unknown"), error: \(error?.localizedDescription ?? "Unknown error")")
    }
    
    public func didBluetoothState(_ state: QCBluetoothState) {
        print("Bluetooth state: \(state.rawValue)")
    }
}

// MARK: - QCSDKManagerDelegate
extension BluetoothManager: QCSDKManagerDelegate {
    public func didUpdateBatteryLevel(_ battery: Int, charging: Bool) {
        deviceInfo.batteryLevel = battery
        deviceInfo.isCharging = charging
    }
    
    public func didUpdateMedia(withPhotoCount photo: Int, videoCount video: Int, audioCount audio: Int, type: Int) {
        deviceInfo.photoCount = photo
        deviceInfo.videoCount = video
        deviceInfo.audioCount = audio
    }
    
    public func didReceiveAIChatImageData(_ imageData: Data) {
        print("🎨 AI image received: \(imageData.count) bytes")
        
        // Store in device info for immediate access
        deviceInfo.aiImageData = imageData
        
        // Verify it's a valid image
        if let image = UIImage(data: imageData) {
            print("✅ Valid AI image: \(image.size.width)x\(image.size.height)")
            
            // Post notification for all listeners (gallery, handlers, etc.)
            // The AIImageHandler will handle saving to photo library and gallery
            DispatchQueue.main.async {
                NotificationCenter.default.post(
                    name: .aiImageReceived,
                    object: imageData,
                    userInfo: [
                        "timestamp": Date(),
                        "size": imageData.count,
                        "dimensions": "\(image.size.width)x\(image.size.height)"
                    ]
                )
            }
        } else {
            print("❌ Invalid image data received")
            DispatchQueue.main.async {
                NotificationCenter.default.post(
                    name: Notification.Name("AIImageCaptureFailed"),
                    object: nil,
                    userInfo: ["error": "Invalid image data"]
                )
            }
        }
    }
}
