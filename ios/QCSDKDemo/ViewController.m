//
//  ViewController.m
//  QCSDKDemo
//
//  Created by steve on 2025/7/22.
//

#import "ViewController.h"
#import <QCSDK/QCVersionHelper.h>
#import <QCSDK/QCSDKManager.h>
#import <QCSDK/QCSDKCmdCreator.h>
#import "GlassesMediaDownloader.h"

#import "QCScanViewController.h"
#import "QCCentralManager.h"

typedef NS_ENUM(NSInteger, QGDeviceActionType) {
    /// Get hardware version, firmware version, and WiFi firmware versions
    QGDeviceActionTypeGetVersion = 0,

    /// Set the current device time
    QGDeviceActionTypeSetTime,

    /// Get battery level and charging status
    QGDeviceActionTypeGetBattery,

    /// Get the number of photos, videos, and audio files on the device
    QGDeviceActionTypeGetMediaInfo,

    /// Trigger the device to take a photo
    QGDeviceActionTypeTakePhoto,

    /// Start or stop video recording
    QGDeviceActionTypeToggleVideoRecording,

    /// Start or stop audio recording
    QGDeviceActionTypeToggleAudioRecording,
    
    /// Take AI Image
    QGDeviceActionTypeToggleTakeAIImage,

    /// Download media over Wi-Fi
    QGDeviceActionTypeDownloadMedia,

    /// Reserved for future use
    QGDeviceActionTypeReserved,
};



@interface ViewController ()<UITableViewDelegate, UITableViewDataSource,QCCentralManagerDelegate,QCSDKManagerDelegate>

@property(nonatomic,strong)UIBarButtonItem *rightItem;
@property(nonatomic,strong)UITableView *tableView;

@property(nonatomic,copy)NSString *hardVersion;
@property(nonatomic,copy)NSString *firmVersion;
@property(nonatomic,copy)NSString *hardWiFiVersion;
@property(nonatomic,copy)NSString *firmWiFiVersion;

@property(nonatomic,copy)NSString *mac;

@property(nonatomic,assign)NSInteger battary;
@property(nonatomic,assign)BOOL charging;

@property(nonatomic,assign)NSInteger photoCount;
@property(nonatomic,assign)NSInteger videoCount;
@property(nonatomic,assign)NSInteger audioCount;

@property(nonatomic,assign)BOOL recordingVideo;
@property(nonatomic,assign)BOOL recordingAudio;

@property(nonatomic,strong)NSData *aiImageData;

@property(nonatomic,strong)GlassesMediaDownloader *mediaDownloader;
@property(nonatomic,copy)NSString *mediaDownloadStatus;
@property(nonatomic,strong)UIImage *latestDownloadedMediaPreview;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"Feature(Tap to get data)";
    
    self.rightItem = [[UIBarButtonItem alloc] initWithTitle:@"Search"
                                                      style:(UIBarButtonItemStylePlain)
                                                     target:self
                                                     action:@selector(rightAction)];
    self.navigationItem.rightBarButtonItem = self.rightItem;
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:(UITableViewStylePlain)];
    self.tableView.backgroundColor = [UIColor clearColor];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.estimatedRowHeight = 60;
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    self.tableView.hidden = YES;
    [self.view addSubview:self.tableView];
    
    [QCSDKManager shareInstance].delegate = self;
}

#pragma mark - Device Data Report
- (void)didUpdateBatteryLevel:(NSInteger)battery charging:(BOOL)charging {
    self.battary = battery;
    self.charging = charging;
    [self.tableView reloadData];
}

- (void)didUpdateMediaWithPhotoCount:(NSInteger)photo videoCount:(NSInteger)video audioCount:(NSInteger)audio type:(NSInteger)type {
    
    self.photoCount = photo;
    self.videoCount = video;
    self.audioCount = audio;
    [self.tableView reloadData];
}

- (void)didReceiveAIChatImageData:(NSData *)imageData {
    NSLog(@"didReceiveAIChatImageData");
    self.aiImageData = imageData;
    [self.tableView reloadData];
}

#pragma mark - Feature Fuctions
- (void)getHardVersionAndFirmVersion {
    [QCSDKCmdCreator getDeviceVersionInfoSuccess:^(NSString * _Nonnull hdVersion, NSString * _Nonnull firmVersion, NSString * _Nonnull hdWifiVersion, NSString * _Nonnull firmWifiVersion) {
        
        self.hardVersion = hdVersion;
        self.firmVersion = firmVersion;
        self.hardWiFiVersion = hdWifiVersion;
        self.firmWiFiVersion = firmWifiVersion;
        [self.tableView reloadData];
        NSLog(@"hard Version:%@",hdVersion);
        NSLog(@"firm Version:%@",firmVersion);
        NSLog(@"hard Wifi Version:%@",hdWifiVersion);
        NSLog(@"firm Wifi Version:%@",firmWifiVersion);
    } fail:^{
        NSLog(@"get version fail");
    }];
}

- (void)getMacAddress {
    //[QCSDKCmdCreator get
    [QCSDKCmdCreator getDeviceMacAddressSuccess:^(NSString * _Nullable macAddress) {
        self.mac = macAddress;
        [self.tableView reloadData];
    } fail:^{
        NSLog(@"get mac address fail");
    }];
}

- (void)setTime {
    [QCSDKCmdCreator setupDeviceDateTime:^(BOOL isSuccess, NSError * _Nullable err) {
        if (err) {
            NSLog(@"get err fail");
        }
    }];
}

- (void)getBattary {
    [QCSDKCmdCreator getDeviceBattery:^(NSInteger battary, BOOL charging) {
        
        self.battary = battary;
        self.charging = charging;
        [self.tableView reloadData];
    } fail:^{
        
    }];
}

- (void)getMediaInfo {
    [QCSDKCmdCreator getDeviceMedia:^(NSInteger photo, NSInteger video, NSInteger audio, NSInteger type) {
        
        self.photoCount = photo;
        self.videoCount = video;
        self.audioCount = audio;
        [self.tableView reloadData];
    } fail:^{
        
    }];
}

- (void)takePhoto {
    //
    [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModePhoto) success:^{
        
    } fail:^(NSInteger mode) {
        NSLog(@"set fail,current device model:%zd",mode);
    }];
}

- (void)recordVideo {
    
    if (self.recordingVideo) {
        
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeVideoStop) success:^{
            self.recordingVideo = NO;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);
        }];
    }
    else {
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeVideo) success:^{
            self.recordingVideo = YES;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);

        }];
    }
}

- (void)recordAudio {
    if (self.recordingVideo) {
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAudioStop) success:^{
            self.recordingAudio = NO;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);
        }];
    } else {
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAudio) success:^{
            self.recordingAudio = YES;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);
        }];
    }
}

- (void)takeAIImage {
    //- (void)didReceiveAIChatImageData:(NSData *)imageData
    [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAIPhoto) success:^{
        
    } fail:^(NSInteger mode) {
        NSLog(@"set fail,current device model:%zd",mode);
    }];
}

#pragma mark - Actions
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [QCCentralManager shared].delegate = self;
    [self didState:[QCCentralManager shared].deviceState];
}

- (void)rightAction {
    
    if([self.rightItem.title isEqualToString:@"Unbind"]) {
        [[QCCentralManager shared] remove];
    }
    else if ([self.rightItem.title isEqualToString:@"Search"])  {
        QCScanViewController *viewCtrl = [[QCScanViewController alloc] init];
        [self.navigationController pushViewController:viewCtrl animated:true];
    }
}

#pragma mark - QCCentralManagerDelegate
- (void)didState:(QCState)state {
    self.title = @"Feature";
    switch(state) {
        case QCStateUnbind:
            self.rightItem.title = @"Search";
            self.tableView.hidden = YES;
            break;
        case QCStateConnecting:
            self.title = [QCCentralManager shared].connectedPeripheral.name;
            self.rightItem.title = @"Connecting";
            self.rightItem.enabled = NO;
            self.tableView.hidden = YES;
            break;
        case QCStateConnected:
            self.title = [NSString stringWithFormat:@"%@(Tap to get data)",[QCCentralManager shared].connectedPeripheral.name];
            self.rightItem.title = @"Unbind";
            self.rightItem.enabled = YES;
            self.tableView.hidden = NO;
            break;
        case QCStateUnkown:
            break;
        case QCStateDisconnecting:
        case QCStateDisconnected:
            self.rightItem.title = @"Search";
            self.rightItem.enabled = YES;
            self.tableView.hidden = YES;
            break;
    }
}

- (void)didBluetoothState:(QCBluetoothState)state {
    
}

- (void)didConnected:(CBPeripheral *)peripheral     //用户可以返回设备类型
{
    NSLog(@"didConnected");
    self.rightItem.enabled = YES;
    self.title = peripheral.name;
}

- (void)didDisconnecte:(CBPeripheral *)peripheral {
    NSLog(@"didDisconnecte");
    self.title = @"Feature";
    
    self.rightItem.title = @"Search";
    self.rightItem.enabled = YES;
    self.tableView.hidden = YES;
}

- (void)didFailConnected:(CBPeripheral *)peripheral {
    
    NSLog(@"didFailConnected");
    self.rightItem.enabled = YES;
}


#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return QGDeviceActionTypeReserved;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

    static NSString *cellIdentifier = @"Cell";

    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];

    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellIdentifier];
    }

    cell.detailTextLabel.numberOfLines = 0;
    cell.detailTextLabel.lineBreakMode = NSLineBreakByWordWrapping;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.imageView.image = nil;
    cell.detailTextLabel.text = @"";

    switch ((QGDeviceActionType)indexPath.row) {
        case QGDeviceActionTypeGetVersion:
            cell.textLabel.text = @"Get hard Version & firm Version";
            cell.detailTextLabel.text = [NSString stringWithFormat:@"hardVersion:%@,\nfirmVersion:%@,\nhardWifiVersion:%@,\nfirmWifiVersion:%@", self.hardVersion, self.firmVersion, self.hardWiFiVersion, self.firmWiFiVersion];
            break;
        case QGDeviceActionTypeSetTime:
            cell.textLabel.text = @"Set Time";
            cell.detailTextLabel.text = @"";
            break;
        case QGDeviceActionTypeGetBattery:
            cell.textLabel.text = @"Get Battary";
            cell.detailTextLabel.text = [NSString stringWithFormat:@"battary:%zd,charing:%zd", self.battary, (NSInteger)self.charging];
            break;
        case QGDeviceActionTypeGetMediaInfo:
            cell.textLabel.text = @"Get media info";
            cell.detailTextLabel.text = [NSString stringWithFormat:@"photo:%zd,video:%zd,audio:%zd", self.photoCount, self.videoCount, self.audioCount];
            break;
        case QGDeviceActionTypeTakePhoto:
            cell.textLabel.text = @"Take Photo";
            break;
        case QGDeviceActionTypeToggleVideoRecording:
            cell.textLabel.text = self.recordingVideo ? @"Stop Recording Video" : @"Start Recording Video";
            break;
        case QGDeviceActionTypeToggleAudioRecording:
            cell.textLabel.text = self.recordingAudio ? @"Stop Record audio" : @"Start Record audio";
            break;
        case QGDeviceActionTypeToggleTakeAIImage:
            cell.textLabel.text = @"Take AI Image";
            if (self.aiImageData) {
                cell.imageView.image = [UIImage imageWithData:self.aiImageData];
            }
            break;
        case QGDeviceActionTypeDownloadMedia:
            cell.textLabel.text = @"Download Media Over Wi-Fi";
            cell.detailTextLabel.text = self.mediaDownloadStatus ?: @"Tap to download media files over the device hotspot.";
            if (self.latestDownloadedMediaPreview) {
                cell.imageView.image = self.latestDownloadedMediaPreview;
            }
            break;
        case QGDeviceActionTypeReserved:
            break;
        default:
            break;
    }

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    QGDeviceActionType actionType = (QGDeviceActionType)indexPath.row;

    switch (actionType) {
        case QGDeviceActionTypeGetVersion:
            [self getHardVersionAndFirmVersion];
            break;
        case QGDeviceActionTypeSetTime:
            [self setTime];
            break;
        case QGDeviceActionTypeGetBattery:
            [self getBattary];
            break;
        case QGDeviceActionTypeGetMediaInfo:
            [self getMediaInfo];
            break;
        case QGDeviceActionTypeTakePhoto:
            [self takePhoto];
            break;
        case QGDeviceActionTypeToggleVideoRecording:
            [self recordVideo];
            break;
        case QGDeviceActionTypeToggleAudioRecording:
            [self recordAudio];
            break;
        case QGDeviceActionTypeToggleTakeAIImage:
            [self takeAIImage];
            break;
        case QGDeviceActionTypeDownloadMedia:
            [self downloadMediaOverWiFi];
            break;
        case QGDeviceActionTypeReserved:
        default:
            break;
    }

}

- (void)downloadMediaOverWiFi {
    __weak typeof(self) weakSelf = self;
    self.mediaDownloadStatus = @"Preparing Wi-Fi download...";
    self.latestDownloadedMediaPreview = nil;
    [self.tableView reloadData];

    self.mediaDownloader = [[GlassesMediaDownloader alloc] initWithStatusHandler:^(NSString *status, UIImage * _Nullable previewImage) {
        dispatch_async(dispatch_get_main_queue(), ^{
            weakSelf.mediaDownloadStatus = status;
            if (previewImage) {
                weakSelf.latestDownloadedMediaPreview = previewImage;
            }
            [weakSelf.tableView reloadData];
        });
    }];

    [self.mediaDownloader startDownloadWithCompletion:^(NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (error) {
                weakSelf.mediaDownloadStatus = [NSString stringWithFormat:@"Download failed: %@", error.localizedDescription ?: @"Unknown error"];
            } else {
                weakSelf.mediaDownloadStatus = @"Download complete.";
            }
            weakSelf.mediaDownloader = nil;
            [weakSelf.tableView reloadData];
        });
    }];
}

@end
