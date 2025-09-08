#!/usr/bin/env python3
"""
macOS-specific pairing example for HeyCyan Glasses
"""

import asyncio
import logging
import sys
import platform
from heycyan_sdk.client import HeyCyanClient
from heycyan_sdk.pairing_manager import PairingManager

# Only import macOS helper on Darwin
if platform.system() == "Darwin":
    from heycyan_sdk.macos_bluetooth import MacOSBluetoothHelper

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


async def check_macos_setup():
    """Check macOS Bluetooth setup and prerequisites"""
    if platform.system() != "Darwin":
        logger.error("This script is for macOS only")
        return False
    
    helper = MacOSBluetoothHelper()
    
    # Check Bluetooth status
    status = helper.get_bluetooth_status()
    logger.info(f"Bluetooth status: {status}")
    
    if not status["enabled"]:
        logger.info("Enabling Bluetooth...")
        helper.enable_bluetooth()
    
    # Check if blueutil is installed
    if not helper.is_blueutil_installed():
        logger.warning("blueutil is not installed")
        logger.info(helper.install_blueutil_instructions())
        logger.info("Continuing without blueutil (limited functionality)")
    else:
        logger.info("blueutil is installed ✓")
    
    return True


async def list_paired_devices():
    """List all paired Bluetooth devices on macOS"""
    helper = MacOSBluetoothHelper()
    devices = await helper.list_devices()
    
    if devices:
        logger.info(f"Found {len(devices)} paired device(s):")
        for device in devices:
            status = "connected" if device.get("connected") else "disconnected"
            logger.info(f"  • {device['name']} ({device['address']}) - {status}")
    else:
        logger.info("No paired devices found")
    
    return devices


async def main():
    """Main macOS pairing example"""
    
    # Check macOS setup
    if not await check_macos_setup():
        return
    
    # Initialize SDK and pairing manager
    client = HeyCyanClient()
    pairing_mgr = PairingManager()
    
    # Show previously paired HeyCyan devices
    paired_heycyan = pairing_mgr.get_paired_devices()
    if paired_heycyan:
        logger.info("\nPreviously paired HeyCyan devices:")
        for device in paired_heycyan:
            logger.info(f"  • {device['name']} ({device['address']})")
            logger.info(f"    Last connected: {device['last_connected']}")
    
    # List all Bluetooth devices
    logger.info("\nAll paired Bluetooth devices:")
    await list_paired_devices()
    
    try:
        # Scan for HeyCyan devices
        logger.info("\nScanning for HeyCyan devices...")
        devices = await client.scan_devices(duration=10.0)
        
        if not devices:
            logger.warning("No HeyCyan devices found")
            logger.info("\nmacOS Pairing Tips:")
            logger.info("1. Make sure the glasses are in pairing mode")
            logger.info("2. Check System Preferences > Bluetooth")
            logger.info("3. For BLE devices, pairing happens automatically on first connect")
            logger.info("4. Some devices need to be 'forgotten' and re-paired if having issues")
            return
        
        # Display found devices
        logger.info(f"\nFound {len(devices)} HeyCyan device(s):")
        for i, device in enumerate(devices):
            paired_status = "✓ Paired" if pairing_mgr.is_paired(device.address) else "Not paired"
            logger.info(f"  [{i}] {device.name} - {device.address} ({paired_status})")
        
        # Select device
        if len(devices) == 1:
            selected = devices[0]
            logger.info(f"\nAuto-selected: {selected.name}")
        else:
            idx = int(input("\nSelect device number: "))
            selected = devices[idx]
            logger.info(f"Selected: {selected.name}")
        
        # Check if already paired
        if pairing_mgr.is_paired(selected.address):
            logger.info("Device is already paired, attempting direct connection...")
            connected = await client.connect(selected.address)
        else:
            logger.info("Starting pairing process...")
            logger.info("\nmacOS Pairing Notes:")
            logger.info("• BLE devices usually pair automatically on first connect")
            logger.info("• You may see a pairing request notification")
            logger.info("• If pairing fails, try through System Preferences")
            
            connected = await client.pair_device(selected.address, selected.name)
        
        if connected:
            logger.info("✓ Successfully connected!")
            
            # Test the connection
            logger.info("\nTesting device functions...")
            
            # Take a photo
            logger.info("Taking photo...")
            if await client.take_photo():
                logger.info("✓ Photo captured")
            else:
                logger.error("✗ Photo capture failed")
            
            await asyncio.sleep(2)
            
            # Start/stop video
            logger.info("Testing video recording...")
            if await client.start_video():
                logger.info("✓ Video started")
                await asyncio.sleep(3)
                if await client.stop_video():
                    logger.info("✓ Video stopped")
        else:
            logger.error("Failed to connect")
            logger.info("\nTroubleshooting:")
            logger.info("1. Open System Preferences > Bluetooth")
            logger.info("2. Look for the HeyCyan device")
            logger.info("3. If present, click 'Connect' or remove and re-pair")
            logger.info("4. Make sure the glasses are in pairing mode")
    
    except KeyboardInterrupt:
        logger.info("\nInterrupted by user")
    except Exception as e:
        logger.error(f"Error: {e}")
    finally:
        logger.info("\nDisconnecting...")
        await client.disconnect()
        logger.info("Done")


if __name__ == "__main__":
    if platform.system() != "Darwin":
        print("This script is for macOS only. Use pair_and_connect.py for other platforms.")
        sys.exit(1)
    
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        logger.info("Program interrupted")
        sys.exit(0)