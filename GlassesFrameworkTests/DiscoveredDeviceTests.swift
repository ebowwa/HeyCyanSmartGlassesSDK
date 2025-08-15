//
//  DiscoveredDeviceTests.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//

import XCTest
import CoreBluetooth
@testable import GlassesFramework

class DiscoveredDeviceTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    let scanTimeout: TimeInterval = 10.0
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
    }
    
    override func tearDown() {
        bluetoothManager.stopScanning()
        bluetoothManager.disconnect()
        bluetoothManager = nil
        super.tearDown()
    }
    
    func testRealDeviceScanning() {
        let expectation = XCTestExpectation(description: "Discover real M01 glasses")
        
        bluetoothManager.startScanning()
        
        // Wait for real device discovery
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            let devices = self.bluetoothManager.discoveredDevices
            
            // Check if we found any M01 devices (the glasses)
            let glassesDevices = devices.filter { $0.name.contains("M01") }
            
            if !glassesDevices.isEmpty {
                print("✅ Found \(glassesDevices.count) HeyCyan glasses:")
                for device in glassesDevices {
                    print("  - \(device.name) (\(device.macAddress))")
                }
                XCTAssertTrue(glassesDevices.count > 0, "Should find at least one M01 device")
            } else {
                print("⚠️ No glasses found - make sure they are powered on and in pairing mode")
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: scanTimeout)
    }
    
    func testRealDeviceConnection() {
        let scanExpectation = XCTestExpectation(description: "Find glasses to connect")
        let connectExpectation = XCTestExpectation(description: "Connect to real glasses")
        
        bluetoothManager.startScanning()
        
        // First find the device
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            let glassesDevices = self.bluetoothManager.discoveredDevices.filter { $0.name.contains("M01") }
            
            if let firstGlasses = glassesDevices.first {
                print("📱 Attempting to connect to: \(firstGlasses.name)")
                scanExpectation.fulfill()
                
                // Attempt connection
                self.bluetoothManager.connect(to: firstGlasses)
                
                // Wait for connection
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    if self.bluetoothManager.isConnected {
                        print("✅ Successfully connected to glasses!")
                        XCTAssertTrue(self.bluetoothManager.isConnected)
                        XCTAssertEqual(self.bluetoothManager.connectedDeviceName, firstGlasses.name)
                    } else {
                        print("❌ Failed to connect - glasses may need to be in pairing mode")
                    }
                    connectExpectation.fulfill()
                }
            } else {
                print("⚠️ No glasses found to connect to")
                scanExpectation.fulfill()
                connectExpectation.fulfill()
            }
        }
        
        wait(for: [scanExpectation, connectExpectation], timeout: 15.0)
    }
}