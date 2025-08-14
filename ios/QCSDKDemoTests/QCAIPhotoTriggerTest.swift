//
//  QCAIPhotoTriggerTest.swift
//  QCSDKDemoTests
//
//  Test different methods to trigger AI photo capture
//

import Testing
import Foundation
import CoreBluetooth

/// Test different trigger methods for AI photo capture
struct QCAIPhotoTriggerTest {
    
    /// Test various methods to trigger actual photo capture
    @Test func testAIPhotoTriggerMethods() async throws {
        let manager = QCCentralManager.shared()
        
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🔍 Testing AI Photo Trigger Methods\n")
        
        // Set up delegate to catch any images
        class TriggerDelegate: NSObject, QCSDKManagerDelegate {
            var imageReceived = false
            var lastImageTime: Date?
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                imageReceived = true
                lastImageTime = Date()
                print("   🎉 AI IMAGE RECEIVED! Size: \(imageData.count) bytes")
            }
        }
        
        let delegate = TriggerDelegate()
        QCSDKManager().delegate = delegate
        
        // Method 1: Set AI Photo mode and wait
        print("1️⃣ Method 1: Set AI Photo mode and wait for auto-trigger")
        print("   Setting AI Photo mode...")
        
        let exp1 = TriggerExpectation()
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("   ✅ AI Photo mode set")
            exp1.fulfill()
        }, fail: { error in
            print("   ❌ Failed: \(error)")
            exp1.fulfill()
        })
        
        await exp1.wait(timeout: 3)
        
        print("   ⏳ Waiting 10 seconds for auto-capture...")
        print("   👆 TRY: Press the glasses button now!")
        try await Task.sleep(nanoseconds: 10_000_000_000)
        
        if delegate.imageReceived {
            print("   ✅ Method 1 worked! Image received")
        } else {
            print("   ❌ No image received with Method 1")
        }
        
        // Method 2: Toggle between Photo and AI Photo rapidly
        print("\n2️⃣ Method 2: Rapid mode switching")
        delegate.imageReceived = false
        
        for i in 1...5 {
            print("   Attempt \(i)/5...")
            
            // Switch to regular photo
            QCSDKCmdCreator.setDeviceMode(.photo, success: { }, fail: { _ in })
            try await Task.sleep(nanoseconds: 500_000_000)
            
            // Switch to AI photo
            QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: { }, fail: { _ in })
            try await Task.sleep(nanoseconds: 500_000_000)
            
            if delegate.imageReceived {
                print("   ✅ Method 2 worked on attempt \(i)!")
                break
            }
        }
        
        if !delegate.imageReceived {
            print("   ❌ No image received with Method 2")
        }
        
        // Method 3: Open WiFi in AI Photo mode
        print("\n3️⃣ Method 3: WiFi trigger in AI Photo mode")
        delegate.imageReceived = false
        
        let exp3 = TriggerExpectation()
        QCSDKCmdCreator.openWifi(with: .aiPhoto, success: { ssid, password in
            print("   ✅ WiFi opened: \(ssid)")
            exp3.fulfill()
        }, fail: { error in
            print("   ❌ WiFi failed: \(error)")
            exp3.fulfill()
        })
        
        await exp3.wait(timeout: 5)
        
        print("   ⏳ Waiting 5 seconds for capture...")
        try await Task.sleep(nanoseconds: 5_000_000_000)
        
        if delegate.imageReceived {
            print("   ✅ Method 3 worked! Image received")
        } else {
            print("   ❌ No image received with Method 3")
        }
        
        // Method 4: Set video mode briefly then AI Photo
        print("\n4️⃣ Method 4: Video mode then AI Photo")
        delegate.imageReceived = false
        
        // Set video mode
        QCSDKCmdCreator.setDeviceMode(.video, success: {
            print("   Video mode set")
        }, fail: { _ in })
        
        try await Task.sleep(nanoseconds: 2_000_000_000)
        
        // Switch to AI Photo
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("   Switched to AI Photo")
        }, fail: { _ in })
        
        print("   ⏳ Waiting 5 seconds...")
        try await Task.sleep(nanoseconds: 5_000_000_000)
        
        if delegate.imageReceived {
            print("   ✅ Method 4 worked!")
        } else {
            print("   ❌ No image received with Method 4")
        }
        
        // Summary
        print("\n📊 RESULTS:")
        print("────────────")
        if delegate.lastImageTime != nil {
            print("✅ AI Photos CAN be triggered!")
            print("   Last image received at: \(delegate.lastImageTime!)")
        } else {
            print("⚠️ No AI photos were captured automatically")
            print("\n💡 IMPORTANT:")
            print("   • AI Photo mode appears to require MANUAL trigger")
            print("   • Press the BUTTON on the glasses after setting AI Photo mode")
            print("   • Or use touch gestures if configured")
            print("\n📱 To capture AI photos:")
            print("   1. Set device to AI Photo mode")
            print("   2. Press the physical button on glasses")
            print("   3. Image will be processed and sent to app")
        }
    }
    
    /// Test if regular photo mode is faster
    @Test func testRegularPhotoSpeed() async throws {
        let manager = QCCentralManager.shared()
        
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n📷 Testing Regular Photo Mode Speed\n")
        
        // Set regular photo mode
        print("Setting regular Photo mode...")
        let exp = TriggerExpectation()
        
        QCSDKCmdCreator.setDeviceMode(.photo, success: {
            print("✅ Photo mode activated")
            exp.fulfill()
        }, fail: { error in
            print("❌ Failed: \(error)")
            exp.fulfill()
        })
        
        await exp.wait(timeout: 3)
        
        print("\n👆 MANUAL ACTION REQUIRED:")
        print("   Press the glasses button 5 times to take photos")
        print("   Observing for 30 seconds...\n")
        
        for i in 1...30 {
            print("   \(i)/30 seconds...")
            try await Task.sleep(nanoseconds: 1_000_000_000)
        }
        
        print("\n📊 Regular Photo Mode:")
        print("   • Captures happen immediately on button press")
        print("   • No AI processing delay")
        print("   • Images stored on device (check media count)")
        
        // Check media count
        let mediaExp = TriggerExpectation()
        
        QCSDKCmdCreator.getDeviceMedia({ photo, video, audio, size in
            print("\n📁 Media on device:")
            print("   Photos: \(photo)")
            print("   Videos: \(video)")
            print("   Audio: \(audio)")
            print("   Total size: \(size) bytes")
            mediaExp.fulfill()
        }, fail: {
            print("   Could not get media info")
            mediaExp.fulfill()
        })
        
        await mediaExp.wait(timeout: 5)
    }
}

// Helper class
class TriggerExpectation {
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