//
//  AudioPlaybackTests.swift
//  GlassesFrameworkTests
//
//  Tests for audio playback through the glasses
//

import XCTest
import AVFoundation
import CoreBluetooth
@testable import GlassesFramework

class AudioPlaybackTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var audioPlayer: AVAudioPlayer?
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        
        print("\n🎵 Audio Playback Test Setup")
        ensureConnection()
        setupAudioSession()
    }
    
    override func tearDown() {
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
        XCTAssertTrue(bluetoothManager.isConnected, "Must be connected to glasses to run audio tests")
    }
    
    private func setupAudioSession() {
        let audioSession = AVAudioSession.sharedInstance()
        do {
            // Configure for Bluetooth playback
            try audioSession.setCategory(.playback, mode: .default, options: [.allowBluetooth, .allowBluetoothA2DP])
            try audioSession.setActive(true)
            print("✅ Audio session configured for Bluetooth")
            
            // Verify glasses are the output
            let currentRoute = audioSession.currentRoute
            for output in currentRoute.outputs {
                print("   Audio output: \(output.portName) (\(output.portType.rawValue))")
            }
        } catch {
            print("❌ Failed to setup audio session: \(error)")
        }
    }
    
    func testPlaySoundInLeftEar() {
        print("\n==========================================")
        print("🎧 TESTING LEFT EAR AUDIO PLAYBACK")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Play sound in left ear")
        
        // Generate and save audio file for left ear
        let leftEarURL = generateToneFile(frequency: 1000.0, duration: 3.0, leftChannel: true, rightChannel: false)
        
        print("🎵 Playing 1000Hz tone in LEFT ear only for 3 seconds...")
        print("   ℹ️ You should hear a HIGH-PITCHED BEEP in your LEFT ear ONLY")
        print("   🔊 Using AVAudioPlayer for better Bluetooth compatibility")
        
        do {
            // Create player with the generated file
            audioPlayer = try AVAudioPlayer(contentsOf: leftEarURL)
            audioPlayer?.volume = 1.0  // Maximum volume
            audioPlayer?.prepareToPlay()
            
            // Play the sound
            audioPlayer?.play()
            
            // Wait for playback to complete
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.5) {
                print("✅ Finished playing left ear test")
                
                // Now test right ear
                self.testRightEar {
                    // Finally test both ears
                    self.testBothEars {
                        expectation.fulfill()
                    }
                }
            }
            
        } catch {
            print("❌ Failed to play audio: \(error)")
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 15.0)
    }
    
    private func testRightEar(completion: @escaping () -> Void) {
        print("\n🎵 Now playing 1000Hz tone in RIGHT ear only for 3 seconds...")
        print("   ℹ️ You should hear the sound ONLY in your RIGHT ear")
        
        let rightEarURL = generateToneFile(frequency: 1000.0, duration: 3.0, leftChannel: false, rightChannel: true)
        
        do {
            audioPlayer = try AVAudioPlayer(contentsOf: rightEarURL)
            audioPlayer?.volume = 1.0
            audioPlayer?.prepareToPlay()
            audioPlayer?.play()
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.5) {
                print("✅ Finished playing right ear test")
                completion()
            }
        } catch {
            print("❌ Failed to play right ear audio: \(error)")
            completion()
        }
    }
    
    private func testBothEars(completion: @escaping () -> Void) {
        print("\n🎵 Finally playing 1000Hz tone in BOTH ears for 3 seconds...")
        print("   ℹ️ You should hear the sound in BOTH ears (stereo)")
        
        let stereoURL = generateToneFile(frequency: 1000.0, duration: 3.0, leftChannel: true, rightChannel: true)
        
        do {
            audioPlayer = try AVAudioPlayer(contentsOf: stereoURL)
            audioPlayer?.volume = 1.0
            audioPlayer?.prepareToPlay()
            audioPlayer?.play()
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.5) {
                print("✅ Finished playing stereo test")
                print("\n==========================================")
                print("✅ AUDIO PLAYBACK TEST COMPLETE")
                print("   - Left ear: 1000Hz tone")
                print("   - Right ear: 1000Hz tone")
                print("   - Both ears: 1000Hz tone (stereo)")
                print("==========================================\n")
                completion()
            }
        } catch {
            print("❌ Failed to play stereo audio: \(error)")
            completion()
        }
    }
    
    private func generateToneFile(frequency: Double, duration: Double, leftChannel: Bool, rightChannel: Bool) -> URL {
        let sampleRate = 44100.0
        let frameCount = Int(sampleRate * duration)
        
        // Create audio format
        let audioFormat = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 2)!
        
        // Create buffer
        let buffer = AVAudioPCMBuffer(pcmFormat: audioFormat, frameCapacity: AVAudioFrameCount(frameCount))!
        buffer.frameLength = AVAudioFrameCount(frameCount)
        
        // Generate sine wave
        let amplitude: Float = 0.8  // Slightly less than max to avoid clipping
        for frame in 0..<frameCount {
            let value = amplitude * sinf(Float(2.0 * Double.pi * frequency * Double(frame) / sampleRate))
            
            // Left channel
            buffer.floatChannelData?[0][frame] = leftChannel ? value : 0.0
            
            // Right channel  
            buffer.floatChannelData?[1][frame] = rightChannel ? value : 0.0
        }
        
        // Write to temporary file
        let tempDir = FileManager.default.temporaryDirectory
        let fileName = "tone_L\(leftChannel ? "1" : "0")_R\(rightChannel ? "1" : "0").caf"
        let fileURL = tempDir.appendingPathComponent(fileName)
        
        do {
            let audioFile = try AVAudioFile(forWriting: fileURL, settings: audioFormat.settings)
            try audioFile.write(from: buffer)
            print("📁 Generated audio file: \(fileName)")
        } catch {
            print("❌ Failed to write audio file: \(error)")
        }
        
        return fileURL
    }
}