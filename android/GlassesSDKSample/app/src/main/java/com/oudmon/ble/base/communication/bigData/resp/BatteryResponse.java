package com.oudmon.ble.base.communication.bigData.resp;

/* loaded from: classes2.dex */
public class BatteryResponse extends BaseResponse {
    private int battery;
    private boolean charging;
    private byte[] subData;

    @Override // com.oudmon.ble.base.communication.bigData.resp.BaseResponse
    public boolean acceptData(byte[] bArr) {
        try {
            this.subData = bArr;
            this.battery = bArr[6];
            boolean z = true;
            if (bArr[7] != 1) {
                z = false;
            }
            this.charging = z;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public byte[] getSubData() {
        return this.subData;
    }

    public int getBattery() {
        return this.battery;
    }

    public boolean isCharging() {
        return this.charging;
    }
}
