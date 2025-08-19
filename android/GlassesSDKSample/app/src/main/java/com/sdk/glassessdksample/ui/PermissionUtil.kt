package com.sdk.glassessdksample.ui
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * @author hzy ,
 * @date  2020/12/22
 * <p>
 * "程序应该是写给其他人读的,
 * 让机器来运行它只是一个附带功能"
 **/
fun requestCallPhonePermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback,
) {
    XXPermissions.with(activity)
        .permission(Permission.READ_PHONE_STATE)
        .permission(Permission.READ_CALL_LOG)
        .permission(Permission.CALL_PHONE)
        .permission(Permission.READ_CONTACTS)
        .permission(Permission.ANSWER_PHONE_CALLS)
        .request(requestCallback)
}

fun hasCallPhonePermission(
    activity: FragmentActivity,
): Boolean {
    val permissions = mutableListOf<String>()
    permissions.add(Permission.READ_PHONE_STATE)
    permissions.add(Permission.READ_CALL_LOG)
    permissions.add(Permission.CALL_PHONE)
    permissions.add(Permission.READ_CONTACTS)
    permissions.add(Permission.ANSWER_PHONE_CALLS)
    return XXPermissions.isGranted(activity, permissions)
}

fun hasCameraPermission(
    context: Context,
): Boolean {
    val permissions = mutableListOf<String>()
    permissions.add(Permission.CAMERA)
    return XXPermissions.isGranted(context, permissions)
}

fun hasSMSPermission(
    activity: FragmentActivity,
): Boolean {
    val permissions = mutableListOf<String>()
    permissions.add(Permission.READ_SMS)
    permissions.add(Permission.RECEIVE_SMS)
    return XXPermissions.isGranted(activity, permissions)
}

fun hasContactPermission(activity: FragmentActivity): Boolean {
    return XXPermissions.isGranted(activity, Permission.READ_CONTACTS)
}

fun hasLocationPermission(activity: FragmentActivity): Boolean {
    return XXPermissions.isGranted(activity, Permission.ACCESS_FINE_LOCATION)
}

fun hasBgLocationPermission(activity: FragmentActivity): Boolean {
    return XXPermissions.isGranted(activity, Permission.ACCESS_BACKGROUND_LOCATION)
}

fun hasCallPermission(activity: FragmentActivity): Boolean {
    val permissions = mutableListOf<String>()
    permissions.add(Permission.READ_PHONE_STATE)
    permissions.add(Permission.READ_CALL_LOG)
    permissions.add(Permission.CALL_PHONE)
    permissions.add(Permission.ANSWER_PHONE_CALLS)
    return XXPermissions.isGranted(activity, permissions)
}

fun hasBluetooth(activity: FragmentActivity): Boolean {
    val permissions = mutableListOf<String>()
    permissions.add(Permission.BLUETOOTH_SCAN)
    permissions.add(Permission.BLUETOOTH_CONNECT)
    permissions.add(Permission.BLUETOOTH_ADVERTISE)
    return XXPermissions.isGranted(activity, permissions)
}


fun requestSMSPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback,
) {
    XXPermissions.with(activity)
        .permission(Permission.READ_SMS)
        .permission(Permission.RECEIVE_SMS)
        .request(requestCallback)
}

fun requestLocationPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback,
) {
    XXPermissions.with(activity)
        .permission(Permission.ACCESS_COARSE_LOCATION)
        .permission(Permission.ACCESS_FINE_LOCATION)
        .request(requestCallback)
}

fun requestBluetoothPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback
) {
    XXPermissions.with(activity)
        .permission(Permission.BLUETOOTH_SCAN)
        .permission(Permission.BLUETOOTH_CONNECT)
        .permission(Permission.BLUETOOTH_ADVERTISE)
        .permission(Permission.ACCESS_FINE_LOCATION)
        .request(requestCallback)
}

fun requestCallPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback,
) {
    XXPermissions.with(activity)
        .permission(Permission.READ_PHONE_STATE)
        .permission(Permission.READ_CALL_LOG)
        .permission(Permission.CALL_PHONE)
        .permission(Permission.ANSWER_PHONE_CALLS)
        .request(requestCallback)
}

fun requestContactPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback,
) {
    XXPermissions.with(activity)
        .permission(Permission.READ_CONTACTS)
        .request(requestCallback)
}

fun requestBgLocation(activity: FragmentActivity, requestCallback: OnPermissionCallback) {
    XXPermissions.with(activity)
        .permission(Permission.ACCESS_COARSE_LOCATION)
        .permission(Permission.ACCESS_FINE_LOCATION)
        .permission(Permission.ACCESS_BACKGROUND_LOCATION)
        .request(requestCallback)
}

fun requestAlertWindowPermission(activity: FragmentActivity) {
    XXPermissions.with(activity).permission(Permission.SYSTEM_ALERT_WINDOW)
}

fun requestNearbyWifiDevicesPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback
) {
    XXPermissions.with(activity)
        .permission(Permission.NEARBY_WIFI_DEVICES)
        .request(requestCallback)
}

fun hasNearbyWifiDevicesPermission(
    activity: FragmentActivity
): Boolean {
    return XXPermissions.isGranted(activity, Permission.NEARBY_WIFI_DEVICES)
}


fun requestAllPermission(
    activity: FragmentActivity,
    callback: OnPermissionCallback
) {
    XXPermissions.with(activity)
//        .permission(Permission.WRITE_EXTERNAL_STORAGE)
//        .permission(Permission.READ_EXTERNAL_STORAGE)
        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
        .request(callback)
}


fun requestCameraPermission(
    activity: FragmentActivity,
    requestCallback: OnPermissionCallback,
) {
    XXPermissions.with(activity).permission(
        Permission.CAMERA
    ).request(requestCallback)
}