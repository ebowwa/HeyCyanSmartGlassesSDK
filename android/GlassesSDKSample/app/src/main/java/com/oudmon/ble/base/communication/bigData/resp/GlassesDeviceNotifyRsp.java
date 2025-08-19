package com.oudmon.ble.base.communication.bigData.resp;

/* loaded from: classes2.dex */
public class GlassesDeviceNotifyRsp extends BaseResponse {
    private byte[] loadData;

    @Override // com.oudmon.ble.base.communication.bigData.resp.BaseResponse
    public boolean acceptData(byte[] bArr) {
        this.loadData = bArr;
        return false;
    }

    public byte[] getLoadData() {
        return this.loadData;
    }
}
