package com.oudmon.ble.base.communication.bigData.resp;

/* loaded from: classes2.dex */
public abstract class BaseResponse {
    protected int cmdType;

    public abstract boolean acceptData(byte[] bArr);

    public int getCmdType() {
        return this.cmdType;
    }

    public void setCmdType(int i) {
        this.cmdType = i;
    }
}
