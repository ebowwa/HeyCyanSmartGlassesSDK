//
//  WiFiManagementTests.swift
//  GlassesFrameworkTests
//
//  Tests for WiFi management and configuration
//

import XCTest
@testable import GlassesFramework

class WiFiManagementTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        print("\n📶 WiFi Management Test Setup")
        ensureConnection()
    }
    
    override func tearDown() {
        bluetoothManager = nil
        super.tearDown()
    }
    
    private func ensureConnection() {
        guard !bluetoothManager.isConnected else { return }
        
        let expectation = XCTestExpectation(description: "Connect to glasses")
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if let device = self.bluetoothManager.discoveredDevices.first(where: { $0.name.contains("M01") }) {
                self.bluetoothManager.connect(to: device)
                DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
                    expectation.fulfill()
                }
            } else {
                expectation.fulfill()
            }
        }
        wait(for: [expectation], timeout: 8.0)
    }
    
    func testOpenWiFiWithMode() {
        print("\n==========================================")
        print("📶 TESTING WIFI OPEN WITH MODE")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Open WiFi")
        
        // Test opening WiFi in transfer mode
        let testMode = DeviceOperationMode.transfer
        
        bluetoothManager.openWiFiWithMode(testMode, success: { ssid, password in
            print("✅ WiFi opened successfully")
            print("   SSID: \(ssid ?? "N/A")")
            print("   Password: \(password ?? "N/A")")
            
            if let ssid = ssid {
                XCTAssertFalse(ssid.isEmpty, "SSID should not be empty")
            }
            
            expectation.fulfill()
        }, fail: { errorCode in
            print("❌ Failed to open WiFi with error code: \(errorCode)")
            expectation.fulfill()
        })
        
        wait(for: [expectation], timeout: 10.0)
    }
    
    func testGetBluetoothStatus() {
        print("\n==========================================")
        print("📶 TESTING BLUETOOTH STATUS")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Get Bluetooth status")
        
        bluetoothManager.getBluetoothStatus { status in
            print("📊 Bluetooth Status: \(status ? "Enabled" : "Disabled")")
            XCTAssertTrue(status, "Bluetooth should be enabled when connected")
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testSetBluetoothStatus() {
        print("\n==========================================")
        print("📶 TESTING SET BLUETOOTH STATUS")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Set Bluetooth status")
        
        // Note: Be careful with this test as disabling Bluetooth will disconnect the device
        print("⚠️ Skipping Bluetooth disable test to maintain connection")
        print("   In production, this would test enabling/disabling Bluetooth")
        
        // Just verify we can call the method with enable=true
        bluetoothManager.setBluetoothStatus(true) { success in
            if success {
                print("✅ Bluetooth enable command sent successfully")
            } else {
                print("❌ Failed to set Bluetooth status")
            }
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
}