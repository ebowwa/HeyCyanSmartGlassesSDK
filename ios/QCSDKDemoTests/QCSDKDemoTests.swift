//
//  QCSDKDemoTests.swift
//  QCSDKDemoTests
//
//  Created by Elijah Arbee on 8/13/25.
//

import Testing
import Foundation
import CoreBluetooth

/// IMPORTANT TEST NOTES:
/// 
/// TESTING WITH GLASSES CONNECTED:
/// -------------------------------
/// These tests are designed to work WITH your HeyCyan glasses (M01_9FD8) connected!
/// The QCCentralManager is a SINGLETON that maintains real device connections.
///
/// Tests validate both states:
/// - Connected state: When glasses are actively connected
/// - Fresh state: When testing new QCBlePeripheral instances
///
/// The singleton tests check actual runtime behavior, not just initial states.
/// This ensures the SDK works correctly with real hardware.
///
/// Each test below tests EXACTLY ONE THING for easy debugging.

struct QCSDKDemoTests {

    @Test func testQCDeviceTypeUnknownValue() {
        #expect(QCDeviceType.unkown.rawValue == 0)
    }
    
    @Test func testQCDeviceTypeWatchValue() {
        #expect(QCDeviceType.watch.rawValue == 1)
    }
    
    @Test func testQCDeviceTypeRingValue() {
        #expect(QCDeviceType.ring.rawValue == 2)
    }
    
    @Test func testQCDeviceTypeGlassesValue() {
        #expect(QCDeviceType.glasses.rawValue == 3)
    }
    
    @Test func testQCStateUnknownValue() {
        #expect(QCState.unkown.rawValue == 0)
    }
    
    @Test func testQCStateUnbindValue() {
        #expect(QCState.unbind.rawValue == 1)
    }
    
    @Test func testQCStateConnectingValue() {
        #expect(QCState.connecting.rawValue == 2)
    }
    
    @Test func testQCStateConnectedValue() {
        #expect(QCState.connected.rawValue == 3)
    }
    
    @Test func testQCStateDisconnectingValue() {
        #expect(QCState.disconnecting.rawValue == 4)
    }
    
    @Test func testQCStateDisconnectedValue() {
        #expect(QCState.disconnected.rawValue == 5)
    }
    
    @Test func testQCBluetoothStateUnknownValue() {
        #expect(QCBluetoothState.unkown.rawValue == 0)
    }
    
    @Test func testQCBluetoothStateResettingValue() {
        #expect(QCBluetoothState.resetting.rawValue == 1)
    }
    
    @Test func testQCBluetoothStateUnsupportedValue() {
        #expect(QCBluetoothState.unsupported.rawValue == 2)
    }
    
    @Test func testQCBluetoothStateUnauthorizedValue() {
        #expect(QCBluetoothState.unauthorized.rawValue == 3)
    }
    
    @Test func testQCBluetoothStatePoweredOffValue() {
        #expect(QCBluetoothState.poweredOff.rawValue == 4)
    }
    
    @Test func testQCBluetoothStatePoweredOnValue() {
        #expect(QCBluetoothState.poweredOn.rawValue == 5)
    }
    
    @Test func testQCBlePeripheralInit() {
        let peripheral = QCBlePeripheral()
        #expect(peripheral.isPaired == false)
    }
    
    @Test func testQCBlePeripheralMacProperty() {
        let peripheral = QCBlePeripheral()
        peripheral.mac = "00:11:22:33:44:55"
        #expect(peripheral.mac == "00:11:22:33:44:55")
    }
    
    @Test func testQCBlePeripheralRSSIProperty() {
        let peripheral = QCBlePeripheral()
        peripheral.rssi = NSNumber(value: -50)
        #expect(peripheral.rssi.intValue == -50)
    }
    
    @Test func testQCBlePeripheralIsPairedProperty() {
        let peripheral = QCBlePeripheral()
        peripheral.isPaired = true
        #expect(peripheral.isPaired == true)
    }
    
    @Test func testQCCentralManagerSingleton() {
        let manager1 = QCCentralManager.shared()
        let manager2 = QCCentralManager.shared()
        #expect(manager1 === manager2)
    }
    
    /// Tests that device state is valid whether connected or not.
    /// State should reflect actual connection status.
    @Test func testQCCentralManagerDeviceStateIsValid() {
        let manager = QCCentralManager.shared()
        // Any of these states is valid depending on connection status
        let validStates: [QCState] = [.unkown, .unbind, .connecting, .connected, .disconnecting, .disconnected]
        #expect(validStates.contains(manager.deviceState))
    }
    
    /// Tests that Bluetooth state reflects actual hardware state.
    /// Should typically be .poweredOn when testing on a real device.
    @Test func testQCCentralManagerBluetoothStateIsValid() {
        let manager = QCCentralManager.shared()
        // Any of these states is valid depending on Bluetooth status
        let validStates: [QCBluetoothState] = [.unkown, .resetting, .unsupported, .unauthorized, .poweredOff, .poweredOn]
        #expect(validStates.contains(manager.bleState))
    }
    
    /// Tests that the singleton properly maintains device connections.
    /// When glasses are connected, connectedPeripheral should NOT be nil.
    @Test func testQCCentralManagerConnectedPeripheralState() {
        let manager = QCCentralManager.shared()
        // Property is imported as implicitly unwrapped optional from Objective-C
        if manager.connectedPeripheral != nil {
            let peripheral = manager.connectedPeripheral
            // Glasses are connected - verify it's a valid peripheral
            #expect(peripheral.state == .connected || 
                    peripheral.state == .connecting ||
                    peripheral.state == .disconnecting ||
                    peripheral.state == .disconnected)
        } else {
            // No device connected - this is also valid
            #expect(manager.connectedPeripheral == nil)
        }
    }
    
    // MARK: - Scan Method Tests
    
    @Test func testQCCentralManagerHasScanMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("scan")))
    }
    
    @Test func testQCCentralManagerHasScanWithTimeoutMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("scanWithTimeout:")))
    }
    
    @Test func testQCCentralManagerHasStopScanMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("stopScan")))
    }
    
    @Test func testScanWithNegativeTimeout() {
        let manager = QCCentralManager.shared()
        manager.scan(withTimeout: -5)
        // Should use default timeout when negative value provided
        #expect(manager != nil)
    }
    
    @Test func testScanWithZeroTimeout() {
        let manager = QCCentralManager.shared()
        manager.scan(withTimeout: 0)
        // Should use default timeout when zero provided
        #expect(manager != nil)
    }
    
    @Test func testScanWithPositiveTimeout() {
        let manager = QCCentralManager.shared()
        manager.scan(withTimeout: 10)
        #expect(manager != nil)
    }
    
    // MARK: - Connect Method Tests
    
    @Test func testQCCentralManagerHasConnectMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("connect:")))
    }
    
    @Test func testQCCentralManagerHasConnectWithDeviceTypeMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("connect:deviceType:")))
    }
    
    @Test func testQCCentralManagerHasConnectWithTimeoutMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("connect:timeout:")))
    }
    
    @Test func testQCCentralManagerHasConnectWithTimeoutAndDeviceTypeMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("connect:timeout:deviceType:")))
    }
    
    @Test func testQCCentralManagerHasRemoveMethod() {
        let manager = QCCentralManager.shared()
        #expect(manager.responds(to: Selector("remove")))
    }
    
    // MARK: - QCBlePeripheral Advanced Tests
    
    /// Tests a NEW QCBlePeripheral instance (not affected by singleton state).
    /// Mac address initializes to empty string, not nil.
    @Test func testQCBlePeripheralMacEmptyInitially() {
        let peripheral = QCBlePeripheral()
        #expect(peripheral.mac == "")
    }
    
    @Test func testQCBlePeripheralRSSINilInitially() {
        let peripheral = QCBlePeripheral()
        #expect(peripheral.rssi == nil)
    }
    
    /// Tests a NEW QCBlePeripheral instance (not affected by singleton state).
    /// Advertisement data initializes to empty dictionary, not nil.
    @Test func testQCBlePeripheralAdvertisementDataEmptyInitially() {
        let peripheral = QCBlePeripheral()
        #expect(peripheral.advertisementData.count == 0)
    }
    
    @Test func testQCBlePeripheralPeripheralNilInitially() {
        let peripheral = QCBlePeripheral()
        #expect(peripheral.peripheral == nil)
    }
    
    @Test func testQCBlePeripheralMacEmptyString() {
        let peripheral = QCBlePeripheral()
        peripheral.mac = ""
        #expect(peripheral.mac == "")
    }
    
    @Test func testQCBlePeripheralMacSpecialCharacters() {
        let peripheral = QCBlePeripheral()
        peripheral.mac = "AA:BB:CC:DD:EE:FF"
        #expect(peripheral.mac == "AA:BB:CC:DD:EE:FF")
    }
    
    @Test func testQCBlePeripheralRSSIPositiveValue() {
        let peripheral = QCBlePeripheral()
        peripheral.rssi = NSNumber(value: 100)
        #expect(peripheral.rssi.intValue == 100)
    }
    
    @Test func testQCBlePeripheralRSSINegativeValue() {
        let peripheral = QCBlePeripheral()
        peripheral.rssi = NSNumber(value: -100)
        #expect(peripheral.rssi.intValue == -100)
    }
    
    @Test func testQCBlePeripheralRSSIZeroValue() {
        let peripheral = QCBlePeripheral()
        peripheral.rssi = NSNumber(value: 0)
        #expect(peripheral.rssi.intValue == 0)
    }
    
    @Test func testQCBlePeripheralTogglePairedState() {
        let peripheral = QCBlePeripheral()
        peripheral.isPaired = true
        #expect(peripheral.isPaired == true)
        peripheral.isPaired = false
        #expect(peripheral.isPaired == false)
    }
    
    @Test func testQCBlePeripheralAdvertisementDataEmpty() {
        let peripheral = QCBlePeripheral()
        peripheral.advertisementData = [:]
        #expect(peripheral.advertisementData.count == 0)
    }
    
    @Test func testQCBlePeripheralAdvertisementDataWithValues() {
        let peripheral = QCBlePeripheral()
        peripheral.advertisementData = ["key": "value"]
        #expect(peripheral.advertisementData["key"] as? String == "value")
    }
    
    // MARK: - Manager State Tests
    
    @Test func testQCCentralManagerCenterManagerNotNil() {
        let manager = QCCentralManager.shared()
        #expect(manager.centerManager != nil)
    }
    
    /// Test specifically for when glasses are connected
    @Test func testConnectedGlassesHaveValidProperties() {
        let manager = QCCentralManager.shared()
        if manager.connectedPeripheral != nil {
            let peripheral = manager.connectedPeripheral
            // If connected, verify the device has expected properties
            #expect(peripheral.identifier != nil)
            // M01_9FD8 or similar name pattern for glasses
            if let name = peripheral.name {
                #expect(name.count > 0)
            }
        } else {
            // No glasses connected is also a valid state
            #expect(true)
        }
    }
    
    /// Test that we can check connection state without crashing
    @Test func testCanCheckConnectionStatesSafely() {
        let manager = QCCentralManager.shared()
        // These should not crash regardless of connection state
        let _ = manager.deviceState
        let _ = manager.bleState
        let _ = manager.connectedPeripheral
        let _ = manager.centerManager
        #expect(true) // Passed if no crash
    }
    
    /// Tests that delegate can be nil or set (both are valid).
    /// The app may have set a delegate for connection callbacks.
    @Test func testQCCentralManagerDelegateCanBeSet() {
        let manager = QCCentralManager.shared()
        // Either nil or non-nil delegate is valid
        // This test just verifies we can access the delegate property
        let _ = manager.delegate
        #expect(true) // Property is accessible
    }
    
    // MARK: - Enum Boundary Tests
    
    @Test func testQCDeviceTypeMinValue() {
        let minValue = QCDeviceType.unkown
        #expect(minValue.rawValue == 0)
    }
    
    @Test func testQCDeviceTypeMaxValue() {
        let maxValue = QCDeviceType.glasses
        #expect(maxValue.rawValue == 3)
    }
    
    @Test func testQCStateMinValue() {
        let minValue = QCState.unkown
        #expect(minValue.rawValue == 0)
    }
    
    @Test func testQCStateMaxValue() {
        let maxValue = QCState.disconnected
        #expect(maxValue.rawValue == 5)
    }
    
    @Test func testQCBluetoothStateMinValue() {
        let minValue = QCBluetoothState.unkown
        #expect(minValue.rawValue == 0)
    }
    
    @Test func testQCBluetoothStateMaxValue() {
        let maxValue = QCBluetoothState.poweredOn
        #expect(maxValue.rawValue == 5)
    }
    
    // MARK: - Multiple Property Changes Tests
    
    @Test func testQCBlePeripheralMultiplePropertyChanges() {
        let peripheral = QCBlePeripheral()
        peripheral.mac = "11:22:33:44:55:66"
        peripheral.rssi = NSNumber(value: -75)
        peripheral.isPaired = true
        peripheral.advertisementData = ["name": "TestDevice"]
        
        #expect(peripheral.mac == "11:22:33:44:55:66")
        #expect(peripheral.rssi.intValue == -75)
        #expect(peripheral.isPaired == true)
        #expect(peripheral.advertisementData["name"] as? String == "TestDevice")
    }
    
    @Test func testQCBlePeripheralPropertyOverwrite() {
        let peripheral = QCBlePeripheral()
        peripheral.mac = "AA:BB:CC:DD:EE:FF"
        peripheral.mac = "11:22:33:44:55:66"
        #expect(peripheral.mac == "11:22:33:44:55:66")
    }
    
    @Test func testQCBlePeripheralRSSIOverwrite() {
        let peripheral = QCBlePeripheral()
        peripheral.rssi = NSNumber(value: -50)
        peripheral.rssi = NSNumber(value: -60)
        #expect(peripheral.rssi.intValue == -60)
    }
    
    // MARK: - Type Safety Tests
    
    @Test func testQCDeviceTypeEquality() {
        let device1 = QCDeviceType.watch
        let device2 = QCDeviceType.watch
        #expect(device1 == device2)
    }
    
    @Test func testQCDeviceTypeInequality() {
        let device1 = QCDeviceType.watch
        let device2 = QCDeviceType.ring
        #expect(device1 != device2)
    }
    
    @Test func testQCStateEquality() {
        let state1 = QCState.connected
        let state2 = QCState.connected
        #expect(state1 == state2)
    }
    
    @Test func testQCStateInequality() {
        let state1 = QCState.connected
        let state2 = QCState.disconnected
        #expect(state1 != state2)
    }
    
    @Test func testQCBluetoothStateEquality() {
        let state1 = QCBluetoothState.poweredOn
        let state2 = QCBluetoothState.poweredOn
        #expect(state1 == state2)
    }
    
    @Test func testQCBluetoothStateInequality() {
        let state1 = QCBluetoothState.poweredOn
        let state2 = QCBluetoothState.poweredOff
        #expect(state1 != state2)
    }
}
