package com.oudmon.ble.base.bluetooth.queue;

/* loaded from: classes2.dex */
public class BleDataBean {
    private byte[] data;
    private int sleepTime;
    private int subLength;

    public BleDataBean(byte[] bArr, int i) {
        this.data = bArr;
        this.subLength = i;
    }

    public BleDataBean(byte[] bArr, int i, int i2) {
        this.data = bArr;
        this.subLength = i;
        this.sleepTime = i2;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public int getSubLength() {
        return this.subLength;
    }

    public void setSubLength(int i) {
        this.subLength = i;
    }
}
