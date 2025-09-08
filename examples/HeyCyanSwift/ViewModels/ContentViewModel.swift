//
//  ContentViewModel.swift
//  HeyCyanSwift
//
//  View model for main content view coordination
//

import SwiftUI
import Combine
import GlassesFramework

class ContentViewModel: ObservableObject {
    @Published var showingScanView = false
    @Published var showingGallery = false
    @Published var isConnected = false
    @Published var connectedDeviceName = ""
    
    let bluetoothManager = GlassesSDK.bluetoothManager
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        // Subscribe to BluetoothManager changes
        bluetoothManager.$isConnected
            .receive(on: DispatchQueue.main)
            .assign(to: &$isConnected)
        
        bluetoothManager.$connectedDeviceName
            .receive(on: DispatchQueue.main)
            .assign(to: &$connectedDeviceName)
    }
    
    // Navigation actions
    func showScanner() {
        showingScanView = true
    }
    
    func showGallery() {
        showingGallery = true
    }
    
    func handleConnectionAction() {
        if isConnected {
            bluetoothManager.disconnect()
        } else {
            showScanner()
        }
    }
    
    // Computed properties for UI
    var navigationTitle: String {
        isConnected 
            ? connectedDeviceName 
            : "HeyCyan Glasses"
    }
    
    var connectionButtonTitle: String {
        isConnected ? "Disconnect" : "Search"
    }
}