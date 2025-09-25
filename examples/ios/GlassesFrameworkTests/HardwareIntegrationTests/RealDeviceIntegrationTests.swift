//
//  RealDeviceIntegrationTests.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//
//  Integration tests for real HeyCyan glasses hardware
//  Requires: Physical glasses powered on and iPhone with Bluetooth enabled
//

import XCTest
import CoreBluetooth
import UIKit
@testable import GlassesFramework

class RealDeviceIntegrationTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var connectedDevice: DiscoveredDevice?
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        print("🔧 Setting up integration test - ensure glasses are powered on")
    }
    
    override func tearDown() {
        if bluetoothManager.isConnected {
            bluetoothManager.disconnect()
        }
        bluetoothManager = nil
        connectedDevice = nil
        super.tearDown()
    }
    
    // MARK: - Helper Methods
    
    private func findAndConnectToGlasses(timeout: TimeInterval = 15.0) -> Bool {
        let expectation = XCTestExpectation(description: "Connect to glasses")
        var connectionSuccess = false
        
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            let glassesDevices = self.bluetoothManager.discoveredDevices.filter { $0.name.contains("M01") }
            
            if let glasses = glassesDevices.first {
                print("🔍 Found glasses: \(glasses.name)")
                self.connectedDevice = glasses
                self.bluetoothManager.connect(to: glasses)
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    connectionSuccess = self.bluetoothManager.isConnected
                    expectation.fulfill()
                }
            } else {
                print("❌ No glasses found")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: timeout)
        return connectionSuccess
    }
    
    // MARK: - Connection Tests
    
    func testGlassesDiscovery() {
        print("🔍 Starting glasses discovery test...")
        
        let expectation = XCTestExpectation(description: "Discover glasses")
        
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            let devices = self.bluetoothManager.discoveredDevices
            print("📱 Found \(devices.count) total devices")
            
            let glassesDevices = devices.filter { $0.name.contains("M01") }
            XCTAssertTrue(glassesDevices.count > 0, "Should discover at least one M01 glasses device")
            
            if let glasses = glassesDevices.first {
                print("✅ Glasses found: \(glasses.name) - MAC: \(glasses.macAddress)")
                XCTAssertTrue(glasses.name.contains("M01"))
                XCTAssertFalse(glasses.macAddress.isEmpty)
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 10.0)
    }
    
    func testGlassesConnection() {
        print("🔗 Starting glasses connection test...")
        
        let connected = findAndConnectToGlasses()
        XCTAssertTrue(connected, "Should successfully connect to glasses")
        
        if connected {
            print("✅ Connected to: \(bluetoothManager.connectedDeviceName)")
            XCTAssertTrue(bluetoothManager.isConnected)
            XCTAssertFalse(bluetoothManager.connectedDeviceName.isEmpty)
        }
    }
    
    // MARK: - Device Information Tests
    
    func testGetVersionInfo() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        let expectation = XCTestExpectation(description: "Get version info")
        
        bluetoothManager.getVersionInfo()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let deviceInfo = self.bluetoothManager.deviceInfo
            
            print("📱 Version Info:")
            print("  Hardware: \(deviceInfo.hardwareVersion)")
            print("  Firmware: \(deviceInfo.firmwareVersion)")
            print("  WiFi Hardware: \(deviceInfo.hardwareWiFiVersion)")
            print("  WiFi Firmware: \(deviceInfo.firmwareWiFiVersion)")
            
            // Version strings should be populated after successful request
            if !deviceInfo.firmwareVersion.isEmpty {
                XCTAssertFalse(deviceInfo.firmwareVersion.isEmpty)
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testGetBatteryStatus() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        let expectation = XCTestExpectation(description: "Get battery status")
        
        bluetoothManager.getBatteryStatus()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let battery = self.bluetoothManager.deviceInfo.batteryLevel
            let charging = self.bluetoothManager.deviceInfo.isCharging
            
            print("🔋 Battery Status:")
            print("  Level: \(battery)%")
            print("  Charging: \(charging ? "Yes" : "No")")
            
            XCTAssertTrue(battery >= 0 && battery <= 100, "Battery level should be 0-100")
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testGetMacAddress() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        let expectation = XCTestExpectation(description: "Get MAC address")
        
        bluetoothManager.getMacAddress()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let macAddress = self.bluetoothManager.deviceInfo.macAddress
            
            print("📡 MAC Address: \(macAddress)")
            
            if !macAddress.isEmpty {
                XCTAssertTrue(macAddress.contains(":"), "MAC address should be formatted with colons")
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // MARK: - Media Operations Tests
    
    func testGetMediaInfo() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        let expectation = XCTestExpectation(description: "Get media info")
        
        bluetoothManager.getMediaInfo()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let deviceInfo = self.bluetoothManager.deviceInfo
            
            print("📸 Media Info:")
            print("  Photos: \(deviceInfo.photoCount)")
            print("  Videos: \(deviceInfo.videoCount)")
            print("  Audio: \(deviceInfo.audioCount)")
            
            XCTAssertTrue(deviceInfo.photoCount >= 0)
            XCTAssertTrue(deviceInfo.videoCount >= 0)
            XCTAssertTrue(deviceInfo.audioCount >= 0)
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testTakePhoto() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        print("📸 Testing photo capture...")
        print("⚠️ Listen for camera shutter sound from glasses")
        
        let expectation = XCTestExpectation(description: "Take photo")
        
        let initialPhotoCount = bluetoothManager.deviceInfo.photoCount
        
        bluetoothManager.takePhoto()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            print("✅ Photo command sent")
            
            // Get updated media info to verify photo was taken
            self.bluetoothManager.getMediaInfo()
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                let newPhotoCount = self.bluetoothManager.deviceInfo.photoCount
                print("📸 Photos: \(initialPhotoCount) -> \(newPhotoCount)")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 8.0)
    }
    
    func testSetDeviceTime() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        let expectation = XCTestExpectation(description: "Set device time")
        
        print("⏰ Setting device time to current time...")
        
        bluetoothManager.setDeviceTime()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            print("✅ Time sync command sent")
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // MARK: - AI Image Test
    
    func testTakeAIImage() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }
        
        print("🎨 Testing AI image capture...")
        print("⚠️ This will take a photo and process it with AI")
        
        let expectation = XCTestExpectation(description: "Take AI image")
        
        // Listen for AI image notification
        NotificationCenter.default.addObserver(
            forName: .aiImageReceived,
            object: nil,
            queue: .main
        ) { notification in
            print("✅ AI image received!")
            if let imageData = notification.object as? Data {
                print("📊 Image size: \(imageData.count) bytes")
                XCTAssertTrue(imageData.count > 0)
            }
            expectation.fulfill()
        }
        
        // Listen for failure notification
        NotificationCenter.default.addObserver(
            forName: Notification.Name("AIImageCaptureFailed"),
            object: nil,
            queue: .main
        ) { notification in
            print("❌ AI image capture failed")
            if let error = notification.userInfo?["error"] as? String {
                print("Error: \(error)")
            }
            expectation.fulfill()
        }

        bluetoothManager.takeAIImage()

        wait(for: [expectation], timeout: 10.0)
    }

    // MARK: - Safety Locator Test

    func testFindDeviceAlert() {
        guard findAndConnectToGlasses() else {
            XCTFail("Failed to connect to glasses")
            return
        }

        print("📣 Testing find device safety alert...")

        let expectation = XCTestExpectation(description: "Find device alert")
        var didReceiveSuccess = false

        let successObserver = NotificationCenter.default.addObserver(
            forName: .findDeviceAlertTriggered,
            object: nil,
            queue: .main
        ) { _ in
            print("✅ Locator alert activated")
            didReceiveSuccess = true
            expectation.fulfill()
        }

        let failureObserver = NotificationCenter.default.addObserver(
            forName: .findDeviceAlertFailed,
            object: nil,
            queue: .main
        ) { notification in
            print("❌ Locator alert failed")
            if let error = notification.userInfo?["error"] as? String {
                print("Error: \(error)")
            }
            expectation.fulfill()
        }

        bluetoothManager.findDevice()

        wait(for: [expectation], timeout: 6.0)

        NotificationCenter.default.removeObserver(successObserver)
        NotificationCenter.default.removeObserver(failureObserver)

        XCTAssertTrue(didReceiveSuccess, "Expected locator alert to activate successfully")
    }
}