
//
//  ContentView.swift
//  HeyCyanSwift
//
//  Created by Elijah Arbee on 8/14/25.
//

import SwiftUI
import GlassesFramework

struct ContentView: View {
    @StateObject private var viewModel = ContentViewModel()
    
    var body: some View {
        NavigationView {
            MainContentView(viewModel: viewModel)
                .navigationTitle(viewModel.navigationTitle)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ConnectionToolbarButton(viewModel: viewModel)
                }
                .sheet(isPresented: $viewModel.showingScanView) {
                    ScanView()
                }
                .sheet(isPresented: $viewModel.showingGallery) {
                    AIGalleryView()
                }
        }
    }
}

// MARK: - Main Content
private struct MainContentView: View {
    @ObservedObject var viewModel: ContentViewModel
    
    var body: some View {
        VStack {
            if viewModel.bluetoothManager.isConnected {
                ConnectedDeviceView(viewModel: viewModel)
            } else {
                ConnectionStatusView(
                    isConnected: false,
                    deviceName: "",
                    onSearchTapped: viewModel.showScanner
                )
            }
        }
    }
}

// MARK: - Connected Device View
private struct ConnectedDeviceView: View {
    @ObservedObject var viewModel: ContentViewModel
    
    var body: some View {
        VStack {
            GalleryButtonView(action: viewModel.showGallery)
            DeviceActionsListView(bluetoothManager: viewModel.bluetoothManager)
        }
    }
}

// MARK: - Toolbar Button
private struct ConnectionToolbarButton: ToolbarContent {
    @ObservedObject var viewModel: ContentViewModel
    
    var body: some ToolbarContent {
        ToolbarItem(placement: .navigationBarTrailing) {
            Button(viewModel.connectionButtonTitle) {
                viewModel.handleConnectionAction()
            }
        }
    }
}

#Preview {
    ContentView()
}
