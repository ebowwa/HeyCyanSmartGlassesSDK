//
//  TestWiFiModesSequence.swift
//  QCSDKDemoTests
//
//  Test WiFi modes with proper sequencing
//

import Testing
import Foundation

struct TestWiFiModesSequence {
    
    @Test func testWiFiModesWithCleanup() async throws {
        let manager = QCCentralManager.shared()
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🔍 Testing WiFi modes with proper cleanup...\n")
        
        // First, let's try to close any existing WiFi
        print("🔄 Attempting to close existing WiFi...")
        await closeWiFi()
        
        // Test AI Photo mode first (since it worked before)
        print("\n1️⃣ Testing AI Photo mode:")
        await testMode(.aiPhoto, "AI Photo")
        
        // Wait and close
        try await Task.sleep(nanoseconds: 3_000_000_000)
        await closeWiFi()
        
        // Test Transfer mode
        print("\n2️⃣ Testing Transfer mode:")
        await testMode(.transfer, "Transfer")
        
        // Wait and close
        try await Task.sleep(nanoseconds: 3_000_000_000)
        await closeWiFi()
        
        // Test Photo mode
        print("\n3️⃣ Testing Photo mode:")
        await testMode(.photo, "Photo")
        
        print("\n✅ Test sequence complete")
    }
    
    @Test func testWiFiModeAfterDeviceModeSet() async throws {
        let manager = QCCentralManager.shared()
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🎯 Testing WiFi after setting device mode...\n")
        
        // Close any existing WiFi
        await closeWiFi()
        try await Task.sleep(nanoseconds: 2_000_000_000)
        
        // Set device to AI Photo mode first
        print("📸 Setting device to AI Photo mode...")
        let modeExp = SimpleExpectation2()
        var modeSet = false
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("   ✅ Device set to AI Photo mode")
            modeSet = true
            modeExp.fulfill()
        }, fail: { errorCode in
            print("   ❌ Failed to set mode: \(errorCode)")
            modeExp.fulfill()
        })
        
        await modeExp.wait(timeout: 5)
        
        if modeSet {
            // Wait for mode to stabilize
            try await Task.sleep(nanoseconds: 2_000_000_000)
            
            // Now try WiFi in AI Photo mode
            print("\n📡 Opening WiFi in AI Photo mode...")
            await testMode(.aiPhoto, "AI Photo (after device mode set)")
        }
    }
    
    private func testMode(_ mode: QCOperatorDeviceMode, _ name: String) async {
        let exp = SimpleExpectation2()
        
        QCSDKCmdCreator.openWifi(with: mode, success: { ssid, password in
            print("   ✅ \(name): SSID: \(ssid), Password: \(password)")
            exp.fulfill()
        }, fail: { errorCode in
            print("   ❌ \(name) failed with error: \(errorCode)")
            exp.fulfill()
        })
        
        await exp.wait(timeout: 10)
    }
    
    private func closeWiFi() async {
        // Try closing WiFi by setting transfer stop mode
        let exp = SimpleExpectation2()
        
        QCSDKCmdCreator.setDeviceMode(.transferStop, success: {
            print("   ✅ WiFi closed")
            exp.fulfill()
        }, fail: { errorCode in
            print("   ⚠️ Close WiFi returned: \(errorCode)")
            exp.fulfill()
        })
        
        await exp.wait(timeout: 5)
    }
}

class SimpleExpectation2 {
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