package com.oudmon.ble.base.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.core.os.HandlerCompat;
import com.elvishew.xlog.XLog;
import com.iflytek.sparkchain.utils.constants.ErrorCode;
import com.oudmon.ble.base.bluetooth.DeviceManager;
import com.oudmon.qc_utils.bluetooth.BluetoothUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/* loaded from: classes2.dex */
public class BleScannerHelper {
    private static final String HANDLER_TOKEN = "stop_token";
    private static final String TAG = "BleScannerHelper";
    private static BleScannerHelper bleScannerHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int timeOut = ErrorCode.MSP_ERROR_HTTP_BASE;

    private BleScannerHelper() {
    }

    public static BleScannerHelper getInstance() {
        if (bleScannerHelper == null) {
            synchronized (BleScannerHelper.class) {
                if (bleScannerHelper == null) {
                    bleScannerHelper = new BleScannerHelper();
                }
            }
        }
        return bleScannerHelper;
    }

    public void reSetCallback() {
        bleScannerHelper = null;
    }

    public void scanDevice(final Context context, UUID uuid, final ScanWrapperCallback scanWrapperCallback) {
        this.handler.removeCallbacksAndMessages(HANDLER_TOKEN);
        if (!BluetoothUtils.isEnabledBluetooth(context)) {
            BleScannerCompat.getScanner(context).scanning = false;
            return;
        }
        if (BleScannerCompat.getScanner(context).isScanning()) {
            XLog.m137i("isScanning:true");
            stopScan(context);
        }
        HandlerCompat.postDelayed(this.handler, new Runnable() { // from class: com.oudmon.ble.base.scan.BleScannerHelper.1
            @Override // java.lang.Runnable
            public void run() {
                BleScannerHelper.this.stopScan(context);
                ScanWrapperCallback scanWrapperCallback2 = scanWrapperCallback;
                if (scanWrapperCallback2 != null) {
                    scanWrapperCallback2.onScanFailed(0);
                }
            }
        }, HANDLER_TOKEN, 12000L);
        BleScannerCompat.getScanner(context).startScan(scanWrapperCallback);
    }

    public void stopScan(Context context) {
        this.handler.removeCallbacksAndMessages(HANDLER_TOKEN);
        if (!BluetoothUtils.isEnabledBluetooth(context)) {
            BleScannerCompat.getScanner(context).scanning = false;
        } else {
            BleScannerCompat.getScanner(context).stopScan();
        }
    }

    public boolean scanTheDevice(final Context context, final String str, final OnTheScanResult onTheScanResult) {
        this.handler.removeCallbacksAndMessages(HANDLER_TOKEN);
        if (!BluetoothUtils.isEnabledBluetooth(context)) {
            return false;
        }
        HandlerCompat.postDelayed(this.handler, new Runnable() { // from class: com.oudmon.ble.base.scan.BleScannerHelper.2
            @Override // java.lang.Runnable
            public void run() {
                BleScannerHelper.this.stopScan(context);
                OnTheScanResult onTheScanResult2 = onTheScanResult;
                if (onTheScanResult2 != null) {
                    onTheScanResult2.onScanFailed(0);
                }
            }
        }, HANDLER_TOKEN, this.timeOut);
        BleScannerCompat.getScanner(context).startScan(new ScanWrapperCallback() { // from class: com.oudmon.ble.base.scan.BleScannerHelper.3
            @Override // com.oudmon.ble.base.scan.ScanWrapperCallback
            public void onParsedData(BluetoothDevice bluetoothDevice, ScanRecord scanRecord) {
            }

            @Override // com.oudmon.ble.base.scan.ScanWrapperCallback
            public void onStart() {
                XLog.m137i("start");
            }

            @Override // com.oudmon.ble.base.scan.ScanWrapperCallback
            public void onStop() {
                XLog.m137i("stop");
                try {
                    Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                    if (bondedDevices.size() > 0) {
                        for (BluetoothDevice bluetoothDevice : bondedDevices) {
                            if (bluetoothDevice != null && bluetoothDevice.getName() != null && bluetoothDevice.getAddress() != null && bluetoothDevice.getAddress().equalsIgnoreCase(str)) {
                                onTheScanResult.onResult(bluetoothDevice);
                                XLog.m137i("系统绑定了手环");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override // com.oudmon.ble.base.scan.ScanWrapperCallback
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                if (bluetoothDevice.getAddress().equalsIgnoreCase(str)) {
                    onTheScanResult.onResult(bluetoothDevice);
                    XLog.m137i(bluetoothDevice.getAddress());
                }
            }

            @Override // com.oudmon.ble.base.scan.ScanWrapperCallback
            public void onScanFailed(int i) {
                onTheScanResult.onScanFailed(i);
                XLog.m132e("------------" + i);
            }

            @Override // com.oudmon.ble.base.scan.ScanWrapperCallback
            public void onBatchScanResults(List<ScanResult> list) {
                Iterator<ScanResult> it = list.iterator();
                while (it.hasNext()) {
                    BluetoothDevice device = it.next().getDevice();
                    if (device.getAddress().equalsIgnoreCase(str)) {
                        onTheScanResult.onResult(device);
                    }
                }
            }
        });
        return true;
    }

    public void removeSystemBle(String str) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                XLog.m137i(bluetoothDevice.getName());
                XLog.m137i(bluetoothDevice.getAddress());
                if (bluetoothDevice != null && bluetoothDevice.getName() != null && bluetoothDevice.getAddress() != null) {
                    XLog.m137i("移除" + str);
                    if (!TextUtils.isEmpty(str) && str.equalsIgnoreCase(bluetoothDevice.getAddress())) {
                        removeBondDevice(defaultAdapter, bluetoothDevice.getAddress());
                    }
                }
            }
        }
    }

    public void removeSystemBle() throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                if (bluetoothDevice != null && bluetoothDevice.getName() != null && bluetoothDevice.getAddress() != null) {
                    String deviceAddress = DeviceManager.getInstance().getDeviceAddress();
                    if (!TextUtils.isEmpty(deviceAddress) && deviceAddress.equalsIgnoreCase(bluetoothDevice.getAddress())) {
                        removeBondDevice(defaultAdapter, bluetoothDevice.getAddress());
                    }
                }
            }
        }
    }

    public void removeMacSystemBond(String str) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                if (bluetoothDevice != null && bluetoothDevice.getName() != null && bluetoothDevice.getAddress() != null && !TextUtils.isEmpty(str) && str.equalsIgnoreCase(bluetoothDevice.getAddress())) {
                    removeBondDevice(defaultAdapter, bluetoothDevice.getAddress());
                }
            }
        }
    }

    public boolean isMacSystemBond(String str) {
        Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (bondedDevices.size() <= 0) {
            return false;
        }
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            if (bluetoothDevice != null && bluetoothDevice.getAddress() != null && !TextUtils.isEmpty(str) && str.equalsIgnoreCase(bluetoothDevice.getAddress())) {
                return true;
            }
        }
        return false;
    }

    private void removeBondDevice(BluetoothAdapter bluetoothAdapter, String str) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        XLog.m132e("1removeBond:" + str);
        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(str);
        try {
            Method method = BluetoothDevice.class.getMethod("removeBond", new Class[0]);
            method.setAccessible(true);
            method.invoke(remoteDevice, new Object[0]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    public int getTimeOut() {
        return this.timeOut;
    }

    public void setTimeOut(int i) {
        this.timeOut = i;
    }
}
