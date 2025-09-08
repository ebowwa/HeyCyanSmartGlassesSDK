# HeyCyan Glasses SDK - Python

A Python SDK for controlling HeyCyan smart glasses via Bluetooth Low Energy (BLE).

## Features

- 🔍 **Device Discovery**: Scan and connect to HeyCyan Glasses via BLE
- 📷 **Media Control**: Take photos, record videos and audio
- 🎨 **AI Generation**: Generate AI images with custom prompts
- 🔋 **Device Monitoring**: Battery status, media counts, and device info
- 🔊 **Settings Management**: Volume control and time synchronization
- ⚡ **Async/Await**: Modern asynchronous Python API
- 🖥️ **Cross-Platform**: Works on Windows, macOS, and Linux

## Installation

### From PyPI (recommended)
```bash
pip install heycyan-sdk
```

### From Source
```bash
git clone https://github.com/ebowwa/HeyCyanGlassesSDK.git
cd HeyCyanGlassesSDK/python
pip install -e .
```

## Requirements

- Python 3.7+
- Bluetooth Low Energy support on your system
- `bleak` library (automatically installed)

## Quick Start

```python
import asyncio
from heycyan_sdk import DeviceManager

async def main():
    # Create device manager
    manager = DeviceManager()
    
    # Scan for devices
    devices = await manager.scan(duration=5.0)
    
    if devices:
        # Connect to first device
        device = devices[0]
        if await manager.connect(device):
            # Take a photo
            await manager.take_photo()
            
            # Get battery status
            battery = await manager.get_battery_status()
            print(f"Battery: {battery.level}%")
            
            # Disconnect
            await manager.disconnect()

asyncio.run(main())
```

## API Documentation

### DeviceManager

The main interface for interacting with HeyCyan Glasses.

#### Methods

##### `scan(duration: float = 5.0) -> List[Device]`
Scan for available HeyCyan Glasses devices.

##### `connect(device: Device) -> bool`
Connect to a specific device.

##### `disconnect()`
Disconnect from the current device.

##### `take_photo() -> bool`
Take a photo with the glasses.

##### `start_video_recording() -> bool`
Start recording video.

##### `stop_video_recording() -> bool`
Stop recording video.

##### `start_audio_recording() -> bool`
Start recording audio.

##### `stop_audio_recording() -> bool`
Stop recording audio.

##### `generate_ai_image(prompt: str = "") -> bool`
Generate an AI image with optional prompt.

##### `get_battery_status() -> BatteryStatus`
Get current battery level and charging status.

##### `update_media_count()`
Update media file counts from device.

##### `set_volume(level: int) -> bool`
Set device volume (0-100).

### Device Models

#### Device
```python
@dataclass
class Device:
    name: str
    address: str
    rssi: int
    connection_state: ConnectionState
    device_info: Optional[DeviceInfo]
    battery_status: Optional[BatteryStatus]
    media_count: Optional[MediaCount]
```

#### BatteryStatus
```python
@dataclass
class BatteryStatus:
    level: int  # 0-100
    charging: bool
    voltage: float
```

#### MediaCount
```python
@dataclass
class MediaCount:
    photos: int
    videos: int
    audio_files: int
    ai_images: int
```

## Examples

### Basic Usage
```python
# See examples/basic_usage.py for complete example
python examples/basic_usage.py
```

### Interactive CLI
```python
# Run the interactive command-line interface
python examples/interactive_cli.py
```

### Callbacks and Events

```python
import asyncio
from heycyan_sdk import DeviceManager

async def main():
    manager = DeviceManager()
    
    # Setup callbacks
    def on_battery_update(battery):
        print(f"Battery updated: {battery.level}%")
    
    def on_media_update(media):
        print(f"Photos: {media.photos}, Videos: {media.videos}")
    
    manager.set_battery_callback(on_battery_update)
    manager.set_media_callback(on_media_update)
    
    # Connect and use device...

asyncio.run(main())
```

### Recording Video with Duration

```python
async def record_video(manager, duration_seconds=10):
    """Record video for specified duration"""
    if await manager.start_video_recording():
        print(f"Recording for {duration_seconds} seconds...")
        await asyncio.sleep(duration_seconds)
        await manager.stop_video_recording()
        print("Recording complete!")
```

### AI Image Generation

```python
async def generate_ai_images(manager):
    """Generate multiple AI images with different prompts"""
    prompts = [
        "A futuristic cityscape",
        "Abstract art in blue tones",
        "Nature landscape at sunset"
    ]
    
    for prompt in prompts:
        print(f"Generating: {prompt}")
        if await manager.generate_ai_image(prompt):
            await asyncio.sleep(5)  # Wait for generation
```

## Platform-Specific Notes

### Linux
- May require running with `sudo` for BLE access
- Or add user to `bluetooth` group: `sudo usermod -a -G bluetooth $USER`

### macOS
- Requires Bluetooth permissions in System Preferences
- May need to grant Terminal/IDE Bluetooth access

### Windows
- Requires Windows 10+ with Bluetooth support
- May need to pair glasses through Windows Settings first

## Troubleshooting

### Cannot find devices
- Ensure Bluetooth is enabled on your system
- Check glasses are in pairing mode (usually holding power button)
- On Linux, try running with `sudo`

### Connection fails
- Ensure glasses aren't connected to another device
- Try resetting Bluetooth on both devices
- Check glasses battery level

### Commands not working
- Verify connection status with `manager.is_connected`
- Check device mode - some operations conflict (e.g., can't record video while recording audio)
- Monitor logs for error messages

## Development

### Setup Development Environment
```bash
# Clone repository
git clone https://github.com/ebowwa/HeyCyanGlassesSDK.git
cd HeyCyanGlassesSDK/python

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install in development mode
pip install -e ".[dev]"
```

### Running Tests
```bash
pytest tests/
```

### Code Formatting
```bash
black heycyan_sdk/
flake8 heycyan_sdk/
mypy heycyan_sdk/
```

## Architecture

The SDK is structured in layers:

1. **BLE Layer** (`core/ble_client.py`): Low-level Bluetooth communication
2. **Command Layer** (`commands/device_commands.py`): Protocol implementation
3. **Manager Layer** (`core/device_manager.py`): High-level API
4. **Models** (`models/device.py`): Data structures and enums

## Protocol Details

The SDK implements the proprietary HeyCyan BLE protocol:

- **Service UUIDs**: 
  - Primary: `7905FFF0-B5CE-4E99-A40F-4B1E122D00D0`
  - Secondary: `6e40fff0-b5a3-f393-e0a9-e50e24dcca9e`

- **Commands**: Byte-based protocol with command type, subtype, and parameters
- **Responses**: Async notifications via BLE characteristics
- **Data Transfer**: Large data (AI images) via dedicated data characteristic

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This SDK is proprietary software. Contact HeyCyan for licensing information.

## Support

- GitHub Issues: [https://github.com/ebowwa/HeyCyanGlassesSDK/issues](https://github.com/ebowwa/HeyCyanGlassesSDK/issues)
- Documentation: [https://github.com/ebowwa/HeyCyanGlassesSDK](https://github.com/ebowwa/HeyCyanGlassesSDK)
- Contact: dev@heycyan.com

## Changelog

### Version 1.0.0 (2024-01-05)
- Initial Python SDK release
- Full feature parity with iOS and Android SDKs
- Async/await API design
- Interactive CLI example
- Cross-platform support