//
//  DeviceActionTypeTests.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//

import XCTest
@testable import GlassesFramework

class DeviceActionTypeTests: XCTestCase {
    
    func testDeviceActionTypeRawValues() {
        XCTAssertEqual(DeviceActionType.getVersion.rawValue, 0)
        XCTAssertEqual(DeviceActionType.setTime.rawValue, 1)
        XCTAssertEqual(DeviceActionType.getBattery.rawValue, 2)
        XCTAssertEqual(DeviceActionType.getMediaInfo.rawValue, 3)
        XCTAssertEqual(DeviceActionType.takePhoto.rawValue, 4)
        XCTAssertEqual(DeviceActionType.toggleVideoRecording.rawValue, 5)
        XCTAssertEqual(DeviceActionType.toggleAudioRecording.rawValue, 6)
        XCTAssertEqual(DeviceActionType.takeAIImage.rawValue, 7)
    }
    
    func testDeviceActionTypeTitles() {
        XCTAssertEqual(DeviceActionType.getVersion.title, "Get Version Info")
        XCTAssertEqual(DeviceActionType.setTime.title, "Set Device Time")
        XCTAssertEqual(DeviceActionType.getBattery.title, "Get Battery Status")
        XCTAssertEqual(DeviceActionType.getMediaInfo.title, "Get Media Info")
        XCTAssertEqual(DeviceActionType.takePhoto.title, "Take Photo")
        XCTAssertEqual(DeviceActionType.toggleVideoRecording.title, "Toggle Video Recording")
        XCTAssertEqual(DeviceActionType.toggleAudioRecording.title, "Toggle Audio Recording")
        XCTAssertEqual(DeviceActionType.takeAIImage.title, "Take AI Image")
    }
    
    func testAllCasesCount() {
        XCTAssertEqual(DeviceActionType.allCases.count, 8)
    }
    
    func testAllCasesContainsAllTypes() {
        let allCases = DeviceActionType.allCases
        
        XCTAssertTrue(allCases.contains(.getVersion))
        XCTAssertTrue(allCases.contains(.setTime))
        XCTAssertTrue(allCases.contains(.getBattery))
        XCTAssertTrue(allCases.contains(.getMediaInfo))
        XCTAssertTrue(allCases.contains(.takePhoto))
        XCTAssertTrue(allCases.contains(.toggleVideoRecording))
        XCTAssertTrue(allCases.contains(.toggleAudioRecording))
        XCTAssertTrue(allCases.contains(.takeAIImage))
    }
    
    func testIterationOverAllCases() {
        var count = 0
        for _ in DeviceActionType.allCases {
            count += 1
        }
        XCTAssertEqual(count, 8)
    }
    
    func testTitleUniqueness() {
        let titles = DeviceActionType.allCases.map { $0.title }
        let uniqueTitles = Set(titles)
        XCTAssertEqual(titles.count, uniqueTitles.count, "All titles should be unique")
    }
}