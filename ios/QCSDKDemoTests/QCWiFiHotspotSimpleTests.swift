//
//  QCWiFiHotspotSimpleTests.swift
//  QCSDKDemoTests
//
//  Simple tests for HeyCyan glasses WiFi hotspot without iOS connection
//

import Testing
import Foundation
import CoreBluetooth

/// Simple WiFi hotspot tests that don't require WiFiAutoConnect
///
/// PREREQUISITES:
/// - HeyCyan glasses (M01_9FD8) must be connected via Bluetooth
///
/// TEST FLOW:
/// 1. Verify glasses are connected via Bluetooth
/// 2. Open WiFi hotspot on glasses
/// 3. Get SSID and password
/// 4. Get IP address of glasses
struct QCWiFiHotspotSimpleTests {
    
    // MARK: - Helper Functions
    
    /// Verify glasses are connected before WiFi tests
    private func verifyGlassesConnected() -> Bool {
        let manager = QCCentralManager.shared()
        return manager.connectedPeripheral != nil && manager.deviceState == .connected
    }
    
    // MARK: - WiFi Hotspot Tests
    
    /// Test opening WiFi hotspot on glasses and getting credentials
    @Test func testOpenWiFiHotspotSimple() async throws {
        // Verify glasses are connected
        #expect(verifyGlassesConnected(), "Glasses must be connected to test WiFi hotspot")
        
        let expectation = WiFiSimpleTestExpectation()
        var ssid: String?
        var password: String?
        
        print("\n📡 Opening WiFi hotspot on glasses...")
        
        // Open WiFi hotspot with Transfer mode (for media transfer)
        QCSDKCmdCreator.openWifi(with: .transfer, success: { receivedSSID, receivedPassword in
            print("✅ WiFi Hotspot opened successfully!")
            print("   SSID: \(receivedSSID)")
            print("   Password: \(receivedPassword)")
            
            ssid = receivedSSID
            password = receivedPassword
            expectation.fulfill()
        }, fail: { errorCode in
            print("❌ Failed to open WiFi hotspot: \(errorCode)")
            expectation.fulfill()
        })
        
        // Wait for hotspot to open (max 15 seconds)
        await expectation.wait(timeout: 15)
        
        #expect(ssid != nil, "Should receive WiFi SSID")
        #expect(password != nil, "Should receive WiFi password")
        
        if let wifiSSID = ssid, let wifiPassword = password {
            #expect(!wifiSSID.isEmpty, "SSID should not be empty")
            #expect(!wifiPassword.isEmpty, "Password should not be empty")
            
            // Typically SSID format: "M01_XXXX" or similar
            #expect(wifiSSID.contains("M01") || wifiSSID.contains("9FD8"), 
                    "SSID should contain device identifier")
            
            print("\n📋 WiFi Credentials:")
            print("   To connect manually:")
            print("   1. Go to Settings > WiFi")
            print("   2. Select network: \(wifiSSID)")
            print("   3. Enter password: \(wifiPassword)")
        }
    }
    
    /// Test getting WiFi IP address after opening hotspot
    @Test func testGetWiFiIPAfterOpen() async throws {
        // Verify glasses are connected
        #expect(verifyGlassesConnected(), "Glasses must be connected to test WiFi")
        
        // First open WiFi hotspot
        let openExpectation = WiFiSimpleTestExpectation()
        var hotspotOpened = false
        
        print("\n📡 Opening WiFi hotspot...")
        
        QCSDKCmdCreator.openWifi(with: .transfer, success: { ssid, password in
            print("✅ Hotspot opened: \(ssid)")
            hotspotOpened = true
            openExpectation.fulfill()
        }, fail: { errorCode in
            print("❌ Failed to open hotspot: \(errorCode)")
            openExpectation.fulfill()
        })
        
        await openExpectation.wait(timeout: 15)
        #expect(hotspotOpened, "WiFi hotspot should be opened first")
        
        // Wait a moment for WiFi to stabilize
        print("⏳ Waiting for WiFi to stabilize...")
        try await Task.sleep(nanoseconds: 2_000_000_000) // 2 seconds
        
        // Get IP address
        let ipExpectation = WiFiSimpleTestExpectation()
        var receivedIP: String?
        
        print("🔍 Getting WiFi IP address...")
        
        QCSDKCmdCreator.getDeviceWifiIPSuccess({ ipAddress in
            if let ip = ipAddress {
                print("✅ WiFi IP Address: \(ip)")
                receivedIP = ip
            } else {
                print("⚠️ No IP address received (may need to connect to WiFi first)")
            }
            ipExpectation.fulfill()
        }, failed: {
            print("❌ Failed to get WiFi IP address")
            ipExpectation.fulfill()
        })
        
        await ipExpectation.wait(timeout: 5)
        
        if let ip = receivedIP {
            #expect(!ip.isEmpty, "IP address should not be empty")
            
            // Validate IP format (basic check)
            let ipComponents = ip.split(separator: ".")
            #expect(ipComponents.count == 4, "IP should have 4 octets")
            
            // Typically glasses use 192.168.x.x range
            #expect(ip.hasPrefix("192.168") || ip.hasPrefix("10."), 
                    "IP should be in private network range")
            
            print("\n🌐 You can access glasses at: http://\(ip)")
        } else {
            print("\n⚠️ Note: IP address may only be available after connecting to the glasses WiFi")
        }
    }
    
    /// Test different WiFi modes that glasses support
    @Test func testDifferentWiFiModes() async throws {
        // Verify glasses are connected
        #expect(verifyGlassesConnected(), "Glasses must be connected to test WiFi modes")
        
        // Test available modes: transfer, OTA, photo, AI photo
        let modes: [(QCOperatorDeviceMode, String)] = [
            (.transfer, "Transfer (Media)"),
            (.OTA, "OTA (Firmware Update)"),
            (.photo, "Photo"),
            (.aiPhoto, "AI Photo")
        ]
        
        print("\n🔄 Testing different WiFi modes...\n")
        
        for (mode, description) in modes {
            print("Testing mode: \(description) (raw: \(mode.rawValue))")
            
            let expectation = WiFiSimpleTestExpectation()
            var success = false
            
            QCSDKCmdCreator.openWifi(with: mode, success: { ssid, password in
                print("   ✅ \(description) mode opened")
                print("      SSID: \(ssid)")
                print("      Pass: \(password)")
                success = true
                expectation.fulfill()
            }, fail: { errorCode in
                print("   ❌ \(description) mode failed: \(errorCode)")
                expectation.fulfill()
            })
            
            await expectation.wait(timeout: 10)
            
            if success {
                print("   ✓ \(description) mode is supported\n")
            } else {
                print("   ✗ \(description) mode not available\n")
            }
            
            // Brief pause between mode tests
            try await Task.sleep(nanoseconds: 1_000_000_000) // 1 second
        }
    }
    
    /// Test complete WiFi information flow
    @Test func testCompleteWiFiInfo() async throws {
        print("\n🚀 Starting complete WiFi information test...\n")
        
        // Step 1: Verify Bluetooth connection
        #expect(verifyGlassesConnected(), "Glasses must be connected")
        print("✅ Step 1: Glasses connected via Bluetooth")
        
        // Step 2: Open WiFi hotspot
        let openExpectation = WiFiSimpleTestExpectation()
        var ssid: String?
        var password: String?
        
        QCSDKCmdCreator.openWifi(with: .transfer, success: { receivedSSID, receivedPassword in
            ssid = receivedSSID
            password = receivedPassword
            openExpectation.fulfill()
        }, fail: { errorCode in
            print("Failed with error: \(errorCode)")
            openExpectation.fulfill()
        })
        
        await openExpectation.wait(timeout: 15)
        
        guard let wifiSSID = ssid, let wifiPassword = password else {
            #expect(false, "Failed to open WiFi hotspot")
            return
        }
        
        print("✅ Step 2: WiFi hotspot opened")
        print("   SSID: \(wifiSSID)")
        print("   Password: \(wifiPassword)")
        
        // Step 3: Wait and get IP
        print("\n⏳ Step 3: Waiting for WiFi to stabilize...")
        try await Task.sleep(nanoseconds: 3_000_000_000) // 3 seconds
        
        let ipExpectation = WiFiSimpleTestExpectation()
        var ipAddress: String?
        
        QCSDKCmdCreator.getDeviceWifiIPSuccess({ ip in
            ipAddress = ip
            ipExpectation.fulfill()
        }, failed: {
            ipExpectation.fulfill()
        })
        
        await ipExpectation.wait(timeout: 5)
        
        if let ip = ipAddress {
            print("✅ Step 4: Got IP address: \(ip)")
        } else {
            print("⚠️ Step 4: IP address not available (connect to WiFi first)")
        }
        
        // Summary
        print("\n🎉 Complete WiFi information gathered!")
        print("\n📊 Summary:")
        print("   - SSID: \(wifiSSID)")
        print("   - Password: \(wifiPassword)")
        if let ip = ipAddress {
            print("   - IP: \(ip)")
            print("   - URL: http://\(ip)")
        }
        print("\n📱 To connect:")
        print("   1. Go to Settings > WiFi")
        print("   2. Select: \(wifiSSID)")
        print("   3. Password: \(wifiPassword)")
        if ipAddress != nil {
            print("   4. Access glasses via browser or media app")
        }
    }
}

// MARK: - Test Support

/// Simple test expectation for async coordination
class WiFiSimpleTestExpectation {
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