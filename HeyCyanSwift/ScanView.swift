//
//  ScanView.swift
//  HeyCyanSwift
//
//  Created on 2025/8/14.
//

import SwiftUI

struct ScanView: View {
    @ObservedObject var bluetoothManager = BluetoothManager.shared
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            VStack {
                if bluetoothManager.isScanning {
                    ProgressView("Scanning for devices...")
                        .padding()
                }
                
                if bluetoothManager.discoveredDevices.isEmpty {
                    if bluetoothManager.isScanning {
                        Spacer()
                        VStack(spacing: 16) {
                            Text("Searching for devices...")
                                .font(.headline)
                                .foregroundColor(.secondary)
                            Text("Make sure your device is powered on and in pairing mode")
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                        }
                        .padding()
                        Spacer()
                    } else {
                        Spacer()
                        Text("No devices found")
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                } else {
                    List(bluetoothManager.discoveredDevices) { device in
                        DeviceRow(device: device) {
                            print("ScanView: Connecting to device: \(device.name)")
                            bluetoothManager.stopScanning()
                            bluetoothManager.connect(to: device)
                            dismiss()
                        }
                    }
                }
            }
            .navigationTitle("Search Devices")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        bluetoothManager.stopScanning()
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    if bluetoothManager.isScanning {
                        Button("Stop") {
                            bluetoothManager.stopScanning()
                        }
                    } else {
                        Button("Scan") {
                            bluetoothManager.startScanning()
                        }
                    }
                }
            }
            .onAppear {
                bluetoothManager.startScanning()
            }
            .onDisappear {
                bluetoothManager.stopScanning()
            }
        }
    }
}

struct DeviceRow: View {
    let device: DiscoveredDevice
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: 4) {
                Text(device.name)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                if !device.macAddress.isEmpty {
                    Text(device.macAddress)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            .padding(.vertical, 8)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    ScanView()
}