//
//  AIGalleryView.swift
//  HeyCyanSwift
//
//  AI Image Gallery for HeyCyan Glasses
//

import SwiftUI
import Photos

struct AIImage: Identifiable {
    let id = UUID()
    let image: UIImage
    let timestamp: Date
}

class AIImageStore: ObservableObject {
    static let shared = AIImageStore()
    @Published var images: [AIImage] = []
    
    private let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    private let imagesFolder: URL
    
    private init() {
        imagesFolder = documentsDirectory.appendingPathComponent("AIImages")
        try? FileManager.default.createDirectory(at: imagesFolder, withIntermediateDirectories: true)
        loadImages()
        
        // Set up global listener for AI images
        NotificationCenter.default.addObserver(
            forName: .aiImageReceived,
            object: nil,
            queue: .main
        ) { [weak self] notification in
            print("🌍 Global listener: AI image received")
            if let imageData = notification.object as? Data,
               let image = UIImage(data: imageData) {
                print("✅ Global: Adding image to store")
                self?.addImage(image)
            }
        }
    }
    
    func addImage(_ image: UIImage) {
        print("📝 AIImageStore: Adding new image to gallery")
        let aiImage = AIImage(image: image, timestamp: Date())
        images.insert(aiImage, at: 0)
        saveImageToDisk(aiImage)
        print("✅ Image saved. Total images: \(images.count)")
    }
    
    func removeImage(_ aiImage: AIImage) {
        images.removeAll { $0.id == aiImage.id }
        deleteImageFromDisk(aiImage)
    }
    
    func clearAll() {
        for image in images {
            deleteImageFromDisk(image)
        }
        images.removeAll()
    }
    
    private func saveImageToDisk(_ aiImage: AIImage) {
        let filename = "\(aiImage.id.uuidString).jpg"
        let url = imagesFolder.appendingPathComponent(filename)
        if let data = aiImage.image.jpegData(compressionQuality: 0.8) {
            try? data.write(to: url)
        }
    }
    
    private func deleteImageFromDisk(_ aiImage: AIImage) {
        let filename = "\(aiImage.id.uuidString).jpg"
        let url = imagesFolder.appendingPathComponent(filename)
        try? FileManager.default.removeItem(at: url)
    }
    
    private func loadImages() {
        guard let files = try? FileManager.default.contentsOfDirectory(at: imagesFolder, includingPropertiesForKeys: [.creationDateKey]) else { return }
        
        for file in files {
            if let data = try? Data(contentsOf: file),
               let image = UIImage(data: data),
               let attributes = try? FileManager.default.attributesOfItem(atPath: file.path),
               let creationDate = attributes[.creationDate] as? Date {
                let aiImage = AIImage(image: image, timestamp: creationDate)
                images.append(aiImage)
            }
        }
        
        images.sort { $0.timestamp > $1.timestamp }
    }
}

struct AIGalleryView: View {
    @StateObject private var imageStore = AIImageStore.shared
    @State private var selectedImage: AIImage?
    @State private var showingShareSheet = false
    @State private var imageToShare: UIImage?
    @State private var showingDeleteConfirmation = false
    
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var body: some View {
        NavigationView {
            ScrollView {
                if imageStore.images.isEmpty {
                    VStack(spacing: 20) {
                        Image(systemName: "photo.on.rectangle.angled")
                            .font(.system(size: 60))
                            .foregroundColor(.gray)
                        
                        Text("No AI Images Yet")
                            .font(.headline)
                            .foregroundColor(.secondary)
                        
                        Text("Take AI photos with your glasses to see them here")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }
                    .frame(maxWidth: .infinity, minHeight: 400)
                } else {
                    LazyVGrid(columns: columns, spacing: 10) {
                        ForEach(imageStore.images) { aiImage in
                            ImageThumbnail(image: aiImage.image)
                                .onTapGesture {
                                    selectedImage = aiImage
                                }
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle("AI Gallery")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showingDeleteConfirmation = true
                    }) {
                        Image(systemName: "trash")
                    }
                    .disabled(imageStore.images.isEmpty)
                }
            }
            .alert("Delete All Images?", isPresented: $showingDeleteConfirmation) {
                Button("Delete All", role: .destructive) {
                    imageStore.clearAll()
                }
                Button("Cancel", role: .cancel) {}
            } message: {
                Text("This will permanently delete all AI images from the gallery.")
            }
            .sheet(item: $selectedImage) { image in
                ImageDetailView(aiImage: image, onShare: { img in
                    imageToShare = img
                    showingShareSheet = true
                })
            }
            .sheet(isPresented: $showingShareSheet) {
                if let imageToShare = imageToShare {
                    ShareSheet(image: imageToShare)
                }
            }
            // Images are automatically added via the global listener in AIImageStore.init()
            // No need for duplicate listener here
        }
    }
}

struct ImageThumbnail: View {
    let image: UIImage
    
    var body: some View {
        Image(uiImage: image)
            .resizable()
            .aspectRatio(contentMode: .fill)
            .frame(height: 120)
            .clipped()
            .cornerRadius(8)
    }
}

struct ImageDetailView: View {
    let aiImage: AIImage
    let onShare: (UIImage) -> Void
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            VStack {
                Image(uiImage: aiImage.image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                
                HStack {
                    Text("Captured: \(aiImage.timestamp, style: .date) at \(aiImage.timestamp, style: .time)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                }
                .padding()
            }
            .navigationTitle("AI Image")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Done") {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        onShare(aiImage.image)
                        dismiss()
                    }) {
                        Image(systemName: "square.and.arrow.up")
                    }
                }
            }
        }
    }
}

struct ShareSheet: UIViewControllerRepresentable {
    let image: UIImage
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        let controller = UIActivityViewController(
            activityItems: [image],
            applicationActivities: nil
        )
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

extension Notification.Name {
    static let aiImageReceived = Notification.Name("aiImageReceived")
}

#Preview {
    AIGalleryView()
}