# QCNetworkKit

## Overview
Network connectivity and exploration module for QC SDK smart glasses.

## Contents
- `NetworkExplorer` - Network discovery, streaming, and socket management
- `WiFiAutoConnect` - Automated WiFi connection handling

## Public API

### Classes
- `NetworkExplorer` - TCP/IP network exploration and streaming
- `WiFiAutoConnect` - WiFi connection automation

### Protocols
- `NetworkExplorerDelegate` - Network discovery and streaming callbacks

## Features
- TCP/IP socket management
- Network service discovery
- Stream-based data transfer
- WiFi hotspot connection automation
- Network status monitoring

## Integration
1. Add as Framework target in Xcode
2. Link against required frameworks
3. Import via `#import <QCNetworkKit/QCNetworkKit.h>` or module import

## Dependencies
- Foundation.framework
- SystemConfiguration.framework
- Network.framework (iOS 12+)