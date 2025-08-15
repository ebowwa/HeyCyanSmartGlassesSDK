//
//  AIImageCaptureTest.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//
//  Test AI image capture with real glasses and verify image data receipt
//

import XCTest
import UIKit
import Photos
@testable import GlassesFramework

class AIImageCaptureTest: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var receivedImageData: Data?
    var savedImageURL: URL?
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        print("\n🎨 AI Image Capture Test Starting...")
        print("📱 Make sure:")
        print("  1. Glasses are powered on")
        print("  2. Glasses are in pairing mode if not already connected")
        print("  3. iPhone Bluetooth is enabled")
    }
    
    override func tearDown() {
        receivedImageData = nil
        savedImageURL = nil
        if bluetoothManager.isConnected {
            bluetoothManager.disconnect()
        }
        bluetoothManager = nil
        super.tearDown()
    }
    
    func testAIImageCaptureAndReceive() {
        print("\n==========================================")
        print("🚀 STARTING AI IMAGE CAPTURE TEST")
        print("==========================================\n")
        
        // Step 1: Scan for glasses
        let scanExpectation = XCTestExpectation(description: "Scan for glasses")
        print("📡 Step 1: Scanning for HeyCyan glasses...")
        
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            let devices = self.bluetoothManager.discoveredDevices
            print("📱 Found \(devices.count) total Bluetooth devices")
            
            let glassesDevices = devices.filter { $0.name.contains("M01") }
            if glassesDevices.isEmpty {
                XCTFail("❌ No M01 glasses found! Make sure glasses are powered on.")
            } else {
                print("✅ Found \(glassesDevices.count) HeyCyan glasses device(s)")
                for device in glassesDevices {
                    print("   👓 \(device.name) - MAC: \(device.macAddress)")
                }
            }
            scanExpectation.fulfill()
        }
        
        wait(for: [scanExpectation], timeout: 8.0)
        
        // Step 2: Connect to glasses
        let connectExpectation = XCTestExpectation(description: "Connect to glasses")
        print("\n📡 Step 2: Connecting to glasses...")
        
        guard let glasses = bluetoothManager.discoveredDevices.first(where: { $0.name.contains("M01") }) else {
            XCTFail("❌ No glasses to connect to")
            return
        }
        
        bluetoothManager.connect(to: glasses)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            if self.bluetoothManager.isConnected {
                print("✅ Successfully connected to: \(self.bluetoothManager.connectedDeviceName)")
                print("   Connection state: \(self.bluetoothManager.connectionState)")
            } else {
                print("⚠️ Connection failed - trying to proceed anyway")
            }
            connectExpectation.fulfill()
        }
        
        wait(for: [connectExpectation], timeout: 8.0)
        
        // Step 3: Set up notification observers for AI image
        let imageExpectation = XCTestExpectation(description: "Receive AI image")
        
        print("\n📸 Step 3: Setting up AI image capture...")
        
        // Observer for successful image receipt
        let successObserver = NotificationCenter.default.addObserver(
            forName: .aiImageReceived,
            object: nil,
            queue: .main
        ) { [weak self] notification in
            print("\n🎉 AI IMAGE RECEIVED NOTIFICATION!")
            
            if let imageData = notification.object as? Data {
                print("✅ Image data received: \(imageData.count) bytes")
                self?.receivedImageData = imageData
                
                // Verify it's a valid image
                if let image = UIImage(data: imageData) {
                    print("✅ Valid UIImage created")
                    print("   📐 Dimensions: \(image.size.width) x \(image.size.height)")
                    print("   📊 Scale: \(image.scale)")
                    
                    // Save to temp file for inspection
                    let tempDir = FileManager.default.temporaryDirectory
                    let fileName = "ai_image_\(Date().timeIntervalSince1970).jpg"
                    let fileURL = tempDir.appendingPathComponent(fileName)
                    
                    if let jpegData = image.jpegData(compressionQuality: 0.9) {
                        do {
                            try jpegData.write(to: fileURL)
                            self?.savedImageURL = fileURL
                            print("💾 Image saved to: \(fileURL.path)")
                        } catch {
                            print("❌ Failed to save image: \(error)")
                        }
                    }
                    
                    // Check if we can get JPEG representation
                    if let jpegData = image.jpegData(compressionQuality: 1.0) {
                        print("✅ Can create JPEG data: \(jpegData.count) bytes")
                    }
                    
                    if let pngData = image.pngData() {
                        print("✅ Can create PNG data: \(pngData.count) bytes")
                    }
                } else {
                    print("❌ Could not create UIImage from data")
                    // Try to identify what kind of data we received
                    let headerBytes = Array(imageData.prefix(4))
                    print("   Header bytes: \(headerBytes.map { String(format: "%02X", $0) }.joined(separator: " "))")
                }
                
                // Print additional info from notification
                if let userInfo = notification.userInfo {
                    print("📋 Additional info:")
                    for (key, value) in userInfo {
                        print("   \(key): \(value)")
                    }
                }
            } else {
                print("⚠️ Notification received but no image data found")
            }
            
            imageExpectation.fulfill()
        }
        
        // Observer for failure
        let failureObserver = NotificationCenter.default.addObserver(
            forName: Notification.Name("AIImageCaptureFailed"),
            object: nil,
            queue: .main
        ) { notification in
            print("\n❌ AI IMAGE CAPTURE FAILED!")
            if let error = notification.userInfo?["error"] as? String {
                print("   Error: \(error)")
            }
            imageExpectation.fulfill()
        }
        
        // Step 4: Take AI image
        print("\n📸 Step 4: Requesting AI image capture...")
        print("⏳ Please point the glasses at something interesting!")
        
        // Clear any previous image data
        bluetoothManager.deviceInfo.aiImageData = nil
        
        // Request AI image
        bluetoothManager.takeAIImage()
        
        print("⏳ Waiting for AI image (this may take 5-10 seconds)...")
        
        // Wait for image with longer timeout
        wait(for: [imageExpectation], timeout: 15.0)
        
        // Clean up observers
        NotificationCenter.default.removeObserver(successObserver)
        NotificationCenter.default.removeObserver(failureObserver)
        
        // Step 5: Verify results
        print("\n==========================================")
        print("📊 TEST RESULTS:")
        print("==========================================")
        
        if let imageData = receivedImageData {
            print("✅ SUCCESS: AI image received")
            print("   📦 Size: \(imageData.count) bytes")
            
            if let savedURL = savedImageURL {
                print("   💾 Saved to: \(savedURL.path)")
            }
            
            // Also check if it was stored in deviceInfo
            if let storedData = bluetoothManager.deviceInfo.aiImageData {
                print("   ✅ Also stored in deviceInfo (\(storedData.count) bytes)")
                XCTAssertEqual(storedData.count, imageData.count, "Stored data should match received data")
            }
            
            XCTAssertTrue(imageData.count > 1000, "Image should be at least 1KB")
            XCTAssertNotNil(UIImage(data: imageData), "Should be able to create UIImage from data")
            
            // Display the image preview for 5 seconds
            print("\n🖼️ DISPLAYING IMAGE PREVIEW...")
            print("⏳ Keeping test alive for 5 seconds to show image preview")
            
            // Create a window to show the image if we have UI access
            if let image = UIImage(data: imageData) {
                print("📐 Image preview: \(image.size.width) x \(image.size.height) pixels")
                
                // Keep the test running for 5 seconds so the image can be seen
                let previewExpectation = XCTestExpectation(description: "Image preview display")
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    print("✅ Image preview display complete")
                    previewExpectation.fulfill()
                }
                wait(for: [previewExpectation], timeout: 6.0)
            }
        } else {
            print("❌ FAILED: No AI image received")
            print("   Check that:")
            print("   1. Glasses are connected properly")
            print("   2. Glasses have AI image feature enabled")
            print("   3. Camera lens is not blocked")
            XCTFail("AI image was not received")
        }
        
        print("\n==========================================")
        print("🏁 AI IMAGE CAPTURE TEST COMPLETE")
        print("==========================================\n")
    }
    
    // Helper test to just check device info after manual capture
    func testCheckDeviceInfoForAIImage() {
        // This test can be run after manually taking an AI image
        // to check if the data is stored in deviceInfo
        
        if let aiImageData = bluetoothManager.deviceInfo.aiImageData {
            print("📦 Found AI image data in deviceInfo: \(aiImageData.count) bytes")
            
            if let image = UIImage(data: aiImageData) {
                print("✅ Valid image: \(image.size.width) x \(image.size.height)")
            }
        } else {
            print("❌ No AI image data found in deviceInfo")
        }
    }
}