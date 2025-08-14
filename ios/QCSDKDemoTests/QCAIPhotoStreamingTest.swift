//
//  QCAIPhotoStreamingTest.swift
//  QCSDKDemoTests
//
//  Test AI Photo streaming capabilities
//

import Testing
import Foundation
import CoreBluetooth
import UIKit

/// Test using AI Photo mode for streaming-like functionality
struct QCAIPhotoStreamingTest {
    
    /// Test rapid AI photo capture for pseudo-streaming
    @Test func testAIPhotoStreaming() async throws {
        let manager = QCCentralManager.shared()
        
        // Verify connection
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🎬 AI Photo Streaming Test Starting...\n")
        
        // Set up delegate to receive AI images
        class StreamingDelegate: NSObject, QCSDKManagerDelegate {
            var receivedImages: [Data] = []
            var imageTimestamps: [Date] = []
            var latestImageExpectation: StreamExpectation?
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                receivedImages.append(imageData)
                imageTimestamps.append(Date())
                
                let imageCount = receivedImages.count
                print("📸 Frame \(imageCount) received - Size: \(imageData.count) bytes")
                
                // Calculate FPS if we have multiple images
                if imageTimestamps.count > 1 {
                    let timeDiff = imageTimestamps.last!.timeIntervalSince(imageTimestamps[imageTimestamps.count - 2])
                    let fps = 1.0 / timeDiff
                    print("   ⏱️ Time since last frame: \(String(format: "%.2f", timeDiff))s")
                    print("   📊 Effective FPS: \(String(format: "%.2f", fps))")
                }
                
                // Notify waiting expectation
                latestImageExpectation?.fulfill()
            }
        }
        
        let streamDelegate = StreamingDelegate()
        let sdkManager = QCSDKManager()
        sdkManager.delegate = streamDelegate
        
        // Step 1: Set device to AI Photo mode
        print("1️⃣ Setting device to AI Photo mode...")
        let modeExp = StreamExpectation()
        var modeSet = false
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("   ✅ AI Photo mode activated")
            modeSet = true
            modeExp.fulfill()
        }, fail: { errorCode in
            print("   ❌ Failed to set AI Photo mode: \(errorCode)")
            modeExp.fulfill()
        })
        
        await modeExp.wait(timeout: 5)
        
        guard modeSet else {
            print("❌ Could not activate AI Photo mode")
            return
        }
        
        // Wait a moment for mode to stabilize
        try await Task.sleep(nanoseconds: 2_000_000_000)
        
        // Step 2: Try continuous capture
        print("\n2️⃣ Testing continuous AI photo capture...")
        print("   🎯 Attempting to capture multiple frames...")
        
        // Method 1: Try rapid mode switching to trigger captures
        for i in 1...5 {
            print("\n   📸 Triggering capture \(i)...")
            
            let captureExp = StreamExpectation()
            streamDelegate.latestImageExpectation = captureExp
            
            // Try different methods to trigger capture
            if i % 2 == 0 {
                // Method A: Toggle between modes
                QCSDKCmdCreator.setDeviceMode(.photo, success: {
                    // Immediately switch back to AI Photo
                    QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
                        print("      Mode toggled")
                    }, fail: { _ in })
                }, fail: { _ in })
            } else {
                // Method B: Re-set AI Photo mode
                QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
                    print("      AI mode re-set")
                }, fail: { _ in })
            }
            
            // Wait for image with timeout
            await captureExp.wait(timeout: 5)
            
            // Small delay between captures
            try await Task.sleep(nanoseconds: 500_000_000) // 0.5 seconds
        }
        
        // Step 3: Try WiFi-based streaming
        print("\n3️⃣ Testing WiFi-based AI photo streaming...")
        
        let wifiExp = StreamExpectation()
        var wifiOpened = false
        
        QCSDKCmdCreator.openWifi(with: .aiPhoto, success: { ssid, password in
            print("   ✅ WiFi opened for AI Photo")
            print("      SSID: \(ssid)")
            print("      Password: \(password)")
            wifiOpened = true
            wifiExp.fulfill()
        }, fail: { errorCode in
            print("   ❌ WiFi failed: \(errorCode)")
            wifiExp.fulfill()
        })
        
        await wifiExp.wait(timeout: 10)
        
        if wifiOpened {
            // Wait for potential WiFi-based images
            print("   ⏳ Waiting for WiFi-based AI images...")
            try await Task.sleep(nanoseconds: 5_000_000_000)
        }
        
        // Step 4: Analyze results
        print("\n📊 Streaming Test Results:")
        print("   Total images received: \(streamDelegate.receivedImages.count)")
        
        if streamDelegate.receivedImages.count > 0 {
            let totalSize = streamDelegate.receivedImages.reduce(0) { $0 + $1.count }
            let avgSize = totalSize / streamDelegate.receivedImages.count
            print("   Average image size: \(avgSize) bytes")
            
            if streamDelegate.imageTimestamps.count > 1 {
                let totalTime = streamDelegate.imageTimestamps.last!.timeIntervalSince(streamDelegate.imageTimestamps.first!)
                let avgFPS = Double(streamDelegate.receivedImages.count - 1) / totalTime
                print("   Average FPS: \(String(format: "%.2f", avgFPS))")
                print("   Total capture time: \(String(format: "%.2f", totalTime))s")
            }
            
            // Check image formats
            var jpegCount = 0
            var pngCount = 0
            
            for imageData in streamDelegate.receivedImages {
                let header = [UInt8](imageData.prefix(4))
                if header.count >= 3 && header[0] == 0xFF && header[1] == 0xD8 {
                    jpegCount += 1
                } else if header.count >= 4 && header[0] == 0x89 && header[1] == 0x50 {
                    pngCount += 1
                }
            }
            
            print("   Image formats: JPEG: \(jpegCount), PNG: \(pngCount)")
        }
        
        print("\n✅ Streaming test complete")
    }
    
    /// Test burst mode AI photo capture
    @Test func testAIPhotoBurstMode() async throws {
        let manager = QCCentralManager.shared()
        
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n📸 AI Photo Burst Mode Test...\n")
        
        class BurstDelegate: NSObject, QCSDKManagerDelegate {
            var burstImages: [Data] = []
            var burstStartTime: Date?
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                if burstStartTime == nil {
                    burstStartTime = Date()
                }
                burstImages.append(imageData)
                
                let elapsed = Date().timeIntervalSince(burstStartTime!)
                print("📸 Burst image \(burstImages.count) - \(String(format: "%.2f", elapsed))s")
            }
        }
        
        let burstDelegate = BurstDelegate()
        QCSDKManager().delegate = burstDelegate
        
        // Activate AI Photo mode
        print("Activating AI Photo burst mode...")
        let modeExp = StreamExpectation()
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("✅ AI Photo mode active")
            modeExp.fulfill()
        }, fail: { errorCode in
            print("❌ Mode activation failed: \(errorCode)")
            modeExp.fulfill()
        })
        
        await modeExp.wait(timeout: 5)
        
        // Try rapid command sending for burst
        print("\nSending burst commands...")
        
        for i in 1...10 {
            print("Burst trigger \(i)")
            
            // Try multiple trigger methods rapidly
            QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: { }, fail: { _ in })
            
            // Very short delay
            try await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
        }
        
        // Wait for images to arrive
        print("\nWaiting for burst images...")
        try await Task.sleep(nanoseconds: 5_000_000_000)
        
        print("\n📊 Burst Results:")
        print("   Images captured: \(burstDelegate.burstImages.count)")
        
        if let startTime = burstDelegate.burstStartTime, burstDelegate.burstImages.count > 0 {
            let duration = Date().timeIntervalSince(startTime)
            let burstRate = Double(burstDelegate.burstImages.count) / duration
            print("   Burst rate: \(String(format: "%.2f", burstRate)) images/second")
        }
    }
    
    /// Test video-like streaming using WiFi
    @Test func testWiFiVideoStreaming() async throws {
        let manager = QCCentralManager.shared()
        
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🎥 Testing WiFi Video Streaming...\n")
        
        // First try video mode
        print("1️⃣ Testing video mode...")
        let videoExp = StreamExpectation()
        var videoModeSet = false
        
        QCSDKCmdCreator.setDeviceMode(.video, success: {
            print("   ✅ Video mode activated")
            videoModeSet = true
            videoExp.fulfill()
        }, fail: { errorCode in
            print("   ❌ Video mode failed: \(errorCode)")
            videoExp.fulfill()
        })
        
        await videoExp.wait(timeout: 5)
        
        if videoModeSet {
            // Open WiFi for video streaming
            print("\n2️⃣ Opening WiFi for video streaming...")
            let wifiExp = StreamExpectation()
            
            QCSDKCmdCreator.openWifi(with: .video, success: { ssid, password in
                print("   ✅ Video WiFi opened!")
                print("      SSID: \(ssid)")
                print("      Password: \(password)")
                wifiExp.fulfill()
                
                // Get IP for potential streaming endpoint
                QCSDKCmdCreator.getDeviceWifiIPSuccess({ ip in
                    print("   🌐 Streaming IP: \(ip)")
                    print("   📡 Potential stream URL: http://\(ip):8080/stream")
                }, failed: {
                    print("   ⚠️ Could not get IP")
                })
            }, fail: { errorCode in
                print("   ❌ Video WiFi failed: \(errorCode)")
                wifiExp.fulfill()
            })
            
            await wifiExp.wait(timeout: 10)
        }
        
        // Try other streaming-related modes
        print("\n3️⃣ Testing other streaming modes...")
        
        let modes: [(QCOperatorDeviceMode, String)] = [
            (.transfer, "Transfer (might support live preview)"),
            (.photo, "Photo (continuous capture)"),
            (.aiPhoto, "AI Photo (smart streaming)")
        ]
        
        for (mode, description) in modes {
            print("\n   Testing: \(description)")
            
            let exp = StreamExpectation()
            
            QCSDKCmdCreator.openWifi(with: mode, success: { ssid, password in
                if !ssid.isEmpty {
                    print("      ✅ \(description): \(ssid)")
                }
                exp.fulfill()
            }, fail: { errorCode in
                print("      ❌ \(description) error: \(errorCode)")
                exp.fulfill()
            })
            
            await exp.wait(timeout: 5)
            try await Task.sleep(nanoseconds: 1_000_000_000)
        }
        
        print("\n✅ WiFi streaming test complete")
        print("   💡 Check if any modes enable live streaming")
    }
}

// MARK: - Support Classes

/// Simple expectation for async coordination
class StreamExpectation {
    private var done = false
    
    func fulfill() {
        done = true
    }
    
    func wait(timeout: TimeInterval) async {
        let end = Date().addingTimeInterval(timeout)
        while Date() < end && !done {
            try? await Task.sleep(nanoseconds: 100_000_000)
        }
    }
}