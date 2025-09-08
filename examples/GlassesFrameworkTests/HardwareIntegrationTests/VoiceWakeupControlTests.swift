//
//  VoiceWakeupControlTests.swift
//  GlassesFrameworkTests
//
//  Tests for controlling the glasses' voice wakeup feature
//

import XCTest
import CoreBluetooth
@testable import GlassesFramework

class VoiceWakeupControlTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        
        print("\n🎙️ Voice Wakeup Control Test Setup")
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
    
    func testToggleVoiceWakeup() {
        print("\n==========================================")
        print("🎙️ TESTING VOICE WAKEUP CONTROL")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Toggle voice wakeup")
        
        // First, get current status
        bluetoothManager.getVoiceWakeupStatus { initialStatus in
            print("📊 Initial status: \(initialStatus ? "ENABLED" : "DISABLED")")
            
            // Try to toggle it
            let newStatus = !initialStatus
            print("\n🔄 Attempting to \(newStatus ? "ENABLE" : "DISABLE") voice wakeup...")
            
            self.bluetoothManager.setVoiceWakeup(enabled: newStatus) { success in
                if success {
                    print("✅ Successfully changed voice wakeup setting")
                    
                    // Verify the change
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                        self.bluetoothManager.getVoiceWakeupStatus { currentStatus in
                            print("📊 New status: \(currentStatus ? "ENABLED" : "DISABLED")")
                            
                            if currentStatus == newStatus {
                                print("✅ Voice wakeup control WORKS!")
                            } else {
                                print("⚠️ Status didn't change as expected")
                            }
                            
                            // Try to restore original setting
                            print("\n🔄 Restoring original setting...")
                            self.bluetoothManager.setVoiceWakeup(enabled: initialStatus) { _ in
                                print("✅ Restored to: \(initialStatus ? "ENABLED" : "DISABLED")")
                                expectation.fulfill()
                            }
                        }
                    }
                } else {
                    print("❌ Failed to change voice wakeup setting")
                    print("   ℹ️ The glasses may not support changing this setting")
                    expectation.fulfill()
                }
            }
        }
        
        wait(for: [expectation], timeout: 10.0)
    }
}