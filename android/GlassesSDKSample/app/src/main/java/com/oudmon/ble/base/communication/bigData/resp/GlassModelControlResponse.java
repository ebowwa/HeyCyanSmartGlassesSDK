package com.oudmon.ble.base.communication.bigData.resp;

import com.elvishew.xlog.XLog;
import com.oudmon.ble.base.communication.utils.ByteUtil;
import java.util.Arrays;

/* loaded from: classes2.dex */
public class GlassModelControlResponse extends BaseResponse {
    private int dataType;
    private int glassWorkType;
    private int imageCount;
    private int otaStatus;
    private String p2pIp;
    private int recordAudioDuration;
    private int recordCount;
    private int videoAngle;
    private int videoCount;
    private int videoDuration;
    private int errorCode = 1;
    private int workTypeIng = 0;

    @Override // com.oudmon.ble.base.communication.bigData.resp.BaseResponse
    public boolean acceptData(byte[] bArr) {
        int i;
        try {
            this.dataType = bArr[7];
            XLog.m137i("dataType->" + this.dataType);
            int i2 = this.dataType;
            if (i2 == 4) {
                this.imageCount = ByteUtil.bytesToInt(Arrays.copyOfRange(bArr, 8, 10));
                this.videoCount = ByteUtil.bytesToInt(Arrays.copyOfRange(bArr, 10, 12));
                this.recordCount = ByteUtil.bytesToInt(Arrays.copyOfRange(bArr, 12, 14));
            } else if (i2 == 1) {
                byte b = bArr[8];
                this.glassWorkType = b;
                if (b == 1 || b == 2 || b == 4 || b == 6 || b == 8 || b == 7 || b == 11 || b == 12 || b == 18) {
                    if (bArr.length > 9) {
                        byte b2 = bArr[9];
                        this.errorCode = b2;
                        if (b2 != 0) {
                            XLog.m137i("暂时不处理");
                        } else {
                            this.workTypeIng = bArr[10];
                            XLog.m137i("workTypeIng->" + this.workTypeIng);
                        }
                    }
                } else if (b == 5 && (i = this.errorCode) != 0) {
                    this.otaStatus = i;
                }
            } else if (i2 == 3) {
                this.p2pIp = ByteUtil.byteToInt(bArr[10]) + "." + ByteUtil.byteToInt(bArr[11]) + "." + ByteUtil.byteToInt(bArr[12]) + "." + ByteUtil.byteToInt(bArr[12]);
            } else if (i2 == 2) {
                this.videoAngle = bArr[8];
                this.videoDuration = ByteUtil.bytesToInt(Arrays.copyOfRange(bArr, 9, 11));
            } else if (i2 == 6) {
                this.recordAudioDuration = ByteUtil.bytesToInt(Arrays.copyOfRange(bArr, 9, 11));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getDataType() {
        return this.dataType;
    }

    public int getGlassWorkType() {
        return this.glassWorkType;
    }

    public int getImageCount() {
        return this.imageCount;
    }

    public int getVideoCount() {
        return this.videoCount;
    }

    public int getRecordCount() {
        return this.recordCount;
    }

    public String getP2pIp() {
        return this.p2pIp;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getWorkTypeIng() {
        return this.workTypeIng;
    }

    public int getVideoAngle() {
        return this.videoAngle;
    }

    public int getVideoDuration() {
        return this.videoDuration;
    }

    public int getOtaStatus() {
        return this.otaStatus;
    }

    public int getRecordAudioDuration() {
        return this.recordAudioDuration;
    }
}
