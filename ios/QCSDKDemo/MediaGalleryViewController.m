//
//  MediaGalleryViewController.m
//  QCSDKDemo
//
//  Created by Claude on 2025/9/24.
//

#import "MediaGalleryViewController.h"
#import <AVFoundation/AVFoundation.h>
#import <AVKit/AVKit.h>

@interface MediaGalleryViewController () <UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, strong) NSArray<NSURL *> *mediaFiles;
@property (nonatomic, strong) NSMutableArray<UIImage *> *thumbnails;
@property (nonatomic, strong) UIActivityIndicatorView *loadingIndicator;
@property (nonatomic, strong) UILabel *emptyLabel;

@end

@implementation MediaGalleryViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    self.title = @"Media Gallery";
    self.view.backgroundColor = [UIColor systemBackgroundColor];

    [self setupNavigationBar];
    [self setupCollectionView];
    [self setupLoadingIndicator];
    [self setupEmptyLabel];

    [self loadMediaFiles];
}

- (void)setupNavigationBar {
    UIBarButtonItem *closeButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone
                                                                                   target:self
                                                                                   action:@selector(closeGallery)];
    self.navigationItem.rightBarButtonItem = closeButton;
}

- (void)setupCollectionView {
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    layout.minimumLineSpacing = 8.0;
    layout.minimumInteritemSpacing = 8.0;

    CGFloat itemSize = (self.view.bounds.size.width - 32.0) / 3.0;
    layout.itemSize = CGSizeMake(itemSize, itemSize);

    self.collectionView = [[UICollectionView alloc] initWithFrame:self.view.bounds collectionViewLayout:layout];
    self.collectionView.backgroundColor = [UIColor systemBackgroundColor];
    self.collectionView.dataSource = self;
    self.collectionView.delegate = self;
    self.collectionView.alwaysBounceVertical = YES;

    [self.collectionView registerClass:[UICollectionViewCell class] forCellWithReuseIdentifier:@"MediaCell"];

    [self.view addSubview:self.collectionView];
}

- (void)setupLoadingIndicator {
    self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleLarge];
    self.loadingIndicator.center = self.view.center;
    self.loadingIndicator.hidesWhenStopped = YES;
    [self.view addSubview:self.loadingIndicator];
}

- (void)setupEmptyLabel {
    self.emptyLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 300, 100)];
    self.emptyLabel.center = self.view.center;
    self.emptyLabel.text = @"No media files found\n\nDownload media from glasses to see them here";
    self.emptyLabel.textAlignment = NSTextAlignmentCenter;
    self.emptyLabel.numberOfLines = 0;
    self.emptyLabel.textColor = [UIColor systemGrayColor];
    self.emptyLabel.hidden = YES;
    [self.view addSubview:self.emptyLabel];
}

- (void)loadMediaFiles {
    [self.loadingIndicator startAnimating];
    self.emptyLabel.hidden = YES;

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *mediaDirectoryPath = self.mediaDirectoryPath;
        if (!mediaDirectoryPath) {
            // Default to Documents/GlassesMedia
            NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
            NSString *documentsDirectory = paths.firstObject;
            mediaDirectoryPath = [documentsDirectory stringByAppendingPathComponent:@"GlassesMedia"];
        }

        NSURL *mediaDirectoryURL = [NSURL fileURLWithPath:mediaDirectoryPath];
        NSArray *keys = @[NSURLNameKey, NSURLTypeIdentifierKey, NSURLContentModificationDateKey];

        NSError *error = nil;
        NSArray *contents = [[NSFileManager defaultManager] contentsOfDirectoryAtURL:mediaDirectoryURL
                                                              includingPropertiesForKeys:keys
                                                                                 options:NSDirectoryEnumerationSkipsHiddenFiles
                                                                                   error:&error];

        NSMutableArray<NSURL *> *mediaFiles = [NSMutableArray array];

        for (NSURL *fileURL in contents) {
            NSString *fileExtension = fileURL.pathExtension.lowercaseString;
            if ([@[@"jpg", @"jpeg", @"png", @"heic", @"mov", @"mp4", @"m4v"] containsObject:fileExtension]) {
                [mediaFiles addObject:fileURL];
            }
        }

        // Sort by modification date
        [mediaFiles sortUsingComparator:^NSComparisonResult(NSURL *url1, NSURL *url2) {
            NSDate *date1 = nil;
            NSDate *date2 = nil;
            [url1 getResourceValue:&date1 forKey:NSURLContentModificationDateKey error:nil];
            [url2 getResourceValue:&date2 forKey:NSURLContentModificationDateKey error:nil];
            return [date2 compare:date1]; // Most recent first
        }];

        dispatch_async(dispatch_get_main_queue(), ^{
            [self.loadingIndicator stopAnimating];
            self.mediaFiles = mediaFiles;
            [self.collectionView reloadData];

            if (self.mediaFiles.count == 0) {
                self.emptyLabel.hidden = NO;
            } else {
                [self generateThumbnails];
            }
        });
    });
}

- (void)generateThumbnails {
    self.thumbnails = [NSMutableArray array];

    for (NSURL *fileURL in self.mediaFiles) {
        NSString *fileExtension = fileURL.pathExtension.lowercaseString;

        if ([@[@"jpg", @"jpeg", @"png", @"heic"] containsObject:fileExtension]) {
            // For images, create a thumbnail
            UIImage *image = [UIImage imageWithContentsOfFile:fileURL.path];
            if (image) {
                // Create thumbnail
                UIGraphicsBeginImageContextWithOptions(CGSizeMake(100, 100), NO, 0.0);
                [image drawInRect:CGRectMake(0, 0, 100, 100)];
                UIImage *thumbnail = UIGraphicsGetImageFromCurrentImageContext();
                UIGraphicsEndImageContext();
                [self.thumbnails addObject:thumbnail ?: [UIImage new]];
            } else {
                [self.thumbnails addObject:[UIImage new]];
            }
        } else {
            // For videos, create a placeholder thumbnail
            UIImage *videoThumbnail = [self generateVideoThumbnail:fileURL];
            [self.thumbnails addObject:videoThumbnail];
        }
    }

    [self.collectionView reloadData];
}

- (UIImage *)generateVideoThumbnail:(NSURL *)videoURL {
    AVAsset *asset = [AVAsset assetWithURL:videoURL];
    AVAssetImageGenerator *generator = [AVAssetImageGenerator assetImageGeneratorWithAsset:asset];
    generator.appliesPreferredTrackTransform = YES;

    NSError *error = nil;
    CGImageRef imageRef = [generator copyCGImageAtTime:kCMTimeZero actualTime:NULL error:&error];

    if (imageRef) {
        UIImage *image = [UIImage imageWithCGImage:imageRef];
        CGImageRelease(imageRef);
        return image;
    }

    // Return a default video thumbnail
    return [self createVideoPlaceholderThumbnail];
}

- (UIImage *)createVideoPlaceholderThumbnail {
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(100, 100), NO, 0.0);
    CGContextRef context = UIGraphicsGetCurrentContext();

    // Gray background
    [[UIColor grayColor] setFill];
    CGContextFillRect(context, CGRectMake(0, 0, 100, 100));

    // Play icon
    [[UIColor whiteColor] setFill];
    CGContextMoveToPoint(context, 35, 25);
    CGContextAddLineToPoint(context, 35, 75);
    CGContextAddLineToPoint(context, 70, 50);
    CGContextClosePath(context);
    CGContextFillPath(context);

    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    return image;
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.mediaFiles.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    UICollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"MediaCell" forIndexPath:indexPath];

    // Clear previous content
    for (UIView *subview in cell.contentView.subviews) {
        [subview removeFromSuperview];
    }

    if (indexPath.row < self.mediaFiles.count) {
        NSURL *fileURL = self.mediaFiles[indexPath.row];
        UIImage *thumbnail = (indexPath.row < self.thumbnails.count) ? self.thumbnails[indexPath.row] : nil;

        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(4, 4, cell.contentView.bounds.size.width - 8, cell.contentView.bounds.size.height - 8)];
        imageView.contentMode = UIViewContentModeScaleAspectFill;
        imageView.clipsToBounds = YES;
        imageView.image = thumbnail;
        imageView.backgroundColor = [UIColor systemGrayColor];

        [cell.contentView addSubview:imageView];

        // Add video badge for video files
        NSString *fileExtension = fileURL.pathExtension.lowercaseString;
        if ([@[@"mov", @"mp4", @"m4v"] containsObject:fileExtension]) {
            UIView *videoBadge = [[UIView alloc] initWithFrame:CGRectMake(imageView.bounds.size.width - 20, imageView.bounds.size.height - 20, 16, 16)];
            videoBadge.backgroundColor = [UIColor blackColor];
            videoBadge.alpha = 0.7;
            videoBadge.layer.cornerRadius = 8;

            UILabel *playLabel = [[UILabel alloc] initWithFrame:videoBadge.bounds];
            playLabel.text = @"▶";
            playLabel.textColor = [UIColor whiteColor];
            playLabel.font = [UIFont systemFontOfSize:8];
            playLabel.textAlignment = NSTextAlignmentCenter;
            [videoBadge addSubview:playLabel];

            [imageView addSubview:videoBadge];
        }
    }

    return cell;
}

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row < self.mediaFiles.count) {
        NSURL *fileURL = self.mediaFiles[indexPath.row];
        [self openMediaFile:fileURL];
    }
}

- (void)openMediaFile:(NSURL *)fileURL {
    NSString *fileExtension = fileURL.pathExtension.lowercaseString;

    if ([@[@"jpg", @"jpeg", @"png", @"heic"] containsObject:fileExtension]) {
        // Open image viewer
        [self openImageInFullscreen:fileURL];
    } else if ([@[@"mov", @"mp4", @"m4v"] containsObject:fileExtension]) {
        // Open video player
        [self playVideo:fileURL];
    }
}

- (void)openImageInFullscreen:(NSURL *)imageURL {
    UIImage *image = [UIImage imageWithContentsOfFile:imageURL.path];
    if (!image) return;

    UIViewController *imageViewer = [[UIViewController alloc] init];
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:imageViewer.view.bounds];
    imageView.image = image;
    imageView.contentMode = UIViewContentModeScaleAspectFit;
    imageView.backgroundColor = [UIColor blackColor];
    imageView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    [imageViewer.view addSubview:imageView];

    // Add tap to dismiss
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:imageViewer action:@selector(dismissViewControllerAnimated:completion:)];
    [imageView addGestureRecognizer:tap];

    [self presentViewController:imageViewer animated:YES completion:nil];
}

- (void)playVideo:(NSURL *)videoURL {
    AVPlayer *player = [AVPlayer playerWithURL:videoURL];
    AVPlayerViewController *playerViewController = [[AVPlayerViewController alloc] init];
    playerViewController.player = player;

    [self presentViewController:playerViewController animated:YES completion:^{
        [player play];
    }];
}

- (void)closeGallery {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator {
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];

    [coordinator animateAlongsideTransition:^(id<UIViewControllerTransitionCoordinatorContext> context) {
        UICollectionViewFlowLayout *layout = (UICollectionViewFlowLayout *)self.collectionView.collectionViewLayout;
        CGFloat itemSize = (size.width - 32.0) / 3.0;
        layout.itemSize = CGSizeMake(itemSize, itemSize);
        [self.collectionView.collectionViewLayout invalidateLayout];
    } completion:nil];
}

@end