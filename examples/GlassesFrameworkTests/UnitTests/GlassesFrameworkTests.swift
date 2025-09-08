//
//  GlassesFrameworkTests.swift
//  GlassesFrameworkTests
//
//  Created by Elijah Arbee on 8/15/25.
//

import XCTest
@testable import GlassesFramework

class GlassesFrameworkTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    func testFrameworkVersion() {
        XCTAssertEqual(GlassesSDK.version, "1.0.0", "Framework version should be 1.0.0")
    }
    
    func testFrameworkInitialization() {
        XCTAssertNoThrow(GlassesSDK.initialize(), "Framework initialization should not throw")
    }
    
    func testBluetoothManagerSingleton() {
        let manager1 = GlassesSDK.bluetoothManager
        let manager2 = GlassesSDK.bluetoothManager
        XCTAssertTrue(manager1 === manager2, "BluetoothManager should be a singleton")
    }
    
    func testBluetoothManagerSharedInstance() {
        let shared = BluetoothManager.shared
        let sdkManager = GlassesSDK.bluetoothManager
        XCTAssertTrue(shared === sdkManager, "BluetoothManager.shared should be the same as GlassesSDK.bluetoothManager")
    }
}
