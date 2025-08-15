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
    
    let bluetoothManager = GlassesSDK.bluetoothManager
    
    // Navigation actions
    func showScanner() {
        showingScanView = true
    }
    
    func showGallery() {
        showingGallery = true
    }
    
    func handleConnectionAction() {
        if bluetoothManager.isConnected {
            bluetoothManager.disconnect()
        } else {
            showScanner()
        }
    }
    
    // Computed properties for UI
    var navigationTitle: String {
        bluetoothManager.isConnected 
            ? bluetoothManager.connectedDeviceName 
            : "HeyCyan Glasses"
    }
    
    var connectionButtonTitle: String {
        bluetoothManager.isConnected ? "Disconnect" : "Search"
    }
}