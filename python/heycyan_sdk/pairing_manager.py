"""
Device pairing and bonding management for HeyCyan Glasses
"""

import asyncio
import logging
import json
import os
from pathlib import Path
from typing import Dict, List, Optional
from datetime import datetime
import platform

logger = logging.getLogger(__name__)


class PairingManager:
    """Manages device pairing, bonding, and persistent connections"""
    
    def __init__(self, config_dir: Optional[Path] = None):
        """Initialize pairing manager
        
        Args:
            config_dir: Directory to store pairing information
        """
        if config_dir is None:
            config_dir = Path.home() / ".heycyan"
        
        self.config_dir = Path(config_dir)
        self.config_dir.mkdir(exist_ok=True)
        self.paired_devices_file = self.config_dir / "paired_devices.json"
        self._paired_devices = self._load_paired_devices()
    
    def _load_paired_devices(self) -> Dict:
        """Load saved paired devices from disk"""
        if self.paired_devices_file.exists():
            try:
                with open(self.paired_devices_file, 'r') as f:
                    return json.load(f)
            except Exception as e:
                logger.error(f"Failed to load paired devices: {e}")
        return {}
    
    def _save_paired_devices(self):
        """Save paired devices to disk"""
        try:
            with open(self.paired_devices_file, 'w') as f:
                json.dump(self._paired_devices, f, indent=2)
        except Exception as e:
            logger.error(f"Failed to save paired devices: {e}")
    
    def add_paired_device(self, address: str, name: str, bonding_key: Optional[bytes] = None):
        """Add a paired device to the registry
        
        Args:
            address: Device MAC address
            name: Device friendly name
            bonding_key: Optional bonding key for reconnection
        """
        self._paired_devices[address] = {
            "name": name,
            "paired_at": datetime.now().isoformat(),
            "last_connected": datetime.now().isoformat(),
            "bonding_key": bonding_key.hex() if bonding_key else None,
            "platform": platform.system()
        }
        self._save_paired_devices()
        logger.info(f"Added paired device: {name} ({address})")
    
    def remove_paired_device(self, address: str):
        """Remove a paired device
        
        Args:
            address: Device MAC address
        """
        if address in self._paired_devices:
            name = self._paired_devices[address]["name"]
            del self._paired_devices[address]
            self._save_paired_devices()
            logger.info(f"Removed paired device: {name} ({address})")
            
            # Also remove system pairing if on Linux
            if platform.system() == "Linux":
                self._remove_system_pairing(address)
    
    def _remove_system_pairing(self, address: str):
        """Remove system-level pairing (Linux)"""
        try:
            import subprocess
            subprocess.run(
                ["bluetoothctl", "remove", address],
                capture_output=True,
                timeout=10
            )
        except Exception as e:
            logger.warning(f"Could not remove system pairing: {e}")
    
    def get_paired_devices(self) -> List[Dict]:
        """Get list of all paired devices"""
        return [
            {"address": addr, **info}
            for addr, info in self._paired_devices.items()
        ]
    
    def is_paired(self, address: str) -> bool:
        """Check if a device is paired
        
        Args:
            address: Device MAC address
            
        Returns:
            True if device is paired
        """
        return address in self._paired_devices
    
    def get_bonding_key(self, address: str) -> Optional[bytes]:
        """Get saved bonding key for a device
        
        Args:
            address: Device MAC address
            
        Returns:
            Bonding key bytes or None
        """
        if address in self._paired_devices:
            key_hex = self._paired_devices[address].get("bonding_key")
            if key_hex:
                return bytes.fromhex(key_hex)
        return None
    
    def update_last_connected(self, address: str):
        """Update last connected timestamp for a device"""
        if address in self._paired_devices:
            self._paired_devices[address]["last_connected"] = datetime.now().isoformat()
            self._save_paired_devices()
    
    async def system_pair(self, address: str) -> bool:
        """Perform system-level pairing
        
        Args:
            address: Device MAC address
            
        Returns:
            True if pairing successful
        """
        system = platform.system()
        
        if system == "Linux":
            return await self._pair_linux(address)
        elif system == "Darwin":  # macOS
            # macOS handles pairing automatically during connection
            return True
        elif system == "Windows":
            return await self._pair_windows(address)
        else:
            logger.warning(f"Unsupported platform for pairing: {system}")
            return False
    
    async def _pair_linux(self, address: str) -> bool:
        """Pair on Linux using bluetoothctl"""
        try:
            import subprocess
            
            # Remove device if already exists (to force re-pairing)
            subprocess.run(
                ["bluetoothctl", "remove", address],
                capture_output=True,
                timeout=5
            )
            
            # Start pairing
            result = subprocess.run(
                ["bluetoothctl", "pair", address],
                capture_output=True,
                text=True,
                timeout=30
            )
            
            if result.returncode != 0:
                logger.error(f"Pairing failed: {result.stderr}")
                return False
            
            # Trust the device
            subprocess.run(
                ["bluetoothctl", "trust", address],
                capture_output=True,
                timeout=10
            )
            
            logger.info(f"Successfully paired with {address} on Linux")
            return True
            
        except Exception as e:
            logger.error(f"Linux pairing failed: {e}")
            return False
    
    async def _pair_windows(self, address: str) -> bool:
        """Pair on Windows (requires elevated permissions)"""
        # Windows pairing is more complex and usually requires
        # using Windows.Devices.Bluetooth APIs or manual pairing
        logger.warning("Automatic pairing on Windows not fully implemented")
        logger.info("Please pair the device manually through Windows Bluetooth settings")
        return False
    
    def clear_all_pairings(self):
        """Remove all paired devices"""
        addresses = list(self._paired_devices.keys())
        for address in addresses:
            self.remove_paired_device(address)
        logger.info("Cleared all paired devices")