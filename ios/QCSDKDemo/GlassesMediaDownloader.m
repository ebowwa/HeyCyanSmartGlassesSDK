#import "GlassesMediaDownloader.h"

#import <QCSDK/QCSDKCmdCreator.h>
#import <NetworkExtension/NetworkExtension.h>

static NSString * const GlassesMediaDownloaderErrorDomain = @"GlassesMediaDownloaderErrorDomain";

typedef NS_ENUM(NSInteger, GlassesMediaDownloaderErrorCode) {
    GlassesMediaDownloaderErrorCodeWifiCredentials = 1,
    GlassesMediaDownloaderErrorCodeWifiIP,
    GlassesMediaDownloaderErrorCodeHotspotUnavailable,
    GlassesMediaDownloaderErrorCodeManifest,
    GlassesMediaDownloaderErrorCodeDownload,
    GlassesMediaDownloaderErrorCodeFilesystem
};

@interface GlassesMediaDownloader ()
@property (nonatomic, copy) GlassesMediaDownloaderStatusHandler statusHandler;
@property (nonatomic, copy) GlassesMediaDownloaderCompletionHandler completionHandler;
@property (nonatomic, copy) NSString *ssid;
@property (nonatomic, copy) NSString *password;
@property (nonatomic, copy) NSString *deviceIP;
@property (nonatomic, strong) NSURLSession *session;
@property (nonatomic, assign) BOOL didFinish;
@end

@implementation GlassesMediaDownloader

- (instancetype)initWithStatusHandler:(GlassesMediaDownloaderStatusHandler)statusHandler {
    self = [super init];
    if (self) {
        _statusHandler = [statusHandler copy];
        _session = [NSURLSession sessionWithConfiguration:NSURLSessionConfiguration.defaultSessionConfiguration];
    }
    return self;
}

- (void)startDownloadWithCompletion:(GlassesMediaDownloaderCompletionHandler)completion {
    self.completionHandler = [completion copy];
    self.didFinish = NO;
    [self updateStatus:@"Requesting Wi-Fi credentials..." preview:nil];
    [self requestWifiCredentials];
}

#pragma mark - Flow

- (void)requestWifiCredentials {
    __weak typeof(self) weakSelf = self;
    [QCSDKCmdCreator openWifiWithMode:QCOperatorDeviceModeTransfer success:^(NSString *ssid, NSString *password) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        strongSelf.ssid = ssid ?: @"";
        strongSelf.password = password ?: @"";
        [strongSelf updateStatus:[NSString stringWithFormat:@"Received hotspot: %@", ssid ?: @"<unknown>"] preview:nil];
        [strongSelf requestDeviceIP];
    } fail:^(NSInteger code) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        NSString *description = [NSString stringWithFormat:@"Failed to request Wi-Fi credentials (code %ld).", (long)code];
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeWifiCredentials userInfo:@{NSLocalizedDescriptionKey : description}];
        [strongSelf finishWithError:error];
    }];
}

- (void)requestDeviceIP {
    __weak typeof(self) weakSelf = self;
    [self updateStatus:@"Retrieving device IP address..." preview:nil];
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        if (ipAddress.length == 0) {
            NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeWifiIP userInfo:@{NSLocalizedDescriptionKey : @"Device did not report a Wi-Fi IP address."}];
            [strongSelf finishWithError:error];
            return;
        }
        strongSelf.deviceIP = ipAddress;
        [strongSelf joinHotspot];
    } failed:^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeWifiIP userInfo:@{NSLocalizedDescriptionKey : @"Failed to retrieve device Wi-Fi IP address."}];
        [strongSelf finishWithError:error];
    }];
}

- (void)joinHotspot {
    [self updateStatus:@"Joining device hotspot..." preview:nil];
    if (@available(iOS 11.0, *)) {
        NEHotspotConfiguration *configuration = nil;
        if (self.password.length > 0) {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid passphrase:self.password isWEP:NO];
        } else {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid];
        }
        configuration.joinOnce = YES;
        __weak typeof(self) weakSelf = self;
        [[NEHotspotConfigurationManager sharedManager] applyConfiguration:configuration completionHandler:^(NSError * _Nullable error) {
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf) { return; }
            if (!error || error.code == NEHotspotConfigurationErrorAlreadyAssociated) {
                [strongSelf updateStatus:@"Connected to device hotspot." preview:nil];
                [strongSelf downloadManifest];
            } else {
                NSString *description = [NSString stringWithFormat:@"Unable to join hotspot (%@).", error.localizedDescription ?: @"unknown error"];
                NSError *joinError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeHotspotUnavailable userInfo:@{NSLocalizedDescriptionKey : description}];
                [strongSelf finishWithError:joinError];
            }
        }];
    } else {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeHotspotUnavailable userInfo:@{NSLocalizedDescriptionKey : @"Hotspot configuration requires iOS 11 or later."}];
        [self finishWithError:error];
    }
}

- (void)downloadManifest {
    NSString *manifestString = [NSString stringWithFormat:@"http://%@/files/media.config", self.deviceIP];
    NSURL *manifestURL = [NSURL URLWithString:manifestString];
    if (!manifestURL) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"Invalid manifest URL."}];
        [self finishWithError:error];
        return;
    }

    [self updateStatus:@"Fetching media manifest..." preview:nil];
    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:manifestURL completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        if (error) {
            NSString *description = [NSString stringWithFormat:@"Failed to download manifest: %@", error.localizedDescription ?: @"unknown error"];
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *description = [NSString stringWithFormat:@"Manifest request returned HTTP %ld.", (long)httpResponse.statusCode];
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSString *configString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        if (!configString) {
            configString = [[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
        }
        if (configString.length == 0) {
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"Media manifest is empty."}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSArray<NSURL *> *fileURLs = [strongSelf fileURLsFromManifestString:configString baseURL:manifestURL];
        [strongSelf prepareDownloadDirectoryWithManifest:fileURLs baseURL:manifestURL];
    }];
    [task resume];
}

- (NSArray<NSURL *> *)fileURLsFromManifestString:(NSString *)manifest baseURL:(NSURL *)manifestURL {
    NSArray<NSString *> *lines = [manifest componentsSeparatedByCharactersInSet:NSCharacterSet.newlineCharacterSet];
    NSMutableArray<NSURL *> *fileURLs = [NSMutableArray array];
    NSURL *baseURL = [manifestURL URLByDeletingLastPathComponent];
    for (NSString *line in lines) {
        NSString *trimmed = [line stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if (trimmed.length == 0) { continue; }
        if ([trimmed hasPrefix:@"#"]) { continue; }
        NSURL *resolvedURL = nil;
        if ([trimmed hasPrefix:@"http://"] || [trimmed hasPrefix:@"https://"]) {
            resolvedURL = [NSURL URLWithString:trimmed];
        } else {
            resolvedURL = [NSURL URLWithString:trimmed relativeToURL:baseURL];
        }
        if (resolvedURL) {
            [fileURLs addObject:resolvedURL.absoluteURL];
        }
    }
    return fileURLs.copy;
}

- (void)prepareDownloadDirectoryWithManifest:(NSArray<NSURL *> *)fileURLs baseURL:(NSURL *)manifestURL {
    (void)manifestURL;
    NSString *documentsDirectory = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
    if (documentsDirectory.length == 0) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeFilesystem userInfo:@{NSLocalizedDescriptionKey : @"Unable to locate the Documents directory."}];
        [self finishWithError:error];
        return;
    }

    NSString *mediaDirectoryPath = [documentsDirectory stringByAppendingPathComponent:@"GlassesMedia"];
    NSURL *mediaDirectoryURL = [NSURL fileURLWithPath:mediaDirectoryPath isDirectory:YES];

    NSError *directoryError = nil;
    if (![[NSFileManager defaultManager] createDirectoryAtURL:mediaDirectoryURL withIntermediateDirectories:YES attributes:nil error:&directoryError]) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeFilesystem userInfo:@{NSLocalizedDescriptionKey : directoryError.localizedDescription ?: @"Unable to prepare download directory."}];
        [self finishWithError:error];
        return;
    }

    if (fileURLs.count == 0) {
        [self updateStatus:@"No media listed in manifest." preview:nil];
        [self finishWithError:nil];
        return;
    }

    [self downloadFiles:fileURLs toDirectory:mediaDirectoryURL atIndex:0 latestPreview:nil];
}

- (void)downloadFiles:(NSArray<NSURL *> *)fileURLs toDirectory:(NSURL *)directoryURL atIndex:(NSUInteger)index latestPreview:(UIImage * _Nullable)latestPreview {
    if (index >= fileURLs.count) {
        [self updateStatus:@"All media downloaded." preview:latestPreview];
        [self finishWithError:nil];
        return;
    }

    NSURL *fileURL = fileURLs[index];
    NSString *filename = fileURL.lastPathComponent.length > 0 ? fileURL.lastPathComponent : [NSString stringWithFormat:@"media_%lu", (unsigned long)index];
    NSURL *destinationURL = [directoryURL URLByAppendingPathComponent:filename];

    [self updateStatus:[NSString stringWithFormat:@"Downloading %lu/%lu: %@", (unsigned long)(index + 1), (unsigned long)fileURLs.count, filename] preview:latestPreview];

    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:fileURL completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        if (error) {
            NSString *description = [NSString stringWithFormat:@"Failed to download %@: %@", filename, error.localizedDescription ?: @"unknown error"];
            NSError *downloadError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:downloadError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *description = [NSString stringWithFormat:@"Failed to download %@: HTTP %ld", filename, (long)httpResponse.statusCode];
            NSError *downloadError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:downloadError];
            return;
        }

        if (data.length == 0) {
            NSString *description = [NSString stringWithFormat:@"%@ is empty.", filename];
            NSError *downloadError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:downloadError];
            return;
        }

        [[NSFileManager defaultManager] removeItemAtURL:destinationURL error:nil];
        NSError *writeError = nil;
        if (![data writeToURL:destinationURL options:NSDataWritingAtomic error:&writeError]) {
            NSString *description = [NSString stringWithFormat:@"Failed to save %@: %@", filename, writeError.localizedDescription ?: @"unknown error"];
            NSError *filesystemError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeFilesystem userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:filesystemError];
            return;
        }

        UIImage *previewImage = [strongSelf previewImageForData:data];
        UIImage *nextPreview = previewImage ?: latestPreview;
        NSString *status = [NSString stringWithFormat:@"Saved %@", filename];
        [strongSelf updateStatus:status preview:nextPreview];
        [strongSelf downloadFiles:fileURLs toDirectory:directoryURL atIndex:index + 1 latestPreview:nextPreview];
    }];
    [task resume];
}

#pragma mark - Helpers

- (UIImage * _Nullable)previewImageForData:(NSData *)data {
    UIImage *image = [UIImage imageWithData:data scale:UIScreen.mainScreen.scale];
    return image;
}

- (void)updateStatus:(NSString *)status preview:(UIImage * _Nullable)preview {
    if (!self.statusHandler) { return; }
    dispatch_async(dispatch_get_main_queue(), ^{
        self.statusHandler(status, preview);
    });
}

- (void)finishWithError:(NSError * _Nullable)error {
    if (self.didFinish) { return; }
    self.didFinish = YES;
    GlassesMediaDownloaderCompletionHandler completion = self.completionHandler;
    if (completion) {
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(error);
        });
    }
    self.completionHandler = nil;
}

@end
