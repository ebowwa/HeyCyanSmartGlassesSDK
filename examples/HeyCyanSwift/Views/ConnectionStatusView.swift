//
//  ConnectionStatusView.swift
//  HeyCyanSwift
//
//  Displays the current connection status with the glasses
//

import SwiftUI

struct ConnectionStatusView: View {
    let isConnected: Bool
    let deviceName: String
    let onSearchTapped: () -> Void
    
    var body: some View {
        if isConnected {
            ConnectedHeaderView(deviceName: deviceName)
        } else {
            DisconnectedView(onSearchTapped: onSearchTapped)
        }
    }
}

private struct ConnectedHeaderView: View {
    let deviceName: String
    
    var body: some View {
        HStack {
            Image(systemName: "checkmark.circle.fill")
                .foregroundColor(.green)
            Text("Connected to \(deviceName)")
                .font(.headline)
        }
        .padding()
        .background(Color.green.opacity(0.1))
        .cornerRadius(10)
        .padding(.horizontal)
    }
}

private struct DisconnectedView: View {
    let onSearchTapped: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "eyeglasses")
                .font(.system(size: 80))
                .foregroundColor(.gray)
            
            Text("No Device Connected")
                .font(.title2)
                .foregroundColor(.secondary)
            
            Button(action: onSearchTapped) {
                Label("Search for Devices", systemImage: "magnifyingglass")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
        }
        .padding()
    }
}

#Preview {
    VStack {
        ConnectionStatusView(
            isConnected: false,
            deviceName: "",
            onSearchTapped: {}
        )
        
        ConnectionStatusView(
            isConnected: true,
            deviceName: "M01 Glasses",
            onSearchTapped: {}
        )
    }
}