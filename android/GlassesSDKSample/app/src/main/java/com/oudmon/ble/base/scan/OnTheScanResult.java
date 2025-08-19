package com.oudmon.ble.base.scan;

import android.bluetooth.BluetoothDevice;

/* loaded from: classes2.dex */
public interface OnTheScanResult {
    void onResult(BluetoothDevice bluetoothDevice);

    void onScanFailed(int i);
}
