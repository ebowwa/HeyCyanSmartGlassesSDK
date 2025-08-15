"""
HeyCyan Glasses SDK for Python
Implements iOS-like persistent binding and connection management
Based on iOS SDK binding mechanism
"""

import asyncio
import json
import os
from enum import Enum
from pathlib import Path
from typing import Optional, Callable, Any
from datetime import datetime
import logging
from bleak import BleakScanner, BleakClient, BleakError

# Setup logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class QCState(Enum):
    """Connection states matching iOS SDK"""
    UNBIND = "QCStateUnbind"
    CONNECTING = "QCStateConnecting"
    CONNECTED = "QCStateConnected"
    DISCONNECTING = "QCStateDisconnecting"
    DISCONNECTED = "QCStateDisconnected"

class HeyCyanSDK:
    """Main SDK class for HeyCyan Glasses with iOS-like binding mechanism"""
    
    # Configuration
    RECONNECT_INTERVAL = 6.0  # seconds, matching iOS timer
    CONFIG_FILE = Path.home() / ".heycyan_config.json"
    
    # HeyCyan characteristic UUID (from iOS SDK)
    HEYCYAN_SERVICE_UUID = "19B10000-E8F2-537E-4F6C-D104768A1214"
    HEYCYAN_CHAR_UUID = "19B10001-E8F2-537E-4F6C-D104768A1214"
    
    def __init__(self):
        self.state = QCState.UNBIND
        self.client: Optional[BleakClient] = None
        self.bound_device_uuid: Optional[str] = None
        self.is_paired = False
        self.reconnect_task: Optional[asyncio.Task] = None
        self.data_handler: Optional[Callable] = None
        self.connection_lock = asyncio.Lock()
        
        # Load persistent configuration
        self._load_config()
    
    def _load_config(self):
        """Load persistent binding configuration from disk (mimics iOS NSUserDefaults)"""
        if self.CONFIG_FILE.exists():
            try:
                with open(self.CONFIG_FILE, 'r') as f:
                    config = json.load(f)
                    # Match iOS key name exactly
                    self.bound_device_uuid = config.get('QCLastConnectedIdentifier')
                    if self.bound_device_uuid:
                        logger.info(f"Loaded bound device: {self.bound_device_uuid}")
                        self.state = QCState.DISCONNECTED
            except Exception as e:
                logger.error(f"Failed to load config: {e}")
    
    def _save_config(self):
        """Save persistent binding configuration to disk (mimics iOS NSUserDefaults)"""
        try:
            config = {
                'QCLastConnectedIdentifier': self.bound_device_uuid,
                'last_updated': datetime.now().isoformat()
            }
            with open(self.CONFIG_FILE, 'w') as f:
                json.dump(config, f, indent=2)
            logger.info(f"Saved binding config for device: {self.bound_device_uuid}")
        except Exception as e:
            logger.error(f"Failed to save config: {e}")
    
    @property
    def is_bind_device(self) -> bool:
        """Check if a device is currently bound (matching iOS isBindDevice method)"""
        return self.bound_device_uuid is not None
    
    async def scan_devices(self, duration: float = 5.0) -> list:
        """Scan for available HeyCyan devices"""
        logger.info(f"Scanning for devices for {duration} seconds...")
        devices = await BleakScanner.discover(timeout=duration)
        
        heycyan_devices = []
        for device in devices:
            # Look for HeyCyan devices
            if device.name:
                heycyan_devices.append({
                    'name': device.name,
                    'address': device.address,
                    'rssi': device.rssi,
                    'metadata': device.metadata
                })
        
        logger.info(f"Found {len(heycyan_devices)} devices")
        return heycyan_devices
    
    async def connect(self, device_address: str, data_handler: Callable[[Any, bytes], None]):
        """
        Connect to HeyCyan glasses and establish persistent binding
        Implements the iOS connection flow:
        1. Initial Pairing: Scan → Connect → Store UUID → Mark as paired
        2. Binding Persistence: UUID saved to QCLastConnectedIdentifier
        
        Args:
            device_address: MAC address or UUID of the device
            data_handler: Callback function for handling received data
        """
        async with self.connection_lock:
            if self.state == QCState.CONNECTED:
                logger.warning("Already connected")
                return
            
            self.state = QCState.CONNECTING
            self.data_handler = data_handler
            
            try:
                logger.info(f"Connecting to {device_address}...")
                self.client = BleakClient(device_address)
                await self.client.connect()
                
                if self.client.is_connected:
                    self.state = QCState.CONNECTED
                    self.bound_device_uuid = device_address
                    self.is_paired = True
                    
                    # Save binding persistently (iOS: NSUserDefaults)
                    self._save_config()
                    
                    # Start notifications for data characteristic
                    await self.client.start_notify(self.HEYCYAN_CHAR_UUID, data_handler)
                    
                    logger.info(f"Successfully connected and bound to {device_address}")
                    
                    # Start monitoring connection
                    asyncio.create_task(self._monitor_connection())
                else:
                    raise BleakError("Failed to establish connection")
                    
            except Exception as e:
                logger.error(f"Connection failed: {e}")
                self.state = QCState.DISCONNECTED
                
                # Start auto-reconnection if this was a bound device
                if self.is_bind_device:
                    await self._start_reconnection()
    
    async def disconnect(self):
        """Disconnect from current device but maintain binding"""
        async with self.connection_lock:
            if self.state != QCState.CONNECTED:
                return
            
            self.state = QCState.DISCONNECTING
            
            try:
                if self.client:
                    await self.client.stop_notify(self.HEYCYAN_CHAR_UUID)
                    await self.client.disconnect()
                    self.client = None
                
                self.state = QCState.DISCONNECTED
                logger.info("Disconnected from device")
                
                # Start reconnection since binding is maintained
                if self.is_bind_device:
                    await self._start_reconnection()
                    
            except Exception as e:
                logger.error(f"Disconnect error: {e}")
                self.state = QCState.DISCONNECTED
    
    async def remove(self):
        """Remove device binding and disconnect (matching iOS remove method exactly)"""
        logger.info("Removing device binding...")
        
        # Cancel reconnection if active
        if self.reconnect_task:
            self.reconnect_task.cancel()
            self.reconnect_task = None
        
        # Disconnect if connected
        if self.state == QCState.CONNECTED:
            await self.disconnect()
        
        # Clear binding
        self.bound_device_uuid = None
        self.is_paired = False
        self.state = QCState.UNBIND
        
        # Clear persistent storage
        if self.CONFIG_FILE.exists():
            self.CONFIG_FILE.unlink()
        
        logger.info("Device binding removed successfully")
    
    async def _start_reconnection(self):
        """Start auto-reconnection timer (matching iOS behavior - 6 second intervals)"""
        if self.reconnect_task and not self.reconnect_task.done():
            return
        
        self.reconnect_task = asyncio.create_task(self._reconnect_loop())
    
    async def _reconnect_loop(self):
        """Auto-reconnection loop with iOS-matching interval"""
        while self.is_bind_device and self.state != QCState.CONNECTED:
            logger.info(f"Attempting reconnection to {self.bound_device_uuid}...")
            
            try:
                await self.connect(self.bound_device_uuid, self.data_handler)
                if self.state == QCState.CONNECTED:
                    logger.info("Reconnection successful")
                    break
            except Exception as e:
                logger.debug(f"Reconnection attempt failed: {e}")
            
            # iOS uses 6 second reconnection interval
            await asyncio.sleep(self.RECONNECT_INTERVAL)
    
    async def _monitor_connection(self):
        """Monitor connection health and trigger reconnection if needed"""
        while self.state == QCState.CONNECTED:
            try:
                if self.client and not self.client.is_connected:
                    logger.warning("Connection lost, triggering reconnection...")
                    self.state = QCState.DISCONNECTED
                    await self._start_reconnection()
                    break
            except Exception as e:
                logger.error(f"Connection monitoring error: {e}")
            
            await asyncio.sleep(1.0)
    
    async def auto_connect(self, data_handler: Callable[[Any, bytes], None]):
        """
        Automatically connect to bound device if one exists
        Mimics iOS app launch behavior with stored UUID
        """
        if self.is_bind_device:
            logger.info(f"Auto-connecting to bound device: {self.bound_device_uuid}")
            self.state = QCState.CONNECTING  # iOS behavior on app launch
            await self.connect(self.bound_device_uuid, data_handler)
        else:
            logger.info("No bound device found")
    
    def get_state(self) -> str:
        """Get current connection state"""
        return self.state.value
    
    async def send_command(self, command: bytes):
        """Send command to connected glasses"""
        if self.state != QCState.CONNECTED or not self.client:
            raise RuntimeError("Not connected to device")
        
        try:
            await self.client.write_gatt_char(self.HEYCYAN_CHAR_UUID, command)
            logger.debug(f"Sent command: {command.hex()}")
        except Exception as e:
            logger.error(f"Failed to send command: {e}")
            raise
    
    async def read_characteristic(self, char_uuid: str) -> bytes:
        """Read data from a specific characteristic"""
        if self.state != QCState.CONNECTED or not self.client:
            raise RuntimeError("Not connected to device")
        
        try:
            data = await self.client.read_gatt_char(char_uuid)
            return data
        except Exception as e:
            logger.error(f"Failed to read characteristic {char_uuid}: {e}")
            raise


# Example usage
async def main():
    """Example of how to use the HeyCyan SDK with iOS-like binding"""
    sdk = HeyCyanSDK()
    
    def handle_data(sender, data):
        """Handle incoming data from glasses"""
        print(f"Received {len(data)} bytes from {sender}")
        # Process HeyCyan glasses data here
    
    # Check if device is already bound (iOS-like behavior)
    if sdk.is_bind_device:
        print(f"Found bound device: {sdk.bound_device_uuid}")
        await sdk.auto_connect(handle_data)
    else:
        # Scan for new devices
        print("Scanning for HeyCyan devices...")
        devices = await sdk.scan_devices()
        
        if devices:
            print(f"Found {len(devices)} devices:")
            for i, device in enumerate(devices):
                print(f"  {i}: {device['name']} [{device['address']}] RSSI: {device['rssi']}")
            
            # Connect to first device (or implement selection logic)
            if len(devices) > 0:
                await sdk.connect(devices[0]['address'], handle_data)
    
    # Keep running and display state
    try:
        while True:
            await asyncio.sleep(5)
            print(f"State: {sdk.get_state()}")
    except KeyboardInterrupt:
        print("\nShutting down...")
        # Disconnect but keep binding (iOS-like)
        await sdk.disconnect()
        
        # Or completely unbind (remove pairing)
        # await sdk.remove()


if __name__ == "__main__":
    asyncio.run(main())