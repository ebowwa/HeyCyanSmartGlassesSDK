//
//  NotificationExtensions.swift
//  GlassesFramework
//
//  Created on 2025/8/15.
//

import Foundation

public extension Notification.Name {
    static let aiImageReceived = Notification.Name("aiImageReceived")
    static let findDeviceAlertTriggered = Notification.Name("findDeviceAlertTriggered")
    static let findDeviceAlertFailed = Notification.Name("findDeviceAlertFailed")
}