//
//  GlassesFramework.swift
//  GlassesFramework
//
//  Created by Elijah Arbee on 8/15/25.
//

import Foundation

/// GlassesSDK provides all the functionality for connecting to and controlling HeyCyan smart glasses
public class GlassesSDK {
    
    /// The shared instance of the Bluetooth manager for device operations
    public static let bluetoothManager = BluetoothManager.shared
    
    /// Framework version information
    public static let version = "1.0.0"
    
    /// Initialize the framework
    public static func initialize() {
        print("GlassesFramework initialized - version \(version)")
    }
}

