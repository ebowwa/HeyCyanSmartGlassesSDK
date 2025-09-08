//
//  ConnectionBondingTests.swift
//  GlassesFrameworkTests
//
//  Created by Test Suite on 8/15/25.
//
//  Comprehensive tests for connection, disconnection, and bonding/pairing
//  with real HeyCyan glasses hardware
//

import XCTest
import CoreBluetooth
@testable import GlassesFramework

class ConnectionBondingTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var connectedDevice: DiscoveredDevice?
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        print("\n🔧 Connection & Bonding Test Setup")
        print("📱 Ensure:")
        print("  • Glasses are powered on")
        print("  • Bluetooth is enabled on iPhone")
        print("  • Glasses are in pairing mode if not already bonded")
    }
    
    override func tearDown() {
        // Clean disconnect after each test
        if bluetoothManager.isConnected {
            bluetoothManager.disconnect()
            // Wait for disconnect to complete
            Thread.sleep(forTimeInterval: 2.0)
        }
        connectedDevice = nil
        bluetoothManager = nil
        super.tearDown()
    }
    
    // MARK: - Test Full Connection Lifecycle
    
    func testFullConnectionLifecycle() {
        print("\n==========================================")
        print("🔄 TESTING FULL CONNECTION LIFECYCLE")
        print("==========================================\n")
        
        // Phase 1: Discovery
        print("📡 Phase 1: Device Discovery")
        let discoveryExpectation = XCTestExpectation(description: "Discover glasses")
        
        bluetoothManager.startScanning()
        XCTAssertTrue(bluetoothManager.isScanning, "Should be scanning after startScanning()")
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            let devices = self.bluetoothManager.discoveredDevices
            print("   Found \(devices.count) devices")
            
            let glassesDevices = devices.filter { $0.name.contains("M01") }
            XCTAssertFalse(glassesDevices.isEmpty, "Should find at least one M01 device")
            
            if let glasses = glassesDevices.first {
                print("   ✅ Discovered: \(glasses.name) - MAC: \(glasses.macAddress)")
                self.connectedDevice = glasses
            }
            
            discoveryExpectation.fulfill()
        }
        
        wait(for: [discoveryExpectation], timeout: 8.0)
        
        // Phase 2: Stop Scanning
        print("\n📡 Phase 2: Stop Scanning")
        bluetoothManager.stopScanning()
        XCTAssertFalse(bluetoothManager.isScanning, "Should not be scanning after stopScanning()")
        print("   ✅ Scanning stopped")
        
        // Phase 3: Connection
        guard let device = connectedDevice else {
            XCTFail("No device found to connect to")
            return
        }
        
        print("\n🔗 Phase 3: Connection")
        print("   Attempting to connect to: \(device.name)")
        
        let connectionExpectation = XCTestExpectation(description: "Connect to glasses")
        
        // Monitor connection state changes
        var stateChanges: [QCState] = []
        let stateObserver = NotificationCenter.default.addObserver(
            forName: Notification.Name("ConnectionStateChanged"),
            object: nil,
            queue: .main
        ) { notification in
            if let state = notification.userInfo?["state"] as? QCState {
                stateChanges.append(state)
                print("   State change: \(self.stateDescription(for: state))")
            }
        }
        
        bluetoothManager.connect(to: device)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            if self.bluetoothManager.isConnected {
                print("   ✅ Connected successfully")
                print("   Device name: \(self.bluetoothManager.connectedDeviceName)")
                print("   Connection state: \(self.stateDescription(for: self.bluetoothManager.connectionState))")
                XCTAssertTrue(self.bluetoothManager.isConnected)
                XCTAssertEqual(self.bluetoothManager.connectedDeviceName, device.name)
            } else {
                print("   ⚠️ Connection failed or pending")
                print("   Current state: \(self.stateDescription(for: self.bluetoothManager.connectionState))")
            }
            connectionExpectation.fulfill()
        }
        
        wait(for: [connectionExpectation], timeout: 8.0)
        
        // Phase 4: Verify Connection
        print("\n✅ Phase 4: Verify Connection")
        if bluetoothManager.isConnected {
            print("   Testing device operations while connected...")
            
            // Test getting device info
            let infoExpectation = XCTestExpectation(description: "Get device info")
            
            bluetoothManager.getVersionInfo()
            bluetoothManager.getBatteryStatus()
            bluetoothManager.getMacAddress()
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
                let deviceInfo = self.bluetoothManager.deviceInfo
                print("   📱 Device Info:")
                print("      Battery: \(deviceInfo.batteryLevel)%")
                print("      Charging: \(deviceInfo.isCharging)")
                if !deviceInfo.firmwareVersion.isEmpty {
                    print("      Firmware: \(deviceInfo.firmwareVersion)")
                }
                if !deviceInfo.macAddress.isEmpty {
                    print("      MAC: \(deviceInfo.macAddress)")
                }
                infoExpectation.fulfill()
            }
            
            wait(for: [infoExpectation], timeout: 5.0)
        }
        
        // Phase 5: Disconnection
        print("\n🔌 Phase 5: Disconnection")
        let disconnectionExpectation = XCTestExpectation(description: "Disconnect from glasses")
        
        bluetoothManager.disconnect()
        print("   Disconnect command sent")
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            XCTAssertFalse(self.bluetoothManager.isConnected, "Should be disconnected")
            XCTAssertTrue(self.bluetoothManager.connectedDeviceName.isEmpty, "Device name should be cleared")
            print("   ✅ Disconnected successfully")
            print("   Final state: \(self.stateDescription(for: self.bluetoothManager.connectionState))")
            disconnectionExpectation.fulfill()
        }
        
        wait(for: [disconnectionExpectation], timeout: 5.0)
        
        // Clean up observer
        NotificationCenter.default.removeObserver(stateObserver)
        
        print("\n==========================================")
        print("✅ CONNECTION LIFECYCLE TEST COMPLETE")
        print("==========================================\n")
    }
    
    // MARK: - Test Reconnection (Bonding)
    
    func testReconnectionAfterBonding() {
        print("\n==========================================")
        print("🔄 TESTING RECONNECTION (BONDING)")
        print("==========================================\n")
        
        // First connection
        print("📱 Initial Connection:")
        guard establishConnection() else {
            XCTFail("Failed to establish initial connection")
            return
        }
        
        let deviceName = bluetoothManager.connectedDeviceName
        print("   Connected to: \(deviceName)")
        
        // Disconnect
        print("\n🔌 Disconnecting...")
        bluetoothManager.disconnect()
        Thread.sleep(forTimeInterval: 3.0)
        XCTAssertFalse(bluetoothManager.isConnected)
        print("   ✅ Disconnected")
        
        // Reconnection (should be faster if bonded)
        print("\n🔄 Attempting Reconnection (testing bonding)...")
        let reconnectStart = Date()
        
        bluetoothManager.startScanning()
        
        let reconnectExpectation = XCTestExpectation(description: "Reconnect to bonded device")
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let devices = self.bluetoothManager.discoveredDevices
            if let bondedDevice = devices.first(where: { $0.name == deviceName }) {
                print("   Found bonded device: \(bondedDevice.name)")
                
                self.bluetoothManager.connect(to: bondedDevice)
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    let reconnectTime = Date().timeIntervalSince(reconnectStart)
                    
                    if self.bluetoothManager.isConnected {
                        print("   ✅ Reconnected successfully in \(String(format: "%.2f", reconnectTime)) seconds")
                        print("   Bonding appears to be working!")
                        XCTAssertTrue(self.bluetoothManager.isConnected)
                        XCTAssertEqual(self.bluetoothManager.connectedDeviceName, deviceName)
                    } else {
                        print("   ⚠️ Reconnection failed")
                    }
                    reconnectExpectation.fulfill()
                }
            } else {
                print("   ❌ Bonded device not found in scan")
                reconnectExpectation.fulfill()
            }
        }
        
        wait(for: [reconnectExpectation], timeout: 10.0)
        
        print("\n==========================================")
        print("✅ RECONNECTION TEST COMPLETE")
        print("==========================================\n")
    }
    
    // MARK: - Test Multiple Connect/Disconnect Cycles
    
    func testMultipleConnectionCycles() {
        print("\n==========================================")
        print("🔄 TESTING MULTIPLE CONNECTION CYCLES")
        print("==========================================\n")
        
        let cycles = 3
        var successfulCycles = 0
        
        for i in 1...cycles {
            print("\n📱 Cycle \(i)/\(cycles):")
            
            // Connect
            if establishConnection() {
                print("   ✅ Connected")
                successfulCycles += 1
                
                // Stay connected for 2 seconds
                Thread.sleep(forTimeInterval: 2.0)
                
                // Disconnect
                bluetoothManager.disconnect()
                Thread.sleep(forTimeInterval: 2.0)
                
                XCTAssertFalse(bluetoothManager.isConnected)
                print("   ✅ Disconnected")
            } else {
                print("   ❌ Connection failed")
            }
            
            // Wait between cycles
            if i < cycles {
                print("   ⏳ Waiting before next cycle...")
                Thread.sleep(forTimeInterval: 3.0)
            }
        }
        
        print("\n📊 Results: \(successfulCycles)/\(cycles) cycles successful")
        XCTAssertEqual(successfulCycles, cycles, "All connection cycles should succeed")
        
        print("\n==========================================")
        print("✅ MULTIPLE CYCLES TEST COMPLETE")
        print("==========================================\n")
    }
    
    // MARK: - Test Connection State Transitions
    
    func testConnectionStateTransitions() {
        print("\n==========================================")
        print("🔄 TESTING CONNECTION STATE TRANSITIONS")
        print("==========================================\n")
        
        var observedStates: [QCState] = []
        let stateExpectation = XCTestExpectation(description: "Observe state transitions")
        
        // Set up state observer
        let observer = NotificationCenter.default.addObserver(
            forName: Notification.Name("ConnectionStateChanged"),
            object: nil,
            queue: .main
        ) { notification in
            if let state = notification.userInfo?["state"] as? QCState {
                observedStates.append(state)
                print("📊 State transition: \(self.stateDescription(for: state))")
            }
        }
        
        // Start with unbind state
        print("Initial state: \(stateDescription(for: bluetoothManager.connectionState))")
        XCTAssertEqual(bluetoothManager.connectionState, .unbind)
        
        // Trigger connection
        _ = establishConnection()
        
        // Wait to observe all state transitions
        DispatchQueue.main.asyncAfter(deadline: .now() + 8.0) {
            print("\n📊 Observed state transitions:")
            for (index, state) in observedStates.enumerated() {
                print("   \(index + 1). \(self.stateDescription(for: state))")
            }
            
            // Verify we saw expected transitions
            if self.bluetoothManager.isConnected {
                XCTAssertTrue(observedStates.contains(.connecting) || observedStates.contains(.connected),
                            "Should observe connecting or connected state")
            }
            
            stateExpectation.fulfill()
        }
        
        wait(for: [stateExpectation], timeout: 10.0)
        
        // Clean up
        NotificationCenter.default.removeObserver(observer)
        
        print("\n==========================================")
        print("✅ STATE TRANSITIONS TEST COMPLETE")
        print("==========================================\n")
    }
    
    // MARK: - Helper Methods
    
    private func establishConnection() -> Bool {
        let expectation = XCTestExpectation(description: "Establish connection")
        var connected = false
        
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if let device = self.bluetoothManager.discoveredDevices.first(where: { $0.name.contains("M01") }) {
                self.bluetoothManager.connect(to: device)
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    connected = self.bluetoothManager.isConnected
                    expectation.fulfill()
                }
            } else {
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 10.0)
        return connected
    }
    
    private func stateDescription(for state: QCState) -> String {
        switch state {
        case .unbind:
            return "Unbind (Not paired)"
        case .connecting:
            return "Connecting..."
        case .connected:
            return "Connected"
        case .disconnecting:
            return "Disconnecting..."
        case .disconnected:
            return "Disconnected"
        default:
            return "Unknown (\(state.rawValue))"
        }
    }
}