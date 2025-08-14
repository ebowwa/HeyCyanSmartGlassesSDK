//
//  QCBluetoothStreamingTests.swift
//  QCSDKDemoTests
//
//  Tests for evaluating AI image capture as a pseudo-streaming solution
//

import Testing
import Foundation
import CoreBluetooth
@testable import QCSDK

/// Tests to evaluate using rapid AI image captures for pseudo-video streaming
///
/// PREREQUISITES:
/// - HeyCyan glasses must be powered on and connected
/// - Bluetooth must be enabled
/// - Test will measure actual throughput and frame rates
///
/// HYPOTHESIS:
/// - AI images are thumbnails (likely 100-200KB)
/// - Bluetooth bandwidth limits transfer to ~1-2 seconds per image
/// - Best case: 0.5 FPS, worst case: 0.2 FPS
struct QCBluetoothStreamingTests {
    
    // MARK: - Single Frame Performance
    
    /// Test capturing a single AI image and measure timing
    @Test func testSingleAIImageTiming() async throws {
        let manager = QCCentralManager.shared()
        
        // Ensure we're connected
        #expect(manager.connectedPeripheral != nil, "Must be connected to glasses")
        #expect(manager.deviceState == .connected, "Must be in connected state")
        
        // Set up delegate to receive AI image
        class TimingDelegate: NSObject, QCSDKManagerDelegate {
            var imageData: Data?
            var captureStartTime: Date?
            var captureEndTime: Date?
            var expectation: TestExpectation?
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                self.imageData = imageData
                self.captureEndTime = Date()
                expectation?.fulfill()
            }
        }
        
        let delegate = TimingDelegate()
        let expectation = TestExpectation()
        delegate.expectation = expectation
        
        let sdkManager = QCSDKManager()
        sdkManager.delegate = delegate
        
        // Trigger AI image capture
        delegate.captureStartTime = Date()
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("AI Photo mode triggered at \(Date())")
        }, fail: { errorCode in
            print("Failed to trigger AI Photo: \(errorCode)")
        })
        
        // Wait for image (max 10 seconds)
        await expectation.wait(timeout: 10)
        
        // Calculate timing
        if let startTime = delegate.captureStartTime,
           let endTime = delegate.captureEndTime,
           let imageData = delegate.imageData {
            
            let captureTime = endTime.timeIntervalSince(startTime)
            let imageSizeKB = Double(imageData.count) / 1024.0
            let throughputKBps = imageSizeKB / captureTime
            
            print("📊 Single AI Image Capture Metrics:")
            print("   - Total time: \(String(format: "%.2f", captureTime)) seconds")
            print("   - Image size: \(String(format: "%.1f", imageSizeKB)) KB")
            print("   - Throughput: \(String(format: "%.1f", throughputKBps)) KB/s")
            print("   - Theoretical FPS: \(String(format: "%.2f", 1.0/captureTime))")
            
            // Verify our hypothesis
            #expect(captureTime >= 1.0, "Capture should take at least 1 second")
            #expect(imageSizeKB < 500, "Thumbnail should be less than 500KB")
            #expect(captureTime < 10.0, "Capture should complete within 10 seconds")
        } else {
            Issue.record("Failed to capture AI image")
        }
    }
    
    // MARK: - Rapid Capture Test
    
    /// Test rapid successive AI image captures to measure actual frame rate
    @Test func testRapidAIImageCapture() async throws {
        let manager = QCCentralManager.shared()
        
        #expect(manager.connectedPeripheral != nil, "Must be connected")
        
        class StreamingDelegate: NSObject, QCSDKManagerDelegate {
            var capturedFrames: [(data: Data, timestamp: Date)] = []
            var isCapturing = false
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                capturedFrames.append((data: imageData, timestamp: Date()))
                isCapturing = false
            }
        }
        
        let delegate = StreamingDelegate()
        let sdkManager = QCSDKManager()
        sdkManager.delegate = delegate
        
        let testDuration: TimeInterval = 30.0 // Test for 30 seconds
        let captureInterval: TimeInterval = 2.0 // Try to capture every 2 seconds
        
        print("🎬 Starting rapid capture test for \(Int(testDuration)) seconds...")
        print("   Target interval: \(captureInterval) seconds")
        
        let startTime = Date()
        var captureAttempts = 0
        
        while Date().timeIntervalSince(startTime) < testDuration {
            if !delegate.isCapturing {
                delegate.isCapturing = true
                captureAttempts += 1
                
                QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
                    // Capture triggered
                }, fail: { _ in
                    delegate.isCapturing = false
                })
            }
            
            // Wait before next attempt
            try await Task.sleep(nanoseconds: UInt64(captureInterval * 1_000_000_000))
        }
        
        // Analyze results
        let actualDuration = Date().timeIntervalSince(startTime)
        let framesReceived = delegate.capturedFrames.count
        
        print("\n📊 Rapid Capture Test Results:")
        print("   - Test duration: \(String(format: "%.1f", actualDuration)) seconds")
        print("   - Capture attempts: \(captureAttempts)")
        print("   - Frames received: \(framesReceived)")
        print("   - Success rate: \(String(format: "%.1f%%", Double(framesReceived)/Double(captureAttempts) * 100))")
        
        if framesReceived > 1 {
            // Calculate actual frame rate
            let averageFPS = Double(framesReceived) / actualDuration
            print("   - Average FPS: \(String(format: "%.3f", averageFPS))")
            
            // Calculate inter-frame intervals
            var intervals: [TimeInterval] = []
            for i in 1..<delegate.capturedFrames.count {
                let interval = delegate.capturedFrames[i].timestamp.timeIntervalSince(
                    delegate.capturedFrames[i-1].timestamp
                )
                intervals.append(interval)
            }
            
            if !intervals.isEmpty {
                let avgInterval = intervals.reduce(0, +) / Double(intervals.count)
                let minInterval = intervals.min() ?? 0
                let maxInterval = intervals.max() ?? 0
                
                print("   - Avg frame interval: \(String(format: "%.2f", avgInterval)) seconds")
                print("   - Min frame interval: \(String(format: "%.2f", minInterval)) seconds")
                print("   - Max frame interval: \(String(format: "%.2f", maxInterval)) seconds")
            }
            
            // Calculate data statistics
            let totalDataKB = delegate.capturedFrames.reduce(0) { $0 + $1.data.count } / 1024
            let avgFrameSizeKB = totalDataKB / framesReceived
            
            print("   - Total data: \(totalDataKB) KB")
            print("   - Avg frame size: \(avgFrameSizeKB) KB")
            print("   - Data rate: \(String(format: "%.1f", Double(totalDataKB)/actualDuration)) KB/s")
            
            // Verify our hypothesis
            #expect(averageFPS < 1.0, "FPS should be less than 1 (as expected)")
            #expect(averageFPS > 0.1, "FPS should be at least 0.1 (minimum viable)")
        }
    }
    
    // MARK: - Stress Test
    
    /// Test what happens when we request AI images as fast as possible
    @Test func testMaximumCaptureRate() async throws {
        let manager = QCCentralManager.shared()
        
        #expect(manager.connectedPeripheral != nil, "Must be connected")
        
        class StressDelegate: NSObject, QCSDKManagerDelegate {
            var capturedCount = 0
            var errorCount = 0
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                capturedCount += 1
            }
        }
        
        let delegate = StressDelegate()
        let sdkManager = QCSDKManager()
        sdkManager.delegate = delegate
        
        print("🔥 Starting stress test - requesting AI images as fast as possible...")
        
        let testDuration: TimeInterval = 10.0 // Shorter test to avoid overheating
        let startTime = Date()
        var requestCount = 0
        
        while Date().timeIntervalSince(startTime) < testDuration {
            requestCount += 1
            
            QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
                // Success
            }, fail: { _ in
                delegate.errorCount += 1
            })
            
            // Minimal delay to avoid completely flooding
            try await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
        }
        
        // Wait a bit for final responses
        try await Task.sleep(nanoseconds: 2_000_000_000) // 2 seconds
        
        let actualDuration = Date().timeIntervalSince(startTime)
        
        print("\n📊 Stress Test Results:")
        print("   - Requests sent: \(requestCount)")
        print("   - Images received: \(delegate.capturedCount)")
        print("   - Errors: \(delegate.errorCount)")
        print("   - Effective FPS: \(String(format: "%.3f", Double(delegate.capturedCount)/actualDuration))")
        print("   - Request rate: \(String(format: "%.1f", Double(requestCount)/actualDuration)) req/s")
        
        // Check if glasses throttle rapid requests
        let captureRatio = Double(delegate.capturedCount) / Double(requestCount)
        print("   - Capture success ratio: \(String(format: "%.1f%%", captureRatio * 100))")
        
        if captureRatio < 0.5 {
            print("   ⚠️ Glasses appear to throttle rapid AI image requests")
        }
        
        #expect(delegate.capturedCount > 0, "Should capture at least some images")
        #expect(delegate.capturedCount < requestCount, "Glasses should throttle excessive requests")
    }
    
    // MARK: - Viability Assessment
    
    /// Final test to determine if AI image streaming is viable
    @Test func testStreamingViability() async throws {
        print("\n🎯 STREAMING VIABILITY ASSESSMENT")
        print(String(repeating: "=", count: 50))
        
        // Run a 20-second streaming simulation
        let manager = QCCentralManager.shared()
        #expect(manager.connectedPeripheral != nil, "Must be connected")
        
        class ViabilityDelegate: NSObject, QCSDKManagerDelegate {
            var frames: [Data] = []
            var isCapturing = false
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                frames.append(imageData)
                isCapturing = false
            }
        }
        
        let delegate = ViabilityDelegate()
        let sdkManager = QCSDKManager()
        sdkManager.delegate = delegate
        
        let testDuration: TimeInterval = 20.0
        let targetFPS = 1.0 // Our target is 1 FPS
        let captureInterval = 1.0 / targetFPS
        
        print("Testing \(Int(testDuration))-second stream at target \(targetFPS) FPS...")
        
        let startTime = Date()
        var attempts = 0
        
        while Date().timeIntervalSince(startTime) < testDuration {
            if !delegate.isCapturing {
                delegate.isCapturing = true
                attempts += 1
                
                QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {}, fail: { _ in
                    delegate.isCapturing = false
                })
            }
            
            try await Task.sleep(nanoseconds: UInt64(captureInterval * 1_000_000_000))
        }
        
        // Wait for final frames
        try await Task.sleep(nanoseconds: 2_000_000_000)
        
        // Calculate results
        let actualFPS = Double(delegate.frames.count) / testDuration
        let successRate = Double(delegate.frames.count) / Double(attempts) * 100
        
        print("\n📊 VIABILITY RESULTS:")
        print("   Target FPS: \(targetFPS)")
        print("   Achieved FPS: \(String(format: "%.3f", actualFPS))")
        print("   Success rate: \(String(format: "%.1f%%", successRate))")
        
        // Determine viability
        let isViable = actualFPS >= 0.5 // At least 0.5 FPS
        
        if isViable {
            print("\n✅ VERDICT: Marginally viable for slow monitoring use cases")
            print("   - Suitable for: Security monitoring, time-lapse viewing")
            print("   - NOT suitable for: Real-time interaction, video calls")
        } else {
            print("\n❌ VERDICT: Not viable for streaming")
            print("   - Frame rate too low for any practical use")
            print("   - Consider alternative approaches")
        }
        
        print("\n💡 RECOMMENDATIONS:")
        if actualFPS < 0.5 {
            print("   1. This approach is too slow for streaming")
            print("   2. Wait for proper streaming SDK support")
            print("   3. Consider using regular photo capture + WiFi download")
        } else if actualFPS < 1.0 {
            print("   1. Only use for non-critical monitoring")
            print("   2. Implement aggressive frame buffering")
            print("   3. Set user expectations about delay")
        } else {
            print("   1. Implement with heavy caching")
            print("   2. Use for preview/monitoring only")
            print("   3. Provide WiFi option for better quality")
        }
        
        #expect(actualFPS > 0.1, "Should achieve at least 0.1 FPS")
    }
}