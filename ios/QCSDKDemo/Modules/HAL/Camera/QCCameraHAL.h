//
//  QCCameraHAL.h
//  Hardware Abstraction Layer - Camera
//
//  Camera interface for Cyan Glasses
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, QCCameraMode) {
    QCCameraModePhoto = 0,
    QCCameraModeVideo,
    QCCameraModeAI,
    QCCameraModeStreaming
};

typedef NS_ENUM(NSInteger, QCCameraResolution) {
    QCCameraResolution720p = 0,
    QCCameraResolution1080p,
    QCCameraResolution4K
};

@protocol QCCameraHALDelegate <NSObject>
@optional
- (void)cameraDidCapturePhoto:(NSData *)photoData;
- (void)cameraDidStartRecording;
- (void)cameraDidStopRecording:(NSURL *)videoURL;
- (void)cameraDidReceiveFrame:(CVPixelBufferRef)pixelBuffer;
- (void)cameraDidEncounterError:(NSError *)error;
@end

@interface QCCameraHAL : NSObject

@property (nonatomic, weak) id<QCCameraHALDelegate> delegate;
@property (nonatomic, readonly) BOOL isRecording;
@property (nonatomic, readonly) QCCameraMode currentMode;
@property (nonatomic, assign) QCCameraResolution resolution;

// Camera Control
- (void)initializeCamera;
- (void)shutdownCamera;

// Photo Operations
- (void)capturePhoto;
- (void)capturePhotoWithCompletion:(void (^)(NSData * _Nullable photoData, NSError * _Nullable error))completion;

// Video Operations
- (void)startVideoRecording;
- (void)stopVideoRecording;
- (NSTimeInterval)currentRecordingDuration;

// AI Photo Operations
- (void)captureAIPhoto;
- (void)processAIImage:(NSData *)imageData;

// Streaming
- (void)startStreaming;
- (void)stopStreaming;

// Settings
- (void)setCameraMode:(QCCameraMode)mode;
- (void)setFlashEnabled:(BOOL)enabled;
- (void)setHDREnabled:(BOOL)enabled;

@end

NS_ASSUME_NONNULL_END