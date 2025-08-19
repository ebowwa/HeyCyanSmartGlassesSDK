package com.oudmon.ble.base.communication;

import com.elvishew.xlog.XLog;
import com.google.firebase.crashlytics.buildtools.ndk.internal.elf.EMachine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.cli.HelpFormatter;
import com.oudmon.ble.base.bluetooth.BleOperateManager;
import com.oudmon.ble.base.bluetooth.queue.BleDataBean;
import com.oudmon.ble.base.bluetooth.queue.BleThreadManager;
import com.oudmon.ble.base.communication.bigData.bean.GlassModelControl;
import com.oudmon.ble.base.communication.bigData.bean.SyncTime;
import com.oudmon.ble.base.communication.bigData.bean.WifiInfoReq;
import com.oudmon.ble.base.communication.bigData.resp.AiChatResponse;
import com.oudmon.ble.base.communication.bigData.resp.BatteryResponse;
import com.oudmon.ble.base.communication.bigData.resp.ClassBluetoothResponse;
import com.oudmon.ble.base.communication.bigData.resp.DeviceInfoResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassModelControlResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassesAiVoicePlayStatusRsp;
import com.oudmon.ble.base.communication.bigData.resp.GlassesAiVoiceRsp;
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyListener;
import com.oudmon.ble.base.communication.bigData.resp.GlassesTouchSupportRsp;
import com.oudmon.ble.base.communication.bigData.resp.GlassesWearRsp;
import com.oudmon.ble.base.communication.bigData.resp.PictureThumbnailsResponse;
import com.oudmon.ble.base.communication.bigData.resp.SyncTimeResponse;
import com.oudmon.ble.base.communication.bigData.resp.VolumeControlResponse;
import com.oudmon.ble.base.communication.utils.ByteUtil;
import com.oudmon.ble.base.communication.utils.CRC16;
import com.oudmon.ble.base.request.EnableNotifyRequest;
import com.oudmon.ble.base.request.WriteRequest;
import com.oudmon.qc_utils.bytes.DataTransferUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public class LargeDataHandler {
    public static final byte ACTION_BT_CONNECT = 73;
    public static final byte ACTION_BT_MAC_Protocol = 46;
    public static final byte ACTION_DEVICE_AI_VOICE = 68;
    public static final byte ACTION_DEVICE_DATA_REPORTING = 115;
    public static final byte ACTION_DEVICE_HEART_BEAT = 69;
    public static final byte ACTION_DEVICE_INFO = 67;
    public static final byte ACTION_DEVICE_WEAR = 70;
    public static final byte ACTION_DEVICE_WEAR_SUPPORT = 71;
    public static final byte ACTION_GLASSES_BATTERY = 66;
    public static final byte ACTION_GLASSES_CONTROL = 65;
    public static final byte ACTION_GPT_UPLOAD = 89;
    public static final byte ACTION_OTA_SOC = -4;
    public static final byte ACTION_PICTURE_THUMBNAILS = -3;
    public static final byte ACTION_SPEAK_SOUND_SWITCH = 82;
    public static final byte ACTION_SYNC_TIME = 64;
    public static final byte ACTION_VOICE_STATUS = 72;
    public static final byte ACTION_VOLUME_CONTROL = 81;
    private static LargeDataHandler mInstance;
    private static final UUID SERIAL_PORT_SERVICE = UUID.fromString("de5bf728-d711-4e47-af26-65e3012a5dc7");
    private static final UUID SERIAL_PORT_CHARACTER_NOTIFY = UUID.fromString("de5bf729-d711-4e47-af26-65e3012a5dc7");
    private static final UUID SERIAL_PORT_CHARACTER_WRITE = UUID.fromString("de5bf72a-d711-4e47-af26-65e3012a5dc7");
    private GlassesDeviceNotifyListener deviceNotifyListener = new GlassesDeviceNotifyListener();
    private ConcurrentHashMap<String, ILargeDataResponse<BatteryResponse>> batterySparseArray = new ConcurrentHashMap<>();
    private EnableNotifyRequest enableNotifyRequest = new EnableNotifyRequest(SERIAL_PORT_SERVICE, SERIAL_PORT_CHARACTER_NOTIFY, new EnableNotifyRequest.ListenerCallback() { // from class: com.oudmon.ble.base.communication.LargeDataHandler.1
        @Override // com.oudmon.ble.base.request.EnableNotifyRequest.ListenerCallback
        public void enable(boolean z) {
            if (z) {
                return;
            }
            XLog.m137i("enable:" + z);
            LargeDataHandler.this.initEnable();
        }
    });
    private ConcurrentHashMap<Integer, ILargeDataResponse> respMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ILargeDataResponse> noClearMap = new ConcurrentHashMap<>();
    private int mPackageLength = JPackageManager.getInstance().getLength();

    private LargeDataHandler() {
        this.noClearMap.put(Integer.valueOf(EMachine.EM_XGATE), this.deviceNotifyListener);
    }

    public static LargeDataHandler getInstance() {
        if (mInstance == null) {
            synchronized (LargeDataHandler.class) {
                if (mInstance == null) {
                    mInstance = new LargeDataHandler();
                }
            }
        }
        return mInstance;
    }

    public void initEnable() {
        this.enableNotifyRequest.setEnable(true);
        BleOperateManager.getInstance().execute(this.enableNotifyRequest);
        this.noClearMap.put(Integer.valueOf(EMachine.EM_XGATE), this.deviceNotifyListener);
    }

    public void disEnable() {
        this.enableNotifyRequest.setEnable(false);
        BleOperateManager.getInstance().execute(this.enableNotifyRequest);
    }

    public void packageLength() {
        this.mPackageLength = JPackageManager.getInstance().getLength();
    }

    public void initPackageNotify(ILargeDataResponse<AiChatResponse> iLargeDataResponse) {
        this.respMap.put(89, iLargeDataResponse);
    }

    public void removeGptNotify() {
        this.respMap.remove(89);
    }

    public void addOutDeviceListener(int i, ILargeDataResponse iLargeDataResponse) {
        this.deviceNotifyListener.setOutRspIOdmOpResponse(i, iLargeDataResponse);
    }

    public void removeOutDeviceListener(int i) {
        this.deviceNotifyListener.removeCallback(i);
    }

    public void syncTime(ILargeDataResponse<SyncTimeResponse> iLargeDataResponse) {
        this.respMap.put(64, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(64, new SyncTime(1).getSubData()), this.mPackageLength));
    }

    public void addBatteryCallBack(String str, ILargeDataResponse<BatteryResponse> iLargeDataResponse) {
        this.batterySparseArray.put(str, iLargeDataResponse);
    }

    public void removeBatteryCallBack(String str) {
        this.batterySparseArray.remove(str);
    }

    public void syncBattery() {
        this.respMap.put(66, new ILargeDataResponse<BatteryResponse>() { // from class: com.oudmon.ble.base.communication.LargeDataHandler.2
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public void parseData(int i, BatteryResponse batteryResponse) {
                Iterator it = LargeDataHandler.this.batterySparseArray.values().iterator();
                while (it.hasNext()) {
                    ((ILargeDataResponse) it.next()).parseData(i, batteryResponse);
                }
            }
        });
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(66, new byte[2]), this.mPackageLength, 200));
    }

    public void openBT() {
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(73, new byte[]{2, 1}), this.mPackageLength));
    }

    public void setVolumeControl(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(81, new byte[]{2, 1, (byte) i, (byte) i2, (byte) i3, 2, (byte) i4, (byte) i5, (byte) i6, 3, (byte) i7, (byte) i8, (byte) i9, (byte) i10}), this.mPackageLength));
    }

    public void getVolumeControl(ILargeDataResponse<VolumeControlResponse> iLargeDataResponse) {
        this.respMap.put(81, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(81, new byte[]{1}), this.mPackageLength));
    }

    public void writeIpToSoc(String str, ILargeDataResponse<BatteryResponse> iLargeDataResponse) {
        this.respMap.put(-4, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(-4, new WifiInfoReq(str).getSubData()), this.mPackageLength));
    }

    public void syncDeviceInfo(ILargeDataResponse<DeviceInfoResponse> iLargeDataResponse) {
        this.respMap.put(67, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(67, new byte[2]), this.mPackageLength, 200));
    }

    public void aiVoiceWake(boolean z, boolean z2, ILargeDataResponse<GlassesAiVoiceRsp> iLargeDataResponse) {
        this.respMap.put(68, iLargeDataResponse);
        if (z) {
            BleThreadManager.getInstance().addData(new BleDataBean(addHeader(68, new byte[]{2, z2 ? (byte) 1 : (byte) 0}), this.mPackageLength));
        } else {
            BleThreadManager.getInstance().addData(new BleDataBean(addHeader(68, new byte[]{1, 0}), this.mPackageLength));
        }
    }

    public void aiVoicePlay(int i, ILargeDataResponse<GlassesAiVoicePlayStatusRsp> iLargeDataResponse) {
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(72, new byte[]{2, (byte) i}), this.mPackageLength));
    }

    public void wearCheck(boolean z, boolean z2, ILargeDataResponse<GlassesWearRsp> iLargeDataResponse) {
        this.respMap.put(70, iLargeDataResponse);
        if (z) {
            BleThreadManager.getInstance().addData(new BleDataBean(addHeader(70, new byte[]{2, z2 ? (byte) 1 : (byte) 0}), this.mPackageLength));
        } else {
            BleThreadManager.getInstance().addData(new BleDataBean(addHeader(70, new byte[]{1, 0}), this.mPackageLength));
        }
    }

    public void wearFunctionSupport(ILargeDataResponse<GlassesTouchSupportRsp> iLargeDataResponse) {
        this.respMap.put(71, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(71, new byte[]{1, 0}), this.mPackageLength));
    }

    public void glassesControl(byte[] bArr, ILargeDataResponse<GlassModelControlResponse> iLargeDataResponse) {
        this.respMap.put(65, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(65, new GlassModelControl(bArr).getSubData()), this.mPackageLength));
    }

    public void speakSoundSwitch(boolean z) {
        byte[] bArr = new byte[2];
        bArr[0] = 2;
        if (z) {
            bArr[1] = 2;
        } else {
            bArr[1] = 1;
        }
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(82, bArr), this.mPackageLength));
    }

    public void removeGlassesControlCallback() {
        this.respMap.remove(65);
    }

    public void getPictureThumbnails(final ILargeDataImageResponse iLargeDataImageResponse) {
        this.respMap.put(-3, new ILargeDataResponse<PictureThumbnailsResponse>() { // from class: com.oudmon.ble.base.communication.LargeDataHandler.3
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public void parseData(int i, PictureThumbnailsResponse pictureThumbnailsResponse) {
                XLog.m137i(ByteUtil.byteArrayToString(pictureThumbnailsResponse.getSubData()));
                if ((i & 255) == 253) {
                    int iBytesToInt = ByteUtil.bytesToInt(Arrays.copyOfRange(pictureThumbnailsResponse.getSubData(), 7, 9));
                    int iBytesToInt2 = ByteUtil.bytesToInt(Arrays.copyOfRange(pictureThumbnailsResponse.getSubData(), 9, 11));
                    XLog.m137i(iBytesToInt2 + HelpFormatter.DEFAULT_LONG_OPT_PREFIX + iBytesToInt);
                    if (iBytesToInt <= 0) {
                        return;
                    }
                    int i2 = iBytesToInt2 + 1;
                    if (i2 != iBytesToInt) {
                        LargeDataHandler.this.syncPictureThumbnails(i2);
                        iLargeDataImageResponse.parseData(i, false, Arrays.copyOfRange(pictureThumbnailsResponse.getSubData(), 11, pictureThumbnailsResponse.getSubData().length));
                    } else {
                        iLargeDataImageResponse.parseData(i, true, Arrays.copyOfRange(pictureThumbnailsResponse.getSubData(), 11, pictureThumbnailsResponse.getSubData().length));
                    }
                }
            }
        });
        syncPictureThumbnails(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncPictureThumbnails(int i) {
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(-3, new byte[]{1, (byte) ByteUtil.loword(i), (byte) ByteUtil.hiword(i)}), this.mPackageLength));
    }

    public void syncHeartBeat(int i) {
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(69, new byte[]{(byte) i, 1}), this.mPackageLength));
    }

    public void syncClassicBluetooth(ILargeDataResponse<ClassBluetoothResponse> iLargeDataResponse) {
        this.respMap.put(46, iLargeDataResponse);
        BleThreadManager.getInstance().addData(new BleDataBean(addHeader(46, new byte[1]), this.mPackageLength));
    }

    public ConcurrentHashMap<Integer, ILargeDataResponse> getRespMap() {
        return this.respMap;
    }

    public ConcurrentHashMap<Integer, ILargeDataResponse> getNoClearMap() {
        if (this.noClearMap.isEmpty()) {
            this.noClearMap.put(Integer.valueOf(EMachine.EM_XGATE), this.deviceNotifyListener);
        }
        return this.noClearMap;
    }

    public void cleanMap() {
        this.respMap.clear();
    }

    private byte[] addHeader(int i, byte[] bArr) {
        byte[] bArr2 = new byte[(bArr == null ? 0 : bArr.length) + 6];
        bArr2[0] = -68;
        bArr2[1] = (byte) i;
        if (bArr != null && bArr.length > 0) {
            System.arraycopy(DataTransferUtils.shortToBytes((short) bArr.length), 0, bArr2, 2, 2);
            System.arraycopy(DataTransferUtils.shortToBytes((short) CRC16.calcCrc16(bArr)), 0, bArr2, 4, 2);
            System.arraycopy(bArr, 0, bArr2, 6, bArr.length);
        } else {
            bArr2[4] = -1;
            bArr2[5] = -1;
        }
        return bArr2;
    }

    private WriteRequest getWriteRequest(byte[] bArr) {
        XLog.m137i("getWriteRequest: data=" + DataTransferUtils.getHexString(bArr));
        WriteRequest noRspInstance = WriteRequest.getNoRspInstance(SERIAL_PORT_SERVICE, SERIAL_PORT_CHARACTER_WRITE);
        noRspInstance.setValue(bArr);
        return noRspInstance;
    }
}
