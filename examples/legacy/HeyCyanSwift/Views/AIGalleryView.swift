//
//  AIGalleryView.swift
//  HeyCyanSwift
//
//  AI Image Gallery for HeyCyan Glasses
//

import SwiftUI
import Photos
import GlassesFramework

struct AIImage: Identifiable {
    let id: UUID
    let image: UIImage
    let timestamp: Date
    
    init(id: UUID = UUID(), image: UIImage, timestamp: Date) {
        self.id = id
        self.image = image
        self.timestamp = timestamp
    }
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
        
        // Note: AIImageHandler already observes .aiImageReceived and adds to the store
        // We don't need another observer here to avoid duplicates
    }
    
    func addImage(_ image: UIImage) {
        print("📝 AIImageStore: Adding new image to gallery")
        print("📊 Stack trace: \(Thread.callStackSymbols.prefix(5).joined(separator: "\n"))")
        let aiImage = AIImage(image: image, timestamp: Date())
        images.insert(aiImage, at: 0)
        saveImageToDisk(aiImage)
        print("✅ Image saved. Total images: \(images.count)")
    }
    
    func removeImage(_ aiImage: AIImage) {
        print("🗑️ Removing image with ID: \(aiImage.id)")
        let beforeCount = images.count
        images.removeAll { $0.id == aiImage.id }
        let afterCount = images.count
        print("🗑️ Images before: \(beforeCount), after: \(afterCount)")
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
        do {
            try FileManager.default.removeItem(at: url)
            print("✅ Deleted file: \(filename)")
        } catch {
            print("❌ Failed to delete file: \(error)")
        }
    }
    
    private func loadImages() {
        guard let files = try? FileManager.default.contentsOfDirectory(at: imagesFolder, includingPropertiesForKeys: [.creationDateKey]) else { return }
        
        for file in files {
            // Extract UUID from filename (format: "UUID.jpg")
            let filename = file.lastPathComponent
            let uuidString = filename.replacingOccurrences(of: ".jpg", with: "")
            
            if let uuid = UUID(uuidString: uuidString),
               let data = try? Data(contentsOf: file),
               let image = UIImage(data: data),
               let attributes = try? FileManager.default.attributesOfItem(atPath: file.path),
               let creationDate = attributes[.creationDate] as? Date {
                let aiImage = AIImage(id: uuid, image: image, timestamp: creationDate)
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
    @State private var isSelectionMode = false
    @State private var selectedImages = Set<UUID>()
    @State private var showingSaveAlert = false
    @State private var saveAlertMessage = ""
    @State private var viewMode: ViewMode = .grid
    
    enum ViewMode {
        case grid
        case scroll
    }
    
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var selectedCount: Int {
        selectedImages.count
    }
    
    var body: some View {
        NavigationView {
            if viewMode == .scroll {
                TikTokScrollView(
                    imageStore: imageStore,
                    viewMode: $viewMode,
                    imageToShare: $imageToShare,
                    showingShareSheet: $showingShareSheet
                )
            } else {
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
                            ZStack(alignment: .topTrailing) {
                                ImageThumbnail(image: aiImage.image)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 8)
                                            .stroke(Color.blue, lineWidth: selectedImages.contains(aiImage.id) && isSelectionMode ? 3 : 0)
                                    )
                                    .scaleEffect(selectedImages.contains(aiImage.id) && isSelectionMode ? 0.95 : 1.0)
                                    .animation(.easeInOut(duration: 0.1), value: selectedImages.contains(aiImage.id))
                                    .onTapGesture {
                                        if isSelectionMode {
                                            toggleSelection(for: aiImage)
                                        } else {
                                            selectedImage = aiImage
                                        }
                                    }
                                    .onLongPressGesture {
                                        if !isSelectionMode {
                                            withAnimation {
                                                isSelectionMode = true
                                                toggleSelection(for: aiImage)
                                            }
                                        }
                                    }
                                
                                // Selection checkbox
                                if isSelectionMode {
                                    Circle()
                                        .fill(selectedImages.contains(aiImage.id) ? Color.blue : Color.white)
                                        .frame(width: 28, height: 28)
                                        .overlay(
                                            Image(systemName: selectedImages.contains(aiImage.id) ? "checkmark" : "")
                                                .font(.system(size: 16, weight: .bold))
                                                .foregroundColor(.white)
                                        )
                                        .overlay(
                                            Circle()
                                                .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                                        )
                                        .padding(8)
                                }
                            }
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle(isSelectionMode ? "\(selectedCount) Selected" : "AI Gallery")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    if isSelectionMode {
                        Button("Cancel") {
                            exitSelectionMode()
                        }
                    }
                }
                
                ToolbarItemGroup(placement: .navigationBarTrailing) {
                    if isSelectionMode {
                        // Selection mode actions
                        Button(action: {
                            selectAll()
                        }) {
                            Image(systemName: "checkmark.square")
                        }
                        
                        Button(action: {
                            saveSelectedToPhotoLibrary()
                        }) {
                            Image(systemName: "square.and.arrow.down")
                        }
                        .disabled(selectedImages.isEmpty)
                        
                        Button(action: {
                            showingDeleteConfirmation = true
                        }) {
                            Image(systemName: "trash")
                        }
                        .disabled(selectedImages.isEmpty)
                    } else {
                        // Normal mode actions
                        Button(action: {
                            viewMode = viewMode == .grid ? .scroll : .grid
                        }) {
                            Image(systemName: viewMode == .grid ? "rectangle.stack" : "square.grid.3x3")
                        }
                        .disabled(imageStore.images.isEmpty)
                        
                        Button(action: {
                            isSelectionMode = true
                        }) {
                            Text("Select")
                        }
                        .disabled(imageStore.images.isEmpty)
                    }
                }
            }
            .alert(isSelectionMode ? "Delete Selected Images?" : "Delete All Images?", isPresented: $showingDeleteConfirmation) {
                Button("Delete", role: .destructive) {
                    if isSelectionMode {
                        deleteSelectedImages()
                    } else {
                        imageStore.clearAll()
                    }
                }
                Button("Cancel", role: .cancel) {}
            } message: {
                Text(isSelectionMode ? 
                    "This will permanently delete \(selectedCount) selected image(s) from the gallery." :
                    "This will permanently delete all AI images from the gallery.")
            }
            .alert("Photo Library", isPresented: $showingSaveAlert) {
                Button("OK") { }
            } message: {
                Text(saveAlertMessage)
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
    
    // MARK: - Helper Functions
    
    private func toggleSelection(for aiImage: AIImage) {
        if selectedImages.contains(aiImage.id) {
            selectedImages.remove(aiImage.id)
        } else {
            selectedImages.insert(aiImage.id)
        }
    }
    
    private func selectAll() {
        if selectedImages.count == imageStore.images.count {
            // If all selected, deselect all
            selectedImages.removeAll()
        } else {
            // Select all
            selectedImages = Set(imageStore.images.map { $0.id })
        }
    }
    
    private func exitSelectionMode() {
        isSelectionMode = false
        selectedImages.removeAll()
    }
    
    private func deleteSelectedImages() {
        let imagesToDelete = imageStore.images.filter { selectedImages.contains($0.id) }
        for image in imagesToDelete {
            imageStore.removeImage(image)
        }
        exitSelectionMode()
    }
    
    private func saveSelectedToPhotoLibrary() {
        let imagesToSave = imageStore.images.filter { selectedImages.contains($0.id) }
        
        PHPhotoLibrary.requestAuthorization { status in
            DispatchQueue.main.async {
                switch status {
                case .authorized, .limited:
                    var savedCount = 0
                    for aiImage in imagesToSave {
                        UIImageWriteToSavedPhotosAlbum(aiImage.image, nil, nil, nil)
                        savedCount += 1
                    }
                    self.saveAlertMessage = "\(savedCount) image(s) saved to photo library!"
                    self.showingSaveAlert = true
                    self.exitSelectionMode()
                case .denied, .restricted:
                    self.saveAlertMessage = "Photo library access denied. Please enable in Settings."
                    self.showingSaveAlert = true
                case .notDetermined:
                    self.saveAlertMessage = "Photo library access not determined."
                    self.showingSaveAlert = true
                @unknown default:
                    self.saveAlertMessage = "Unknown error occurred."
                    self.showingSaveAlert = true
                }
            }
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
    @State private var showingSaveAlert = false
    @State private var saveAlertMessage = ""
    
    func saveToPhotoLibrary(_ image: UIImage) {
        PHPhotoLibrary.requestAuthorization { status in
            DispatchQueue.main.async {
                switch status {
                case .authorized, .limited:
                    UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil)
                    saveAlertMessage = "Image saved to photo library!"
                    showingSaveAlert = true
                case .denied, .restricted:
                    saveAlertMessage = "Photo library access denied. Please enable in Settings."
                    showingSaveAlert = true
                case .notDetermined:
                    saveAlertMessage = "Photo library access not determined."
                    showingSaveAlert = true
                @unknown default:
                    saveAlertMessage = "Unknown error occurred."
                    showingSaveAlert = true
                }
            }
        }
    }
    
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
                
                ToolbarItemGroup(placement: .navigationBarTrailing) {
                    Button(action: {
                        saveToPhotoLibrary(aiImage.image)
                    }) {
                        Image(systemName: "square.and.arrow.down")
                    }
                    
                    Button(action: {
                        onShare(aiImage.image)
                        dismiss()
                    }) {
                        Image(systemName: "square.and.arrow.up")
                    }
                }
            }
            .alert("Photo Library", isPresented: $showingSaveAlert) {
                Button("OK") { }
            } message: {
                Text(saveAlertMessage)
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

// MARK: - TikTok Style Scroll View

struct TikTokScrollView: View {
    @ObservedObject var imageStore: AIImageStore
    @Binding var viewMode: AIGalleryView.ViewMode
    @Binding var imageToShare: UIImage?
    @Binding var showingShareSheet: Bool
    
    @State private var currentIndex = 0
    @State private var dragAmount = CGSize.zero
    @State private var showingSaveAlert = false
    @State private var saveAlertMessage = ""
    @State private var scrollDirection: Axis.Set = .vertical
    
    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()
            
            if imageStore.images.isEmpty {
                emptyStateView
            } else {
                imageScrollView
            }
        }
        .alert("Photo Library", isPresented: $showingSaveAlert) {
            Button("OK") { }
        } message: {
            Text(saveAlertMessage)
        }
    }
    
    @ViewBuilder
    private var emptyStateView: some View {
        VStack(spacing: 20) {
            Image(systemName: "photo.on.rectangle.angled")
                .font(.system(size: 60))
                .foregroundColor(.gray)
            
            Text("No AI Images Yet")
                .font(.headline)
                .foregroundColor(.white)
            
            Text("Take AI photos with your glasses to see them here")
                .font(.caption)
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
        }
    }
    
    @ViewBuilder
    private var imageScrollView: some View {
        TabView(selection: $currentIndex) {
            ForEach(Array(imageStore.images.enumerated()), id: \.element.id) { index, aiImage in
                imageCell(index: index, aiImage: aiImage)
                    .tag(index)
            }
        }
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
        .ignoresSafeArea()
    }
    
    @ViewBuilder
    private func imageCell(index: Int, aiImage: AIImage) -> some View {
        ZStack {
            // Full screen image
            Image(uiImage: aiImage.image)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.black)
                
            // Overlay controls
            VStack {
                topBar(index: index, aiImage: aiImage)
                    .padding()
                
                Spacer()
                
                bottomActions(aiImage: aiImage)
                    .padding()
                    .background(
                        LinearGradient(
                            gradient: Gradient(colors: [Color.black.opacity(0.8), Color.clear]),
                            startPoint: .bottom,
                            endPoint: .top
                        )
                        .frame(height: 150)
                    )
            }
        }
    }
    
    @ViewBuilder
    private func topBar(index: Int, aiImage: AIImage) -> some View {
        HStack {
            Button(action: {
                viewMode = .grid
            }) {
                Image(systemName: "xmark")
                    .font(.title2)
                    .foregroundColor(.white)
                    .padding()
                    .background(Circle().fill(Color.black.opacity(0.5)))
            }
            
            Spacer()
            
            Text("\(index + 1) / \(imageStore.images.count)")
                .font(.headline)
                .foregroundColor(.white)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(Capsule().fill(Color.black.opacity(0.5)))
            
            Spacer()
            
            Button(action: {
                imageToShare = aiImage.image
                showingShareSheet = true
            }) {
                Image(systemName: "square.and.arrow.up")
                    .font(.title2)
                    .foregroundColor(.white)
                    .padding()
                    .background(Circle().fill(Color.black.opacity(0.5)))
            }
        }
    }
    
    @ViewBuilder
    private func bottomActions(aiImage: AIImage) -> some View {
        HStack(spacing: 30) {
            // Save to photos
            Button(action: {
                saveToPhotoLibrary(aiImage.image)
            }) {
                VStack(spacing: 4) {
                    Image(systemName: "square.and.arrow.down")
                        .font(.title)
                    Text("Save")
                        .font(.caption)
                }
                .foregroundColor(.white)
            }
            
            // Delete
            Button(action: {
                deleteImage(aiImage)
            }) {
                VStack(spacing: 4) {
                    Image(systemName: "trash")
                        .font(.title)
                    Text("Delete")
                        .font(.caption)
                }
                .foregroundColor(.white)
            }
            
            // Info
            VStack(spacing: 4) {
                Image(systemName: "info.circle")
                    .font(.title)
                Text(aiImage.timestamp, style: .date)
                    .font(.caption)
            }
            .foregroundColor(.white)
        }
    }
    
    private func saveToPhotoLibrary(_ image: UIImage) {
        PHPhotoLibrary.requestAuthorization { status in
            DispatchQueue.main.async {
                switch status {
                case .authorized, .limited:
                    UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil)
                    saveAlertMessage = "Image saved to photo library!"
                    showingSaveAlert = true
                case .denied, .restricted:
                    saveAlertMessage = "Photo library access denied. Please enable in Settings."
                    showingSaveAlert = true
                case .notDetermined:
                    saveAlertMessage = "Photo library access not determined."
                    showingSaveAlert = true
                @unknown default:
                    saveAlertMessage = "Unknown error occurred."
                    showingSaveAlert = true
                }
            }
        }
    }
    
    private func deleteImage(_ aiImage: AIImage) {
        imageStore.removeImage(aiImage)
        if currentIndex >= imageStore.images.count && currentIndex > 0 {
            currentIndex = imageStore.images.count - 1
        }
    }
}


#Preview {
    AIGalleryView()
}