"""
Public BLE client interface - hides protocol details
"""

import asyncio
import logging
from typing import Optional, List, Callable
from bleak import BleakClient, BleakScanner
from .pairing_manager import PairingManager

logger = logging.getLogger(__name__)

# Try to import private protocol, fall back to environment variables or config
try:
    from ._protocol import (
        _PRIMARY_SERVICE, _SECONDARY_SERVICE,
        _CHAR_COMMAND, _CHAR_NOTIFY, _CHAR_DATA,
        _CMD_PHOTO, _CMD_VIDEO_START, _CMD_VIDEO_STOP,
        _CMD_AUDIO_START, _CMD_AUDIO_STOP, _CMD_AI_PHOTO
    )
except ImportError:
    # For PyPI distribution, these would be loaded from compiled module
    # or encrypted configuration
    import os
    _PRIMARY_SERVICE = os.getenv("HEYCYAN_PRIMARY_SERVICE", "")
    _SECONDARY_SERVICE = os.getenv("HEYCYAN_SECONDARY_SERVICE", "")
    _CHAR_COMMAND = os.getenv("HEYCYAN_CHAR_COMMAND", "")
    _CHAR_NOTIFY = os.getenv("HEYCYAN_CHAR_NOTIFY", "")
    _CHAR_DATA = os.getenv("HEYCYAN_CHAR_DATA", "")
    
    # Commands would be loaded from encrypted config
    _CMD_PHOTO = bytes()
    _CMD_VIDEO_START = bytes()
    _CMD_VIDEO_STOP = bytes()
    _CMD_AUDIO_START = bytes()
    _CMD_AUDIO_STOP = bytes()
    _CMD_AI_PHOTO = bytes()


class HeyCyanClient:
    """
    Public interface for HeyCyan Glasses - no protocol details exposed
    """
    
    def __init__(self):
        self._client = None
        self._connected = False
        self._pairing_manager = PairingManager()
        self._current_device = None
        
    async def scan_devices(self, duration: float = 5.0):
        """Scan for HeyCyan devices"""
        devices = []
        
        def callback(device, adv_data):
            # Device detection logic without exposing UUIDs
            if self._is_heycyan_device(device, adv_data):
                devices.append(device)
        
        scanner = BleakScanner(callback)
        await scanner.start()
        await asyncio.sleep(duration)
        await scanner.stop()
        
        return devices
    
    def _is_heycyan_device(self, device, adv_data):
        """Check if device is HeyCyan - logic hidden"""
        # Check name patterns without exposing service UUIDs
        if device.name and ("HeyCyan" in device.name or "Glasses" in device.name):
            return True
        # Service UUID check hidden from public API
        return self._check_services(adv_data)
    
    def _check_services(self, adv_data):
        """Private service check"""
        if not adv_data.service_uuids:
            return False
        # Check against private UUIDs without exposing them
        return any(uuid in (_PRIMARY_SERVICE, _SECONDARY_SERVICE) 
                  for uuid in adv_data.service_uuids)
    
    async def connect(self, device_address: str):
        """Connect to device by address"""
        self._client = BleakClient(device_address)
        self._connected = await self._client.connect()
        
        if self._connected:
            await self._setup_notifications()
        
        return self._connected
    
    async def pair_device(self, device_address: str, device_name: str = "HeyCyan Glasses"):
        """Pair/bond with device before connecting"""
        try:
            # Check if already paired
            if self._pairing_manager.is_paired(device_address):
                logger.info(f"Device {device_address} already paired, attempting reconnection")
                bonding_key = self._pairing_manager.get_bonding_key(device_address)
            else:
                # Perform system pairing first
                if not await self._pairing_manager.system_pair(device_address):
                    logger.error("System pairing failed")
                    return False
                bonding_key = None
            
            # Now connect with pairing
            self._client = BleakClient(device_address)
            self._connected = await self._client.connect()
            
            if self._connected:
                self._current_device = {"address": device_address, "name": device_name}
                
                # Perform bonding handshake if needed
                bonding_key = await self._perform_bonding(bonding_key)
                
                # Save pairing info
                self._pairing_manager.add_paired_device(device_address, device_name, bonding_key)
                self._pairing_manager.update_last_connected(device_address)
                
                await self._setup_notifications()
                logger.info(f"Successfully paired and connected to {device_name} ({device_address})")
            
            return self._connected
            
        except Exception as e:
            logger.error(f"Pairing failed: {e}")
            return False
    
    async def _perform_bonding(self, existing_key: Optional[bytes] = None):
        """Perform device-specific bonding/authentication
        
        Args:
            existing_key: Previously saved bonding key for reconnection
            
        Returns:
            New or existing bonding key
        """
        # Send bonding request based on HeyCyan protocol
        # This might involve sending a specific command sequence
        if _CHAR_COMMAND:
            try:
                if existing_key:
                    # Use existing bonding key for reconnection
                    logger.info("Using saved bonding key for reconnection")
                    await self._client.write_gatt_char(_CHAR_COMMAND, existing_key)
                else:
                    # Send initial bonding command (device-specific)
                    # This would be defined in the protocol
                    bonding_cmd = bytes([0x01, 0x00])  # Example bonding initiation
                    await self._client.write_gatt_char(_CHAR_COMMAND, bonding_cmd)
                    
                    # Wait for bonding response
                    await asyncio.sleep(1.0)
                    
                    # Generate or receive bonding key
                    # In real implementation, this would come from device response
                    bonding_key = bytes([0xFF, 0xEE, 0xDD, 0xCC])  # Example key
                    
                    logger.info("Bonding handshake completed")
                    return bonding_key
                    
                return existing_key
                
            except Exception as e:
                logger.error(f"Bonding failed: {e}")
                raise
        return None
    
    async def disconnect(self):
        """Disconnect from device"""
        if self._client:
            await self._client.disconnect()
            self._connected = False
    
    async def _setup_notifications(self):
        """Setup notifications - UUIDs hidden"""
        if _CHAR_NOTIFY:
            await self._client.start_notify(_CHAR_NOTIFY, self._handle_notify)
        if _CHAR_DATA:
            await self._client.start_notify(_CHAR_DATA, self._handle_data)
    
    def _handle_notify(self, sender, data):
        """Handle notifications internally"""
        # Process without exposing protocol
        pass
    
    def _handle_data(self, sender, data):
        """Handle data transfers internally"""
        # Process without exposing protocol
        pass
    
    async def take_photo(self):
        """Take a photo - command details hidden"""
        if not self._connected:
            return False
        return await self._send_command(_CMD_PHOTO)
    
    async def start_video(self):
        """Start video - command details hidden"""
        if not self._connected:
            return False
        return await self._send_command(_CMD_VIDEO_START)
    
    async def stop_video(self):
        """Stop video - command details hidden"""
        if not self._connected:
            return False
        return await self._send_command(_CMD_VIDEO_STOP)
    
    async def _send_command(self, command):
        """Send command - protocol hidden"""
        if not command or not _CHAR_COMMAND:
            return False
        
        try:
            await self._client.write_gatt_char(_CHAR_COMMAND, command)
            return True
        except Exception as e:
            logger.error(f"Command failed: {e}")
            return False