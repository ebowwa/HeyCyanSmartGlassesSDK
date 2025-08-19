package com.oudmon.qc_utils.bytes;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.base.Ascii;

/* loaded from: classes2.dex */
public class DataTransferUtils {
    public static int enableWeek(int i) {
        return i & 128;
    }

    public static String getHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (bArr != null) {
            for (byte b : bArr) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
        }
        return sb.toString();
    }

    public static byte[] intToBytes(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)};
    }

    public static int bytesToInt(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16);
    }

    public static int byte2Int(byte[] bArr, int i) {
        return ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
    }

    public static short bytesToShort(byte[] bArr, int i) {
        return (short) (((bArr[i + 1] & 255) << 8) | (bArr[i] & 255));
    }

    public static byte[] shortToBytes(short s) {
        return new byte[]{(byte) (s & 255), (byte) ((s >> 8) & 255)};
    }

    public static int arrays2Int(byte[] bArr) {
        if (bArr.length == 1) {
            return bArr[0] & 255;
        }
        if (bArr.length == 2) {
            return bytesToInt(bArr);
        }
        if (bArr.length == 4) {
            return bytesToInt(bArr, 0);
        }
        return -1;
    }

    public static int bytesToInt(byte[] bArr) {
        int i;
        int i2;
        if (bArr.length == 1) {
            return bArr[0] & 255;
        }
        if (bArr.length == 4) {
            i = (bArr[0] & 255) | ((bArr[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bArr[2] << 16) & 16711680);
            i2 = (bArr[3] << Ascii.CAN) & ViewCompat.MEASURED_STATE_MASK;
        } else if (bArr.length == 2) {
            i = bArr[0] & 255;
            i2 = (bArr[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK;
        } else {
            if (bArr.length != 3) {
                return 0;
            }
            i = (bArr[0] & 255) | ((bArr[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK);
            i2 = (bArr[2] << 16) & 16711680;
        }
        return i | i2;
    }

    public static float bytes2Float(byte[] bArr, int i) {
        return Float.intBitsToFloat(bytesToInt(bArr, i));
    }
}
