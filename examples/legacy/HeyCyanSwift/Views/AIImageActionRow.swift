//
//  AIImageActionRow.swift
//  HeyCyanSwift
//
//  Special action row for AI image capture with feedback
//

import SwiftUI
import GlassesFramework

struct AIImageActionRow: View {
    @ObservedObject var bluetoothManager: BluetoothManager
    @ObservedObject var handler: AIImageHandler
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Main row
            HStack {
                Image(systemName: "sparkles")
                    .foregroundColor(.blue)
                    .frame(width: 30)
                
                Text("Take AI Image")
                    .font(.headline)
                
                Spacer()
                
                if handler.isProcessing {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .blue))
                        .scaleEffect(0.8)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(.secondary)
                }
            }
            
            // Status text
            if handler.isProcessing {
                Text("Processing AI image...")
                    .font(.caption)
                    .foregroundColor(.blue)
            } else if handler.lastCapturedImage != nil {
                HStack {
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                        .font(.caption)
                    Text("Last capture successful")
                        .font(.caption)
                        .foregroundColor(.green)
                }
            }
            
            // Preview thumbnail if available
            if let image = handler.lastCapturedImage {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 60)
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.green, lineWidth: 1)
                    )
            }
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
        .onTapGesture {
            if !handler.isProcessing {
                handler.requestAIImage(using: bluetoothManager)
            }
        }
        .alert("AI Image Captured!", isPresented: $handler.showingSuccessAlert) {
            Button("OK") {}
        } message: {
            Text("Your AI image has been saved to the gallery.")
        }
        .alert("Error", isPresented: $handler.showingErrorAlert) {
            Button("OK") {}
        } message: {
            Text(handler.errorMessage)
        }
    }
}