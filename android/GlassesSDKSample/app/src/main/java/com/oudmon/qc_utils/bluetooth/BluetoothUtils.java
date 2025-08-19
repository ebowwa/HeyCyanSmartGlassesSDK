package com.oudmon.qc_utils.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/* loaded from: classes2.dex */
public class BluetoothUtils {
    public static boolean hasLollipop() {
        return true;
    }

    public static boolean isEnabledBluetooth(Context context) {
        try {
            BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService("bluetooth")).getAdapter();
            if (context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le") && adapter != null) {
                return adapter.isEnabled();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
