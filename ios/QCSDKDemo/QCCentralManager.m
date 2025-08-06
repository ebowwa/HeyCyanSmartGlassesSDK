//
//  QCCentralManager.m
//  QCBandSDKDemo
//
//  Created by steve on 2023/2/28.
//

#import "QCCentralManager.h"
#import <QCSDK/QCSDKManager.h>

static NSString *const QCLastConnectedIdentifier = @"QCLastConnectedIdentifier";
static NSInteger const QCBleDefaultTimeout = 15;
static NSInteger const QCBleDefaultConnectTimeout = 6;
@implementation QCBlePeripheral


@end

@interface QCCentralManager()<CBCentralManagerDelegate>

/*中心角色,app*/
@property (strong, nonatomic) CBCentralManager *centerManager;

@property (strong, nonatomic) NSMutableArray<QCBlePeripheral *> *peripherals;

@property (strong, nonatomic) CBPeripheral *connectedPeripheral;

@property (nonatomic,copy) void(^connectCompletedHandle)(BOOL);

@property (nonatomic,strong)NSTimer *reconTimer;

@property (assign,nonatomic)QCDeviceType deviceType;

@property (nonatomic,assign)QCState deviceState;

@property (nonatomic,assign)QCBluetoothState bleState;

@property (nonatomic,assign) NSInteger scanTimeout;

@property (nonatomic,assign) NSInteger connectTimeout;
@end

@implementation QCCentralManager

+ (instancetype)shared {
    static QCCentralManager * instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[QCCentralManager alloc] init];
    });
    return instance;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        NSDictionary *options = [NSDictionary dictionaryWithObjectsAndKeys:
                                 //蓝牙power没打开时alert提示框
                                 [NSNumber numberWithBool:YES],CBCentralManagerOptionShowPowerAlertKey,
                                 [NSNumber numberWithBool:YES],CBConnectPeripheralOptionNotifyOnConnectionKey,
                                 //重设centralManager恢复的IdentifierKey
                                 @"QCWXBluetoothRestore",CBCentralManagerOptionRestoreIdentifierKey,
                                 nil];
        
        _centerManager = [[CBCentralManager alloc] initWithDelegate:self queue:dispatch_get_main_queue() options:options];
        _peripherals = [[NSMutableArray alloc] init];
        _scanTimeout = QCBleDefaultTimeout;
        _connectTimeout = QCBleDefaultTimeout;
    }
    return self;
}

#pragma mark - Params
- (void)setScanTimeout:(NSInteger)scanTimeout {
    if(scanTimeout >= 0) {
        _scanTimeout = scanTimeout;
    }
    else {
        _scanTimeout = QCBleDefaultTimeout;
    }
}

- (void)setConnectTimeout:(NSInteger)connectTimeout {
    if(connectTimeout >= 0) {
        _connectTimeout = connectTimeout;
    }
    else {
        _connectTimeout = QCBleDefaultConnectTimeout;
    }
}

- (void)setDeviceState:(QCState)deviceState {
    _deviceState = deviceState;
    
    if(self.delegate && [self.delegate respondsToSelector:@selector(didState:)]) {
        [self.delegate didState:self.deviceState];
    }
}

#pragma mark - Public Fuction
- (void)scan; {
    [self scanWithTimeout:QCBleDefaultTimeout];
}

- (void)scanWithTimeout:(NSInteger)timeout {
    self.scanTimeout = timeout;
    
    [self stopScan];
    [self.peripherals removeAllObjects];
    
    NSArray <CBUUID*>*uuids = @[[CBUUID UUIDWithString:QCSDKSERVERUUID1],[CBUUID UUIDWithString:QCSDKSERVERUUID2]];
    NSArray *connectedP = [self retrieveConnectPeripheral:uuids];
    NSMutableArray * tempAray = [[NSMutableArray alloc] init];
    for (CBPeripheral *per in connectedP) {
        QCBlePeripheral *qcPer = [[QCBlePeripheral alloc] init];
        qcPer.peripheral = per;
        qcPer.mac = @"";//The paired device cannot read the mac from the system information, and can read the mac by sending instructions after the connection is successful
        qcPer.isPaired = YES;
        [tempAray addObject:qcPer];
    }
    
    [self.peripherals addObjectsFromArray:tempAray];
    
    if([connectedP count] > 0) {
        [self.peripherals sortedArrayUsingComparator:^NSComparisonResult(QCBlePeripheral *  _Nonnull obj1, QCBlePeripheral *  _Nonnull obj2) {
            return NSOrderedSame;
        }];
        if(self.delegate && [self.delegate respondsToSelector:@selector(didScanPeripherals:)]) {
            [self.delegate didScanPeripherals:self.peripherals];
        }
    }
    
    NSDictionary *option = @{CBCentralManagerScanOptionAllowDuplicatesKey : [NSNumber numberWithBool:NO]};
    [_centerManager scanForPeripheralsWithServices:nil options:option];
    
    [self stopTimer];
    self.reconTimer = [NSTimer scheduledTimerWithTimeInterval:self.scanTimeout target:self selector:@selector(stopScanFinishTimer:) userInfo:nil repeats:NO];
    [[NSRunLoop currentRunLoop]addTimer:self.reconTimer forMode: NSRunLoopCommonModes];
}

- (void)stopScan {
    
    if (!_centerManager) {
        return;
    }
    [_centerManager stopScan];
}

- (void)connect:(CBPeripheral *)peripheral {    
    [self connect:peripheral deviceType:QCDeviceTypeRing];
}

- (void)connect:(CBPeripheral *)peripheral deviceType:(QCDeviceType)deviceType {
    [self connect:peripheral timeout:QCBleDefaultConnectTimeout deviceType:deviceType];
}

- (void)connect:(CBPeripheral *)peripheral timeout:(NSInteger)timeout {
    [self connect:peripheral timeout:timeout deviceType:QCDeviceTypeRing];
}

- (void)connect:(CBPeripheral *)peripheral timeout:(NSInteger)timeout deviceType:(QCDeviceType)deviceType {
    
    self.connectTimeout = timeout;
    
    self.deviceType = deviceType;
    
    self.connectedPeripheral = peripheral;
    
    [self stopTimer];
    [self connectCurrentPeripheral];
    self.reconTimer = [NSTimer scheduledTimerWithTimeInterval:self.connectTimeout target:self selector:@selector(connectCurrentPeripheral) userInfo:nil repeats:YES];
    [[NSRunLoop currentRunLoop]addTimer:self.reconTimer forMode: NSRunLoopCommonModes];
}

- (void)connectCurrentPeripheral {
    
    CBPeripheral *lastPer = [self lastPeripheral];
    NSLog(@"connect device:%@",lastPer);
    NSDictionary * options = [NSMutableDictionary new];
    [options setValue:@(YES) forKey:CBConnectPeripheralOptionNotifyOnDisconnectionKey];
    if (@available(iOS 13.0, *)) {
        [options setValue:@(YES) forKey:CBConnectPeripheralOptionEnableTransportBridgingKey];
        if (self.deviceType != QCDeviceTypeRing) {
            // Be sure to use the following method to initialize, otherwise the ansc agent will not be executed and the App will not be able to monitor the accurate status of ancs.
            [options setValue:@(YES) forKey:CBConnectPeripheralOptionRequiresANCS];
        }
    }
    
    [_centerManager connectPeripheral:lastPer options:options];
}

- (void)remove {
    
    [self stopTimer];
    if (self.connectedPeripheral) {
        @try {
            [_centerManager cancelPeripheralConnection:self.connectedPeripheral];
        } @catch (NSException *e) {
            NSLog(@"warn: 取消设备(%@)连接时出现异常", self.connectedPeripheral.name);
        }
    }
    
    [[QCSDKManager shareInstance] removeAllPeripheral];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:QCLastConnectedIdentifier];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    self.deviceState = QCStateDisconnecting;
}


- (void)startToReconnect{
    
    if (self.bleState != QCBluetoothStatePoweredOn) {
        if(self.delegate && [self.delegate respondsToSelector:@selector(didFailConnected:error:)]) {
            [self.delegate didFailConnected:self.connectedPeripheral error:[NSError errorWithDomain:@"Bluetooth powered off" code:-1 userInfo:@{@"message":@"Bluetooth powered off"}]];
        }
        return;
    }
    
    CBPeripheral *lastPer = [self lastPeripheral];
    if(lastPer) {
        [self connect:lastPer];
    }
    else {
        if(self.delegate && [self.delegate respondsToSelector:@selector(didFailConnected:error:)]) {
            [self.delegate didFailConnected:self.connectedPeripheral error:[NSError errorWithDomain:@"CBPeripheral not exist" code:-1 userInfo:@{@"message":@"CBPeripheral not exist"}]];
        }
    }
}

- (CBPeripheral*)lastPeripheral {
    
    if(self.connectedPeripheral) {
        return self.connectedPeripheral;
    }
    
    NSString *uuidStr = [[NSUserDefaults standardUserDefaults] objectForKey:QCLastConnectedIdentifier];
    
    if(uuidStr && uuidStr.length > 0) {
        return [self periperalWithUUID:uuidStr];
    }
    
    return nil;
}

- (BOOL)isBindDevice {
    NSString *uuidStr = [[NSUserDefaults standardUserDefaults] objectForKey:QCLastConnectedIdentifier];
    if(uuidStr && uuidStr.length > 0) {
        return YES;
    }
    return NO;
}

# pragma mark - CBCentralManagerDelegate
- (void)centralManagerDidUpdateState:(CBCentralManager *)central {
    NSLog(@"centralManagerDidUpdateState:%ld",[central state]);
    QCBluetoothState bleState = QCBluetoothStateUnkown;
    
    switch([central state]) {
        case CBManagerStateUnknown:
            bleState = QCBluetoothStateUnkown;
            break;
        case CBManagerStateResetting:
            bleState = QCBluetoothStateResetting;
            break;
        case CBManagerStateUnsupported:
            bleState = QCBluetoothStateUnsupported;
            break;
        case CBManagerStatePoweredOff:
            bleState = QCBluetoothStatePoweredOff;
            break;
        case CBManagerStatePoweredOn:
            bleState = QCBluetoothStatePoweredOn;
            break;
        default:break;
    }
    
    self.bleState = bleState;
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(didBluetoothState:)]) {
        [self.delegate didBluetoothState:bleState];
    }
    
    if([self isBindDevice]) {
        self.deviceState = QCStateConnecting;
    }
    else {
        self.deviceState = QCStateUnbind;
    }
    
    if (bleState == QCBluetoothStatePoweredOn && self.deviceState == QCStateConnecting) {
        [self startToReconnect];
    }
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary<NSString *,id> *)advertisementData RSSI:(NSNumber *)RSSI {
    
//    if(![peripheral.name.lowercaseString hasPrefix:@"o_"]) {
//        return;
//    }
    if(peripheral.name.length == 0) return;
    NSString *mac = [self macFromAdvertisementData:advertisementData];

    NSLog(@"Devices found:%@,mac:%@,id:%@",peripheral.name,mac,peripheral.identifier.UUIDString);
    BOOL isExist = false;
    for (QCBlePeripheral *per in self.peripherals) {
        if([per.peripheral.identifier.UUIDString isEqual:peripheral.identifier.UUIDString]) {
            per.peripheral = peripheral;
            per.mac = mac;
            per.advertisementData = advertisementData;
            per.RSSI = RSSI;
            isExist = true;
            return;
        }
    }
    
    if(!isExist) {
        QCBlePeripheral *per = [[QCBlePeripheral alloc] init];
        per.peripheral = peripheral;
        per.mac = mac;
        per.advertisementData = advertisementData;
        per.RSSI = RSSI;
        [self.peripherals addObject:per];
    }
    
    if(self.delegate && [self.delegate respondsToSelector:@selector(didScanPeripherals:)]) {
        [self.delegate didScanPeripherals:self.peripherals];
    }
}

- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral {
    NSLog(@"Connection to device (%@) succeeded", peripheral.name);
    
    [[QCSDKManager shareInstance] removePeripheral:peripheral];
    [[QCSDKManager shareInstance] addPeripheral:peripheral finished:^(BOOL success) {
        
        [self stopTimer];
        if (success) {
            NSLog(@"Add peripherals successfully");
            [[NSUserDefaults standardUserDefaults] setValue:peripheral.identifier.UUIDString forKey:QCLastConnectedIdentifier];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
            self.deviceState = QCStateConnected;
        }
        else {
            NSLog(@"Failed to add peripheral");
            if(self.delegate && [self.delegate respondsToSelector:@selector(didFailConnected:error:)]) {
                [self.delegate didFailConnected:peripheral error:[NSError errorWithDomain:@"Connect fail" code:-1 userInfo:@{@"message":@"Connect fail"}]];
            }
        }
    }];
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(nullable NSError *)error {
    NSLog(@"Device(%@)didDisconnect，err: %@", peripheral.name, error);
    
    if(self.deviceState == QCStateDisconnecting) { //unbinding device
        self.deviceState = QCStateUnbind;
    }
    else {
        //reconnect device
        [[QCSDKManager shareInstance] removeAllPeripheral];
        self.deviceState = QCStateConnecting;
        [self startToReconnect];
    }
}

- (void)centralManager:(CBCentralManager *)central willRestoreState:(NSDictionary *)dict {
    NSArray *peripherals = dict[CBCentralManagerRestoredStatePeripheralsKey];
    if (peripherals.count > 0) {
        //恢复重连上一次连接的设备
        NSString *uuidStr = [[NSUserDefaults standardUserDefaults] objectForKey:QCLastConnectedIdentifier];
        if (uuidStr.length > 0) {
            for (CBPeripheral* pr in peripherals) {
                if ([uuidStr isEqualToString:pr.identifier.UUIDString]) {
                    self.connectedPeripheral = pr;
                }
            }
        }
    }
}

#pragma mark - Helpers
- (NSArray *)retrieveConnectPeripheral:(NSArray<CBUUID *> *)uuidArray
{
    NSArray *connectedDevice = [_centerManager retrieveConnectedPeripheralsWithServices:uuidArray];
    NSMutableArray *connectedPeripheral = [NSMutableArray arrayWithCapacity:connectedDevice.count];
    [connectedDevice enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (![obj isKindOfClass:[CBPeripheral class]]) {
            return;
        }
        [connectedPeripheral addObject:obj];
    }];
    
    return [connectedPeripheral copy];
}

- (CBPeripheral *)periperalWithUUID:(NSString *)uuid {
    
    if (!uuid) {
        return nil;
    }
    
    NSArray *periperals = nil;
    NSUUID *UUID = [[NSUUID alloc] initWithUUIDString:uuid];
    if(!UUID){
        NSLog(@"NSUUID(%@)合法，但无法创建UUID，原因不明", uuid);
        return nil;
    }
    periperals = [_centerManager retrievePeripheralsWithIdentifiers:@[UUID]];
    if (periperals.count > 0) {
        return [periperals objectAtIndex:0];
    } else {
        return nil;
    }
}

- (NSString *)macFromAdvertisementData:(NSDictionary *)advertisementData {
    NSString *mac = @"";
    NSData *manufacturerData = [advertisementData objectForKey:@"kCBAdvDataManufacturerData"];
    mac = [self macFromData:manufacturerData];
    
    if (mac.length == 0) {
        NSDictionary *serviceData = [advertisementData objectForKey:@"kCBAdvDataServiceData"];
        if ([serviceData isKindOfClass:[NSDictionary class]]) {
            NSArray *allValues = [serviceData allValues];
            if (allValues.count > 0) {
                for (NSData *dataValue in allValues) {
                    if ([dataValue isKindOfClass:[NSData class]]) {
                        mac = [self macFromData:dataValue];
                    }
                }
            }
        }
    }
    return mac;
}

- (NSString*)macFromData:(NSData*)macData {
    NSString *mac = @"";
    if ([macData isKindOfClass:[NSData class]] && macData.length > 0) {
        NSData *data = macData;
        if (data.length >= 10) {
            data = [data subdataWithRange:NSMakeRange(4, 6)];
            Byte *bytes = (Byte *)data.bytes;
            mac = [NSString stringWithFormat:@"%02x:%02x:%02x:%02x:%02x:%02x",
                        bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]];
        } else if (data.length == 8) {
            data = [data subdataWithRange:NSMakeRange(2, 6)];
            Byte *bytes = (Byte *)data.bytes;
            mac = [NSString stringWithFormat:@"%02x:%02x:%02x:%02x:%02x:%02x",
                        bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]];
        } else if (data.length == 6) {
           data = [data subdataWithRange:NSMakeRange(0, 6)];
           Byte *bytes = (Byte *)data.bytes;
           mac = [NSString stringWithFormat:@"%02x:%02x:%02x:%02x:%02x:%02x",
                       bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]];
       }
    }
    
    return mac;
}

- (void)stopTimer
{
    if ([self.reconTimer isValid]) {
        [self.reconTimer invalidate];
        self.reconTimer = nil;
    }
}

- (void)stopConnectFinishTimer:(NSTimer *)timer {
    
    if(self.delegate && [self.delegate respondsToSelector:@selector(didFailConnected:error:)]) {
        [self.delegate didFailConnected:self.connectedPeripheral error:[NSError errorWithDomain:@"timeout" code:-1 userInfo:@{@"message":@"connect timeout"}]];
    }
}

- (void)stopScanFinishTimer:(NSTimer *)timer
{
    [self stopScan];
}
@end
