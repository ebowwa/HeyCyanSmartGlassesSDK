//
//  AIImageHandler.swift
//  HeyCyanSwift
//
//  Handles AI image capture and provides user feedback
//

import SwiftUI
import Combine
import GlassesFramework

class AIImageHandler: ObservableObject {
    @Published var isProcessing = false
    @Published var lastCapturedImage: UIImage?
    @Published var showingSuccessAlert = false
    @Published var showingErrorAlert = false
    @Published var errorMessage = ""
    
    private var cancellables = Set<AnyCancellable>()
    private let imageStore = AIImageStore.shared
    
    init() {
        print("🔵 AIImageHandler init - instance created")
        setupNotificationObserver()
    }
    
    deinit {
        print("🔴 AIImageHandler deinit - instance destroyed")
    }
    
    private func setupNotificationObserver() {
        print("📡 AIImageHandler: Setting up notification observer")
        NotificationCenter.default.publisher(for: .aiImageReceived)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] notification in
                print("📨 AIImageHandler: Received notification")
                self?.handleReceivedImage(notification)
            }
            .store(in: &cancellables)
    }
    
    private func handleReceivedImage(_ notification: Notification) {
        isProcessing = false
        
        guard let imageData = notification.object as? Data else {
            showError("Invalid image data received")
            return
        }
        
        guard let image = UIImage(data: imageData) else {
            showError("Could not process image data")
            return
        }
        
        // Store the image (only once, in the gallery)
        lastCapturedImage = image
        imageStore.addImage(image)
        
        // Show success feedback
        showingSuccessAlert = true
        
        // Haptic feedback
        let impactFeedback = UIImpactFeedbackGenerator(style: .medium)
        impactFeedback.impactOccurred()
    }
    
    func requestAIImage(using bluetoothManager: BluetoothManager) {
        guard !isProcessing else { return }
        
        isProcessing = true
        errorMessage = ""
        
        // Start timeout timer
        DispatchQueue.main.asyncAfter(deadline: .now() + 30) { [weak self] in
            guard let self = self, self.isProcessing else { return }
            self.isProcessing = false
            self.showError("AI image capture timed out. Please try again.")
        }
        
        bluetoothManager.takeAIImage()
    }
    
    private func showError(_ message: String) {
        errorMessage = message
        showingErrorAlert = true
        isProcessing = false
        
        // Error haptic feedback
        let notificationFeedback = UINotificationFeedbackGenerator()
        notificationFeedback.notificationOccurred(.error)
    }
}

// MARK: - AI Image Capture View
struct AIImageCaptureView: View {
    @StateObject private var handler = AIImageHandler()
    @ObservedObject var bluetoothManager: BluetoothManager
    @State private var showingFullImage = false
    
    var body: some View {
        VStack(spacing: 16) {
            // Status indicator
            if handler.isProcessing {
                ProcessingView()
            } else if let image = handler.lastCapturedImage {
                CapturedImageView(image: image, onTap: {
                    showingFullImage = true
                })
            }
            
            // Capture button
            AIImageCaptureButton(
                isProcessing: handler.isProcessing,
                action: {
                    handler.requestAIImage(using: bluetoothManager)
                }
            )
        }
        .alert("AI Image Captured!", isPresented: $handler.showingSuccessAlert) {
            Button("View Gallery") {
                // Navigate to gallery
            }
            Button("OK", role: .cancel) {}
        } message: {
            Text("Your AI image has been saved to the gallery.")
        }
        .alert("Error", isPresented: $handler.showingErrorAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(handler.errorMessage)
        }
        .sheet(isPresented: $showingFullImage) {
            if let image = handler.lastCapturedImage {
                FullImageView(image: image)
            }
        }
    }
}

// MARK: - Processing View
private struct ProcessingView: View {
    @State private var rotation = 0.0
    
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "sparkles")
                .font(.system(size: 40))
                .foregroundColor(.blue)
                .rotationEffect(.degrees(rotation))
                .onAppear {
                    withAnimation(.linear(duration: 2).repeatForever(autoreverses: false)) {
                        rotation = 360
                    }
                }
            
            Text("Processing AI Image...")
                .font(.caption)
                .foregroundColor(.secondary)
            
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
        }
        .padding()
        .background(Color(UIColor.systemGray6))
        .cornerRadius(12)
    }
}

// MARK: - Captured Image View
private struct CapturedImageView: View {
    let image: UIImage
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            VStack {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(maxHeight: 200)
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.blue, lineWidth: 2)
                    )
                
                Text("Tap to view full size")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
    }
}

// MARK: - AI Image Capture Button
private struct AIImageCaptureButton: View {
    let isProcessing: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: isProcessing ? "sparkles.circle.fill" : "sparkles")
                    .font(.system(size: 20))
                
                Text(isProcessing ? "Processing..." : "Capture AI Image")
                    .fontWeight(.medium)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(isProcessing ? Color.gray : Color.blue)
            .foregroundColor(.white)
            .cornerRadius(12)
            .disabled(isProcessing)
        }
        .padding(.horizontal)
    }
}

// MARK: - Full Image View
private struct FullImageView: View {
    let image: UIImage
    @Environment(\.dismiss) private var dismiss
    @State private var scale: CGFloat = 1.0
    
    var body: some View {
        NavigationView {
            ZoomableImageView(image: image)
                .navigationTitle("AI Image")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button("Done") { dismiss() }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            let activityVC = UIActivityViewController(activityItems: [image], applicationActivities: nil)
                            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                               let window = windowScene.windows.first,
                               let rootVC = window.rootViewController {
                                rootVC.present(activityVC, animated: true)
                            }
                        }) {
                            Image(systemName: "square.and.arrow.up")
                        }
                    }
                }
        }
    }
}

// MARK: - Zoomable Image View
private struct ZoomableImageView: UIViewRepresentable {
    let image: UIImage
    
    func makeUIView(context: Context) -> UIScrollView {
        let scrollView = UIScrollView()
        scrollView.delegate = context.coordinator
        scrollView.minimumZoomScale = 1.0
        scrollView.maximumZoomScale = 5.0
        scrollView.bouncesZoom = true
        
        let imageView = UIImageView(image: image)
        imageView.contentMode = .scaleAspectFit
        scrollView.addSubview(imageView)
        
        return scrollView
    }
    
    func updateUIView(_ uiView: UIScrollView, context: Context) {
        if let imageView = uiView.subviews.first as? UIImageView {
            imageView.frame = CGRect(origin: .zero, size: image.size)
            uiView.contentSize = image.size
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }
    
    class Coordinator: NSObject, UIScrollViewDelegate {
        func viewForZooming(in scrollView: UIScrollView) -> UIView? {
            return scrollView.subviews.first
        }
    }
}