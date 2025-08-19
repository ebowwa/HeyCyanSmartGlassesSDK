package com.oudmon.ble.base.scan;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import java.util.List;

/* loaded from: classes2.dex */
public interface ScanWrapperCallback {
    void onBatchScanResults(List<ScanResult> list);

    void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr);

    void onParsedData(BluetoothDevice bluetoothDevice, ScanRecord scanRecord);

    void onScanFailed(int i);

    void onStart();

    void onStop();
}
