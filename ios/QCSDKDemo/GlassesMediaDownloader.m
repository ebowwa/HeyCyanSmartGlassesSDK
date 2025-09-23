#import "GlassesMediaDownloader.h"

#import <NetworkExtension/NetworkExtension.h>

static NSString *const kMediaConfigPath = @"files/media.config";
static NSString *const kMediaDestinationDirectoryName = @"GlassesMedia";

@interface GlassesMediaDownloader ()

@property (nonatomic, copy) NSString *ssid;
@property (nonatomic, copy) NSString *password;
@property (nonatomic, copy) NSString *deviceIP;
@property (nonatomic, copy) NSString *destinationDirectory;
@property (nonatomic, strong) NSURLSession *session;
@property (nonatomic, strong) NSArray<NSString *> *pendingFiles;
@property (nonatomic, assign) NSUInteger currentFileIndex;
@property (nonatomic, strong, nullable) UIImage *latestImage;

@end

@implementation GlassesMediaDownloader

- (void)startDownloadWithSSID:(NSString *)ssid password:(NSString *)password deviceIP:(NSString *)deviceIP {
    self.ssid = ssid ?: @"";
    self.password = password ?: @"";
    self.deviceIP = deviceIP ?: @"";
    self.latestImage = nil;
    self.pendingFiles = @[];
    self.currentFileIndex = 0;

    if (self.ssid.length == 0 || self.deviceIP.length == 0) {
        NSError *error = [NSError errorWithDomain:NSURLErrorDomain
                                             code:NSURLErrorBadURL
                                         userInfo:@{NSLocalizedDescriptionKey : @"Missing SSID or device IP."}];
        [self notifyFinishWithStatus:@"Missing Wi-Fi credentials." error:error];
        return;
    }

    NSString *documentsPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
    if (!documentsPath) {
        NSError *error = [NSError errorWithDomain:NSCocoaErrorDomain
                                             code:NSFileNoSuchFileError
                                         userInfo:@{NSLocalizedDescriptionKey : @"Unable to locate Documents directory."}];
        [self notifyFinishWithStatus:@"Unable to access Documents directory." error:error];
        return;
    }

    self.destinationDirectory = [documentsPath stringByAppendingPathComponent:kMediaDestinationDirectoryName];

    NSError *directoryError = nil;
    if (![[NSFileManager defaultManager] fileExistsAtPath:self.destinationDirectory]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:self.destinationDirectory
                                  withIntermediateDirectories:YES
                                                   attributes:nil
                                                        error:&directoryError];
    }

    if (directoryError) {
        [self notifyFinishWithStatus:@"Failed to create destination folder." error:directoryError];
        return;
    }

    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    configuration.timeoutIntervalForRequest = 30.0;
    configuration.timeoutIntervalForResource = 120.0;
    self.session = [NSURLSession sessionWithConfiguration:configuration];

    [self notifyStatus:[NSString stringWithFormat:@"Connecting to %@…", self.ssid]];
    [self connectToHotspot];
}

#pragma mark - Private helpers

- (void)connectToHotspot {
    if (@available(iOS 11.0, *)) {
        NEHotspotConfiguration *configuration = nil;
        if (self.password.length > 0) {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid
                                                              passphrase:self.password
                                                                   isWEP:NO];
        } else {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid];
        }
        configuration.joinOnce = YES;

        __weak typeof(self) weakSelf = self;
        [[NEHotspotConfigurationManager sharedManager] applyConfiguration:configuration
                                                        completionHandler:^(NSError * _Nullable error) {
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf) { return; }

            if (error && error.code != NEHotspotConfigurationErrorAlreadyAssociated) {
                NSString *message = [NSString stringWithFormat:@"Failed to join hotspot (%@).", error.localizedDescription ?: @"unknown error"];
                [strongSelf notifyFinishWithStatus:message error:error];
                return;
            }

            [strongSelf notifyStatus:@"Connected to glasses hotspot. Fetching manifest…"];
            [strongSelf fetchManifest];
        }];
    } else {
        NSError *error = [NSError errorWithDomain:NSCocoaErrorDomain
                                             code:NSFeatureUnsupportedError
                                         userInfo:@{NSLocalizedDescriptionKey : @"Hotspot configuration is not supported on this iOS version."}];
        [self notifyFinishWithStatus:@"Hotspot configuration not supported." error:error];
    }
}

- (void)fetchManifest {
    NSURL *url = [self urlForPath:kMediaConfigPath];
    if (!url) {
        NSError *error = [NSError errorWithDomain:NSURLErrorDomain
                                             code:NSURLErrorBadURL
                                         userInfo:@{NSLocalizedDescriptionKey : @"Unable to construct manifest URL."}];
        [self notifyFinishWithStatus:@"Invalid manifest URL." error:error];
        return;
    }

    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:url
                                             completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        if (error) {
            NSString *message = [NSString stringWithFormat:@"Failed to download manifest (%@).", error.localizedDescription ?: @"unknown error"];
            [strongSelf notifyFinishWithStatus:message error:error];
            return;
        }

        if (![response isKindOfClass:[NSHTTPURLResponse class]]) {
            NSError *responseError = [NSError errorWithDomain:NSURLErrorDomain
                                                         code:NSURLErrorBadServerResponse
                                                     userInfo:@{NSLocalizedDescriptionKey : @"Unexpected response."}];
            [strongSelf notifyFinishWithStatus:@"Unexpected manifest response." error:responseError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode != 200 || data.length == 0) {
            NSError *statusError = [NSError errorWithDomain:NSURLErrorDomain
                                                       code:httpResponse.statusCode
                                                   userInfo:@{NSLocalizedDescriptionKey : [NSString stringWithFormat:@"Manifest request returned status %ld", (long)httpResponse.statusCode]}];
            [strongSelf notifyFinishWithStatus:@"Manifest download failed." error:statusError];
            return;
        }

        NSArray<NSString *> *files = [strongSelf parseManifest:data];
        if (files.count == 0) {
            NSError *emptyError = [NSError errorWithDomain:NSCocoaErrorDomain
                                                      code:NSFileNoSuchFileError
                                                  userInfo:@{NSLocalizedDescriptionKey : @"Manifest did not list any files."}];
            [strongSelf notifyFinishWithStatus:@"No media listed in manifest." error:emptyError];
            return;
        }

        strongSelf.pendingFiles = files;
        strongSelf.currentFileIndex = 0;
        [strongSelf notifyStatus:[NSString stringWithFormat:@"Found %lu files. Starting download…", (unsigned long)files.count]];
        [strongSelf downloadNextFile];
    }];

    [task resume];
}

- (NSArray<NSString *> *)parseManifest:(NSData *)data {
    NSString *manifest = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    if (!manifest) {
        return @[];
    }

    NSMutableArray<NSString *> *results = [NSMutableArray array];
    NSCharacterSet *newlineSet = [NSCharacterSet newlineCharacterSet];
    NSArray<NSString *> *lines = [manifest componentsSeparatedByCharactersInSet:newlineSet];
    for (NSString *line in lines) {
        NSString *trimmed = [line stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if (trimmed.length == 0 || [trimmed hasPrefix:@"#"]) {
            continue;
        }
        [results addObject:trimmed];
    }
    return results;
}

- (void)downloadNextFile {
    if (self.currentFileIndex >= self.pendingFiles.count) {
        [self notifyFinishWithStatus:@"Completed media download." error:nil];
        return;
    }

    NSString *relativePath = self.pendingFiles[self.currentFileIndex];
    NSString *displayName = relativePath.lastPathComponent.length > 0 ? relativePath.lastPathComponent : relativePath;
    [self notifyStatus:[NSString stringWithFormat:@"Downloading %lu/%lu: %@", (unsigned long)(self.currentFileIndex + 1), (unsigned long)self.pendingFiles.count, displayName]];

    NSURL *url = [self urlForPath:relativePath];
    if (!url) {
        NSError *error = [NSError errorWithDomain:NSURLErrorDomain
                                             code:NSURLErrorBadURL
                                         userInfo:@{NSLocalizedDescriptionKey : [NSString stringWithFormat:@"Invalid URL for %@", relativePath]}];
        [self notifyFinishWithStatus:@"Encountered invalid media URL." error:error];
        return;
    }

    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:url
                                             completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        if (error) {
            NSString *message = [NSString stringWithFormat:@"Failed to download %@ (%@).", displayName, error.localizedDescription ?: @"unknown error"];
            [strongSelf notifyFinishWithStatus:message error:error];
            return;
        }

        if (![response isKindOfClass:[NSHTTPURLResponse class]]) {
            NSError *responseError = [NSError errorWithDomain:NSURLErrorDomain
                                                         code:NSURLErrorBadServerResponse
                                                     userInfo:@{NSLocalizedDescriptionKey : @"Unexpected response."}];
            [strongSelf notifyFinishWithStatus:@"Unexpected response while downloading media." error:responseError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode != 200 || data.length == 0) {
            NSError *statusError = [NSError errorWithDomain:NSURLErrorDomain
                                                       code:httpResponse.statusCode
                                                   userInfo:@{NSLocalizedDescriptionKey : [NSString stringWithFormat:@"Download returned status %ld", (long)httpResponse.statusCode]}];
            [strongSelf notifyFinishWithStatus:@"Media download failed." error:statusError];
            return;
        }

        NSError *writeError = nil;
        NSString *destinationPath = [strongSelf.destinationDirectory stringByAppendingPathComponent:relativePath];
        NSString *destinationFolder = [destinationPath stringByDeletingLastPathComponent];
        if (destinationFolder.length > 0 && ![[NSFileManager defaultManager] fileExistsAtPath:destinationFolder]) {
            [[NSFileManager defaultManager] createDirectoryAtPath:destinationFolder
                                      withIntermediateDirectories:YES
                                                       attributes:nil
                                                            error:&writeError];
        }

        if (!writeError) {
            [data writeToFile:destinationPath options:NSDataWritingAtomic error:&writeError];
        }

        if (writeError) {
            NSString *message = [NSString stringWithFormat:@"Failed to save %@ (%@).", displayName, writeError.localizedDescription ?: @"unknown error"];
            [strongSelf notifyFinishWithStatus:message error:writeError];
            return;
        }

        UIImage *image = [strongSelf previewImageForData:data filePath:destinationPath];
        if (image) {
            strongSelf.latestImage = image;
        }

        strongSelf.currentFileIndex += 1;
        NSString *progressMessage = [NSString stringWithFormat:@"Saved %@ (%lu/%lu)", displayName, (unsigned long)strongSelf.currentFileIndex, (unsigned long)strongSelf.pendingFiles.count];
        [strongSelf notifyStatus:progressMessage image:strongSelf.latestImage];
        [strongSelf downloadNextFile];
    }];

    [task resume];
}

- (nullable UIImage *)previewImageForData:(NSData *)data filePath:(NSString *)filePath {
    NSString *extension = filePath.pathExtension.lowercaseString;
    NSSet<NSString *> *imageExtensions = [NSSet setWithArray:@[@"jpg", @"jpeg", @"png", @"heic", @"heif", @"gif"]];
    if (![imageExtensions containsObject:extension]) {
        return nil;
    }
    UIImage *image = [UIImage imageWithData:data];
    return image;
}

- (NSURL *)urlForPath:(NSString *)path {
    if (path.length == 0) {
        return nil;
    }

    if ([path hasPrefix:@"http://"] || [path hasPrefix:@"https://"]) {
        return [NSURL URLWithString:path];
    }

    NSString *trimmed = [path hasPrefix:@"/"] ? [path substringFromIndex:1] : path;
    NSCharacterSet *allowedSet = [NSCharacterSet URLPathAllowedCharacterSet];
    NSString *encodedPath = [trimmed stringByAddingPercentEncodingWithAllowedCharacters:allowedSet];
    if (!encodedPath) {
        encodedPath = trimmed;
    }

    NSString *urlString = [NSString stringWithFormat:@"http://%@/%@", self.deviceIP, encodedPath];
    return [NSURL URLWithString:urlString];
}

- (void)notifyStatus:(NSString *)status {
    [self notifyStatus:status image:self.latestImage];
}

- (void)notifyStatus:(NSString *)status image:(UIImage *_Nullable)image {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(mediaDownloader:didUpdateStatus:latestImage:)]) {
            [self.delegate mediaDownloader:self didUpdateStatus:status latestImage:image];
        }
    });
}

- (void)notifyFinishWithStatus:(NSString *)status error:(NSError *_Nullable)error {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.delegate respondsToSelector:@selector(mediaDownloader:didFinishWithStatus:error:latestImage:)]) {
            [self.delegate mediaDownloader:self didFinishWithStatus:status error:error latestImage:self.latestImage];
        }
    });
}

@end
