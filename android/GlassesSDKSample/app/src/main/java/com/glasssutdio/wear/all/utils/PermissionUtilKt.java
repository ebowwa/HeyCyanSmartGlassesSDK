package com.glasssutdio.wear.all.utils;

import androidx.fragment.app.FragmentActivity;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PermissionUtil.kt */
@Metadata(m606d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b\u001a\u0016\u0010\t\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\b\u001a\u0016\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b\u001a\u0016\u0010\f\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b\u001a\u0016\u0010\r\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\b\u001a\u0016\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b¨\u0006\u000f"}, m607d2 = {"hasBluetooth", "", "activity", "Landroidx/fragment/app/FragmentActivity;", "hasRecord", "requestBluetoothPermission", "", "requestCallback", "Lcom/hjq/permissions/OnPermissionCallback;", "requestCameraPermission", "callback", "requestLocationPermission", "requestRecordPermission", "requestStoragePermission", "requestWifiPermission", "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class PermissionUtilKt {
    public static final boolean hasBluetooth(FragmentActivity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        ArrayList arrayList = new ArrayList();
        arrayList.add(Permission.BLUETOOTH_SCAN);
        arrayList.add(Permission.BLUETOOTH_CONNECT);
        arrayList.add(Permission.BLUETOOTH_ADVERTISE);
        return XXPermissions.isGranted(activity, arrayList);
    }

    public static final boolean hasRecord(FragmentActivity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        ArrayList arrayList = new ArrayList();
        arrayList.add(Permission.RECORD_AUDIO);
        return XXPermissions.isGranted(activity, arrayList);
    }

    public static final void requestLocationPermission(FragmentActivity activity, OnPermissionCallback requestCallback) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(requestCallback, "requestCallback");
        XXPermissions.with(activity).permission(Permission.ACCESS_COARSE_LOCATION).permission(Permission.ACCESS_FINE_LOCATION).request(requestCallback);
    }

    public static final void requestRecordPermission(FragmentActivity activity, OnPermissionCallback requestCallback) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(requestCallback, "requestCallback");
        XXPermissions.with(activity).permission(Permission.RECORD_AUDIO).request(requestCallback);
    }

    public static final void requestWifiPermission(FragmentActivity activity, OnPermissionCallback requestCallback) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(requestCallback, "requestCallback");
        XXPermissions.with(activity).permission(Permission.BLUETOOTH_SCAN).permission(Permission.BLUETOOTH_CONNECT).permission(Permission.BLUETOOTH_ADVERTISE).permission(Permission.ACCESS_FINE_LOCATION).permission(Permission.NEARBY_WIFI_DEVICES).request(requestCallback);
    }

    public static final void requestStoragePermission(FragmentActivity activity, OnPermissionCallback callback) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(callback, "callback");
        XXPermissions.with(activity).permission(Permission.READ_MEDIA_IMAGES).permission(Permission.READ_MEDIA_IMAGES).request(callback);
    }

    public static final void requestCameraPermission(FragmentActivity activity, OnPermissionCallback callback) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(callback, "callback");
        XXPermissions.with(activity).permission(Permission.CAMERA).request(callback);
    }

    public static final void requestBluetoothPermission(FragmentActivity activity, OnPermissionCallback requestCallback) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(requestCallback, "requestCallback");
        XXPermissions.with(activity).permission(Permission.BLUETOOTH_SCAN).permission(Permission.BLUETOOTH_CONNECT).permission(Permission.BLUETOOTH_ADVERTISE).permission(Permission.ACCESS_FINE_LOCATION).request(requestCallback);
    }
}
