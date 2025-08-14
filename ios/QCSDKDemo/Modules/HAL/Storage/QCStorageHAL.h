//
//  QCStorageHAL.h
//  Hardware Abstraction Layer - Storage
//
//  Storage management interface for Cyan Glasses
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, QCStorageType) {
    QCStorageTypeInternal = 0,
    QCStorageTypeSDCard,
    QCStorageTypeCloud
};

typedef NS_ENUM(NSInteger, QCFileType) {
    QCFileTypePhoto = 0,
    QCFileTypeVideo,
    QCFileTypeAudio,
    QCFileTypeDocument,
    QCFileTypeSystem,
    QCFileTypeOther
};

@protocol QCStorageHALDelegate <NSObject>
@optional
- (void)storageDidUpdateAvailableSpace:(int64_t)bytes;
- (void)storageDidBecomeNearlyFull:(NSInteger)percentageUsed;
- (void)storageDidBecomeFull;
- (void)storageDidCompleteFileOperation:(NSString *)operationId success:(BOOL)success;
- (void)storageDidUpdateTransferProgress:(CGFloat)progress forFile:(NSString *)fileName;
@end

@interface QCStorageHAL : NSObject

@property (nonatomic, weak) id<QCStorageHALDelegate> delegate;
@property (nonatomic, readonly) int64_t totalSpace;      // Bytes
@property (nonatomic, readonly) int64_t availableSpace;  // Bytes
@property (nonatomic, readonly) int64_t usedSpace;       // Bytes
@property (nonatomic, readonly) CGFloat percentageUsed;

// Storage Monitoring
- (void)startStorageMonitoring;
- (void)stopStorageMonitoring;
- (void)updateStorageStatus;

// File Management
- (NSArray<NSString *> *)listFilesOfType:(QCFileType)type;
- (NSArray<NSString *> *)listAllFiles;
- (NSDictionary *)getFileInfo:(NSString *)filePath;
- (int64_t)getFileSize:(NSString *)filePath;

// File Operations
- (BOOL)saveData:(NSData *)data toPath:(NSString *)path;
- (NSData * _Nullable)loadDataFromPath:(NSString *)path;
- (BOOL)deleteFileAtPath:(NSString *)path;
- (BOOL)moveFileFromPath:(NSString *)sourcePath toPath:(NSString *)destinationPath;
- (BOOL)copyFileFromPath:(NSString *)sourcePath toPath:(NSString *)destinationPath;

// Batch Operations
- (void)deleteAllFilesOfType:(QCFileType)type;
- (void)deleteFilesOlderThan:(NSDate *)date;
- (void)compressFilesAtPaths:(NSArray<NSString *> *)paths toArchive:(NSString *)archivePath;

// Media Management
- (NSInteger)photoCount;
- (NSInteger)videoCount;
- (NSInteger)audioCount;
- (int64_t)totalMediaSize;

// Storage Optimization
- (void)cleanupTemporaryFiles;
- (void)optimizeStorage;
- (int64_t)calculateReclaimableSpace;
- (void)clearCache;

// Transfer Operations
- (void)exportFileAtPath:(NSString *)path toURL:(NSURL *)destinationURL;
- (void)importFileFromURL:(NSURL *)sourceURL toPath:(NSString *)destinationPath;
- (void)syncWithCloud;

// Storage Settings
- (void)setStorageWarningThreshold:(CGFloat)percentage;
- (void)enableAutoCleanup:(BOOL)enable;
- (void)setMaxFileAge:(NSTimeInterval)seconds forType:(QCFileType)type;

// Storage Statistics
- (NSDictionary *)getStorageStatistics;
- (NSDictionary *)getStorageUsageByType;
- (NSArray *)getMostRecentFiles:(NSInteger)count;

@end

NS_ASSUME_NONNULL_END