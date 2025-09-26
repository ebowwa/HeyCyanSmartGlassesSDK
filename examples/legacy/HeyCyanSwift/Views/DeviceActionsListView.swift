//
//  DeviceActionsListView.swift
//  HeyCyanSwift
//
//  Displays and handles all device action buttons
//

import SwiftUI
import GlassesFramework

struct DeviceActionsListView: View {
    @ObservedObject var bluetoothManager: BluetoothManager
    @StateObject private var aiImageHandler = AIImageHandler()
    
    var body: some View {
        List {
            ForEach(DeviceActionType.allCases, id: \.self) { action in
                DeviceActionRow(
                    action: action,
                    bluetoothManager: bluetoothManager,
                    aiImageHandler: aiImageHandler
                )
            }
        }
    }
}

struct DeviceActionRow: View {
    let action: DeviceActionType
    @ObservedObject var bluetoothManager: BluetoothManager
    let aiImageHandler: AIImageHandler
    
    var body: some View {
        Group {
            if action == .takeAIImage {
                AIImageActionRow(
                    bluetoothManager: bluetoothManager,
                    handler: aiImageHandler
                )
            } else {
                StandardActionRow(
                    action: action,
                    bluetoothManager: bluetoothManager
                )
            }
        }
    }
}

// Standard action row for non-AI image actions
private struct StandardActionRow: View {
    let action: DeviceActionType
    @ObservedObject var bluetoothManager: BluetoothManager
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                actionIcon
                Text(action.title)
                    .font(.headline)
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundColor(.secondary)
            }
            
            if let detail = detailText {
                Text(detail)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(nil)
            }
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
        .onTapGesture {
            performAction()
        }
    }
    
    private var actionIcon: some View {
        Image(systemName: iconName)
            .foregroundColor(.blue)
            .frame(width: 30)
    }
    
    private var iconName: String {
        switch action {
        case .getVersion: return "info.circle"
        case .setTime: return "clock"
        case .getBattery: return "battery.100"
        case .getMediaInfo: return "photo.on.rectangle"
        case .takePhoto: return "camera"
        case .toggleVideoRecording: return "video"
        case .toggleAudioRecording: return "mic"
        case .takeAIImage: return "sparkles"
        case .findDevice: return "dot.radiowaves.left.and.right"
        }
    }

    private var detailText: String? {
        let info = bluetoothManager.deviceInfo
        
        switch action {
        case .getVersion:
            if !info.hardwareVersion.isEmpty {
                return """
                Hardware: \(info.hardwareVersion)
                Firmware: \(info.firmwareVersion)
                WiFi HW: \(info.hardwareWiFiVersion)
                WiFi FW: \(info.firmwareWiFiVersion)
                """
            }
        case .getBattery:
            if info.batteryLevel > 0 {
                return "Battery: \(info.batteryLevel)%, Charging: \(info.isCharging ? "Yes" : "No")"
            }
        case .getMediaInfo:
            return "Photos: \(info.photoCount), Videos: \(info.videoCount), Audio: \(info.audioCount)"
        case .toggleVideoRecording:
            return info.isRecordingVideo ? "Recording..." : "Tap to start recording"
        case .toggleAudioRecording:
            return info.isRecordingAudio ? "Recording..." : "Tap to start recording"
        case .findDevice:
            return "Play loud audio and flash LEDs to locate nearby glasses."
        default:
            return nil
        }
        return nil
    }
    
    private func performAction() {
        switch action {
        case .getVersion:
            bluetoothManager.getVersionInfo()
        case .setTime:
            bluetoothManager.setDeviceTime()
        case .getBattery:
            bluetoothManager.getBatteryStatus()
        case .getMediaInfo:
            bluetoothManager.getMediaInfo()
        case .takePhoto:
            bluetoothManager.takePhoto()
        case .toggleVideoRecording:
            bluetoothManager.toggleVideoRecording()
        case .toggleAudioRecording:
            bluetoothManager.toggleAudioRecording()
        case .takeAIImage:
            bluetoothManager.takeAIImage()
        case .findDevice:
            bluetoothManager.findDevice()
        }
    }
}

#Preview {
    DeviceActionsListView(bluetoothManager: BluetoothManager.shared)
}