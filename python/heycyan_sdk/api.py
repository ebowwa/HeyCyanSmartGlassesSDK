"""
Public API for HeyCyan Glasses SDK
This is what gets distributed on PyPI - no protocol details exposed
"""

import asyncio
import logging
from typing import Optional, List, Callable, Tuple
from dataclasses import dataclass
from enum import Enum

logger = logging.getLogger(__name__)


class ConnectionState(Enum):
    """Device connection states"""
    DISCONNECTED = "disconnected"
    CONNECTING = "connecting"
    CONNECTED = "connected"


@dataclass
class Device:
    """Represents a HeyCyan Glasses device"""
    name: str
    address: str
    rssi: int = -100


@dataclass
class BatteryInfo:
    """Battery information"""
    level: int  # 0-100
    charging: bool


@dataclass 
class MediaCount:
    """Media file counts on device"""
    photos: int = 0
    videos: int = 0
    audio_files: int = 0


class HeyCyanGlasses:
    """
    Main interface for HeyCyan Glasses control
    
    This is the public API that gets distributed on PyPI.
    All protocol details are hidden in private modules.
    """
    
    def __init__(self):
        # Import private implementation
        try:
            from ._impl import GlassesImpl
            self._impl = GlassesImpl()
        except ImportError:
            # For PyPI version, would load compiled/obfuscated module
            raise RuntimeError(
                "HeyCyan SDK implementation not found. "
                "Please install from PyPI: pip install heycyan-sdk"
            )
        
        self.state = ConnectionState.DISCONNECTED
        self.current_device: Optional[Device] = None
    
    async def scan(self, duration: float = 5.0) -> List[Device]:
        """
        Scan for available HeyCyan Glasses
        
        Args:
            duration: How long to scan in seconds
            
        Returns:
            List of discovered devices
        """
        logger.info(f"Scanning for HeyCyan Glasses for {duration}s...")
        devices = await self._impl.scan_devices(duration)
        
        return [
            Device(
                name=d.get("name", "HeyCyan Glasses"),
                address=d.get("address"),
                rssi=d.get("rssi", -100)
            )
            for d in devices
        ]
    
    async def connect(self, device: Device) -> bool:
        """
        Connect to HeyCyan Glasses
        
        Args:
            device: Device to connect to
            
        Returns:
            True if connected successfully
        """
        self.state = ConnectionState.CONNECTING
        logger.info(f"Connecting to {device.name}...")
        
        success = await self._impl.connect(device.address)
        
        if success:
            self.state = ConnectionState.CONNECTED
            self.current_device = device
            logger.info("Connected successfully")
        else:
            self.state = ConnectionState.DISCONNECTED
            logger.error("Connection failed")
        
        return success
    
    async def disconnect(self):
        """Disconnect from glasses"""
        if self.state == ConnectionState.CONNECTED:
            await self._impl.disconnect()
            self.state = ConnectionState.DISCONNECTED
            self.current_device = None
            logger.info("Disconnected")
    
    # Camera Controls
    
    async def take_photo(self) -> bool:
        """
        Trigger photo capture on glasses
        
        Returns:
            True if command sent successfully
        """
        if self.state != ConnectionState.CONNECTED:
            logger.error("Not connected")
            return False
        
        return await self._impl.capture_photo()
    
    async def start_video_recording(self) -> bool:
        """
        Start video recording on glasses
        
        Returns:
            True if recording started
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        return await self._impl.start_video()
    
    async def stop_video_recording(self) -> bool:
        """
        Stop video recording on glasses
        
        Returns:
            True if recording stopped
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        return await self._impl.stop_video()
    
    async def start_audio_recording(self) -> bool:
        """
        Start audio recording on glasses
        
        Returns:
            True if recording started
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        return await self._impl.start_audio()
    
    async def stop_audio_recording(self) -> bool:
        """
        Stop audio recording on glasses
        
        Returns:
            True if recording stopped
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        return await self._impl.stop_audio()
    
    async def trigger_ai_capture(self) -> bool:
        """
        Trigger AI processing mode
        
        This captures an image on the glasses and sends it for
        AI processing (happens on cloud/phone, not on glasses).
        The processed result may be available through a companion app.
        
        Returns:
            True if AI capture triggered
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        return await self._impl.trigger_ai_mode()
    
    # Device Information
    
    async def get_battery_info(self) -> Optional[BatteryInfo]:
        """
        Get battery status from glasses
        
        Returns:
            BatteryInfo or None if failed
        """
        if self.state != ConnectionState.CONNECTED:
            return None
        
        info = await self._impl.get_battery()
        if info:
            return BatteryInfo(
                level=info.get("level", 0),
                charging=info.get("charging", False)
            )
        
        return None
    
    async def get_media_count(self) -> Optional[MediaCount]:
        """
        Get count of media files stored on glasses
        
        Returns:
            MediaCount or None if failed
        """
        if self.state != ConnectionState.CONNECTED:
            return None
        
        counts = await self._impl.get_media_count()
        if counts:
            return MediaCount(
                photos=counts.get("photos", 0),
                videos=counts.get("videos", 0),
                audio_files=counts.get("audio", 0)
            )
        
        return None
    
    async def sync_time(self) -> bool:
        """
        Sync glasses time with system time
        
        Returns:
            True if time synced successfully
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        return await self._impl.sync_device_time()
    
    async def set_volume(self, level: int) -> bool:
        """
        Set glasses volume
        
        Args:
            level: Volume level 0-100
            
        Returns:
            True if volume set successfully
        """
        if self.state != ConnectionState.CONNECTED:
            return False
        
        level = max(0, min(100, level))
        return await self._impl.set_volume(level)
    
    # Callbacks
    
    def on_battery_update(self, callback: Callable[[BatteryInfo], None]):
        """
        Register callback for battery updates
        
        Args:
            callback: Function called with BatteryInfo when battery changes
        """
        self._impl.register_battery_callback(callback)
    
    def on_connection_change(self, callback: Callable[[ConnectionState], None]):
        """
        Register callback for connection state changes
        
        Args:
            callback: Function called when connection state changes
        """
        self._impl.register_connection_callback(callback)
    
    @property
    def is_connected(self) -> bool:
        """Check if connected to glasses"""
        return self.state == ConnectionState.CONNECTED