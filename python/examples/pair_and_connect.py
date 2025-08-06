#!/usr/bin/env python3
"""
Example of pairing and connecting to HeyCyan Glasses
"""

import asyncio
import logging
import sys
from heycyan_sdk.client import HeyCyanClient

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


async def main():
    """Main example showing pairing and connection flow"""
    
    client = HeyCyanClient()
    
    try:
        # Step 1: Scan for devices
        logger.info("Scanning for HeyCyan devices...")
        devices = await client.scan_devices(duration=10.0)
        
        if not devices:
            logger.error("No HeyCyan devices found")
            return
        
        # Display found devices
        logger.info(f"Found {len(devices)} device(s):")
        for i, device in enumerate(devices):
            logger.info(f"  [{i}] {device.name} - {device.address}")
        
        # Select device (use first one for demo)
        if len(devices) == 1:
            selected = devices[0]
        else:
            # In a real app, you'd have user selection
            idx = int(input("Select device number: "))
            selected = devices[idx]
        
        logger.info(f"Selected device: {selected.name}")
        
        # Step 2: Pair with the device
        logger.info("Starting pairing process...")
        paired = await client.pair_device(selected.address)
        
        if not paired:
            logger.error("Failed to pair with device")
            return
        
        logger.info("Successfully paired with device!")
        
        # Step 3: Now we can use the device
        logger.info("Testing device functions...")
        
        # Take a photo
        logger.info("Taking photo...")
        if await client.take_photo():
            logger.info("Photo captured successfully")
        else:
            logger.error("Failed to capture photo")
        
        # Wait a bit
        await asyncio.sleep(2)
        
        # Start video
        logger.info("Starting video recording...")
        if await client.start_video():
            logger.info("Video recording started")
            
            # Record for 5 seconds
            await asyncio.sleep(5)
            
            # Stop video
            logger.info("Stopping video recording...")
            if await client.stop_video():
                logger.info("Video recording stopped")
        
    except KeyboardInterrupt:
        logger.info("Interrupted by user")
    except Exception as e:
        logger.error(f"Error: {e}")
    finally:
        # Always disconnect
        logger.info("Disconnecting...")
        await client.disconnect()
        logger.info("Disconnected")


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        logger.info("Program interrupted")
        sys.exit(0)