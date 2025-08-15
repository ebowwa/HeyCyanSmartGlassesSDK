//
//  VoiceDetectionTests.swift
//  GlassesFrameworkTests
//
//  Tests for the glasses' built-in voice detection features
//

import XCTest
import CoreBluetooth
@testable import GlassesFramework

class VoiceDetectionTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        
        print("\n🎙️ Voice Detection Test Setup")
        ensureConnection()
    }
    
    override func tearDown() {
        bluetoothManager = nil
        super.tearDown()
    }
    
    private func ensureConnection() {
        guard !bluetoothManager.isConnected else {
            print("✅ Already connected to glasses")
            return
        }
        
        print("🔄 Attempting to connect to glasses...")
        let expectation = XCTestExpectation(description: "Connect to glasses")
        
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if let device = self.bluetoothManager.discoveredDevices.first(where: { $0.name.contains("M01") }) {
                print("📱 Found glasses: \(device.name) - connecting...")
                self.bluetoothManager.connect(to: device)
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    if self.bluetoothManager.isConnected {
                        print("✅ Connected successfully!")
                    }
                    expectation.fulfill()
                }
            } else {
                print("❌ No M01 glasses found")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 10.0)
        XCTAssertTrue(bluetoothManager.isConnected, "Must be connected to glasses")
    }
    
    func testVoiceWakeupStatus() {
        print("\n==========================================")
        print("🎙️ TESTING VOICE WAKEUP STATUS")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Check voice wakeup")
        
        print("🔍 Checking if 'Hey Cyan' voice wakeup is enabled...")
        
        bluetoothManager.getVoiceWakeupStatus { enabled in
            if enabled {
                print("✅ Voice wakeup is ENABLED")
                print("   📢 The glasses are listening for 'Hey Cyan'")
            } else {
                print("❌ Voice wakeup is DISABLED")
                print("   🔇 The glasses are NOT listening for voice commands")
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testWearingDetection() {
        print("\n==========================================")
        print("👓 TESTING WEARING DETECTION")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Check wearing detection")
        
        print("🔍 Checking if wearing detection is enabled...")
        
        bluetoothManager.getWearingDetectionStatus { enabled in
            if enabled {
                print("✅ Wearing detection is ENABLED")
                print("   👓 The glasses can detect when they're being worn")
            } else {
                print("❌ Wearing detection is DISABLED")
                print("   🚫 The glasses cannot detect wearing status")
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testAISpeakMode() {
        print("\n==========================================")
        print("🤖 TESTING AI SPEAK MODE")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Test AI speak mode")
        
        // Test different AI speak modes
        let modes: [(AISpeakMode, String)] = [
            (.start, "Start AI speaking"),
            (.hold, "Hold AI speaking"),
            (.stop, "Stop AI speaking")
        ]
        
        var testIndex = 0
        
        func testNextMode() {
            guard testIndex < modes.count else {
                expectation.fulfill()
                return
            }
            
            let (mode, description) = modes[testIndex]
            print("\n🎤 Setting AI speak mode to: \(description)")
            
            self.bluetoothManager.setAISpeakMode(mode) { success in
                if success {
                    print("   ✅ Successfully set mode: \(description)")
                } else {
                    print("   ❌ Failed to set mode: \(description)")
                }
                
                testIndex += 1
                
                // Wait a bit before testing next mode
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                    testNextMode()
                }
            }
        }
        
        testNextMode()
        
        wait(for: [expectation], timeout: 10.0)
    }
    
    func testHeyCyanDetection() {
        print("\n==========================================")
        print("🗣️ TESTING 'HEY CYAN' DETECTION")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Test Hey Cyan")
        
        // First check if voice wakeup is enabled
        bluetoothManager.getVoiceWakeupStatus { enabled in
            if enabled {
                print("✅ Voice wakeup is active!")
                print("\n📢 PLEASE SAY 'HEY CYAN' TO THE GLASSES")
                print("   Waiting 10 seconds for voice detection...\n")
                
                // Monitor for any response or state change
                var checkCount = 0
                Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
                    checkCount += 1
                    
                    // Check device state
                    let deviceInfo = self.bluetoothManager.deviceInfo
                    print("   [\(checkCount)s] Device state - Recording: \(deviceInfo.isRecordingAudio)")
                    
                    // You could also check for any notifications or state changes here
                    
                    if checkCount >= 10 {
                        timer.invalidate()
                        print("\n📊 Test complete")
                        expectation.fulfill()
                    }
                }
            } else {
                print("⚠️ Voice wakeup is not enabled")
                print("   The glasses won't respond to 'Hey Cyan'")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 15.0)
    }
}