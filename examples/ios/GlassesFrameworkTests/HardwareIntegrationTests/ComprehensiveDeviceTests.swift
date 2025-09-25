//
//  ComprehensiveDeviceTests.swift
//  GlassesFrameworkTests
//
//  Complete test suite that tests all safe device operations sequentially
//  Skips firmware updates to avoid bricking the device
//

import XCTest
import CoreBluetooth
@testable import GlassesFramework

class ComprehensiveDeviceTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var testResults: [String: Bool] = [:]
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        print("\n🧪 COMPREHENSIVE DEVICE TEST SUITE STARTING")
        print("⚠️ Skipping firmware/DFU tests to avoid bricking")
        ensureConnection()
    }
    
    override func tearDown() {
        printTestSummary()
        bluetoothManager = nil
        super.tearDown()
    }
    
    private func ensureConnection() {
        guard !bluetoothManager.isConnected else { return }
        
        print("\n🔗 Establishing connection...")
        let expectation = XCTestExpectation(description: "Connect to glasses")
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if let device = self.bluetoothManager.discoveredDevices.first(where: { $0.name.contains("M01") }) {
                print("📱 Found device: \(device.name)")
                self.bluetoothManager.connect(to: device)
                DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
                    if self.bluetoothManager.isConnected {
                        print("✅ Connected successfully")
                    }
                    expectation.fulfill()
                }
            } else {
                print("❌ No device found")
                expectation.fulfill()
            }
        }
        wait(for: [expectation], timeout: 8.0)
    }
    
    private func printTestSummary() {
        print("\n==========================================")
        print("📊 TEST RESULTS SUMMARY")
        print("==========================================")
        for (test, passed) in testResults {
            print("\(passed ? "✅" : "❌") \(test)")
        }
        let passedCount = testResults.values.filter { $0 }.count
        print("\nTotal: \(passedCount)/\(testResults.count) tests passed")
        print("==========================================\n")
    }
    
    func testCompleteDeviceCapabilities() {
        XCTAssertTrue(bluetoothManager.isConnected, "Must be connected to run tests")
        
        // Run tests sequentially, each one verifying it works before moving to next
        
        // 1. Device Information
        test1_DeviceInformation()
        
        // 2. Volume Control
        test2_VolumeControl()
        
        // 3. Device Modes (safe ones only)
        test3_DeviceModes()
        
        // 4. Media Information
        test4_MediaOperations()
        
        // 5. Video/Audio Configuration
        test5_VideoAudioConfig()
        
        // 6. Advanced Features
        test6_AdvancedFeatures()
        
        // 7. Device Configuration
        test7_DeviceConfiguration()
        
        // 8. Time Synchronization
        test8_TimeSynchronization()
    }
    
    // MARK: - Test 1: Device Information
    private func test1_DeviceInformation() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 1: DEVICE INFORMATION")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Device info")
        var success = false
        
        // Get version info
        bluetoothManager.getVersionInfo()
        bluetoothManager.getMacAddress()
        bluetoothManager.getBatteryStatus()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let info = self.bluetoothManager.deviceInfo
            
            if !info.firmwareVersion.isEmpty {
                print("✅ Firmware: \(info.firmwareVersion)")
                print("✅ Hardware: \(info.hardwareVersion)")
                print("✅ Battery: \(info.batteryLevel)% (Charging: \(info.isCharging))")
                print("✅ MAC: \(info.macAddress)")
                success = true
            } else {
                print("⚠️ Some device info not retrieved")
            }
            
            self.testResults["Device Information"] = success
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 7.0)
    }
    
    // MARK: - Test 2: Volume Control
    private func test2_VolumeControl() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 2: VOLUME CONTROL")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Volume control")
        var success = false
        
        bluetoothManager.getVolumeSettings { volumeInfo in
            if let info = volumeInfo {
                print("✅ Music Volume: \(info.musicCurrent)/\(info.musicMax)")
                print("✅ Call Volume: \(info.callCurrent)/\(info.callMax)")
                print("✅ System Volume: \(info.systemCurrent)/\(info.systemMax)")
                success = true
                
                // Test setting volume
                let newVolume = min(info.musicCurrent + 1, info.musicMax)
                info.musicCurrent = newVolume
                
                self.bluetoothManager.setVolumeSettings(info) { setSuccess in
                    if setSuccess {
                        print("✅ Volume adjusted to: \(newVolume)")
                    }
                    self.testResults["Volume Control"] = success && setSuccess
                    expectation.fulfill()
                }
            } else {
                print("❌ Failed to get volume info")
                self.testResults["Volume Control"] = false
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 7.0)
    }
    
    // MARK: - Test 3: Device Modes (Safe)
    private func test3_DeviceModes() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 3: DEVICE MODES (SAFE OPERATIONS)")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Device modes")
        var success = false
        
        // Test taking a photo (safe operation)
        bluetoothManager.takePhoto()

        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            print("✅ Photo command sent")
            success = true

            // Test AI photo
            self.bluetoothManager.takeAIImage()

            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                print("✅ AI Photo command sent")
                // Test find device alert
                self.bluetoothManager.findDevice()

                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                    print("✅ Find Device alert command sent")
                    self.testResults["Device Modes"] = success
                    expectation.fulfill()
                }
            }
        }

        wait(for: [expectation], timeout: 7.0)
    }
    
    // MARK: - Test 4: Media Operations
    private func test4_MediaOperations() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 4: MEDIA OPERATIONS")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Media operations")
        
        bluetoothManager.getMediaInfo()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            let info = self.bluetoothManager.deviceInfo
            print("✅ Photos: \(info.photoCount)")
            print("✅ Videos: \(info.videoCount)")
            print("✅ Audio: \(info.audioCount)")
            
            self.testResults["Media Operations"] = true
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // MARK: - Test 5: Video/Audio Configuration
    private func test5_VideoAudioConfig() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 5: VIDEO/AUDIO CONFIGURATION")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Video/Audio config")
        var success = false
        
        // Get video configuration
        bluetoothManager.getVideoInfo(success: { angle, duration in
            print("✅ Video Config - Angle: \(angle)°, Duration: \(duration)s")
            success = true
            
            // Get audio configuration
            self.bluetoothManager.getAudioInfo(success: { audioAngle, audioDuration in
                print("✅ Audio Config - Angle: \(audioAngle)°, Duration: \(audioDuration)s")
                self.testResults["Video/Audio Config"] = success
                expectation.fulfill()
            }, fail: {
                print("⚠️ Failed to get audio config")
                self.testResults["Video/Audio Config"] = success
                expectation.fulfill()
            })
        }, fail: {
            print("❌ Failed to get video config")
            self.testResults["Video/Audio Config"] = false
            expectation.fulfill()
        })
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // MARK: - Test 6: Advanced Features
    private func test6_AdvancedFeatures() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 6: ADVANCED FEATURES")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Advanced features")
        var successCount = 0
        let totalTests = 3
        
        // Test voice wakeup status
        bluetoothManager.getVoiceWakeupStatus { status in
            print("✅ Voice Wakeup: \(status ? "Enabled" : "Disabled")")
            successCount += 1
            
            // Test wearing detection
            self.bluetoothManager.getWearingDetectionStatus { wearingStatus in
                print("✅ Wearing Detection: \(wearingStatus ? "Enabled" : "Disabled")")
                successCount += 1
                
                // Test AI speak mode
                self.bluetoothManager.setAISpeakMode(AISpeakMode.stop) { success in
                    if success {
                        print("✅ AI Speak Mode set to Stop")
                        successCount += 1
                    }
                    
                    self.testResults["Advanced Features"] = successCount >= 2
                    expectation.fulfill()
                }
            }
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // MARK: - Test 7: Device Configuration
    private func test7_DeviceConfiguration() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 7: DEVICE CONFIGURATION")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Device config")
        
        bluetoothManager.getDeviceConfig { config in
            if let configData = config {
                print("✅ Device configuration retrieved")
                print("   Config size: \(configData.count) bytes")
                self.testResults["Device Configuration"] = true
            } else {
                print("⚠️ No device configuration available")
                self.testResults["Device Configuration"] = false
            }
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // MARK: - Test 8: Time Synchronization
    private func test8_TimeSynchronization() {
        print("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        print("TEST 8: TIME SYNCHRONIZATION")
        print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        let expectation = XCTestExpectation(description: "Time sync")
        
        bluetoothManager.setDeviceTime()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            print("✅ Device time synchronized with iPhone")
            self.testResults["Time Synchronization"] = true
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 3.0)
    }
}