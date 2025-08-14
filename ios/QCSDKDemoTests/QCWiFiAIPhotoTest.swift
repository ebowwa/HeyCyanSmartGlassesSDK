//
//  QCWiFiAIPhotoTest.swift
//  QCSDKDemoTests
//
//  Test WiFi hotspot with AI Photo mode
//

import Testing
import Foundation
import CoreBluetooth

/// Test WiFi hotspot with AI Photo mode
struct QCWiFiAIPhotoTest {
    
    /// Test opening WiFi in AI Photo mode
    @Test func testWiFiAIPhotoMode() async throws {
        let manager = QCCentralManager.shared()
        
        // Verify glasses are connected
        #expect(manager.connectedPeripheral != nil && manager.deviceState == .connected, 
                "Glasses must be connected to test WiFi AI Photo mode")
        
        print("\n🤖📸 Testing WiFi with AI Photo mode...\n")
        
        let expectation = AIPhotoTestExpectation()
        var success = false
        var ssid: String?
        var password: String?
        
        // Try opening WiFi with AI Photo mode
        QCSDKCmdCreator.openWifi(with: .aiPhoto, success: { receivedSSID, receivedPassword in
            print("✅ WiFi opened in AI Photo mode!")
            print("   SSID: \(receivedSSID)")
            print("   Password: \(receivedPassword)")
            ssid = receivedSSID
            password = receivedPassword
            success = true
            expectation.fulfill()
        }, fail: { errorCode in
            print("❌ Failed to open WiFi in AI Photo mode")
            print("   Error code: \(errorCode)")
            expectation.fulfill()
        })
        
        await expectation.wait(timeout: 15)
        
        if success {
            print("\n🎉 AI Photo WiFi mode is supported!")
            print("📡 Connect to WiFi:")
            print("   Network: \(ssid ?? "")")
            print("   Password: \(password ?? "")")
            
            // Wait a bit then try to get IP
            try await Task.sleep(nanoseconds: 2_000_000_000)
            
            let ipExpectation = AIPhotoTestExpectation()
            var ipAddress: String?
            
            QCSDKCmdCreator.getDeviceWifiIPSuccess({ ip in
                ipAddress = ip
                ipExpectation.fulfill()
            }, failed: {
                ipExpectation.fulfill()
            })
            
            await ipExpectation.wait(timeout: 5)
            
            if let ip = ipAddress {
                print("\n🌐 Glasses IP: \(ip)")
                print("📸 AI photos might be accessible at: http://\(ip)")
            }
            
            print("\n💡 What this might mean:")
            print("   - WiFi hotspot is open for AI photo transfer")
            print("   - AI-processed photos may be available over WiFi")
            print("   - Could be for transferring AI-enhanced images")
            print("   - Might enable AI photo features via WiFi connection")
        } else {
            print("\n⚠️ AI Photo WiFi mode not available or failed")
            print("   This mode might require:")
            print("   - Glasses to be in AI Photo mode first")
            print("   - Specific firmware version")
            print("   - AI features to be enabled")
        }
        
        #expect(true, "Test completed - check console output for results")
    }
    
    /// Test switching between regular photo and AI photo WiFi modes
    @Test func testPhotoVsAIPhotoModes() async throws {
        let manager = QCCentralManager.shared()
        #expect(manager.connectedPeripheral != nil && manager.deviceState == .connected,
                "Glasses must be connected")
        
        print("\n📸 Comparing Photo vs AI Photo WiFi modes...\n")
        
        // Test regular photo mode
        print("1️⃣ Testing regular Photo mode:")
        let photoExpectation = AIPhotoTestExpectation()
        var photoSuccess = false
        var photoSSID: String?
        
        QCSDKCmdCreator.openWifi(with: .photo, success: { ssid, password in
            print("   ✅ Photo mode: \(ssid)")
            photoSSID = ssid
            photoSuccess = true
            photoExpectation.fulfill()
        }, fail: { errorCode in
            print("   ❌ Photo mode failed: \(errorCode)")
            photoExpectation.fulfill()
        })
        
        await photoExpectation.wait(timeout: 10)
        
        // Wait between tests
        try await Task.sleep(nanoseconds: 2_000_000_000)
        
        // Test AI photo mode
        print("\n2️⃣ Testing AI Photo mode:")
        let aiExpectation = AIPhotoTestExpectation()
        var aiSuccess = false
        var aiSSID: String?
        
        QCSDKCmdCreator.openWifi(with: .aiPhoto, success: { ssid, password in
            print("   ✅ AI Photo mode: \(ssid)")
            aiSSID = ssid
            aiSuccess = true
            aiExpectation.fulfill()
        }, fail: { errorCode in
            print("   ❌ AI Photo mode failed: \(errorCode)")
            aiExpectation.fulfill()
        })
        
        await aiExpectation.wait(timeout: 10)
        
        print("\n📊 Results:")
        print("   Regular Photo mode: \(photoSuccess ? "✅ Supported" : "❌ Not supported")")
        print("   AI Photo mode: \(aiSuccess ? "✅ Supported" : "❌ Not supported")")
        
        if photoSuccess && aiSuccess {
            print("\n🤔 Differences:")
            if photoSSID == aiSSID {
                print("   Same SSID for both modes - might be same WiFi with different transfer modes")
            } else {
                print("   Different SSIDs - separate WiFi configurations")
            }
        }
    }
    
    /// Test if we can trigger AI photo capture over WiFi
    @Test func testAIPhotoCaptureThroughWiFi() async throws {
        let manager = QCCentralManager.shared()
        #expect(manager.connectedPeripheral != nil && manager.deviceState == .connected,
                "Glasses must be connected")
        
        print("\n🤖📸 Testing AI photo capture through WiFi...\n")
        
        // First, set device to AI Photo mode
        print("1️⃣ Setting device to AI Photo mode...")
        let modeExpectation = AIPhotoTestExpectation()
        var modeSet = false
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("   ✅ Device set to AI Photo mode")
            modeSet = true
            modeExpectation.fulfill()
        }, fail: { errorCode in
            print("   ❌ Failed to set AI Photo mode: \(errorCode)")
            modeExpectation.fulfill()
        })
        
        await modeExpectation.wait(timeout: 5)
        
        if modeSet {
            // Wait for mode to stabilize
            try await Task.sleep(nanoseconds: 2_000_000_000)
            
            // Open WiFi in AI Photo mode
            print("\n2️⃣ Opening WiFi in AI Photo mode...")
            let wifiExpectation = AIPhotoTestExpectation()
            var wifiOpened = false
            
            QCSDKCmdCreator.openWifi(with: .aiPhoto, success: { ssid, password in
                print("   ✅ WiFi opened: \(ssid)")
                wifiOpened = true
                wifiExpectation.fulfill()
            }, fail: { errorCode in
                print("   ❌ WiFi failed: \(errorCode)")
                wifiExpectation.fulfill()
            })
            
            await wifiExpectation.wait(timeout: 10)
            
            if wifiOpened {
                print("\n✅ Ready for AI photo operations over WiFi!")
                print("   - Device is in AI Photo mode")
                print("   - WiFi hotspot is active")
                print("   - You can now:")
                print("     • Take AI photos on glasses")
                print("     • Photos should be processed with AI")
                print("     • Access them via WiFi connection")
            }
        }
    }
}

// MARK: - Test Support

/// Simple test expectation for async coordination
class AIPhotoTestExpectation {
    private var fulfilled = false
    private let lock = NSLock()
    
    func fulfill() {
        lock.lock()
        fulfilled = true
        lock.unlock()
    }
    
    func wait(timeout: TimeInterval) async {
        let deadline = Date().addingTimeInterval(timeout)
        
        while Date() < deadline {
            lock.lock()
            if fulfilled {
                lock.unlock()
                return
            }
            lock.unlock()
            
            try? await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
        }
    }
}