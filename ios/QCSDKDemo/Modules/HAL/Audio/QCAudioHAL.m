//
//  QCAudioHAL.m
//  Hardware Abstraction Layer - Audio Implementation
//
//  Audio implementation for Cyan Glasses (Microphone & Speaker)
//

#import "QCAudioHAL.h"
#import <QCSDK/QCSDKManager.h>
#import <QCSDK/QCSDKCmdCreator.h>
#import <AudioToolbox/AudioServices.h>

// MARK: - Microphone Implementation

@interface QCMicrophoneHAL ()
@property (nonatomic, readwrite) BOOL isRecording;
@property (nonatomic, copy) NSString *lastRecordingFileName;
@end

@implementation QCMicrophoneHAL

- (instancetype)init {
    self = [super init];
    if (self) {
        _mode = QCMicrophoneModeNormal;
        _inputGain = 1.0;
    }
    return self;
}

- (void)initializeMicrophone {
    // No local initialization needed - we're using glasses mic only
    NSLog(@"🕶️ Microphone HAL initialized for GLASSES recording only");
}

- (void)shutdownMicrophone {
    [self stopRecording];
}

- (void)startRecording {
    if (self.isRecording) return;
    
    NSLog(@"🕶️ HAL: Starting GLASSES ONLY recording...");
    
    // Generate a filename for later reference
    NSString *timestamp = [NSString stringWithFormat:@"%.0f", [[NSDate date] timeIntervalSince1970]];
    self.lastRecordingFileName = [NSString stringWithFormat:@"audio_%@.wav", timestamp];
    
    // Send command to glasses to start audio recording
    [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeAudio success:^{
        NSLog(@"🕶️ HAL: SUCCESS - Glasses recording started");
        NSLog(@"🕶️ HAL: Recording will be saved on glasses as: %@", self.lastRecordingFileName);
        
        self.isRecording = YES;
        
        if ([self.delegate respondsToSelector:@selector(microphoneDidStartRecording)]) {
            [self.delegate microphoneDidStartRecording];
        }
    } fail:^(NSInteger mode) {
        NSLog(@"🕶️ HAL: FAILED - Could not start glasses recording, mode: %zd", mode);
        
        NSError *error = [NSError errorWithDomain:@"QCAudioHAL" 
                                             code:mode 
                                         userInfo:@{NSLocalizedDescriptionKey: @"Failed to start glasses recording"}];
        if ([self.delegate respondsToSelector:@selector(microphoneDidEncounterError:)]) {
            [self.delegate microphoneDidEncounterError:error];
        }
    }];
}

- (void)stopRecording {
    if (!self.isRecording) return;
    
    NSLog(@"🕶️ HAL: Stopping GLASSES recording...");
    
    // Send command to glasses to stop audio recording
    [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeAudioStop success:^{
        NSLog(@"🕶️ HAL: SUCCESS - Glasses recording stopped");
        NSLog(@"🕶️ HAL: Audio saved on glasses. Need WiFi to download: %@", self.lastRecordingFileName);
        
        self.isRecording = NO;
        
        // We'll need to download the file via WiFi to play it
        // For now, just notify that recording is done
        if ([self.delegate respondsToSelector:@selector(microphoneDidStopRecording:)]) {
            // Pass nil since we don't have a local file yet
            [self.delegate microphoneDidStopRecording:nil];
        }
        
    } fail:^(NSInteger mode) {
        NSLog(@"🕶️ HAL: FAILED - Could not stop glasses recording, mode: %zd", mode);
        self.isRecording = NO;
    }];
}

- (void)pauseRecording {
    // Not supported for glasses recording
    NSLog(@"🕶️ HAL: Pause not supported for glasses recording");
}

- (void)resumeRecording {
    // Not supported for glasses recording
    NSLog(@"🕶️ HAL: Resume not supported for glasses recording");
}

- (void)enableNoiseReduction:(BOOL)enable {
    // Configure audio session for noise reduction
    // This would interact with the glasses' DSP if available
}

- (void)enableEchoCancellation:(BOOL)enable {
    // Configure echo cancellation
}

- (void)setInputVolume:(Float32)volume {
    _inputGain = MAX(0.0, MIN(1.0, volume));
    // Apply gain to recording
}

- (void)startVoiceRecognition {
    // Implement voice recognition
    // Could integrate with Speech framework or custom solution
}

- (void)stopVoiceRecognition {
    // Stop voice recognition
}

- (void)downloadLastRecordingFromGlasses:(NSString *)ipAddress 
                               completion:(void (^)(NSURL * _Nullable localURL, NSError * _Nullable error))completion {
    if (!ipAddress || ipAddress.length == 0) {
        NSLog(@"🕶️ HAL: ERROR - No IP address provided for glasses download");
        if (completion) {
            NSError *error = [NSError errorWithDomain:@"QCAudioHAL" 
                                                 code:400 
                                             userInfo:@{NSLocalizedDescriptionKey: @"No glasses IP address provided"}];
            completion(nil, error);
        }
        return;
    }
    
    if (!self.lastRecordingFileName) {
        NSLog(@"🕶️ HAL: ERROR - No recording filename available");
        if (completion) {
            NSError *error = [NSError errorWithDomain:@"QCAudioHAL" 
                                                 code:404 
                                             userInfo:@{NSLocalizedDescriptionKey: @"No recording available to download"}];
            completion(nil, error);
        }
        return;
    }
    
    NSLog(@"🕶️ HAL: Starting WiFi download from glasses IP: %@", ipAddress);
    NSLog(@"🕶️ HAL: Downloading file: %@", self.lastRecordingFileName);
    
    // Build the URL to download from glasses
    // The glasses typically serve files via HTTP on port 8080
    NSString *urlString = [NSString stringWithFormat:@"http://%@:8080/recordings/%@", ipAddress, self.lastRecordingFileName];
    NSURL *downloadURL = [NSURL URLWithString:urlString];
    
    NSLog(@"🕶️ HAL: Download URL: %@", downloadURL.absoluteString);
    
    // Create download task
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDownloadTask *downloadTask = [session downloadTaskWithURL:downloadURL 
                                                         completionHandler:^(NSURL *location, NSURLResponse *response, NSError *error) {
        if (error) {
            NSLog(@"🕶️ HAL: Download FAILED - %@", error.localizedDescription);
            dispatch_async(dispatch_get_main_queue(), ^{
                if (completion) completion(nil, error);
            });
            return;
        }
        
        // Check HTTP response
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode != 200) {
            NSLog(@"🕶️ HAL: Download FAILED - HTTP Status: %ld", (long)httpResponse.statusCode);
            NSError *httpError = [NSError errorWithDomain:@"QCAudioHAL" 
                                                      code:httpResponse.statusCode 
                                                  userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"HTTP Error %ld", (long)httpResponse.statusCode]}];
            dispatch_async(dispatch_get_main_queue(), ^{
                if (completion) completion(nil, httpError);
            });
            return;
        }
        
        // Move downloaded file to Documents directory
        NSFileManager *fileManager = [NSFileManager defaultManager];
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSString *destinationPath = [documentsDirectory stringByAppendingPathComponent:self.lastRecordingFileName];
        NSURL *destinationURL = [NSURL fileURLWithPath:destinationPath];
        
        // Remove existing file if it exists
        [fileManager removeItemAtURL:destinationURL error:nil];
        
        // Move downloaded file
        NSError *moveError;
        BOOL success = [fileManager moveItemAtURL:location toURL:destinationURL error:&moveError];
        
        if (success) {
            NSLog(@"🕶️ HAL: Download SUCCESS - File saved to: %@", destinationPath);
            
            // Get file size for confirmation
            NSDictionary *attributes = [fileManager attributesOfItemAtPath:destinationPath error:nil];
            NSNumber *fileSize = [attributes objectForKey:NSFileSize];
            NSLog(@"🕶️ HAL: Downloaded file size: %@ bytes", fileSize);
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (completion) completion(destinationURL, nil);
            });
        } else {
            NSLog(@"🕶️ HAL: Download FAILED - Could not save file: %@", moveError.localizedDescription);
            dispatch_async(dispatch_get_main_queue(), ^{
                if (completion) completion(nil, moveError);
            });
        }
    }];
    
    [downloadTask resume];
    NSLog(@"🕶️ HAL: Download task started...");
}

@end

// MARK: - Speaker Implementation

@interface QCSpeakerHAL () <AVAudioPlayerDelegate>
@property (nonatomic, strong) AVAudioPlayer *audioPlayer;
@property (nonatomic, strong) AVSpeechSynthesizer *speechSynthesizer;
@property (nonatomic, readwrite) BOOL isPlaying;
@end

@implementation QCSpeakerHAL

- (instancetype)init {
    self = [super init];
    if (self) {
        _speechSynthesizer = [[AVSpeechSynthesizer alloc] init];
        _mode = QCSpeakerModeNormal;
        _volume = 1.0;
    }
    return self;
}

- (void)initializeSpeaker {
    AVAudioSession *session = [AVAudioSession sharedInstance];
    NSError *error;
    
    [session setCategory:AVAudioSessionCategoryPlayback error:&error];
    if (error) {
        if ([self.delegate respondsToSelector:@selector(speakerDidEncounterError:)]) {
            [self.delegate speakerDidEncounterError:error];
        }
        return;
    }
    
    [session setActive:YES error:&error];
    if (error) {
        if ([self.delegate respondsToSelector:@selector(speakerDidEncounterError:)]) {
            [self.delegate speakerDidEncounterError:error];
        }
    }
}

- (void)shutdownSpeaker {
    [self stopPlayback];
    [[AVAudioSession sharedInstance] setActive:NO error:nil];
}

- (void)playAudioData:(NSData *)audioData {
    NSError *error;
    self.audioPlayer = [[AVAudioPlayer alloc] initWithData:audioData error:&error];
    
    if (error) {
        if ([self.delegate respondsToSelector:@selector(speakerDidEncounterError:)]) {
            [self.delegate speakerDidEncounterError:error];
        }
        return;
    }
    
    self.audioPlayer.delegate = self;
    self.audioPlayer.volume = self.volume;
    [self.audioPlayer prepareToPlay];
    [self.audioPlayer play];
    self.isPlaying = YES;
    
    if ([self.delegate respondsToSelector:@selector(speakerDidStartPlayback)]) {
        [self.delegate speakerDidStartPlayback];
    }
}

- (void)playAudioFromURL:(NSURL *)audioURL {
    NSLog(@"🔊 HAL: Attempting to play audio from: %@", audioURL.path);
    
    // Check if file exists
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:audioURL.path]) {
        NSLog(@"🔊 HAL: ERROR - Audio file does not exist!");
        NSError *fileError = [NSError errorWithDomain:@"QCAudioHAL" code:404 userInfo:@{NSLocalizedDescriptionKey: @"Audio file not found"}];
        if ([self.delegate respondsToSelector:@selector(speakerDidEncounterError:)]) {
            [self.delegate speakerDidEncounterError:fileError];
        }
        return;
    }
    
    NSDictionary *attributes = [fileManager attributesOfItemAtPath:audioURL.path error:nil];
    NSNumber *fileSize = [attributes objectForKey:NSFileSize];
    NSLog(@"🔊 HAL: File exists, size: %@ bytes", fileSize);
    
    NSError *error;
    self.audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:audioURL error:&error];
    
    if (error) {
        NSLog(@"🔊 HAL: ERROR creating audio player: %@", error.localizedDescription);
        if ([self.delegate respondsToSelector:@selector(speakerDidEncounterError:)]) {
            [self.delegate speakerDidEncounterError:error];
        }
        return;
    }
    
    NSLog(@"🔊 HAL: Audio player created successfully, duration: %.2f seconds", self.audioPlayer.duration);
    
    self.audioPlayer.delegate = self;
    self.audioPlayer.volume = self.volume;
    BOOL prepared = [self.audioPlayer prepareToPlay];
    NSLog(@"🔊 HAL: Audio player prepared: %@", prepared ? @"YES" : @"NO");
    
    BOOL started = [self.audioPlayer play];
    NSLog(@"🔊 HAL: Audio playback started: %@", started ? @"YES" : @"NO");
    
    self.isPlaying = started;
    
    if ([self.delegate respondsToSelector:@selector(speakerDidStartPlayback)]) {
        [self.delegate speakerDidStartPlayback];
    }
}

- (void)pausePlayback {
    [self.audioPlayer pause];
    self.isPlaying = NO;
    
    if ([self.delegate respondsToSelector:@selector(speakerDidPausePlayback)]) {
        [self.delegate speakerDidPausePlayback];
    }
}

- (void)resumePlayback {
    [self.audioPlayer play];
    self.isPlaying = YES;
}

- (void)stopPlayback {
    [self.audioPlayer stop];
    self.isPlaying = NO;
    
    if ([self.delegate respondsToSelector:@selector(speakerDidFinishPlayback)]) {
        [self.delegate speakerDidFinishPlayback];
    }
}

- (void)setVolume:(Float32)volume animated:(BOOL)animated {
    _volume = MAX(0.0, MIN(1.0, volume));
    
    if (animated && self.audioPlayer) {
        [self.audioPlayer setVolume:_volume fadeDuration:0.3];
    } else if (self.audioPlayer) {
        self.audioPlayer.volume = _volume;
    }
}

- (void)setEqualizer:(QCSpeakerMode)mode {
    _mode = mode;
    // Configure audio processing based on mode
}

- (void)enableSpatialAudio:(BOOL)enable {
    // Configure spatial audio if available
}

- (void)speakText:(NSString *)text {
    AVSpeechUtterance *utterance = [AVSpeechUtterance speechUtteranceWithString:text];
    utterance.rate = AVSpeechUtteranceDefaultSpeechRate;
    utterance.volume = self.volume;
    
    [self.speechSynthesizer speakUtterance:utterance];
}

- (void)speakText:(NSString *)text withVoice:(NSString *)voiceIdentifier {
    AVSpeechUtterance *utterance = [AVSpeechUtterance speechUtteranceWithString:text];
    utterance.voice = [AVSpeechSynthesisVoice voiceWithIdentifier:voiceIdentifier];
    utterance.rate = AVSpeechUtteranceDefaultSpeechRate;
    utterance.volume = self.volume;
    
    [self.speechSynthesizer speakUtterance:utterance];
}

- (void)stopSpeaking {
    [self.speechSynthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
}

- (void)playSystemSound:(NSString *)soundName {
    NSString *path = [[NSBundle mainBundle] pathForResource:soundName ofType:@"wav"];
    if (path) {
        NSURL *url = [NSURL fileURLWithPath:path];
        [self playAudioFromURL:url];
    }
}

- (void)playNotificationSound {
    // Play default notification sound
    AudioServicesPlaySystemSound(1007); // Default notification sound
}

#pragma mark - AVAudioPlayerDelegate

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    self.isPlaying = NO;
    if ([self.delegate respondsToSelector:@selector(speakerDidFinishPlayback)]) {
        [self.delegate speakerDidFinishPlayback];
    }
}

- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError * _Nullable)error {
    self.isPlaying = NO;
    if (error && [self.delegate respondsToSelector:@selector(speakerDidEncounterError:)]) {
        [self.delegate speakerDidEncounterError:error];
    }
}

@end

// MARK: - Combined Audio Manager

@interface QCAudioHAL ()
@property (nonatomic, strong, readwrite) QCMicrophoneHAL *microphone;
@property (nonatomic, strong, readwrite) QCSpeakerHAL *speaker;
@end

@implementation QCAudioHAL

+ (instancetype)sharedInstance {
    static QCAudioHAL *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _microphone = [[QCMicrophoneHAL alloc] init];
        _speaker = [[QCSpeakerHAL alloc] init];
    }
    return self;
}

- (void)configureAudioSession {
    AVAudioSession *session = [AVAudioSession sharedInstance];
    NSError *error;
    
    // Configure for both playback and recording
    [session setCategory:AVAudioSessionCategoryPlayAndRecord 
             withOptions:AVAudioSessionCategoryOptionDefaultToSpeaker
                   error:&error];
    
    if (error) {
        NSLog(@"Error configuring audio session: %@", error);
    }
}

- (void)setAudioSessionActive:(BOOL)active {
    NSError *error;
    [[AVAudioSession sharedInstance] setActive:active error:&error];
    
    if (error) {
        NSLog(@"Error setting audio session active: %@", error);
    }
}

- (void)routeAudioToSpeaker {
    AVAudioSession *session = [AVAudioSession sharedInstance];
    NSError *error;
    
    [session overrideOutputAudioPort:AVAudioSessionPortOverrideSpeaker error:&error];
    
    if (error) {
        NSLog(@"Error routing audio to speaker: %@", error);
    }
}

- (void)routeAudioToBluetooth {
    // Route audio to connected Bluetooth device (glasses)
    // This would typically be handled by the system when glasses are connected
}

- (NSArray<AVAudioSessionPortDescription *> *)availableAudioRoutes {
    return [[AVAudioSession sharedInstance] availableInputs];
}

- (Float32)systemVolume {
    return [[AVAudioSession sharedInstance] outputVolume];
}

- (void)setSystemVolume:(Float32)volume {
    // Note: System volume cannot be directly set on iOS
    // This would need to use MPVolumeView for user control
    NSLog(@"System volume control requires user interaction on iOS");
}

@end