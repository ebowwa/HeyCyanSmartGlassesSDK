//
//  QCScanViewController.m
//  QCBandSDKDemo
//
//  Created by steve on 2023/2/28.
//

#import "QCScanViewController.h"
#import "QCCentralManager.h"

@interface QCScanViewController ()<UITableViewDataSource, UITableViewDelegate,QCCentralManagerDelegate>

@property(nonatomic,strong)UIActivityIndicatorView *indicatorView;
@property (nonatomic,strong) UITableView *tableView;
@property(nonatomic,strong)NSArray *peripherals;
@end

@implementation QCScanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.title = @"Search";
    
    self.indicatorView = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0, 64, self.view.frame.size.width, 20)];
    self.indicatorView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    [self.view addSubview:self.indicatorView];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0,
                                                                   CGRectGetMaxY(self.indicatorView.frame),
                                                                   CGRectGetWidth(self.view.frame),
                                                                   CGRectGetHeight(self.view.frame) - CGRectGetMaxY(self.indicatorView.frame)) style:(UITableViewStylePlain)];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.view addSubview:self.tableView];
    
    [QCCentralManager shared].delegate = self;
    [[QCCentralManager shared] scan];
    [self.indicatorView startAnimating];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [[QCCentralManager shared] stopScan];
}

#pragma mark - QCCentralManagerDelegate
- (void)didScanPeripherals:(NSArray *)peripheralArr; {
    
    self.peripherals = peripheralArr;
    [self.tableView reloadData];
}

- (void)didState:(QCState)state {
    if(state == QCStateConnected) {
        [self.navigationController popViewControllerAnimated:true];
    }
}

- (void)didFailConnected:(CBPeripheral *)peripheral {
    NSLog(@"Connected Fial");
}

- (void)didDisconnecte:(nonnull CBPeripheral *)peripheral {
    
}


#pragma mark - Table view datasource & delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.peripherals.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
    }
    
    cell.textLabel.textColor = UIColor.blueColor;
    QCBlePeripheral *per = self.peripherals[indexPath.row];
    
    cell.textLabel.text = per.peripheral.name;
    cell.detailTextLabel.text = [NSString stringWithFormat:@"%@",per.mac];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    
    [self.indicatorView stopAnimating];
    [[QCCentralManager shared] stopScan];
    
    QCBlePeripheral *per= [self.peripherals objectAtIndex:indexPath.row];
    
    NSLog(@"Connecting to %@,mac:%@",per.peripheral.name,per.mac);

    [[QCCentralManager shared] connect:per.peripheral];
}

@end
