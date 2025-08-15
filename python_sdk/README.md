# HeyCyan Glasses Python SDK

Python implementation of the HeyCyan Glasses SDK with iOS-like persistent binding and connection management.

## Features

This Python SDK implements the same binding mechanism as the iOS SDK:

- **Persistent Binding**: Device UUID stored in `~/.heycyan_config.json` (mimics iOS NSUserDefaults)
- **5 Connection States**: Unbind, Connecting, Connected, Disconnecting, Disconnected
- **Auto-Reconnection**: Timer-based reconnection every 6 seconds after connection loss
- **Binding Management**: Explicit bind/unbind methods with persistent storage
- **Connection Monitoring**: Automatic detection of connection loss and recovery

## Installation

```bash
pip install -r requirements.txt
```

## Basic Usage

```python
import asyncio
from heycyan_sdk import HeyCyanSDK

async def main():
    sdk = HeyCyanSDK()
    
    def handle_data(sender, data):
        print(f"Received: {data.hex()}")
    
    # Auto-connect to previously bound device
    if sdk.is_bind_device:
        await sdk.auto_connect(handle_data)
    else:
        # Scan and connect to new device
        devices = await sdk.scan_devices()
        if devices:
            await sdk.connect(devices[0]['address'], handle_data)
    
    # Keep running
    while True:
        await asyncio.sleep(1)

asyncio.run(main())
```

## Connection States

The SDK tracks the same 5 states as the iOS implementation:

| State | Description |
|-------|-------------|
| `QCStateUnbind` | No device bound |
| `QCStateConnecting` | Actively connecting |
| `QCStateConnected` | Successfully connected |
| `QCStateDisconnecting` | Unbinding in progress |
| `QCStateDisconnected` | Connection lost |

## Key Methods

### Core Connection Methods

- `scan_devices(duration=5.0)` - Scan for available HeyCyan devices
- `connect(device_address, data_handler)` - Connect and bind to device
- `disconnect()` - Disconnect but maintain binding
- `remove()` - Remove binding and disconnect (matches iOS SDK)
- `auto_connect(data_handler)` - Auto-connect to bound device

### State Management

- `is_bind_device` - Check if device is bound (property matching iOS)
- `get_state()` - Get current connection state
- `send_command(command)` - Send command to connected glasses
- `read_characteristic(char_uuid)` - Read data from specific characteristic

## Persistent Binding

The SDK maintains binding information in `~/.heycyan_config.json`:

```json
{
  "QCLastConnectedIdentifier": "device-uuid-here",
  "last_updated": "2024-01-20T10:30:00"
}
```

This allows the SDK to:
1. Remember the last connected device across app restarts
2. Automatically reconnect to the bound device
3. Maintain the pairing relationship until explicitly removed

## Auto-Reconnection

When connection is lost:
1. State changes to `QCStateDisconnected`
2. Reconnection timer starts (6-second intervals matching iOS)
3. Attempts continue until successful or device is removed
4. State returns to `QCStateConnected` on success

## Connection Flow (Matching iOS)

1. **Initial Pairing**: Scan → Connect → Store UUID → Mark as paired
2. **Binding Persistence**: UUID saved to `QCLastConnectedIdentifier`
3. **Reconnection**: Retrieves UUID → Attempts connection with stored address
4. **Unbinding**: Calls `remove()` method → Clears UUID → Cancels connection

## Differences from iOS SDK

While this Python SDK implements the core binding mechanism from iOS, some features are not available due to platform limitations:

- **Background Mode**: Python doesn't have iOS-style background execution
- **State Restoration**: iOS-specific feature for app lifecycle
- **Transport Bridging**: iOS 13+ specific Bluetooth feature
- **ANCS Support**: Apple Notification Center Service is iOS-only

## Error Handling

The SDK includes comprehensive error handling:
- Connection failures trigger auto-reconnection for bound devices
- Graceful shutdown on keyboard interrupt
- Thread-safe connection state management
- Automatic reconnection on unexpected disconnections

## Configuration

Customize behavior by modifying class constants:

```python
HeyCyanSDK.RECONNECT_INTERVAL = 10.0  # Change reconnection interval
HeyCyanSDK.CONFIG_FILE = Path("custom_config.json")  # Custom config location
```

## License

Same as HeyCyan Glasses iOS SDK