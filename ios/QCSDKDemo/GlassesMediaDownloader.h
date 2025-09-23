#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class GlassesMediaDownloader;

@protocol GlassesMediaDownloaderDelegate <NSObject>
@required
- (void)mediaDownloader:(GlassesMediaDownloader *)downloader didUpdateStatus:(NSString *)status latestImage:(UIImage *_Nullable)image;
- (void)mediaDownloader:(GlassesMediaDownloader *)downloader didFinishWithStatus:(NSString *)status error:(NSError *_Nullable)error latestImage:(UIImage *_Nullable)image;
@end

@interface GlassesMediaDownloader : NSObject

@property (nonatomic, weak) id<GlassesMediaDownloaderDelegate> delegate;

- (void)startDownloadWithSSID:(NSString *)ssid password:(NSString *)password deviceIP:(NSString *)deviceIP;

@end

NS_ASSUME_NONNULL_END
