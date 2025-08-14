//
//  QCAIPhotoLongRunningTest.swift
//  QCSDKDemoTests
//
//  Long-running AI Photo streaming test with visual feedback
//

import Testing
import Foundation
import CoreBluetooth
import UIKit

/// Long-running test to observe AI Photo streaming behavior
struct QCAIPhotoLongRunningTest {
    
    /// Run AI photo capture for several minutes with detailed output
    @Test func testLongRunningAIPhotoCapture() async throws {
        let manager = QCCentralManager.shared()
        
        guard manager.connectedPeripheral != nil else {
            print("❌ Glasses not connected - cannot run test")
            return
        }
        
        print("\n")
        print("╔════════════════════════════════════════════════════════════╗")
        print("║         🎬 LONG-RUNNING AI PHOTO CAPTURE TEST 🎬          ║")
        print("║                                                            ║")
        print("║  This test will run for 3 minutes attempting to capture   ║")
        print("║  AI photos continuously. Watch for:                       ║")
        print("║  • Camera sounds from glasses                             ║")
        print("║  • Image previews in the app                              ║")
        print("║  • Frame reception messages below                         ║")
        print("╚════════════════════════════════════════════════════════════╝")
        print("\n")
        
        // Set up delegate to track all received images
        class LongRunningDelegate: NSObject, QCSDKManagerDelegate {
            var imageCount = 0
            var totalBytes = 0
            var firstImageTime: Date?
            var lastImageTime: Date?
            var imageFormats: [String: Int] = [:]
            
            func didReceiveAIChatImageData(_ imageData: Data) {
                imageCount += 1
                totalBytes += imageData.count
                
                if firstImageTime == nil {
                    firstImageTime = Date()
                }
                lastImageTime = Date()
                
                // Detect format
                let format = detectImageFormat(imageData)
                imageFormats[format, default: 0] += 1
                
                // Print detailed frame info
                print("\n┌─────────────────────────────────────────────────")
                print("│ 📸 FRAME #\(imageCount) RECEIVED")
                print("├─────────────────────────────────────────────────")
                print("│ Time: \(DateFormatter.localizedString(from: Date(), dateStyle: .none, timeStyle: .medium))")
                print("│ Size: \(formatBytes(imageData.count))")
                print("│ Format: \(format)")
                
                if let firstTime = firstImageTime {
                    let elapsed = Date().timeIntervalSince(firstTime)
                    let fps = imageCount > 1 ? Double(imageCount - 1) / elapsed : 0
                    print("│ Elapsed: \(String(format: "%.1f", elapsed)) seconds")
                    print("│ Average FPS: \(String(format: "%.2f", fps))")
                }
                
                // Show progress bar
                let progress = min(imageCount, 50)
                let progressBar = String(repeating: "█", count: progress) + String(repeating: "░", count: max(0, 50 - progress))
                print("│ Progress: [\(progressBar)]")
                print("└─────────────────────────────────────────────────")
                
                // Every 10 frames, show summary
                if imageCount % 10 == 0 {
                    printSummary()
                }
            }
            
            func detectImageFormat(_ data: Data) -> String {
                guard data.count >= 4 else { return "Unknown" }
                let header = [UInt8](data.prefix(4))
                
                if header[0] == 0xFF && header[1] == 0xD8 && header[2] == 0xFF {
                    return "JPEG"
                } else if header[0] == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47 {
                    return "PNG"
                } else {
                    return "Unknown"
                }
            }
            
            func formatBytes(_ bytes: Int) -> String {
                if bytes < 1024 {
                    return "\(bytes) bytes"
                } else if bytes < 1024 * 1024 {
                    return String(format: "%.1f KB", Double(bytes) / 1024)
                } else {
                    return String(format: "%.1f MB", Double(bytes) / (1024 * 1024))
                }
            }
            
            func printSummary() {
                print("\n╔════════════════════════════════════════════════╗")
                print("║                  📊 SUMMARY                    ║")
                print("╠════════════════════════════════════════════════╣")
                print("║ Total Images: \(String(format: "%-33d", imageCount)) ║")
                print("║ Total Data: \(String(format: "%-35s", formatBytes(totalBytes))) ║")
                
                if let firstTime = firstImageTime, let lastTime = lastImageTime {
                    let duration = lastTime.timeIntervalSince(firstTime)
                    print("║ Duration: \(String(format: "%-37.1f", duration)) sec ║")
                    
                    if duration > 0 {
                        let avgFPS = Double(imageCount - 1) / duration
                        print("║ Avg FPS: \(String(format: "%-38.2f", avgFPS)) ║")
                    }
                }
                
                for (format, count) in imageFormats {
                    print("║ \(format): \(String(format: "%-41d", count)) ║")
                }
                
                print("╚════════════════════════════════════════════════╝")
            }
        }
        
        let longDelegate = LongRunningDelegate()
        let sdkManager = QCSDKManager()
        sdkManager.delegate = longDelegate
        
        // Phase 1: Activate AI Photo Mode
        print("\n🔧 PHASE 1: Activating AI Photo Mode...")
        print("────────────────────────────────────────")
        
        let modeExp = LongRunExpectation()
        var modeActivated = false
        
        QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
            print("✅ AI Photo mode activated successfully")
            modeActivated = true
            modeExp.fulfill()
        }, fail: { errorCode in
            print("❌ Failed to activate AI Photo mode: \(errorCode)")
            modeExp.fulfill()
        })
        
        await modeExp.wait(timeout: 5)
        
        guard modeActivated else {
            print("\n❌ Cannot proceed - AI Photo mode activation failed")
            return
        }
        
        // Wait for mode to stabilize
        print("⏳ Waiting for mode to stabilize...")
        try await Task.sleep(nanoseconds: 3_000_000_000)
        
        // Phase 2: Continuous Capture Loop
        print("\n🎯 PHASE 2: Starting Continuous Capture")
        print("────────────────────────────────────────")
        print("Duration: 3 minutes")
        print("Method: Periodic mode refresh + WiFi triggers")
        print("\nStarting in 3...")
        try await Task.sleep(nanoseconds: 1_000_000_000)
        print("2...")
        try await Task.sleep(nanoseconds: 1_000_000_000)
        print("1...")
        try await Task.sleep(nanoseconds: 1_000_000_000)
        print("📸 GO!\n")
        
        let testDuration: TimeInterval = 180 // 3 minutes
        let startTime = Date()
        var cycleCount = 0
        
        while Date().timeIntervalSince(startTime) < testDuration {
            cycleCount += 1
            
            let remainingTime = testDuration - Date().timeIntervalSince(startTime)
            print("\n⏱️ Cycle #\(cycleCount) | Remaining: \(String(format: "%.0f", remainingTime)) seconds")
            
            // Try different trigger methods
            switch cycleCount % 4 {
            case 0:
                // Method A: Re-set AI Photo mode
                print("   🔄 Re-setting AI Photo mode...")
                QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
                    print("   ✓ Mode refreshed")
                }, fail: { _ in
                    print("   ✗ Mode refresh failed")
                })
                
            case 1:
                // Method B: Toggle between photo modes
                print("   🔀 Toggling photo modes...")
                QCSDKCmdCreator.setDeviceMode(.photo, success: {
                    // Immediately switch back
                    QCSDKCmdCreator.setDeviceMode(.aiPhoto, success: {
                        print("   ✓ Mode toggled")
                    }, fail: { _ in })
                }, fail: { _ in
                    print("   ✗ Toggle failed")
                })
                
            case 2:
                // Method C: Try WiFi trigger
                print("   📡 Attempting WiFi trigger...")
                QCSDKCmdCreator.openWifi(with: .aiPhoto, success: { ssid, password in
                    print("   ✓ WiFi triggered - SSID: \(ssid)")
                }, fail: { errorCode in
                    print("   ✗ WiFi trigger failed: \(errorCode)")
                })
                
            default:
                // Method D: Just wait for automatic capture
                print("   ⏳ Waiting for automatic capture...")
            }
            
            // Variable wait time between cycles
            let waitTime = cycleCount % 3 == 0 ? 5.0 : 3.0
            try await Task.sleep(nanoseconds: UInt64(waitTime * 1_000_000_000))
            
            // Show visual indicator every 30 seconds
            if Int(Date().timeIntervalSince(startTime)) % 30 == 0 {
                print("\n")
                print("════════════════════════════════════════════════")
                print("  🎬 TEST IN PROGRESS - WATCH YOUR GLASSES 🎬  ")
                print("════════════════════════════════════════════════")
                print("\n")
            }
        }
        
        // Phase 3: Final Summary
        print("\n")
        print("╔════════════════════════════════════════════════════════════╗")
        print("║                  🏁 TEST COMPLETE 🏁                       ║")
        print("╚════════════════════════════════════════════════════════════╝")
        
        longDelegate.printSummary()
        
        // Analysis
        print("\n📈 ANALYSIS:")
        print("────────────")
        
        if longDelegate.imageCount > 0 {
            let avgSize = longDelegate.totalBytes / longDelegate.imageCount
            print("• Average image size: \(longDelegate.formatBytes(avgSize))")
            
            if let firstTime = longDelegate.firstImageTime, let lastTime = longDelegate.lastImageTime {
                let duration = lastTime.timeIntervalSince(firstTime)
                if duration > 0 {
                    let dataRate = Double(longDelegate.totalBytes) / duration
                    print("• Data rate: \(longDelegate.formatBytes(Int(dataRate)))/sec")
                    
                    if longDelegate.imageCount > 1 {
                        let avgInterval = duration / Double(longDelegate.imageCount - 1)
                        print("• Average interval between frames: \(String(format: "%.2f", avgInterval)) seconds")
                    }
                }
            }
            
            print("\n✅ Successfully captured \(longDelegate.imageCount) AI photos")
            print("   Check the app UI to see the processed images")
        } else {
            print("\n⚠️ No AI photos were captured during the test")
            print("   Possible reasons:")
            print("   • Glasses need to be in proper AI Photo mode")
            print("   • Manual trigger may be required on glasses")
            print("   • Battery or connectivity issues")
        }
        
        print("\n👀 Did you observe:")
        print("   • Camera sounds from the glasses?")
        print("   • Images appearing in the app?")
        print("   • Any patterns in capture timing?")
        print("\n")
    }
}

// MARK: - Support Classes

/// Simple expectation for long-running async coordination
class LongRunExpectation {
    private var done = false
    
    func fulfill() {
        done = true
    }
    
    func wait(timeout: TimeInterval) async {
        let end = Date().addingTimeInterval(timeout)
        while Date() < end && !done {
            try? await Task.sleep(nanoseconds: 100_000_000)
        }
    }
}