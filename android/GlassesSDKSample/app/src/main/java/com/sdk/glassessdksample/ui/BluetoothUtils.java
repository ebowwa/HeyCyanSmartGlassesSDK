package com.sdk.glassessdksample.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by hzy on 2016/1/25.
 */
public class BluetoothUtils {
    /**
     * 蓝牙功能是否已经启用
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean isEnabledBluetooth(Context context) {
        try {
            BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            // 不支持BLE
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return false;
            }
            // 不支持蓝牙
            if (adapter == null) {
                return false;
            }
            // 蓝牙未打开
            return adapter.isEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


}
