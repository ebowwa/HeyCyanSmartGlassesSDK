//
//  TestWiFiModes.swift
//  QCSDKDemoTests
//
//  Test what WiFi modes actually work
//

import Testing
import Foundation

struct TestWiFiModes {
    
    @Test func testAllWiFiModes() async throws {
        let manager = QCCentralManager.shared()
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected")
            return
        }
        
        print("\n🔍 Testing all WiFi modes with glasses...\n")
        
        // Define all possible modes to test
        let modes: [(QCOperatorDeviceMode, String)] = [
            (.transfer, "Transfer (Media)"),
            (.photo, "Photo"),
            (.aiPhoto, "AI Photo"),
            (.OTA, "OTA Update"),
            (.video, "Video"),
            (.audio, "Audio")
        ]
        
        for (mode, name) in modes {
            print("Testing: \(name)")
            
            let exp = SimpleExpectation()
            var result = "❌ Failed"
            
            QCSDKCmdCreator.openWifi(with: mode, success: { ssid, password in
                result = "✅ SSID: \(ssid), Pass: \(password)"
                exp.fulfill()
            }, fail: { errorCode in
                result = "❌ Error: \(errorCode)"
                exp.fulfill()
            })
            
            await exp.wait(timeout: 5)
            print("  \(result)\n")
            
            // Wait between tests
            try await Task.sleep(nanoseconds: 1_000_000_000)
        }
        
        print("✅ Test complete - check results above")
    }
}

class SimpleExpectation {
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