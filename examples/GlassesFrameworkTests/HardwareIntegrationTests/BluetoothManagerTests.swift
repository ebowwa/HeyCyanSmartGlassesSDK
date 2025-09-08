//
//  BluetoothManagerTests.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//

import XCTest
import CoreBluetooth
@testable import GlassesFramework

class BluetoothManagerTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
    }
    
    override func tearDown() {
        bluetoothManager = nil
        super.tearDown()
    }
    
    func testInitialState() {
        XCTAssertFalse(bluetoothManager.isConnected)
        XCTAssertFalse(bluetoothManager.isScanning)
        XCTAssertEqual(bluetoothManager.discoveredDevices.count, 0)
        XCTAssertEqual(bluetoothManager.connectedDeviceName, "")
        XCTAssertEqual(bluetoothManager.connectionState, .unbind)
    }
    
    func testDeviceInfoInitialState() {
        let deviceInfo = bluetoothManager.deviceInfo
        XCTAssertEqual(deviceInfo.hardwareVersion, "")
        XCTAssertEqual(deviceInfo.firmwareVersion, "")
        XCTAssertEqual(deviceInfo.macAddress, "")
        XCTAssertEqual(deviceInfo.batteryLevel, 0)
        XCTAssertFalse(deviceInfo.isCharging)
        XCTAssertEqual(deviceInfo.photoCount, 0)
        XCTAssertEqual(deviceInfo.videoCount, 0)
        XCTAssertEqual(deviceInfo.audioCount, 0)
        XCTAssertFalse(deviceInfo.isRecordingVideo)
        XCTAssertFalse(deviceInfo.isRecordingAudio)
        XCTAssertNil(deviceInfo.aiImageData)
    }
    
    func testStartScanning() {
        bluetoothManager.startScanning()
        XCTAssertTrue(bluetoothManager.isScanning)
    }
    
    func testStopScanning() {
        bluetoothManager.startScanning()
        bluetoothManager.stopScanning()
        XCTAssertFalse(bluetoothManager.isScanning)
    }
    
    func testDisconnect() {
        bluetoothManager.disconnect()
        XCTAssertFalse(bluetoothManager.isConnected)
        XCTAssertEqual(bluetoothManager.connectedDeviceName, "")
        XCTAssertEqual(bluetoothManager.discoveredDevices.count, 0)
    }
    
    func testConnectionStateTransitions() {
        // Test state transition handling
        bluetoothManager.didState(.connecting)
        XCTAssertEqual(bluetoothManager.connectionState, .connecting)
        
        bluetoothManager.didState(.connected)
        XCTAssertEqual(bluetoothManager.connectionState, .connected)
        XCTAssertTrue(bluetoothManager.isConnected)
        
        bluetoothManager.didState(.disconnecting)
        XCTAssertEqual(bluetoothManager.connectionState, .disconnecting)
        XCTAssertFalse(bluetoothManager.isConnected)
        
        bluetoothManager.didState(.disconnected)
        XCTAssertEqual(bluetoothManager.connectionState, .disconnected)
        XCTAssertFalse(bluetoothManager.isConnected)
    }
    
    func testBatteryUpdateDelegate() {
        bluetoothManager.didUpdateBatteryLevel(85, charging: true)
        XCTAssertEqual(bluetoothManager.deviceInfo.batteryLevel, 85)
        XCTAssertTrue(bluetoothManager.deviceInfo.isCharging)
        
        bluetoothManager.didUpdateBatteryLevel(50, charging: false)
        XCTAssertEqual(bluetoothManager.deviceInfo.batteryLevel, 50)
        XCTAssertFalse(bluetoothManager.deviceInfo.isCharging)
    }
    
    func testMediaUpdateDelegate() {
        bluetoothManager.didUpdateMedia(withPhotoCount: 10, videoCount: 5, audioCount: 3, type: 0)
        XCTAssertEqual(bluetoothManager.deviceInfo.photoCount, 10)
        XCTAssertEqual(bluetoothManager.deviceInfo.videoCount, 5)
        XCTAssertEqual(bluetoothManager.deviceInfo.audioCount, 3)
    }
    
    func testAIImageDataReceived() {
        let testImageData = Data([0xFF, 0xD8, 0xFF, 0xE0]) // JPEG header bytes
        bluetoothManager.didReceiveAIChatImageData(testImageData)
        
        XCTAssertNotNil(bluetoothManager.deviceInfo.aiImageData)
        XCTAssertEqual(bluetoothManager.deviceInfo.aiImageData?.count, testImageData.count)
    }
}