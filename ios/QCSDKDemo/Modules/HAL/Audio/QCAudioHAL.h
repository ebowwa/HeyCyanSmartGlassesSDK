//
//  QCAudioHAL.h
//  Hardware Abstraction Layer - Audio
//
//  Audio interface for Cyan Glasses (Microphone & Speaker)
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

// MARK: - Microphone Interface

typedef NS_ENUM(NSInteger, QCMicrophoneMode) {
    QCMicrophoneModeNormal = 0,
    QCMicrophoneModeNoiseCancellation,
    QCMicrophoneModeVoiceRecognition,
    QCMicrophoneModeHighQuality
};

@protocol QCMicrophoneHALDelegate <NSObject>
@optional
- (void)microphoneDidStartRecording;
- (void)microphoneDidStopRecording:(NSURL *)audioURL;
- (void)microphoneDidReceiveAudioBuffer:(AVAudioPCMBuffer *)buffer;
- (void)microphoneDidUpdateLevel:(Float32)level;
- (void)microphoneDidEncounterError:(NSError *)error;
@end

@interface QCMicrophoneHAL : NSObject

@property (nonatomic, weak) id<QCMicrophoneHALDelegate> delegate;
@property (nonatomic, readonly) BOOL isRecording;
@property (nonatomic, assign) QCMicrophoneMode mode;
@property (nonatomic, assign) Float32 inputGain;

// Microphone Control
- (void)initializeMicrophone;
- (void)shutdownMicrophone;

// Recording
- (void)startRecording;
- (void)stopRecording;
- (void)pauseRecording;
- (void)resumeRecording;

// Audio Processing
- (void)enableNoiseReduction:(BOOL)enable;
- (void)enableEchoCancellation:(BOOL)enable;
- (void)setInputVolume:(Float32)volume; // 0.0 to 1.0

// Voice Commands
- (void)startVoiceRecognition;
- (void)stopVoiceRecognition;

// WiFi Download
- (void)downloadLastRecordingFromGlasses:(NSString *)ipAddress 
                               completion:(void (^)(NSURL * _Nullable localURL, NSError * _Nullable error))completion;

@end

// MARK: - Speaker Interface

typedef NS_ENUM(NSInteger, QCSpeakerMode) {
    QCSpeakerModeNormal = 0,
    QCSpeakerModeBass,
    QCSpeakerModeTreble,
    QCSpeakerModeVoice
};

@protocol QCSpeakerHALDelegate <NSObject>
@optional
- (void)speakerDidStartPlayback;
- (void)speakerDidFinishPlayback;
- (void)speakerDidPausePlayback;
- (void)speakerDidUpdateProgress:(NSTimeInterval)currentTime duration:(NSTimeInterval)duration;
- (void)speakerDidEncounterError:(NSError *)error;
@end

@interface QCSpeakerHAL : NSObject

@property (nonatomic, weak) id<QCSpeakerHALDelegate> delegate;
@property (nonatomic, readonly) BOOL isPlaying;
@property (nonatomic, assign) QCSpeakerMode mode;
@property (nonatomic, assign) Float32 volume; // 0.0 to 1.0

// Speaker Control
- (void)initializeSpeaker;
- (void)shutdownSpeaker;

// Playback
- (void)playAudioData:(NSData *)audioData;
- (void)playAudioFromURL:(NSURL *)audioURL;
- (void)pausePlayback;
- (void)resumePlayback;
- (void)stopPlayback;

// Audio Settings
- (void)setVolume:(Float32)volume animated:(BOOL)animated;
- (void)setEqualizer:(QCSpeakerMode)mode;
- (void)enableSpatialAudio:(BOOL)enable;

// Text-to-Speech
- (void)speakText:(NSString *)text;
- (void)speakText:(NSString *)text withVoice:(NSString *)voiceIdentifier;
- (void)stopSpeaking;

// System Sounds
- (void)playSystemSound:(NSString *)soundName;
- (void)playNotificationSound;

@end

// MARK: - Combined Audio Manager

@interface QCAudioHAL : NSObject

@property (nonatomic, strong, readonly) QCMicrophoneHAL *microphone;
@property (nonatomic, strong, readonly) QCSpeakerHAL *speaker;

+ (instancetype)sharedInstance;

// Audio Session Management
- (void)configureAudioSession;
- (void)setAudioSessionActive:(BOOL)active;

// Route Management
- (void)routeAudioToSpeaker;
- (void)routeAudioToBluetooth;
- (NSArray<AVAudioSessionPortDescription *> *)availableAudioRoutes;

// Volume Control
- (Float32)systemVolume;
- (void)setSystemVolume:(Float32)volume;

@end

NS_ASSUME_NONNULL_END