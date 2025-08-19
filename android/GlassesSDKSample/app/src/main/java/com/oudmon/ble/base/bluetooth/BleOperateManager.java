package com.oudmon.ble.base.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.elvishew.xlog.XLog;
import com.oudmon.ble.base.communication.CommandHandle;
import com.oudmon.ble.base.communication.Constants;
import com.oudmon.ble.base.communication.ICommandResponse;
import com.oudmon.ble.base.communication.responseImpl.DeviceNotifyListener;
import com.oudmon.ble.base.communication.responseImpl.DeviceSportNotifyListener;
import com.oudmon.ble.base.communication.responseImpl.InnerCameraNotifyListener;
import com.oudmon.ble.base.communication.responseImpl.PackageLengthListener;
import com.oudmon.ble.base.request.BaseRequest;
import com.oudmon.ble.base.request.EnableNotifyRequest;
import com.oudmon.ble.base.request.LocalWriteRequest;
import com.oudmon.qc_utils.bluetooth.BluetoothUtils;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public class BleOperateManager extends HandlerThread implements IBleListener {
    private static BleOperateManager DeviceOperateManager = null;
    private static final String TAG = "DeviceOperateManager";
    private Application application;
    private OnGattEventCallback callback;
    private DeviceNotifyListener deviceNotifyListener;
    private DeviceSportNotifyListener deviceSportNotifyListener;
    private InnerCameraNotifyListener innerCameraNotifyListener;
    private ConcurrentHashMap<Integer, LocalWriteRequest> localWriteRequestConcurrentHashMap;
    private final Context mContext;
    private final Object mLock;
    private boolean mRequestCompleted;
    private final Handler mainThreadHandler;
    private SparseArray<ICommandResponse> notifySparseArray;
    private String reConnectMac;
    private boolean ready;
    Runnable runnable;
    Runnable runnableEnable;
    private FirmwareRunnable timeoutFirmwareRunnable;
    private Handler workThreadHandler;

    public static BleOperateManager getInstance(Application application) {
        if (DeviceOperateManager == null) {
            synchronized (BleOperateManager.class) {
                if (DeviceOperateManager == null) {
                    DeviceOperateManager = new BleOperateManager(application);
                }
            }
        }
        return DeviceOperateManager;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getReConnectMac() {
        return this.reConnectMac;
    }

    public void setReConnectMac(String str) {
        this.reConnectMac = str;
    }

    public void setCallback(OnGattEventCallback onGattEventCallback) {
        this.callback = onGattEventCallback;
    }

    public static BleOperateManager getInstance() {
        return DeviceOperateManager;
    }

    private BleOperateManager(Context context) {
        super(TAG);
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.mLock = new Object();
        this.mRequestCompleted = false;
        this.localWriteRequestConcurrentHashMap = new ConcurrentHashMap<>();
        this.notifySparseArray = new SparseArray<>();
        this.deviceNotifyListener = new DeviceNotifyListener();
        this.timeoutFirmwareRunnable = new FirmwareRunnable();
        this.deviceSportNotifyListener = new DeviceSportNotifyListener();
        this.runnable = new Runnable() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.2
            @Override // java.lang.Runnable
            public void run() {
                XLog.m137i("---lock timeout---");
                BleOperateManager.this.mRequestCompleted = true;
                BleOperateManager.this.notifyLock();
            }
        };
        this.runnableEnable = new Runnable() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.6
            @Override // java.lang.Runnable
            public void run() {
                BleOperateManager.this.mRequestCompleted = true;
                BleOperateManager.this.notifyLock();
                EnableNotifyRequest enableNotifyRequest = new EnableNotifyRequest(Constants.UUID_SERVICE, Constants.UUID_READ, new EnableNotifyRequest.ListenerCallback() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.6.1
                    @Override // com.oudmon.ble.base.request.EnableNotifyRequest.ListenerCallback
                    public void enable(boolean z) {
                        XLog.m137i("enableUUID:" + z);
                        BleOperateManager.this.mainThreadHandler.removeCallbacks(BleOperateManager.this.runnableEnable);
                    }
                });
                enableNotifyRequest.setEnable(true);
                BleOperateManager unused = BleOperateManager.DeviceOperateManager;
                BleOperateManager.getInstance().execute(enableNotifyRequest);
            }
        };
        this.mContext = context;
        BleBaseControl.getInstance(context).setListener(this);
        this.notifySparseArray.put(47, new PackageLengthListener());
        start();
        this.workThreadHandler = new Handler(getLooper());
    }

    public ConcurrentHashMap<Integer, LocalWriteRequest> getLocalWriteRequestConcurrentHashMap() {
        return this.localWriteRequestConcurrentHashMap;
    }

    public void setBluetoothTurnOff(boolean z) {
        BleBaseControl.getInstance().setBluetoothTurnOff(z);
    }

    public void addOutCameraListener(ICommandResponse iCommandResponse) {
        this.innerCameraNotifyListener.setOutRspIOdmOpResponse(iCommandResponse);
    }

    public void removeOutCameraListener() {
        this.innerCameraNotifyListener.setOutRspIOdmOpResponse(null);
    }

    public void addSportDeviceListener(int i, ICommandResponse iCommandResponse) {
        this.deviceSportNotifyListener.setOutRspIOdmOpResponse(i, iCommandResponse);
    }

    public void removeSportDeviceListener(int i) {
        this.deviceSportNotifyListener.removeCallback(i);
    }

    public void addOutDeviceListener(int i, ICommandResponse iCommandResponse) {
        this.deviceNotifyListener.setOutRspIOdmOpResponse(i, iCommandResponse);
    }

    public void removeOutDeviceListener(int i) {
        this.deviceNotifyListener.removeCallback(i);
    }

    public void removeOthersListener() {
        this.deviceNotifyListener.removeOtherCallbacks();
    }

    public boolean addNotifyListener(int i, ICommandResponse iCommandResponse) {
        if (iCommandResponse == null) {
            return false;
        }
        this.notifySparseArray.put(i, iCommandResponse);
        return true;
    }

    public void removeNotifyListener(int i) {
        this.notifySparseArray.delete(i);
    }

    public SparseArray<ICommandResponse> getNotifySparseArray() {
        return this.notifySparseArray;
    }

    public void unBindDevice() {
        setNeedConnect(false);
        disconnect();
        setMacNull();
    }

    public void setMacNull() {
        BleBaseControl.getInstance().setmDeviceAddress(null);
    }

    public void connectDirectly(String str) {
        BleBaseControl.getInstance().connect(str);
    }

    public void connectWithScan(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(this.reConnectMac)) {
            return;
        }
        BleBaseControl.getInstance().setNeedReconnect(true);
        BleBaseControl.getInstance().setmDeviceAddress(str);
        BleBaseControl.getInstance().reconnectOpeningUp();
    }

    public void disconnect() {
        this.ready = false;
        XLog.m132e("disconnect...");
        BleBaseControl.getInstance().disconnectDevice(BleBaseControl.getInstance().getmDeviceAddress());
    }

    public void setNeedConnect(boolean z) {
        BleBaseControl.getInstance().setNeedReconnect(z);
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public boolean execute(final BaseRequest baseRequest) {
        if (!BluetoothUtils.isEnabledBluetooth(this.mContext)) {
            Log.e(TAG, "connectDirectly: 蓝牙未打开");
            return false;
        }
        if (!BleBaseControl.getInstance().ismIsConnected()) {
            return false;
        }
        if (baseRequest.writeRequest && !this.ready) {
            return false;
        }
        this.workThreadHandler.postDelayed(this.runnable, 5000L);
        this.workThreadHandler.post(new Runnable() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (BleOperateManager.this.mLock) {
                    try {
                        if (baseRequest.needInitCharacteristic()) {
                            BluetoothGattCharacteristic bluetoothGattCharacteristicFindTheGattCharacteristic = BleBaseControl.getInstance().findTheGattCharacteristic(baseRequest.getServiceUuid(), baseRequest.getCharUuid());
                            if (bluetoothGattCharacteristicFindTheGattCharacteristic == null) {
                                return;
                            }
                            if (!baseRequest.execute(BleBaseControl.getInstance().getGatt(BleBaseControl.getInstance().getmDeviceAddress()), bluetoothGattCharacteristicFindTheGattCharacteristic)) {
                                BleOperateManager.this.notifyLock();
                            }
                        } else {
                            boolean zExecute = baseRequest.execute(BleBaseControl.getInstance().getGatt(BleBaseControl.getInstance().getmDeviceAddress()), null);
                            XLog.m136i(Boolean.valueOf(zExecute));
                            if (!zExecute) {
                                BleOperateManager.this.notifyLock();
                            }
                        }
                        BleOperateManager.this.mRequestCompleted = false;
                        if (BleBaseControl.getInstance().ismIsConnected()) {
                            BleOperateManager.this.waitUntilActionResponse();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitUntilActionResponse() throws InterruptedException {
        while (!this.mRequestCompleted) {
            try {
                this.mLock.wait();
            } catch (Exception e) {
                Log.e(TAG, "Sleeping interrupted", e);
                return;
            }
        }
    }

    protected void notifyLock() {
        this.workThreadHandler.removeCallbacks(this.runnable);
        synchronized (this.mLock) {
            this.mRequestCompleted = true;
            this.mLock.notifyAll();
        }
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        notifyLock();
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        notifyLock();
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public boolean isConnected() {
        boolean zIsmIsConnected = BleBaseControl.getInstance().ismIsConnected();
        if (!zIsmIsConnected) {
            this.ready = false;
        }
        return zIsmIsConnected;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean z) {
        this.mainThreadHandler.removeCallbacks(this.timeoutFirmwareRunnable);
        if (!this.ready) {
            this.mainThreadHandler.postDelayed(new Runnable() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.3
                @Override // java.lang.Runnable
                public void run() {
                    Intent intent = new Intent(BleAction.BLE_SERVICE_DISCOVERED);
                    intent.putExtra(BleAction.EXTRA_ADDR, BleBaseControl.getInstance().getmDeviceAddress());
                    BleOperateManager.this.mySendBroadcast(intent);
                }
            }, 2500L);
            this.ready = z;
        } else {
            XLog.m137i("已经更新过版本号");
        }
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
        notifyLock();
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void startConnect() {
        mySendBroadcast(new Intent(BleAction.BLE_START_CONNECT));
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleGattConnected(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(BleAction.BLE_GATT_CONNECTED);
        intent.putExtra(BleAction.EXTRA_DEVICE, bluetoothDevice);
        intent.putExtra(BleAction.EXTRA_ADDR, bluetoothDevice.getAddress());
        mySendBroadcast(intent);
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleGattDisconnect(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(BleAction.BLE_GATT_DISCONNECTED);
        intent.putExtra(BleAction.EXTRA_DEVICE, bluetoothDevice);
        intent.putExtra(BleAction.EXTRA_ADDR, bluetoothDevice.getAddress());
        mySendBroadcast(intent);
        synchronized (this.mLock) {
            this.localWriteRequestConcurrentHashMap.clear();
            notifyLock();
        }
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleServiceDiscovered(int i, String str) {
        XLog.m137i("---------bleServiceDiscovered address");
        enableUUID();
        this.mainThreadHandler.postDelayed(new Runnable() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.4
            @Override // java.lang.Runnable
            public void run() {
                BleOperateManager.this.runCommonCmd();
            }
        }, 500L);
        this.mainThreadHandler.postDelayed(this.timeoutFirmwareRunnable, 1000L);
        this.mainThreadHandler.postDelayed(this.timeoutFirmwareRunnable, 1500L);
    }

    public class FirmwareRunnable implements Runnable {
        public FirmwareRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            BleOperateManager.this.runCommonCmd();
        }
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleStatus(int i, int i2) {
        Intent intent = new Intent(BleAction.BLE_STATUS);
        intent.putExtra(BleAction.EXTRA_BLE_STATUS, i);
        intent.putExtra(BleAction.EXTRA_BLE_NEW_STATE, i2);
        mySendBroadcast(intent);
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleCharacteristicRead(String str, String str2, int i, byte[] bArr) {
        Intent intent = new Intent(BleAction.BLE_CHARACTERISTIC_READ);
        intent.putExtra(BleAction.EXTRA_ADDR, str);
        intent.putExtra(BleAction.EXTRA_CHARACTER_UUID, str2);
        intent.putExtra(BleAction.EXTRA_STATUS, i);
        intent.putExtra(BleAction.EXTRA_VALUE, bArr);
        mySendBroadcast(intent);
        notifyLock();
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleCharacteristicNotification() {
        mySendBroadcast(new Intent(BleAction.BLE_CHARACTERISTIC_DISCOVERED));
        notifyLock();
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleCharacteristicWrite(String str, String str2, int i, byte[] bArr) {
        Intent intent = new Intent(BleAction.BLE_CHARACTERISTIC_WRITE);
        intent.putExtra(BleAction.EXTRA_ADDR, str);
        intent.putExtra(BleAction.EXTRA_CHARACTER_UUID, str2);
        intent.putExtra(BleAction.EXTRA_STATUS, i);
        intent.putExtra(BleAction.EXTRA_DATA, bArr);
        mySendBroadcast(intent);
        notifyLock();
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleCharacteristicChanged(String str, String str2, byte[] bArr) {
        Intent intent = new Intent(BleAction.BLE_CHARACTERISTIC_CHANGED);
        intent.putExtra(BleAction.EXTRA_ADDR, str);
        intent.putExtra(BleAction.EXTRA_CHARACTER_UUID, str2);
        intent.putExtra(BleAction.EXTRA_VALUE, bArr);
        mySendBroadcast(intent);
        OnGattEventCallback onGattEventCallback = this.callback;
        if (onGattEventCallback != null) {
            onGattEventCallback.onReceivedData(str2, bArr);
        }
    }

    @Override // com.oudmon.ble.base.bluetooth.IBleListener
    public void bleNoCallback() {
        mySendBroadcast(new Intent(BleAction.BLE_NO_CALLBACK));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mySendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(intent);
    }

    public void enableUUID() {
        this.mainThreadHandler.removeCallbacks(this.runnableEnable);
        this.mainThreadHandler.postDelayed(this.runnableEnable, 4000L);
        EnableNotifyRequest enableNotifyRequest = new EnableNotifyRequest(Constants.UUID_SERVICE, Constants.UUID_READ, new EnableNotifyRequest.ListenerCallback() { // from class: com.oudmon.ble.base.bluetooth.BleOperateManager.5
            @Override // com.oudmon.ble.base.request.EnableNotifyRequest.ListenerCallback
            public void enable(boolean z) {
                XLog.m137i("enableUUID:" + z);
                BleOperateManager.this.mainThreadHandler.removeCallbacks(BleOperateManager.this.runnableEnable);
            }
        });
        enableNotifyRequest.setEnable(true);
        getInstance().execute(enableNotifyRequest);
    }

    public void init() {
        IntentFilter intentFilter = BleAction.getIntentFilter();
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(new QCBluetoothCallbackReceiver(), intentFilter);
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(new QCBluetoothCallbackCloneReceiver(), intentFilter);
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(new QCBluetoothCallbackBigDataCloneReceiver(), intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runCommonCmd() {
        CommandHandle.getInstance().execReadCmd(CommandHandle.getInstance().getReadHwRequest());
        CommandHandle.getInstance().execReadCmd(CommandHandle.getInstance().getReadFmRequest());
    }

    public void classicBluetoothStartScan() {
        BleBaseControl.getInstance().classicBluetoothScan();
    }

    public void classicBluetoothStopScan() {
        BleBaseControl.getInstance().cancelScanBluetooth();
    }

    public void createBondBlueTooth(BluetoothDevice bluetoothDevice) {
        BleBaseControl.getInstance().createBondBlueTooth(bluetoothDevice);
    }

    public void createBondBluetoothJieLi(BluetoothDevice bluetoothDevice) {
        BleBaseControl.getInstance().createBond(bluetoothDevice, 1);
    }

    public void setRtkBindTag(Boolean bool) {
        BleBaseControl.getInstance().setRtkBindTag(bool);
    }

    public BluetoothDevice getMacSystemBond(String str) {
        Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (bondedDevices.size() <= 0) {
            return null;
        }
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            if (bluetoothDevice != null && bluetoothDevice.getName() != null && bluetoothDevice.getAddress() != null && !TextUtils.isEmpty(str) && str.equalsIgnoreCase(bluetoothDevice.getAddress())) {
                return bluetoothDevice;
            }
        }
        return null;
    }

    public void bleCreateBond() {
        BleBaseControl.getInstance().bleCreateBond();
    }

    public void createBondBluetoothJieLiFailRepeat(BluetoothDevice bluetoothDevice) {
        try {
            bluetoothDevice.createBond();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
