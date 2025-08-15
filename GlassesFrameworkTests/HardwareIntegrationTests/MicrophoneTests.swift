//
//  MicrophoneTests.swift
//  GlassesFrameworkTests
//
//  Tests for microphone functionality with the glasses
//

import XCTest
import AVFoundation
import CoreBluetooth
@testable import GlassesFramework

class MicrophoneTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var audioRecorder: AVAudioRecorder?
    var audioPlayer: AVAudioPlayer?
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        
        print("\n🎤 Microphone Test Setup")
        ensureConnection()
        setupAudioSession()
    }
    
    override func tearDown() {
        audioRecorder?.stop()
        audioRecorder = nil
        audioPlayer?.stop()
        audioPlayer = nil
        bluetoothManager = nil
        super.tearDown()
    }
    
    private func ensureConnection() {
        guard !bluetoothManager.isConnected else {
            print("✅ Already connected to glasses")
            return
        }
        
        print("🔄 Attempting to connect to glasses...")
        let expectation = XCTestExpectation(description: "Connect to glasses")
        
        bluetoothManager.startScanning()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if let device = self.bluetoothManager.discoveredDevices.first(where: { $0.name.contains("M01") }) {
                print("📱 Found glasses: \(device.name) - connecting...")
                self.bluetoothManager.connect(to: device)
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    if self.bluetoothManager.isConnected {
                        print("✅ Connected successfully!")
                    }
                    expectation.fulfill()
                }
            } else {
                print("❌ No M01 glasses found")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 10.0)
        XCTAssertTrue(bluetoothManager.isConnected, "Must be connected to glasses to run microphone tests")
    }
    
    private func setupAudioSession() {
        let audioSession = AVAudioSession.sharedInstance()
        do {
            // Configure for recording with Bluetooth
            try audioSession.setCategory(.playAndRecord, mode: .default, options: [.allowBluetooth, .allowBluetoothA2DP])
            try audioSession.setActive(true)
            print("✅ Audio session configured for recording with Bluetooth")
            
            // Check available inputs
            print("🎤 Available audio inputs:")
            if let inputs = audioSession.availableInputs {
                for input in inputs {
                    print("   - \(input.portName) (\(input.portType.rawValue))")
                    if input.portName.contains("M01") || input.portType == .bluetoothHFP {
                        print("   ✅ Found glasses microphone!")
                        // Set glasses as preferred input
                        try audioSession.setPreferredInput(input)
                    }
                }
            }
            
            // Verify current input
            if let currentInput = audioSession.currentRoute.inputs.first {
                print("🎤 Current input: \(currentInput.portName) (\(currentInput.portType.rawValue))")
            }
            
        } catch {
            print("❌ Failed to setup audio session: \(error)")
        }
    }
    
    func testRecordAudioFromGlassesMic() {
        print("\n==========================================")
        print("🎤 TESTING GLASSES MICROPHONE RECORDING")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Record audio from glasses mic")
        
        // Create audio file URL
        let tempDir = FileManager.default.temporaryDirectory
        let audioURL = tempDir.appendingPathComponent("glasses_recording.m4a")
        
        // Setup recorder settings for AAC compression
        let settings: [String: Any] = [
            AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
            AVSampleRateKey: 44100.0,
            AVNumberOfChannelsKey: 1,  // Mono recording
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
        ]
        
        do {
            // Create recorder
            audioRecorder = try AVAudioRecorder(url: audioURL, settings: settings)
            audioRecorder?.prepareToRecord()
            
            // Check if we're using the glasses mic
            let audioSession = AVAudioSession.sharedInstance()
            if let currentInput = audioSession.currentRoute.inputs.first {
                print("🎤 Recording with: \(currentInput.portName)")
                if currentInput.portName.contains("M01") || currentInput.portType == .bluetoothHFP {
                    print("   ✅ Using glasses microphone!")
                } else {
                    print("   ⚠️ WARNING: Not using glasses microphone")
                    print("   Try disconnecting other Bluetooth audio devices")
                }
            }
            
            print("🔴 Recording for 5 seconds...")
            print("   📢 PLEASE SPEAK INTO THE GLASSES MICROPHONE")
            
            // Start recording
            audioRecorder?.record()
            
            // Record for 5 seconds
            DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                self.audioRecorder?.stop()
                print("⏹️ Recording stopped")
                
                // Check file was created
                if FileManager.default.fileExists(atPath: audioURL.path) {
                    do {
                        let attributes = try FileManager.default.attributesOfItem(atPath: audioURL.path)
                        let fileSize = attributes[.size] as? Int64 ?? 0
                        print("✅ Recording saved: \(fileSize) bytes")
                        
                        // Play back the recording
                        self.playbackRecording(url: audioURL) {
                            expectation.fulfill()
                        }
                    } catch {
                        print("❌ Failed to get file info: \(error)")
                        expectation.fulfill()
                    }
                } else {
                    print("❌ Recording file not created")
                    expectation.fulfill()
                }
            }
            
        } catch {
            print("❌ Failed to setup recorder: \(error)")
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 15.0)
    }
    
    private func playbackRecording(url: URL, completion: @escaping () -> Void) {
        print("\n🔊 Playing back recording through glasses...")
        
        do {
            audioPlayer = try AVAudioPlayer(contentsOf: url)
            audioPlayer?.prepareToPlay()
            audioPlayer?.volume = 1.0
            audioPlayer?.play()
            
            let duration = audioPlayer?.duration ?? 0
            print("   ⏱️ Playback duration: \(String(format: "%.1f", duration)) seconds")
            
            DispatchQueue.main.asyncAfter(deadline: .now() + duration + 0.5) {
                print("✅ Playback complete")
                completion()
            }
        } catch {
            print("❌ Failed to play recording: \(error)")
            completion()
        }
    }
    
    func testGlassesAudioRecordingMode() {
        print("\n==========================================")
        print("🎤 TESTING GLASSES AUDIO RECORDING MODE")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Test glasses audio recording mode")
        
        print("📼 Starting audio recording on glasses...")
        bluetoothManager.toggleAudioRecording()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if self.bluetoothManager.deviceInfo.isRecordingAudio {
                print("✅ Glasses are recording audio internally")
                print("   ℹ️ Audio is being saved to glasses storage")
                
                // Record for 5 seconds
                print("⏱️ Recording for 5 more seconds...")
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    print("⏹️ Stopping audio recording...")
                    self.bluetoothManager.toggleAudioRecording()
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                        if !self.bluetoothManager.deviceInfo.isRecordingAudio {
                            print("✅ Audio recording stopped")
                            
                            // Check media info
                            self.bluetoothManager.getMediaInfo()
                            DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                                print("📊 Audio files on glasses: \(self.bluetoothManager.deviceInfo.audioCount)")
                                expectation.fulfill()
                            }
                        } else {
                            print("❌ Failed to stop recording")
                            expectation.fulfill()
                        }
                    }
                }
            } else {
                print("❌ Failed to start audio recording")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 15.0)
    }
    
    func testMicrophoneInputLevel() {
        print("\n==========================================")
        print("🎤 TESTING MICROPHONE INPUT LEVELS")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Monitor mic input levels")
        
        // Create audio file URL for recording
        let tempDir = FileManager.default.temporaryDirectory
        let audioURL = tempDir.appendingPathComponent("level_test.m4a")
        
        // Setup recorder settings
        let settings: [String: Any] = [
            AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
            AVSampleRateKey: 44100.0,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
        ]
        
        do {
            audioRecorder = try AVAudioRecorder(url: audioURL, settings: settings)
            audioRecorder?.isMeteringEnabled = true
            audioRecorder?.prepareToRecord()
            audioRecorder?.record()
            
            print("📊 Monitoring input levels for 5 seconds...")
            print("   📢 MAKE SOME NOISE INTO THE GLASSES MIC!")
            
            var measurements = 0
            Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { timer in
                self.audioRecorder?.updateMeters()
                
                let averagePower = self.audioRecorder?.averagePower(forChannel: 0) ?? -160
                let peakPower = self.audioRecorder?.peakPower(forChannel: 0) ?? -160
                
                // Convert to percentage (0-100)
                let avgPercent = max(0, min(100, (averagePower + 60) * 100 / 60))
                let peakPercent = max(0, min(100, (peakPower + 60) * 100 / 60))
                
                // Create visual meter
                let meterLength = Int(avgPercent / 5)
                let meter = String(repeating: "█", count: meterLength) + String(repeating: "░", count: 20 - meterLength)
                
                print("   Level: \(meter) Avg: \(Int(avgPercent))% Peak: \(Int(peakPercent))%")
                
                measurements += 1
                if measurements >= 50 { // 5 seconds
                    timer.invalidate()
                    self.audioRecorder?.stop()
                    print("✅ Level monitoring complete")
                    expectation.fulfill()
                }
            }
            
        } catch {
            print("❌ Failed to setup level monitoring: \(error)")
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 10.0)
    }
}