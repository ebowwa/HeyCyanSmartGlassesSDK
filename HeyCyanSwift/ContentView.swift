
//
//  ContentView.swift
//  HeyCyanSwift
//
//  Created by Elijah Arbee on 8/14/25.
//

import SwiftUI
import GlassesFramework

struct ContentView: View {
    @StateObject private var bluetoothManager = GlassesSDK.bluetoothManager
    @State private var showingScanView = false
    @State private var showingGallery = false
    
    var body: some View {
        NavigationView {
            VStack {
                if bluetoothManager.isConnected {
                    connectedView
                } else {
                    disconnectedView
                }
            }
            .navigationTitle(bluetoothManager.isConnected ? bluetoothManager.connectedDeviceName : "HeyCyan Glasses")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(bluetoothManager.isConnected ? "Disconnect" : "Search") {
                        if bluetoothManager.isConnected {
                            bluetoothManager.disconnect()
                        } else {
                            showingScanView = true
                        }
                    }
                }
            }
            .sheet(isPresented: $showingScanView) {
                ScanView()
            }
            .sheet(isPresented: $showingGallery) {
                AIGalleryView()
            }
        }
    }
    
    var disconnectedView: some View {
        VStack(spacing: 20) {
            Image(systemName: "eyeglasses")
                .font(.system(size: 80))
                .foregroundColor(.gray)
            
            Text("No Device Connected")
                .font(.title2)
                .foregroundColor(.secondary)
            
            Button(action: {
                showingScanView = true
            }) {
                Label("Search for Devices", systemImage: "magnifyingglass")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
        }
        .padding()
    }
    
    var connectedView: some View {
        VStack {
            // Gallery button at the top
            Button(action: {
                showingGallery = true
            }) {
                HStack {
                    Image(systemName: "photo.stack")
                    Text("AI Gallery")
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.secondary)
                }
                .padding()
                .background(Color(UIColor.systemGray6))
                .cornerRadius(10)
            }
            .padding(.horizontal)
            .padding(.top)
            
            // Device actions list
            List {
                ForEach(DeviceActionType.allCases, id: \.self) { action in
                    DeviceActionRow(action: action, bluetoothManager: bluetoothManager)
                }
            }
        }
    }
}

struct DeviceActionRow: View {
    let action: DeviceActionType
    @ObservedObject var bluetoothManager: BluetoothManager
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(action.title)
                    .font(.headline)
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundColor(.secondary)
            }
            
            if let detail = getDetailText() {
                Text(detail)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(nil)
            }
            
            if action == .takeAIImage, let imageData = bluetoothManager.deviceInfo.aiImageData {
                if let uiImage = UIImage(data: imageData) {
                    Image(uiImage: uiImage)
                        .resizable()
                        .scaledToFit()
                        .frame(height: 100)
                        .cornerRadius(8)
                }
            }
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
        .onTapGesture {
            performAction()
        }
    }
    
    func getDetailText() -> String? {
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
        default:
            return nil
        }
        return nil
    }
    
    func performAction() {
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
        }
    }
}

#Preview {
    ContentView()
}
