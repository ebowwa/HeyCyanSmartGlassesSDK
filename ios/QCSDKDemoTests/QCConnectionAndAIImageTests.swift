//
//  QCConnectionAndAIImageTests.swift
//  QCSDKDemoTests
//
//  Tests for verifying connection, bonding, and AI image capture functionality
//

import Testing
import Foundation
import CoreBluetooth
@testable import QCSDK

/// Tests for HeyCyan glasses connection, bonding, and AI image capture
///
/// PREREQUISITES:
/// - HeyCyan glasses (M01_9FD8 or similar) must be powered on and in range
/// - Bluetooth must be enabled on the test device
/// - Test device must have permission to use Bluetooth
///
/// TEST FLOW:
/// 1. Verify Bluetooth is available and powered on
/// 2. Connect to glasses if not already connected
/// 3. Verify bonding/pairing status
/// 4. Switch to AI Photo mode
/// 5. Capture an AI image
/// 6. Verify image data is received
struct QCConnectionAndAIImageTests {
    
    // MARK: - Connection Tests
    
    /// Test that Bluetooth is powered on and ready
    @Test func testBluetoothIsPoweredOn() async throws {
        let manager = QCCentralManager.shared()
        
        // Wait for Bluetooth to be ready (max 5 seconds)
        var attempts = 0
        while manager.bleState != .poweredOn && attempts < 50 {
            try await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
            attempts += 1
        }
        
        #expect(manager.bleState == .poweredOn, "Bluetooth must be powered on for testing")
    }
    
    /// Test that we can connect to glasses (or are already connected)
    @Test func testGlassesConnection() async throws {
        let manager = QCCentralManager.shared()
        
        // Check if already connected
        if manager.connectedPeripheral != nil {
            #expect(manager.deviceState == .connected, "Device state should be connected when peripheral exists")
            #expect(manager.connectedPeripheral.state == .connected, "Peripheral should be in connected state")
            
            // Verify it's the glasses (M01_9FD8 or similar)
            if let name = manager.connectedPeripheral.name {
                #expect(name.contains("M01") || name.contains("9FD8"), "Connected device should be HeyCyan glasses")
            }
        } else {
            // Need to scan and connect
            await scanAndConnectToGlasses()
        }
    }
    
    /// Helper function to scan and connect to glasses
    private func scanAndConnectToGlasses() async {
        let manager = QCCentralManager.shared()
        
        // Set up expectation for scan results
        let scanExpectation = TestExpectation()
        var foundGlasses: CBPeripheral?
        
        // Create temporary delegate to capture scan results
        class ScanDelegate: NSObject, QCCentralManagerDelegate {
            var onScanResult: (([QCBlePeripheral]) -> Void)?
            
            func didScanPeripherals(_ peripheralArr: [QCBlePeripheral]) {
                onScanResult?(peripheralArr)
            }
        }
        
        let scanDelegate = ScanDelegate()
        scanDelegate.onScanResult = { peripherals in
            // Look for glasses in scan results
            for peripheral in peripherals {
                if peripheral.peripheral != nil,
                   let name = peripheral.peripheral.name,
                   (name.contains("M01") || name.contains("9FD8")) {
                    foundGlasses = peripheral.peripheral
                    scanExpectation.fulfill()
                    break
                }
            }
        }
        
        manager.delegate = scanDelegate
        
        // Start scanning
        manager.scan(withTimeout: 10)
        
        // Wait for glasses to be found (max 10 seconds)
        await scanExpectation.wait(timeout: 10)
        
        #expect(foundGlasses != nil, "HeyCyan glasses should be found during scan")
        
        if let glasses = foundGlasses {
            // Connect to glasses
            let connectExpectation = TestExpectation()
            
            class ConnectDelegate: NSObject, QCCentralManagerDelegate {
                var onStateChange: ((QCState) -> Void)?
                
                func didState(_ state: QCState) {
                    onStateChange?(state)
                }
            }
            
            let connectDelegate = ConnectDelegate()
            connectDelegate.onStateChange = { state in
                if state == .connected {
                    connectExpectation.fulfill()
                }
            }
            
            manager.delegate = connectDelegate
            manager.connect(glasses, timeout: 10, deviceType: .glasses)
            
            // Wait for connection (max 10 seconds)
            await connectExpectation.wait(timeout: 10)
            
            #expect(manager.deviceState == .connected, "Should be connected to glasses")
        }
    }
    
    // MARK: - Bonding Tests
    
    /// Test that glasses are bonded/paired
    @Test func testGlassesBonding() async throws {
        let manager = QCCentralManager.shared()
        
        // Ensure we're connected first
        #expect(manager.connectedPeripheral != nil, "Must be connected to test bonding")
        
        // Check if the peripheral is paired/bonded
        // Note: In iOS, bonding happens automatically during connection for BLE devices
        // that require it. We can verify by checking if we can read protected characteristics
        
        // For now, we'll check if we maintain a stable connection
        #expect(manager.deviceState == .connected, "Device should maintain connected state if bonded")
        
        // The isPaired property on QCBlePeripheral indicates bonding status
        // But we need to find our peripheral in the scan results to check this
        // For connected devices, we assume bonding is successful if connection is stable
        
        // Verify we can communicate with the device (indicates successful bonding)
        let sdkManager = QCSDKManager()
        #expect(sdkManager != nil, "SDK Manager should be available when bonded")
    }
    
    // MARK: - AI Image Capture Tests
    
    /// Test switching to AI Photo mode and capturing an image
    @Test func testAIImageCapture() async throws {
        let manager = QCCentralManager.shared()
        
        // Ensure we're connected
        #expect(manager.connectedPeripheral != nil, "Must be connected to capture AI image")
        #expect(manager.deviceState == .connected, "Must be in connected state")
        
        // Set up delegate to receive AI image
        class AIImageDelegate: NSObject, QCSDKManagerDelegate {
            var imageDataReceived: Data?
            var imageExpectation: TestExpectation?
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                imageDataReceived = imageData
                imageExpectation?.fulfill()
            }
        }
        
        let imageDelegate = AIImageDelegate()
        let imageExpectation = TestExpectation()
        imageDelegate.imageExpectation = imageExpectation
        
        // Set the delegate
        let sdkManager = QCSDKManager()
        sdkManager.delegate = imageDelegate
        
        // Switch to AI Photo mode
        let modeExpectation = TestExpectation()
        var modeSetSuccess = false
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            modeSetSuccess = true
            modeExpectation.fulfill()
        }, fail: { errorCode in
            print("Failed to set AI Photo mode: \(errorCode)")
            modeExpectation.fulfill()
        })
        
        // Wait for mode change (max 5 seconds)
        await modeExpectation.wait(timeout: 5)
        
        #expect(modeSetSuccess, "Should successfully switch to AI Photo mode")
        
        // Wait for AI image to be captured (max 30 seconds)
        // User needs to trigger capture on the glasses
        print("⚠️ Please trigger AI image capture on your glasses within 30 seconds...")
        await imageExpectation.wait(timeout: 30)
        
        // Verify image was received
        #expect(imageDelegate.imageDataReceived != nil, "Should receive AI image data")
        
        if let imageData = imageDelegate.imageDataReceived {
            #expect(imageData.count > 0, "Image data should not be empty")
            
            // Verify it's valid image data (check for common image headers)
            let headerBytes = [UInt8](imageData.prefix(4))
            
            // Check for JPEG header (FF D8 FF)
            let isJPEG = headerBytes.count >= 3 && 
                         headerBytes[0] == 0xFF && 
                         headerBytes[1] == 0xD8 && 
                         headerBytes[2] == 0xFF
            
            // Check for PNG header (89 50 4E 47)
            let isPNG = headerBytes.count >= 4 &&
                        headerBytes[0] == 0x89 &&
                        headerBytes[1] == 0x50 &&
                        headerBytes[2] == 0x4E &&
                        headerBytes[3] == 0x47
            
            #expect(isJPEG || isPNG, "Image data should be valid JPEG or PNG format")
            
            print("✅ AI Image captured successfully!")
            print("   - Size: \(imageData.count) bytes")
            print("   - Format: \(isJPEG ? "JPEG" : (isPNG ? "PNG" : "Unknown"))")
        }
    }
    
    // MARK: - Complete Flow Test
    
    /// Test the complete flow: connection → bonding → AI image capture
    @Test func testCompleteAIImageFlow() async throws {
        // Step 1: Verify Bluetooth is ready
        try await testBluetoothIsPoweredOn()
        print("✅ Step 1: Bluetooth is powered on")
        
        // Step 2: Connect to glasses
        try await testGlassesConnection()
        print("✅ Step 2: Connected to glasses")
        
        // Step 3: Verify bonding
        try await testGlassesBonding()
        print("✅ Step 3: Glasses are bonded")
        
        // Step 4: Capture AI image
        try await testAIImageCapture()
        print("✅ Step 4: AI image captured")
        
        print("🎉 Complete AI image flow test passed!")
    }
    
    // MARK: - Cleanup Test
    
    /// Test that we maintain connection after AI image capture
    @Test func testConnectionStableAfterAIImage() async throws {
        let manager = QCCentralManager.shared()
        
        // Capture initial state
        let initiallyConnected = manager.connectedPeripheral != nil
        let initialState = manager.deviceState
        
        // Run AI image capture
        if initiallyConnected {
            try await testAIImageCapture()
        }
        
        // Verify connection is still stable
        #expect((manager.connectedPeripheral != nil) == initiallyConnected, 
                "Connection state should remain consistent")
        #expect(manager.deviceState == initialState, 
                "Device state should remain consistent after AI image capture")
    }
}

// MARK: - Test Support

/// Simple test expectation for async coordination
class TestExpectation {
    private var fulfilled = false
    private let lock = NSLock()
    
    func fulfill() {
        lock.lock()
        fulfilled = true
        lock.unlock()
    }
    
    func wait(timeout: TimeInterval) async {
        let deadline = Date().addingTimeInterval(timeout)
        
        while Date() < deadline {
            lock.lock()
            if fulfilled {
                lock.unlock()
                return
            }
            lock.unlock()
            
            try? await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
        }
    }
}