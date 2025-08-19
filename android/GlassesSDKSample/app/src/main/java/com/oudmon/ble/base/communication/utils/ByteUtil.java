package com.oudmon.ble.base.communication.utils;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.base.Ascii;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes2.dex */
public class ByteUtil {
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static int byteToInt(byte b) {
        return b & 255;
    }

    public static int hiword(int i) {
        return i >>> 8;
    }

    public static byte int2byte(int i) {
        return (byte) (i & 255);
    }

    public static int loword(int i) {
        return i & 65535;
    }

    public static byte[] intToByte(int i, int i2) {
        if (i2 == 1) {
            byte[] bArr = new byte[i2];
            bArr[0] = (byte) (i & 255);
            return bArr;
        }
        byte[] bArr2 = new byte[i2];
        bArr2[0] = (byte) (i & 255);
        bArr2[1] = (byte) ((65280 & i) >> 8);
        bArr2[2] = (byte) ((16711680 & i) >> 16);
        bArr2[3] = (byte) ((i & ViewCompat.MEASURED_STATE_MASK) >> 24);
        return bArr2;
    }

    public static float[] bytesToFloats(byte[] bArr, int i, boolean z) {
        if (bArr == null) {
            return null;
        }
        float[] fArr = new float[i / 4];
        if (z) {
            ByteBuffer.wrap(bArr).order(ByteOrder.BIG_ENDIAN).asFloatBuffer().get(fArr);
        } else {
            ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(fArr);
        }
        return fArr;
    }

    public static short[] bytesToShorts(byte[] bArr, int i, boolean z) {
        if (bArr == null) {
            return null;
        }
        short[] sArr = new short[i / 2];
        if (z) {
            ByteBuffer.wrap(bArr).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(sArr);
        } else {
            ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sArr);
        }
        return sArr;
    }

    public static String byteToBit(byte b) {
        return "" + ((int) ((byte) ((b >> 7) & 1))) + ((int) ((byte) ((b >> 6) & 1))) + ((int) ((byte) ((b >> 5) & 1))) + ((int) ((byte) ((b >> 4) & 1))) + ((int) ((byte) ((b >> 3) & 1))) + ((int) ((byte) ((b >> 2) & 1))) + ((int) ((byte) ((b >> 1) & 1))) + ((int) ((byte) (b & 1)));
    }

    public static int bytes2IntIncludeSignBit(byte[] bArr) {
        int i;
        byte b;
        if (bArr.length == 1) {
            return bArr[0];
        }
        if (bArr.length == 4) {
            i = (bArr[3] << Ascii.CAN) | ((bArr[2] << Ascii.CAN) >>> 8) | ((bArr[1] << Ascii.CAN) >>> 16);
            b = bArr[0];
        } else if (bArr.length == 2) {
            i = bArr[1] << 8;
            b = bArr[0];
        } else {
            if (bArr.length != 3) {
                return 0;
            }
            i = (bArr[2] << 16) | ((bArr[1] << Ascii.CAN) >>> 16);
            b = bArr[0];
        }
        return i | ((b << Ascii.CAN) >>> 24);
    }

    public static byte[] getBooleanArray(byte b) {
        byte[] bArr = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bArr[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return bArr;
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

    public static byte[] concat(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            return bArr2;
        }
        if (bArr2 == null) {
            return bArr;
        }
        byte[] bArr3 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return bArr3;
    }

    public static int bytesToIntForVersion(byte[] bArr) {
        int i;
        int i2;
        if (bArr.length == 1) {
            return bArr[0] & 255;
        }
        if (bArr.length == 4) {
            i = (bArr[3] & 255) | ((bArr[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bArr[1] << 16) & 16711680);
            i2 = (bArr[0] << Ascii.CAN) & ViewCompat.MEASURED_STATE_MASK;
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

    public static byte[] intToByteBig(int i, int i2) {
        if (i2 == 1) {
            byte[] bArr = new byte[i2];
            bArr[0] = (byte) (i & 255);
            return bArr;
        }
        byte[] bArr2 = new byte[i2];
        bArr2[0] = (byte) ((i >>> 24) & 255);
        bArr2[1] = (byte) ((i >>> 16) & 255);
        bArr2[2] = (byte) ((i >>> 8) & 255);
        bArr2[3] = (byte) (i & 255);
        return bArr2;
    }

    public static int bytesToIntBig(byte[] bArr) {
        if (bArr.length == 1) {
            return bArr[0] & 255;
        }
        return (bArr[3] & 255) | ((((((bArr[0] & 255) << 8) | (bArr[1] & 255)) << 8) | (bArr[2] & 255)) << 8);
    }

    public static String bytesToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b : bArr) {
            sb.append(String.format("%02X", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static String bytesToStringFormat(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b : bArr) {
            sb.append(String.format("%01X", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static String binaryString2hexString(String str) {
        if (str == null || str.equals("") || str.length() % 8 != 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i += 4) {
            int i2 = 0;
            for (int i3 = 0; i3 < 4; i3++) {
                int i4 = i + i3;
                i2 += Integer.parseInt(str.substring(i4, i4 + 1)) << (3 - i3);
            }
            stringBuffer.append(Integer.toHexString(i2));
        }
        return stringBuffer.toString();
    }

    public static String byteArrayToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static String byteArraySubToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            if (sb.toString().length() >= 200) {
                break;
            }
        }
        return sb.toString();
    }

    public static byte[] byteToBitArray(int i) {
        return new byte[]{(byte) ((i >> 7) & 1), (byte) ((i >> 6) & 1), (byte) ((i >> 5) & 1), (byte) ((i >> 4) & 1), (byte) ((i >> 3) & 1), (byte) ((i >> 2) & 1), (byte) ((i >> 1) & 1), (byte) (i & 1)};
    }

    public static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[bArr.length * 2];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            int i2 = i * 2;
            char[] cArr2 = hexArray;
            cArr[i2] = cArr2[(b & 255) >>> 4];
            cArr[i2 + 1] = cArr2[b & 15];
        }
        return new String(cArr);
    }

    public static String bytesToString1(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b : bArr) {
            sb.append(String.format("%02X ", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String lowerCase = str.toLowerCase();
        int length = lowerCase.length() / 2;
        char[] charArray = lowerCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            int iIndexOf = "0123456789abcdef".indexOf(charArray[i2]) << 4;
            int iIndexOf2 = "0123456789abcdef".indexOf(charArray[i2 + 1]);
            if (iIndexOf == -1 || iIndexOf2 == -1) {
                return null;
            }
            bArr[i] = (byte) (iIndexOf2 | iIndexOf);
        }
        return bArr;
    }

    public static String byteAsciiToChar(int... iArr) {
        String str = "";
        for (int i : iArr) {
            str = str + ((char) i);
        }
        return str;
    }

    public static String unicodeByteToStr(byte[] bArr) {
        Character chValueOf;
        StringBuffer stringBuffer = new StringBuffer();
        Character.valueOf((char) 0);
        for (int i = 0; i < bArr.length; i += 2) {
            byte b = bArr[i + 1];
            if (b == 0) {
                chValueOf = Character.valueOf((char) bArr[i]);
            } else {
                chValueOf = Character.valueOf((char) (b | ((bArr[i] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)));
            }
            if (chValueOf.charValue() == 0) {
                break;
            }
            stringBuffer.append(chValueOf);
        }
        return stringBuffer.toString();
    }
}
