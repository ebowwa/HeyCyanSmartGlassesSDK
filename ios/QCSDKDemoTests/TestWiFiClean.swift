//
//  TestWiFiClean.swift
//  QCSDKDemoTests
//
//  Clean test of WiFi modes
//

import Testing
import Foundation

struct TestWiFiClean {
    
    @Test func testCleanWiFiModes() async throws {
        let manager = QCCentralManager.shared()
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🧹 Clean WiFi test starting...\n")
        
        // Test 1: Set device to Transfer mode and open WiFi
        print("1️⃣ Transfer mode test:")
        await setDeviceMode(.transfer, "Transfer")
        try await Task.sleep(nanoseconds: 2_000_000_000)
        await openWiFi(.transfer, "Transfer")
        
        // Close WiFi
        try await Task.sleep(nanoseconds: 3_000_000_000)
        await setDeviceMode(.transferStop, "Transfer Stop")
        
        // Test 2: Set device to AI Photo mode and open WiFi
        print("\n2️⃣ AI Photo mode test:")
        await setDeviceMode(.aiPhoto, "AI Photo")
        try await Task.sleep(nanoseconds: 2_000_000_000)
        await openWiFi(.aiPhoto, "AI Photo")
        
        // Close WiFi
        try await Task.sleep(nanoseconds: 3_000_000_000)
        await setDeviceMode(.transferStop, "Transfer Stop")
        
        // Test 3: Set device to Photo mode and open WiFi
        print("\n3️⃣ Photo mode test:")
        await setDeviceMode(.photo, "Photo")
        try await Task.sleep(nanoseconds: 2_000_000_000)
        await openWiFi(.photo, "Photo")
        
        print("\n✅ Clean test complete")
    }
    
    private func setDeviceMode(_ mode: QCOperatorDeviceMode, _ name: String) async {
        let exp = CleanExpectation()
        
        QCSDKCmdCreator.setDeviceMode(mode, success: {
            print("   ✅ Device mode set to \(name)")
            exp.fulfill()
        }, fail: { errorCode in
            print("   ⚠️ Set device mode \(name) returned: \(errorCode)")
            exp.fulfill()
        })
        
        await exp.wait(timeout: 5)
    }
    
    private func openWiFi(_ mode: QCOperatorDeviceMode, _ name: String) async {
        let exp = CleanExpectation()
        
        QCSDKCmdCreator.openWifi(with: mode, success: { ssid, password in
            if !ssid.isEmpty {
                print("   ✅ WiFi opened - SSID: \(ssid), Password: \(password)")
            } else {
                print("   ⚠️ WiFi opened but empty credentials returned")
            }
            exp.fulfill()
        }, fail: { errorCode in
            print("   ❌ WiFi \(name) failed: \(errorCode)")
            exp.fulfill()
        })
        
        await exp.wait(timeout: 10)
    }
}

class CleanExpectation {
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