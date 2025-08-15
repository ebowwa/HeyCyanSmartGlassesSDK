//
//  SpeechRecognitionManager.swift
//  GlassesFramework
//
//  Manages speech recognition using the glasses' microphone
//

import Foundation
import Speech
import AVFoundation
import Combine

/// Manages speech recognition functionality using the glasses' microphone
public class SpeechRecognitionManager: NSObject {
    
    // MARK: - Properties
    
    private let speechRecognizer: SFSpeechRecognizer?
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest?
    private var recognitionTask: SFSpeechRecognitionTask?
    private let audioEngine = AVAudioEngine()
    
    /// Current transcription result
    @Published public private(set) var transcriptionResult: String = ""
    
    /// Whether speech recognition is currently active
    @Published public private(set) var isRecognizing: Bool = false
    
    /// Recognition error if any
    @Published public private(set) var recognitionError: Error?
    
    /// Completion handler for recognition results
    public var onTranscriptionUpdate: ((String) -> Void)?
    
    /// Completion handler for final results
    public var onFinalTranscription: ((String) -> Void)?
    
    /// Error handler
    public var onError: ((Error) -> Void)?
    
    // MARK: - Initialization
    
    public override init() {
        // Initialize with default locale
        self.speechRecognizer = SFSpeechRecognizer(locale: Locale(identifier: "en-US"))
        super.init()
        
        speechRecognizer?.delegate = self
    }
    
    public init(locale: Locale) {
        self.speechRecognizer = SFSpeechRecognizer(locale: locale)
        super.init()
        
        speechRecognizer?.delegate = self
    }
    
    // MARK: - Authorization
    
    /// Request speech recognition authorization
    public func requestAuthorization(completion: @escaping (Bool) -> Void) {
        SFSpeechRecognizer.requestAuthorization { authStatus in
            DispatchQueue.main.async {
                switch authStatus {
                case .authorized:
                    print("✅ Speech recognition authorized")
                    completion(true)
                case .denied:
                    print("❌ Speech recognition denied")
                    completion(false)
                case .restricted:
                    print("❌ Speech recognition restricted")
                    completion(false)
                case .notDetermined:
                    print("❓ Speech recognition not determined")
                    completion(false)
                @unknown default:
                    completion(false)
                }
            }
        }
    }
    
    /// Check if speech recognition is available
    public var isAvailable: Bool {
        return speechRecognizer?.isAvailable ?? false
    }
    
    // MARK: - Recognition Control
    
    /// Start speech recognition using the glasses' microphone
    public func startRecognition() throws {
        // Cancel any ongoing recognition
        if recognitionTask != nil {
            stopRecognition()
        }
        
        // Configure audio session for glasses mic
        let audioSession = AVAudioSession.sharedInstance()
        try audioSession.setCategory(.record, mode: .measurement, options: [.allowBluetooth])
        try audioSession.setActive(true, options: .notifyOthersOnDeactivation)
        
        // Check if glasses are the current input
        if let currentInput = audioSession.currentRoute.inputs.first {
            print("🎤 Speech recognition using: \(currentInput.portName) (\(currentInput.portType.rawValue))")
            if currentInput.portName.contains("M01") || currentInput.portType == .bluetoothHFP {
                print("   ✅ Using GLASSES MICROPHONE for speech recognition!")
            } else {
                print("   ⚠️ WARNING: Using PHONE MICROPHONE - not glasses!")
                print("   📱 Current input: \(currentInput.portName)")
                
                // Try to find and set glasses as input
                if let availableInputs = audioSession.availableInputs {
                    for input in availableInputs {
                        print("   Available: \(input.portName) (\(input.portType.rawValue))")
                        if input.portName.contains("M01") || input.portType == .bluetoothHFP {
                            try audioSession.setPreferredInput(input)
                            print("   ✅ Switched to glasses microphone!")
                            break
                        }
                    }
                }
            }
        }
        
        // Create recognition request
        recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
        guard let recognitionRequest = recognitionRequest else {
            throw SpeechRecognitionError.requestCreationFailed
        }
        
        // Configure request
        recognitionRequest.shouldReportPartialResults = true
        recognitionRequest.requiresOnDeviceRecognition = false // Use server for better accuracy
        
        // Get input node
        let inputNode = audioEngine.inputNode
        
        // Create recognition task
        recognitionTask = speechRecognizer?.recognitionTask(with: recognitionRequest) { [weak self] result, error in
            guard let self = self else { return }
            
            var isFinal = false
            
            if let result = result {
                self.transcriptionResult = result.bestTranscription.formattedString
                isFinal = result.isFinal
                
                // Call update handler
                self.onTranscriptionUpdate?(self.transcriptionResult)
                
                if isFinal {
                    print("✅ Final transcription: \(self.transcriptionResult)")
                    self.onFinalTranscription?(self.transcriptionResult)
                } else {
                    print("📝 Partial: \(self.transcriptionResult)")
                }
            }
            
            if error != nil || isFinal {
                self.audioEngine.stop()
                inputNode.removeTap(onBus: 0)
                
                self.recognitionRequest = nil
                self.recognitionTask = nil
                self.isRecognizing = false
                
                if let error = error {
                    print("❌ Recognition error: \(error.localizedDescription)")
                    self.recognitionError = error
                    self.onError?(error)
                }
            }
        }
        
        // Configure audio tap
        let recordingFormat = inputNode.outputFormat(forBus: 0)
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { buffer, _ in
            self.recognitionRequest?.append(buffer)
        }
        
        // Start audio engine
        audioEngine.prepare()
        try audioEngine.start()
        
        isRecognizing = true
        print("🎤 Speech recognition started")
    }
    
    /// Stop speech recognition
    public func stopRecognition() {
        if audioEngine.isRunning {
            audioEngine.stop()
            audioEngine.inputNode.removeTap(onBus: 0)
        }
        
        recognitionRequest?.endAudio()
        recognitionRequest = nil
        
        recognitionTask?.cancel()
        recognitionTask = nil
        
        isRecognizing = false
        print("⏹️ Speech recognition stopped")
    }
    
    // MARK: - Continuous Recognition
    
    /// Start continuous speech recognition with automatic restart
    public func startContinuousRecognition() {
        requestAuthorization { [weak self] authorized in
            guard authorized else {
                print("❌ Speech recognition not authorized")
                return
            }
            
            do {
                try self?.startRecognition()
                
            } catch {
                print("❌ Failed to start continuous recognition: \(error)")
                self?.onError?(error)
            }
        }
    }
    
    /// Stop continuous recognition
    public func stopContinuousRecognition() {
        stopRecognition()
    }
}

// MARK: - SFSpeechRecognizerDelegate

extension SpeechRecognitionManager: SFSpeechRecognizerDelegate {
    public func speechRecognizer(_ speechRecognizer: SFSpeechRecognizer, availabilityDidChange available: Bool) {
        print("🎤 Speech recognizer availability changed: \(available)")
        if !available {
            stopRecognition()
        }
    }
}


// MARK: - Error Types

public enum SpeechRecognitionError: LocalizedError {
    case requestCreationFailed
    case audioEngineError
    case notAuthorized
    case glassesNotConnected
    
    public var errorDescription: String? {
        switch self {
        case .requestCreationFailed:
            return "Failed to create speech recognition request"
        case .audioEngineError:
            return "Audio engine error"
        case .notAuthorized:
            return "Speech recognition not authorized"
        case .glassesNotConnected:
            return "Glasses are not connected"
        }
    }
}