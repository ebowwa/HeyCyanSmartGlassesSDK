#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^GlassesMediaDownloaderStatusHandler)(NSString *status, UIImage * _Nullable previewImage);
typedef void (^GlassesMediaDownloaderCompletionHandler)(NSError * _Nullable error);

@interface GlassesMediaDownloader : NSObject

- (instancetype)initWithStatusHandler:(GlassesMediaDownloaderStatusHandler)statusHandler NS_DESIGNATED_INITIALIZER;
- (instancetype)init NS_UNAVAILABLE;

- (void)startDownloadWithCompletion:(GlassesMediaDownloaderCompletionHandler)completion;

@end

NS_ASSUME_NONNULL_END
