//
//  DeviceInfoTests.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//

import XCTest
@testable import GlassesFramework

class DeviceInfoTests: XCTestCase {
    
    var deviceInfo: DeviceInfo!
    
    override func setUp() {
        super.setUp()
        deviceInfo = DeviceInfo()
    }
    
    override func tearDown() {
        deviceInfo = nil
        super.tearDown()
    }
    
    func testDefaultInitialization() {
        XCTAssertEqual(deviceInfo.hardwareVersion, "")
        XCTAssertEqual(deviceInfo.firmwareVersion, "")
        XCTAssertEqual(deviceInfo.hardwareWiFiVersion, "")
        XCTAssertEqual(deviceInfo.firmwareWiFiVersion, "")
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
    
    func testVersionProperties() {
        deviceInfo.hardwareVersion = "1.0.0"
        deviceInfo.firmwareVersion = "2.0.0"
        deviceInfo.hardwareWiFiVersion = "3.0.0"
        deviceInfo.firmwareWiFiVersion = "4.0.0"
        
        XCTAssertEqual(deviceInfo.hardwareVersion, "1.0.0")
        XCTAssertEqual(deviceInfo.firmwareVersion, "2.0.0")
        XCTAssertEqual(deviceInfo.hardwareWiFiVersion, "3.0.0")
        XCTAssertEqual(deviceInfo.firmwareWiFiVersion, "4.0.0")
    }
    
    func testMacAddressProperty() {
        deviceInfo.macAddress = "AA:BB:CC:DD:EE:FF"
        XCTAssertEqual(deviceInfo.macAddress, "AA:BB:CC:DD:EE:FF")
    }
    
    func testBatteryProperties() {
        deviceInfo.batteryLevel = 75
        deviceInfo.isCharging = true
        
        XCTAssertEqual(deviceInfo.batteryLevel, 75)
        XCTAssertTrue(deviceInfo.isCharging)
        
        deviceInfo.batteryLevel = 100
        deviceInfo.isCharging = false
        
        XCTAssertEqual(deviceInfo.batteryLevel, 100)
        XCTAssertFalse(deviceInfo.isCharging)
    }
    
    func testMediaCountProperties() {
        deviceInfo.photoCount = 50
        deviceInfo.videoCount = 25
        deviceInfo.audioCount = 10
        
        XCTAssertEqual(deviceInfo.photoCount, 50)
        XCTAssertEqual(deviceInfo.videoCount, 25)
        XCTAssertEqual(deviceInfo.audioCount, 10)
    }
    
    func testRecordingStateProperties() {
        deviceInfo.isRecordingVideo = true
        XCTAssertTrue(deviceInfo.isRecordingVideo)
        XCTAssertFalse(deviceInfo.isRecordingAudio)
        
        deviceInfo.isRecordingVideo = false
        deviceInfo.isRecordingAudio = true
        XCTAssertFalse(deviceInfo.isRecordingVideo)
        XCTAssertTrue(deviceInfo.isRecordingAudio)
    }
    
    func testAIImageDataProperty() {
        XCTAssertNil(deviceInfo.aiImageData)
        
        let testData = Data([0x01, 0x02, 0x03, 0x04])
        deviceInfo.aiImageData = testData
        
        XCTAssertNotNil(deviceInfo.aiImageData)
        XCTAssertEqual(deviceInfo.aiImageData, testData)
        XCTAssertEqual(deviceInfo.aiImageData?.count, 4)
    }
}