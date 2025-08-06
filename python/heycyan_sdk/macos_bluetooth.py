"""
macOS-specific Bluetooth utilities for HeyCyan Glasses
"""

import subprocess
import logging
import asyncio
from typing import List, Dict, Optional
import re

logger = logging.getLogger(__name__)


class MacOSBluetoothHelper:
    """Helper class for macOS Bluetooth operations"""
    
    @staticmethod
    def is_blueutil_installed() -> bool:
        """Check if blueutil is installed"""
        try:
            result = subprocess.run(
                ["which", "blueutil"],
                capture_output=True,
                timeout=5
            )
            return result.returncode == 0
        except:
            return False
    
    @staticmethod
    def install_blueutil_instructions() -> str:
        """Get instructions for installing blueutil"""
        return """
        To install blueutil on macOS:
        
        Using Homebrew:
            brew install blueutil
        
        Using MacPorts:
            sudo port install blueutil
        
        Manual installation:
            git clone https://github.com/toy/blueutil.git
            cd blueutil
            make
            sudo make install
        """
    
    @classmethod
    async def list_devices(cls) -> List[Dict]:
        """List all Bluetooth devices on macOS"""
        devices = []
        
        if cls.is_blueutil_installed():
            try:
                # Use blueutil to list devices
                result = subprocess.run(
                    ["blueutil", "--paired"],
                    capture_output=True,
                    text=True,
                    timeout=10
                )
                
                if result.returncode == 0:
                    # Parse blueutil output
                    for line in result.stdout.strip().split('\n'):
                        if line:
                            # Format: address: XX:XX:XX:XX:XX:XX, name: "Device Name"
                            match = re.match(r'address: ([\w:]+)(?:, name: "([^"]*)")?', line)
                            if match:
                                address = match.group(1)
                                name = match.group(2) or "Unknown"
                                devices.append({
                                    "address": address,
                                    "name": name,
                                    "paired": True
                                })
            except Exception as e:
                logger.error(f"Error listing devices with blueutil: {e}")
        
        # Fallback: Use system_profiler
        try:
            result = subprocess.run(
                ["system_profiler", "SPBluetoothDataType", "-json"],
                capture_output=True,
                text=True,
                timeout=10
            )
            
            if result.returncode == 0:
                import json
                data = json.loads(result.stdout)
                
                # Parse system_profiler output
                bt_data = data.get("SPBluetoothDataType", [])
                if bt_data and len(bt_data) > 0:
                    devices_info = bt_data[0].get("device_connected", {})
                    for device_id, device_info in devices_info.items():
                        if isinstance(device_info, dict):
                            devices.append({
                                "address": device_info.get("device_address", ""),
                                "name": device_info.get("device_name", device_id),
                                "paired": True,
                                "connected": device_info.get("device_isconnected", "No") == "Yes"
                            })
        except Exception as e:
            logger.error(f"Error listing devices with system_profiler: {e}")
        
        return devices
    
    @classmethod
    async def connect_device(cls, address: str) -> bool:
        """Connect to a Bluetooth device on macOS"""
        if cls.is_blueutil_installed():
            try:
                result = subprocess.run(
                    ["blueutil", "--connect", address],
                    capture_output=True,
                    text=True,
                    timeout=30
                )
                
                if result.returncode == 0:
                    logger.info(f"Connected to {address}")
                    return True
                else:
                    logger.error(f"Failed to connect: {result.stderr}")
            except Exception as e:
                logger.error(f"Error connecting with blueutil: {e}")
        
        # Fallback: Use AppleScript
        return await cls._connect_via_applescript(address)
    
    @classmethod
    async def _connect_via_applescript(cls, address: str) -> bool:
        """Connect using AppleScript (requires user interaction)"""
        applescript = f'''
        tell application "System Preferences"
            activate
            reveal pane "com.apple.preferences.Bluetooth"
        end tell
        
        display dialog "Please connect to device {address} in the Bluetooth preferences window" buttons {{"OK"}} default button "OK"
        '''
        
        try:
            subprocess.run(
                ["osascript", "-e", applescript],
                timeout=60
            )
            return True
        except:
            return False
    
    @classmethod
    async def disconnect_device(cls, address: str) -> bool:
        """Disconnect from a Bluetooth device on macOS"""
        if cls.is_blueutil_installed():
            try:
                result = subprocess.run(
                    ["blueutil", "--disconnect", address],
                    capture_output=True,
                    text=True,
                    timeout=10
                )
                
                if result.returncode == 0:
                    logger.info(f"Disconnected from {address}")
                    return True
                else:
                    logger.error(f"Failed to disconnect: {result.stderr}")
            except Exception as e:
                logger.error(f"Error disconnecting with blueutil: {e}")
        
        return False
    
    @classmethod
    async def unpair_device(cls, address: str) -> bool:
        """Unpair a Bluetooth device on macOS"""
        if cls.is_blueutil_installed():
            try:
                # blueutil doesn't have unpair, but we can use --remove
                result = subprocess.run(
                    ["blueutil", "--unpair", address],
                    capture_output=True,
                    text=True,
                    timeout=10
                )
                
                if result.returncode == 0:
                    logger.info(f"Unpaired {address}")
                    return True
            except:
                pass
        
        # Fallback: Guide user to System Preferences
        logger.info("Please unpair the device manually in System Preferences > Bluetooth")
        return False
    
    @classmethod
    def get_bluetooth_status(cls) -> Dict:
        """Get Bluetooth adapter status"""
        status = {"enabled": False, "discoverable": False}
        
        if cls.is_blueutil_installed():
            try:
                # Check if Bluetooth is on
                result = subprocess.run(
                    ["blueutil", "--power"],
                    capture_output=True,
                    text=True,
                    timeout=5
                )
                
                if result.returncode == 0:
                    status["enabled"] = result.stdout.strip() == "1"
                
                # Check if discoverable
                result = subprocess.run(
                    ["blueutil", "--discoverable"],
                    capture_output=True,
                    text=True,
                    timeout=5
                )
                
                if result.returncode == 0:
                    status["discoverable"] = result.stdout.strip() == "1"
            except:
                pass
        
        return status
    
    @classmethod
    def enable_bluetooth(cls) -> bool:
        """Enable Bluetooth on macOS"""
        if cls.is_blueutil_installed():
            try:
                subprocess.run(
                    ["blueutil", "--power", "1"],
                    check=True,
                    timeout=5
                )
                logger.info("Bluetooth enabled")
                return True
            except:
                pass
        
        logger.warning("Could not enable Bluetooth programmatically")
        return False