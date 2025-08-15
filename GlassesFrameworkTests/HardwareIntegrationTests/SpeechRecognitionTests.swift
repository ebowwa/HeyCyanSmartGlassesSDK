//
//  SpeechRecognitionTests.swift
//  GlassesFrameworkTests
//
//  Tests for speech recognition using the glasses' microphone
//

import XCTest
import Speech
import AVFoundation
@testable import GlassesFramework

class SpeechRecognitionTests: XCTestCase {
    
    var bluetoothManager: BluetoothManager!
    var speechManager: SpeechRecognitionManager!
    
    override func setUp() {
        super.setUp()
        bluetoothManager = BluetoothManager.shared
        speechManager = SpeechRecognitionManager()
        
        print("\n🗣️ Speech Recognition Test Setup")
        ensureConnection()
    }
    
    override func tearDown() {
        speechManager.stopRecognition()
        speechManager = nil
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
        XCTAssertTrue(bluetoothManager.isConnected, "Must be connected to glasses to run speech tests")
    }
    
    func testSpeechRecognitionAuthorization() {
        print("\n==========================================")
        print("🗣️ TESTING SPEECH RECOGNITION AUTHORIZATION")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Request speech authorization")
        
        speechManager.requestAuthorization { authorized in
            print("📝 Speech recognition authorized: \(authorized)")
            
            // Check current authorization status
            let status = SFSpeechRecognizer.authorizationStatus()
            switch status {
            case .authorized:
                print("✅ Speech recognition is authorized")
            case .denied:
                print("❌ Speech recognition was denied")
            case .restricted:
                print("⚠️ Speech recognition is restricted")
            case .notDetermined:
                print("❓ Speech recognition not yet determined")
            @unknown default:
                print("❓ Unknown authorization status")
            }
            
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testBasicSpeechRecognition() {
        print("\n==========================================")
        print("🗣️ TESTING BASIC SPEECH RECOGNITION")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Perform speech recognition")
        
        // Request authorization first
        speechManager.requestAuthorization { [weak self] authorized in
            guard authorized else {
                print("❌ Speech recognition not authorized")
                print("   ℹ️ Please enable Speech Recognition in Settings > Privacy > Speech Recognition")
                expectation.fulfill()
                return
            }
            
            print("✅ Speech recognition authorized")
            
            // Check availability
            if self?.speechManager.isAvailable == true {
                print("✅ Speech recognizer is available")
            } else {
                print("❌ Speech recognizer not available")
                expectation.fulfill()
                return
            }
            
            // Set up handlers
            self?.speechManager.onTranscriptionUpdate = { partial in
                print("📝 Partial transcription: \"\(partial)\"")
            }
            
            self?.speechManager.onFinalTranscription = { final in
                print("✅ Final transcription: \"\(final)\"")
                expectation.fulfill()
            }
            
            self?.speechManager.onError = { error in
                print("❌ Recognition error: \(error.localizedDescription)")
                expectation.fulfill()
            }
            
            // Start recognition
            do {
                print("\n🎤 Starting speech recognition...")
                print("   📢 PLEASE SPEAK INTO THE GLASSES MICROPHONE")
                print("   💬 Say something like: \"Hello, this is a test\"")
                print("   ⏱️ Recognition will run for 10 seconds\n")
                
                try self?.speechManager.startRecognition()
                
                // Stop after 10 seconds
                DispatchQueue.main.asyncAfter(deadline: .now() + 10.0) {
                    print("\n⏹️ Stopping recognition...")
                    self?.speechManager.stopRecognition()
                    
                    // Give it time to process final result
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                        expectation.fulfill()
                    }
                }
                
            } catch {
                print("❌ Failed to start recognition: \(error)")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 15.0)
    }
    
    func testContinuousSpeechRecognition() {
        print("\n==========================================")
        print("🗣️ TESTING CONTINUOUS SPEECH RECOGNITION")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Continuous speech recognition")
        
        var transcriptionCount = 0
        
        // Set up handlers
        speechManager.onTranscriptionUpdate = { partial in
            transcriptionCount += 1
            print("📝 Update #\(transcriptionCount): \"\(partial)\"")
        }
        
        speechManager.onFinalTranscription = { final in
            print("✅ Segment complete: \"\(final)\"")
            print("   Restarting for continuous recognition...")
        }
        
        speechManager.onError = { error in
            print("❌ Error: \(error.localizedDescription)")
        }
        
        print("🎤 Starting continuous recognition...")
        print("   📢 SPEAK MULTIPLE SENTENCES WITH PAUSES")
        print("   💬 Example: \"Testing one two three\" [pause] \"Hello world\" [pause]")
        print("   ⏱️ Will run for 15 seconds\n")
        
        speechManager.startContinuousRecognition()
        
        // Run for 15 seconds
        DispatchQueue.main.asyncAfter(deadline: .now() + 15.0) {
            print("\n⏹️ Stopping continuous recognition...")
            self.speechManager.stopContinuousRecognition()
            
            print("📊 Total transcription updates: \(transcriptionCount)")
            expectation.fulfill()
        }
        
        wait(for: [expectation], timeout: 20.0)
    }
    
    func testSpeechRecognitionWithDifferentLanguages() {
        print("\n==========================================")
        print("🗣️ TESTING MULTI-LANGUAGE RECOGNITION")
        print("==========================================\n")
        
        let languages = [
            ("en-US", "English", "Hello, how are you today"),
            ("es-ES", "Spanish", "Hola, como estas"),
            ("fr-FR", "French", "Bonjour, comment allez-vous")
        ]
        
        for (localeID, language, example) in languages {
            print("\n🌐 Testing \(language) (\(localeID))")
            print("   💬 Say: \"\(example)\"")
            
            let expectation = XCTestExpectation(description: "Test \(language)")
            
            // Create recognizer for specific language
            let localeSpeechManager = SpeechRecognitionManager(locale: Locale(identifier: localeID))
            
            localeSpeechManager.requestAuthorization { authorized in
                guard authorized else {
                    print("   ❌ Not authorized")
                    expectation.fulfill()
                    return
                }
                
                if localeSpeechManager.isAvailable {
                    print("   ✅ \(language) recognizer available")
                } else {
                    print("   ❌ \(language) recognizer not available")
                }
                
                expectation.fulfill()
            }
            
            wait(for: [expectation], timeout: 3.0)
        }
    }
    
    func testSpeechRecognitionAccuracy() {
        print("\n==========================================")
        print("🗣️ TESTING RECOGNITION ACCURACY")
        print("==========================================\n")
        
        let expectation = XCTestExpectation(description: "Test accuracy")
        
        let testPhrases = [
            "The quick brown fox jumps over the lazy dog",
            "Testing one two three four five",
            "HeyCyan glasses are amazing"
        ]
        
        print("📝 Please speak these phrases clearly:")
        for (index, phrase) in testPhrases.enumerated() {
            print("   \(index + 1). \"\(phrase)\"")
        }
        
        var recognizedPhrases: [String] = []
        
        speechManager.requestAuthorization { [weak self] authorized in
            guard authorized else {
                expectation.fulfill()
                return
            }
            
            self?.speechManager.onFinalTranscription = { final in
                print("✅ Recognized: \"\(final)\"")
                recognizedPhrases.append(final)
                
                if recognizedPhrases.count >= testPhrases.count {
                    // Calculate accuracy
                    print("\n📊 Accuracy Report:")
                    for i in 0..<min(testPhrases.count, recognizedPhrases.count) {
                        let expected = testPhrases[i].lowercased()
                        let recognized = recognizedPhrases[i].lowercased()
                        let accuracy = self?.calculateAccuracy(expected: expected, recognized: recognized) ?? 0
                        print("   Phrase \(i+1): \(Int(accuracy * 100))% accurate")
                    }
                    
                    expectation.fulfill()
                }
            }
            
            do {
                print("\n🎤 Starting recognition...")
                try self?.speechManager.startRecognition()
            } catch {
                print("❌ Failed: \(error)")
                expectation.fulfill()
            }
        }
        
        wait(for: [expectation], timeout: 30.0)
    }
    
    private func calculateAccuracy(expected: String, recognized: String) -> Double {
        let expectedWords = expected.split(separator: " ")
        let recognizedWords = recognized.split(separator: " ")
        
        var matches = 0
        let minCount = min(expectedWords.count, recognizedWords.count)
        
        for i in 0..<minCount {
            if expectedWords[i] == recognizedWords[i] {
                matches += 1
            }
        }
        
        let maxCount = max(expectedWords.count, recognizedWords.count)
        return Double(matches) / Double(maxCount)
    }
}