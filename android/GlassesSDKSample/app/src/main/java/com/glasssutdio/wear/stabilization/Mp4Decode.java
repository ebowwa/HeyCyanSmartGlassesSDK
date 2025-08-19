package com.glasssutdio.wear.stabilization;

import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.GlassApplication;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.stabilization.AwEisCommonJni;
import com.glasssutdio.wear.stabilization.AwEisGyroJni;
import com.glasssutdio.wear.stabilization.AwEisImgJni;
import com.google.android.gms.location.DeviceOrientationRequest;
import com.google.firebase.crashlytics.buildtools.ndk.internal.elf.EMachine;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: Mp4Decode.kt */
@Metadata(m606d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001,B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0006\u0010\u001f\u001a\u00020 J \u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020#2\u0006\u0010%\u001a\u00020&H\u0007J\u0010\u0010'\u001a\u00020\u00062\u0006\u0010(\u001a\u00020\u000eH\u0002J\u0010\u0010)\u001a\u00020\u00062\u0006\u0010*\u001a\u00020\u0006H\u0002J\u0010\u0010+\u001a\u00020\u00062\u0006\u0010(\u001a\u00020\u000eH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.¢\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\b\"\u0004\b\u0011\u0010\nR\u001a\u0010\u0012\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\b\"\u0004\b\u0014\u0010\nR\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.¢\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\b\"\u0004\b\u0019\u0010\nR\u000e\u0010\u001a\u001a\u00020\u001bX\u0082.¢\u0006\u0002\n\u0000¨\u0006-"}, m607d2 = {"Lcom/glasssutdio/wear/stabilization/Mp4Decode;", "", "()V", "eisContext", "", "height", "", "getHeight", "()I", "setHeight", "(I)V", "mediaCodec", "Landroid/media/MediaCodec;", "mediaExtractor", "Landroid/media/MediaExtractor;", "outHeight", "getOutHeight", "setOutHeight", "outWidth", "getOutWidth", "setOutWidth", "outputArray", "", "width", "getWidth", "setWidth", "yuvToMp4Encoder", "Lcom/glasssutdio/wear/stabilization/YuvToMp4Encoder;", "convertImageToYuv420ByteArray", "image", "Landroid/media/Image;", "eisInit", "", "eisYuv2Mp4", "inputFile", "Ljava/io/File;", "outputMp4", "callback", "Lcom/glasssutdio/wear/stabilization/Mp4Decode$Mp4DecodeCallback;", "findAudioTrack", "extractor", "mapColorFormat", "colorFormat", "selectVideoTrack", "Mp4DecodeCallback", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class Mp4Decode {
    private long eisContext;
    private MediaCodec mediaCodec;
    private MediaExtractor mediaExtractor;
    private byte[] outputArray;
    private YuvToMp4Encoder yuvToMp4Encoder;
    private int width = 1920;
    private int height = 1080;
    private int outWidth = 1536;
    private int outHeight = 864;

    /* compiled from: Mp4Decode.kt */
    @Metadata(m606d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005H&J(\u0010\b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u0005H&J\u0018\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u0005H&¨\u0006\r"}, m607d2 = {"Lcom/glasssutdio/wear/stabilization/Mp4Decode$Mp4DecodeCallback;", "", "eisEnd", "", "fileName", "", "sourceFilePath", "eisFilePath", "eisError", "sourcePath", "errorInfo", "eisStart", "filePath", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public interface Mp4DecodeCallback {
        void eisEnd(String fileName, String sourceFilePath, String eisFilePath);

        void eisError(String fileName, String sourcePath, String eisFilePath, String errorInfo);

        void eisStart(String fileName, String filePath);
    }

    private final int mapColorFormat(int colorFormat) {
        if (colorFormat != 19) {
            return colorFormat != 21 ? -1 : 2;
        }
        return 0;
    }

    public final int getWidth() {
        return this.width;
    }

    public final void setWidth(int i) {
        this.width = i;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void setHeight(int i) {
        this.height = i;
    }

    public final int getOutWidth() {
        return this.outWidth;
    }

    public final void setOutWidth(int i) {
        this.outWidth = i;
    }

    public final int getOutHeight() {
        return this.outHeight;
    }

    public final void setOutHeight(int i) {
        this.outHeight = i;
    }

    public final void eisInit() throws IOException {
        AssetsUtils assetsUtils = new AssetsUtils(GlassApplication.INSTANCE.getCONTEXT());
        AwEisCommonJni.Companion companion = AwEisCommonJni.INSTANCE;
        String absolutePath = GFileUtilKt.getDCIMFile().getAbsolutePath();
        Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
        companion.nativeInit(absolutePath);
        if (UserConfig.INSTANCE.getInstance().getCamera8Mp()) {
            this.width = 1600;
            this.height = 1200;
            this.outWidth = 1280;
            this.outHeight = 960;
        }
        if (UserConfig.INSTANCE.getInstance().getUseGyro()) {
            File fileUseAssets = assetsUtils.useAssets("gyro/gyro.cfg");
            File fileUseAssets2 = assetsUtils.useAssets("gyro/EISGyroData_run2.txt");
            File fileUseAssets3 = assetsUtils.useAssets("gyro/EISVideoPts_run2.txt");
            AwEisGyroJni.Companion companion2 = AwEisGyroJni.INSTANCE;
            int i = this.width;
            int i2 = this.height;
            int i3 = this.outWidth;
            int i4 = this.outHeight;
            String absolutePath2 = fileUseAssets.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath2, "getAbsolutePath(...)");
            String absolutePath3 = fileUseAssets2.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath3, "getAbsolutePath(...)");
            String absolutePath4 = fileUseAssets3.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath4, "getAbsolutePath(...)");
            this.eisContext = companion2.nativeInit(i, i2, i3, i4, absolutePath2, absolutePath3, absolutePath4);
            return;
        }
        File fileUseAssets4 = assetsUtils.useAssets("image/eis_img_license_20250611.lic");
        AwEisImgJni.Companion companion3 = AwEisImgJni.INSTANCE;
        int i5 = this.width;
        int i6 = this.height;
        String absolutePath5 = fileUseAssets4.getAbsolutePath();
        Intrinsics.checkNotNullExpressionValue(absolutePath5, "getAbsolutePath(...)");
        this.eisContext = companion3.nativeInit(EMachine.EM_M32C, 0.1d, i5, i6, absolutePath5);
    }

    /* JADX WARN: Removed duplicated region for block: B:141:0x03a8 A[Catch: Exception -> 0x04e9, TryCatch #1 {Exception -> 0x04e9, blocks: (B:77:0x0294, B:83:0x02a1, B:88:0x02ad, B:92:0x02b5, B:94:0x02bb, B:96:0x02c5, B:100:0x02e6, B:152:0x0419, B:154:0x041d, B:156:0x0426, B:157:0x042f, B:159:0x0433, B:160:0x0437, B:162:0x043e, B:164:0x0442, B:165:0x0446, B:167:0x0450, B:168:0x0454, B:170:0x0463, B:171:0x0467, B:173:0x0471, B:174:0x0475, B:175:0x0479, B:177:0x047d, B:178:0x0481, B:180:0x0488, B:181:0x048c, B:183:0x0493, B:184:0x0497, B:186:0x04a6, B:189:0x04b7, B:187:0x04ae, B:126:0x037f, B:130:0x0387, B:135:0x0392, B:139:0x03a4, B:141:0x03a8, B:142:0x03ac, B:144:0x03ce, B:113:0x0336, B:118:0x034a, B:122:0x0352, B:143:0x03b1, B:145:0x03d1, B:146:0x03db, B:148:0x03e6, B:149:0x03ea), top: B:212:0x0294 }] */
    /* JADX WARN: Removed duplicated region for block: B:207:0x04f5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void eisYuv2Mp4(File inputFile, File outputMp4, Mp4DecodeCallback callback) throws MediaCodec.CryptoException, IOException {
        Mp4DecodeCallback mp4DecodeCallback;
        MediaFormat mediaFormat;
        int i;
        String str;
        int i2;
        boolean z;
        int i3;
        YuvToMp4Encoder yuvToMp4Encoder;
        int i4;
        int i5;
        boolean z2;
        MediaCodec mediaCodec;
        byte[] bArr;
        int iNativeRun;
        byte[] bArr2;
        MediaCodec mediaCodec2;
        MediaCodec mediaCodec3;
        Mp4DecodeCallback callback2 = callback;
        Intrinsics.checkNotNullParameter(inputFile, "inputFile");
        Intrinsics.checkNotNullParameter(outputMp4, "outputMp4");
        Intrinsics.checkNotNullParameter(callback2, "callback");
        if (this.eisContext != 0) {
            String name = inputFile.getName();
            Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
            String absolutePath = inputFile.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
            callback2.eisStart(name, absolutePath);
            this.outputArray = new byte[((((int) (this.width * 0.8d)) * ((int) (this.height * 0.8d))) * 3) / 2];
            if (!outputMp4.exists()) {
                outputMp4.createNewFile();
            }
            try {
                MediaExtractor mediaExtractor = new MediaExtractor();
                this.mediaExtractor = mediaExtractor;
                mediaExtractor.setDataSource(inputFile.getAbsolutePath());
                MediaExtractor mediaExtractor2 = this.mediaExtractor;
                String str2 = "mediaExtractor";
                if (mediaExtractor2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaExtractor");
                    mediaExtractor2 = null;
                }
                int iSelectVideoTrack = selectVideoTrack(mediaExtractor2);
                if (iSelectVideoTrack == -1) {
                    GFileUtilKt.deleteFile(outputMp4);
                    XLog.m137i("No video track found");
                    String name2 = inputFile.getName();
                    Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
                    String absolutePath2 = inputFile.getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath2, "getAbsolutePath(...)");
                    String absolutePath3 = outputMp4.getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath3, "getAbsolutePath(...)");
                    callback2.eisError(name2, absolutePath2, absolutePath3, "No video track found");
                    return;
                }
                MediaExtractor mediaExtractor3 = this.mediaExtractor;
                if (mediaExtractor3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaExtractor");
                    mediaExtractor3 = null;
                }
                MediaFormat trackFormat = mediaExtractor3.getTrackFormat(iSelectVideoTrack);
                Intrinsics.checkNotNullExpressionValue(trackFormat, "getTrackFormat(...)");
                String string = trackFormat.getString("mime");
                if (string == null) {
                    return;
                }
                MediaCodec mediaCodecCreateDecoderByType = MediaCodec.createDecoderByType(string);
                Intrinsics.checkNotNullExpressionValue(mediaCodecCreateDecoderByType, "createDecoderByType(...)");
                this.mediaCodec = mediaCodecCreateDecoderByType;
                if (mediaCodecCreateDecoderByType == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                    mediaCodecCreateDecoderByType = null;
                }
                MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodecCreateDecoderByType.getCodecInfo().getCapabilitiesForType(string);
                StringBuilder sb = new StringBuilder("Decoder support colorFormats: ");
                String string2 = Arrays.toString(capabilitiesForType.colorFormats);
                Intrinsics.checkNotNullExpressionValue(string2, "toString(...)");
                Log.i("YUV", sb.append(string2).toString());
                int[] colorFormats = capabilitiesForType.colorFormats;
                Intrinsics.checkNotNullExpressionValue(colorFormats, "colorFormats");
                int i6 = 19;
                if (!ArraysKt.contains(colorFormats, 19)) {
                    XLog.m137i("Decoder unsupported color format: 19");
                    i6 = 2135033992;
                }
                trackFormat.setInteger("color-format", i6);
                XLog.m137i("mediaFormat: " + trackFormat);
                MediaCodec mediaCodec4 = this.mediaCodec;
                if (mediaCodec4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                    mediaCodec4 = null;
                }
                mediaCodec4.configure(trackFormat, (Surface) null, (MediaCrypto) null, 0);
                mediaCodec4.start();
                MediaCodec mediaCodec5 = this.mediaCodec;
                if (mediaCodec5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                    mediaCodec5 = null;
                }
                MediaFormat outputFormat = mediaCodec5.getOutputFormat();
                Intrinsics.checkNotNullExpressionValue(outputFormat, "getOutputFormat(...)");
                XLog.m137i("Decoder output format: " + outputFormat);
                int integer = outputFormat.getInteger("color-format");
                XLog.m137i("Decoder final color format: " + integer);
                int integer2 = trackFormat.getInteger("width");
                int integer3 = trackFormat.getInteger("height");
                int integer4 = trackFormat.getInteger("frame-rate");
                Log.d("YUV", "inWidth: " + integer2 + ", inHeight: " + integer3 + ", frameRate: " + integer4 + ", colorFormat: " + integer);
                int i7 = (int) (integer2 * 0.8d);
                int i8 = (int) (integer3 * 0.8d);
                this.outputArray = new byte[((i7 * i8) * 3) / 2];
                MediaExtractor mediaExtractor4 = this.mediaExtractor;
                if (mediaExtractor4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaExtractor");
                    mediaExtractor4 = null;
                }
                int iFindAudioTrack = findAudioTrack(mediaExtractor4);
                if (iFindAudioTrack != -1) {
                    MediaExtractor mediaExtractor5 = this.mediaExtractor;
                    if (mediaExtractor5 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mediaExtractor");
                        mediaExtractor5 = null;
                    }
                    MediaFormat trackFormat2 = mediaExtractor5.getTrackFormat(iFindAudioTrack);
                    XLog.m137i("Audio track found: " + trackFormat2);
                    mediaFormat = trackFormat2;
                } else {
                    mediaFormat = null;
                }
                String absolutePath4 = outputMp4.getAbsolutePath();
                Intrinsics.checkNotNullExpressionValue(absolutePath4, "getAbsolutePath(...)");
                YuvToMp4Encoder yuvToMp4Encoder2 = new YuvToMp4Encoder(integer, mediaFormat, i7, i8, integer4, absolutePath4);
                this.yuvToMp4Encoder = yuvToMp4Encoder2;
                yuvToMp4Encoder2.init();
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                boolean z3 = false;
                while (true) {
                    MediaCodec mediaCodec6 = this.mediaCodec;
                    if (mediaCodec6 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                        i = iFindAudioTrack;
                        mediaCodec6 = null;
                    } else {
                        i = iFindAudioTrack;
                    }
                    int iDequeueInputBuffer = mediaCodec6.dequeueInputBuffer(DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
                    if (iDequeueInputBuffer >= 0) {
                        MediaCodec mediaCodec7 = this.mediaCodec;
                        if (mediaCodec7 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                            mediaCodec7 = null;
                        }
                        ByteBuffer inputBuffer = mediaCodec7.getInputBuffer(iDequeueInputBuffer);
                        MediaExtractor mediaExtractor6 = this.mediaExtractor;
                        if (mediaExtractor6 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException(str2);
                            mediaExtractor6 = null;
                        }
                        Intrinsics.checkNotNull(inputBuffer);
                        int sampleData = mediaExtractor6.readSampleData(inputBuffer, 0);
                        if (sampleData < 0) {
                            MediaCodec mediaCodec8 = this.mediaCodec;
                            if (mediaCodec8 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                                mediaCodec3 = null;
                            } else {
                                mediaCodec3 = mediaCodec8;
                            }
                            mediaCodec3.queueInputBuffer(iDequeueInputBuffer, 0, 0, 0L, 4);
                            z3 = true;
                        } else {
                            MediaCodec mediaCodec9 = this.mediaCodec;
                            if (mediaCodec9 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                                mediaCodec2 = null;
                            } else {
                                mediaCodec2 = mediaCodec9;
                            }
                            MediaExtractor mediaExtractor7 = this.mediaExtractor;
                            if (mediaExtractor7 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException(str2);
                                mediaExtractor7 = null;
                            }
                            mediaCodec2.queueInputBuffer(iDequeueInputBuffer, 0, sampleData, mediaExtractor7.getSampleTime(), 0);
                            MediaExtractor mediaExtractor8 = this.mediaExtractor;
                            if (mediaExtractor8 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException(str2);
                                mediaExtractor8 = null;
                            }
                            mediaExtractor8.advance();
                        }
                    }
                    if (z3) {
                        str = str2;
                        i2 = i;
                        z = false;
                        break;
                    }
                    try {
                        MediaCodec mediaCodec10 = this.mediaCodec;
                        if (mediaCodec10 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                            i4 = i;
                            mediaCodec10 = null;
                        } else {
                            i4 = i;
                        }
                        int iDequeueOutputBuffer = mediaCodec10.dequeueOutputBuffer(bufferInfo, DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
                        if (iDequeueOutputBuffer == -2) {
                            i5 = integer;
                            z2 = z3;
                            str = str2;
                            i2 = i4;
                            MediaCodec mediaCodec11 = this.mediaCodec;
                            if (mediaCodec11 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                                mediaCodec11 = null;
                            }
                            MediaFormat outputFormat2 = mediaCodec11.getOutputFormat();
                            Intrinsics.checkNotNullExpressionValue(outputFormat2, "getOutputFormat(...)");
                            XLog.m137i("Output format changed: " + outputFormat2);
                            Unit unit = Unit.INSTANCE;
                        } else if (iDequeueOutputBuffer != -1) {
                            if (iDequeueOutputBuffer >= 0) {
                                MediaCodec mediaCodec12 = this.mediaCodec;
                                if (mediaCodec12 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                                    mediaCodec12 = null;
                                }
                                Image outputImage = mediaCodec12.getOutputImage(iDequeueOutputBuffer);
                                if (outputImage != null) {
                                    z2 = z3;
                                    if (outputImage.getFormat() == 35) {
                                        byte[] bArrConvertImageToYuv420ByteArray = convertImageToYuv420ByteArray(outputImage);
                                        StringBuilder sbAppend = new StringBuilder().append("colorFormat: ").append(integer).append(", yuvToMp4Encoder.colorFormat: ");
                                        YuvToMp4Encoder yuvToMp4Encoder3 = this.yuvToMp4Encoder;
                                        if (yuvToMp4Encoder3 == null) {
                                            Intrinsics.throwUninitializedPropertyAccessException("yuvToMp4Encoder");
                                            yuvToMp4Encoder3 = null;
                                        }
                                        Log.d("YUV", sbAppend.append(yuvToMp4Encoder3.getColorFormat()).toString());
                                        if (UserConfig.INSTANCE.getInstance().getUseGyro()) {
                                            AwEisGyroJni.Companion companion = AwEisGyroJni.INSTANCE;
                                            str = str2;
                                            long j = this.eisContext;
                                            int iMapColorFormat = mapColorFormat(integer);
                                            i2 = i4;
                                            byte[] bArr3 = this.outputArray;
                                            if (bArr3 == null) {
                                                Intrinsics.throwUninitializedPropertyAccessException("outputArray");
                                                bArr2 = null;
                                            } else {
                                                bArr2 = bArr3;
                                            }
                                            YuvToMp4Encoder yuvToMp4Encoder4 = this.yuvToMp4Encoder;
                                            if (yuvToMp4Encoder4 == null) {
                                                Intrinsics.throwUninitializedPropertyAccessException("yuvToMp4Encoder");
                                                yuvToMp4Encoder4 = null;
                                            }
                                            iNativeRun = companion.nativeRun(j, bArrConvertImageToYuv420ByteArray, iMapColorFormat, bArr2, mapColorFormat(yuvToMp4Encoder4.getColorFormat()));
                                        } else {
                                            str = str2;
                                            i2 = i4;
                                            AwEisImgJni.Companion companion2 = AwEisImgJni.INSTANCE;
                                            long j2 = this.eisContext;
                                            int iMapColorFormat2 = mapColorFormat(integer);
                                            byte[] bArr4 = this.outputArray;
                                            if (bArr4 == null) {
                                                Intrinsics.throwUninitializedPropertyAccessException("outputArray");
                                                bArr = null;
                                            } else {
                                                bArr = bArr4;
                                            }
                                            YuvToMp4Encoder yuvToMp4Encoder5 = this.yuvToMp4Encoder;
                                            if (yuvToMp4Encoder5 == null) {
                                                Intrinsics.throwUninitializedPropertyAccessException("yuvToMp4Encoder");
                                                yuvToMp4Encoder5 = null;
                                            }
                                            iNativeRun = companion2.nativeRun(j2, bArrConvertImageToYuv420ByteArray, iMapColorFormat2, bArr, mapColorFormat(yuvToMp4Encoder5.getColorFormat()));
                                        }
                                        if (iNativeRun != 0) {
                                            String name3 = inputFile.getName();
                                            Intrinsics.checkNotNullExpressionValue(name3, "getName(...)");
                                            String absolutePath5 = inputFile.getAbsolutePath();
                                            Intrinsics.checkNotNullExpressionValue(absolutePath5, "getAbsolutePath(...)");
                                            String absolutePath6 = outputMp4.getAbsolutePath();
                                            Intrinsics.checkNotNullExpressionValue(absolutePath6, "getAbsolutePath(...)");
                                            callback2.eisError(name3, absolutePath5, absolutePath6, "No Support EIS");
                                            z = true;
                                            break;
                                        }
                                        YuvToMp4Encoder yuvToMp4Encoder6 = this.yuvToMp4Encoder;
                                        if (yuvToMp4Encoder6 == null) {
                                            Intrinsics.throwUninitializedPropertyAccessException("yuvToMp4Encoder");
                                            yuvToMp4Encoder6 = null;
                                        }
                                        byte[] bArr5 = this.outputArray;
                                        if (bArr5 == null) {
                                            Intrinsics.throwUninitializedPropertyAccessException("outputArray");
                                            i5 = integer;
                                            bArr5 = null;
                                        } else {
                                            i5 = integer;
                                        }
                                        yuvToMp4Encoder6.encodeFrame(bArr5, bufferInfo.presentationTimeUs);
                                        outputImage.close();
                                        mediaCodec = this.mediaCodec;
                                        if (mediaCodec == null) {
                                            Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                                            mediaCodec = null;
                                        }
                                        mediaCodec.releaseOutputBuffer(iDequeueOutputBuffer, false);
                                    } else {
                                        i5 = integer;
                                    }
                                } else {
                                    i5 = integer;
                                    z2 = z3;
                                }
                                str = str2;
                                i2 = i4;
                                mediaCodec = this.mediaCodec;
                                if (mediaCodec == null) {
                                }
                                mediaCodec.releaseOutputBuffer(iDequeueOutputBuffer, false);
                            } else {
                                i5 = integer;
                                z2 = z3;
                                str = str2;
                                i2 = i4;
                                Log.w("YUV", "no encode outputBufferId: " + iDequeueOutputBuffer);
                            }
                            Unit unit2 = Unit.INSTANCE;
                        } else {
                            i5 = integer;
                            z2 = z3;
                            str = str2;
                            i2 = i4;
                            Unit unit3 = Unit.INSTANCE;
                        }
                        callback2 = callback;
                        integer = i5;
                        z3 = z2;
                        str2 = str;
                        iFindAudioTrack = i2;
                    } catch (Exception e) {
                        e = e;
                        mp4DecodeCallback = callback;
                        if (e.getMessage() != null) {
                            String name4 = inputFile.getName();
                            Intrinsics.checkNotNullExpressionValue(name4, "getName(...)");
                            String absolutePath7 = inputFile.getAbsolutePath();
                            Intrinsics.checkNotNullExpressionValue(absolutePath7, "getAbsolutePath(...)");
                            String absolutePath8 = outputMp4.getAbsolutePath();
                            Intrinsics.checkNotNullExpressionValue(absolutePath8, "getAbsolutePath(...)");
                            mp4DecodeCallback.eisError(name4, absolutePath7, absolutePath8, String.valueOf(e.getMessage()));
                        }
                        XLog.m137i("YUVError decoding video" + e);
                        return;
                    }
                }
                MediaExtractor mediaExtractor9 = this.mediaExtractor;
                if (mediaExtractor9 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    i3 = i2;
                    mediaExtractor9 = null;
                } else {
                    i3 = i2;
                }
                mediaExtractor9.selectTrack(i3);
                ByteBuffer byteBufferAllocate = ByteBuffer.allocate(1048576);
                while (true) {
                    MediaExtractor mediaExtractor10 = this.mediaExtractor;
                    if (mediaExtractor10 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        mediaExtractor10 = null;
                    }
                    int sampleData2 = mediaExtractor10.readSampleData(byteBufferAllocate, 0);
                    if (sampleData2 < 0) {
                        break;
                    }
                    MediaExtractor mediaExtractor11 = this.mediaExtractor;
                    if (mediaExtractor11 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        mediaExtractor11 = null;
                    }
                    bufferInfo.presentationTimeUs = mediaExtractor11.getSampleTime();
                    MediaExtractor mediaExtractor12 = this.mediaExtractor;
                    if (mediaExtractor12 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        mediaExtractor12 = null;
                    }
                    bufferInfo.flags = mediaExtractor12.getSampleFlags();
                    bufferInfo.size = sampleData2;
                    bufferInfo.offset = 0;
                    YuvToMp4Encoder yuvToMp4Encoder7 = this.yuvToMp4Encoder;
                    if (yuvToMp4Encoder7 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("yuvToMp4Encoder");
                        yuvToMp4Encoder7 = null;
                    }
                    Intrinsics.checkNotNull(byteBufferAllocate);
                    yuvToMp4Encoder7.writeSampleData(byteBufferAllocate, bufferInfo);
                    MediaExtractor mediaExtractor13 = this.mediaExtractor;
                    if (mediaExtractor13 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        mediaExtractor13 = null;
                    }
                    mediaExtractor13.advance();
                }
                MediaCodec mediaCodec13 = this.mediaCodec;
                if (mediaCodec13 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                    mediaCodec13 = null;
                }
                mediaCodec13.stop();
                MediaCodec mediaCodec14 = this.mediaCodec;
                if (mediaCodec14 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mediaCodec");
                    mediaCodec14 = null;
                }
                mediaCodec14.release();
                MediaExtractor mediaExtractor14 = this.mediaExtractor;
                if (mediaExtractor14 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    mediaExtractor14 = null;
                }
                mediaExtractor14.release();
                if (UserConfig.INSTANCE.getInstance().getUseGyro()) {
                    AwEisGyroJni.INSTANCE.nativeRelease(this.eisContext);
                } else {
                    AwEisImgJni.INSTANCE.nativeRelease(this.eisContext);
                }
                if (z) {
                    mp4DecodeCallback = callback;
                } else {
                    String name5 = inputFile.getName();
                    Intrinsics.checkNotNullExpressionValue(name5, "getName(...)");
                    String absolutePath9 = inputFile.getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath9, "getAbsolutePath(...)");
                    String absolutePath10 = outputMp4.getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath10, "getAbsolutePath(...)");
                    mp4DecodeCallback = callback;
                    try {
                        mp4DecodeCallback.eisEnd(name5, absolutePath9, absolutePath10);
                    } catch (Exception e2) {
                        e = e2;
                        if (e.getMessage() != null) {
                        }
                        XLog.m137i("YUVError decoding video" + e);
                        return;
                    }
                }
                YuvToMp4Encoder yuvToMp4Encoder8 = this.yuvToMp4Encoder;
                if (yuvToMp4Encoder8 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("yuvToMp4Encoder");
                    yuvToMp4Encoder = null;
                } else {
                    yuvToMp4Encoder = yuvToMp4Encoder8;
                }
                yuvToMp4Encoder.release();
                XLog.m137i("yuv eis release");
            } catch (Exception e3) {
                e = e3;
                mp4DecodeCallback = callback2;
            }
        } else {
            GFileUtilKt.deleteFile(outputMp4);
        }
    }

    private final int findAudioTrack(MediaExtractor extractor) {
        int trackCount = extractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat trackFormat = extractor.getTrackFormat(i);
            Intrinsics.checkNotNullExpressionValue(trackFormat, "getTrackFormat(...)");
            String string = trackFormat.getString("mime");
            if (string != null && StringsKt.startsWith$default(string, "audio/", false, 2, (Object) null)) {
                return i;
            }
        }
        return -1;
    }

    private final int selectVideoTrack(MediaExtractor extractor) {
        int trackCount = extractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat trackFormat = extractor.getTrackFormat(i);
            Intrinsics.checkNotNullExpressionValue(trackFormat, "getTrackFormat(...)");
            String string = trackFormat.getString("mime");
            if (string != null && StringsKt.startsWith$default(string, "video/", false, 2, (Object) null)) {
                extractor.selectTrack(i);
                return i;
            }
        }
        return -1;
    }

    private final byte[] convertImageToYuv420ByteArray(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        ByteBuffer buffer2 = planes[1].getBuffer();
        ByteBuffer buffer3 = planes[2].getBuffer();
        int i = this.width * this.height;
        byte[] bArr = new byte[(i / 2) + i];
        int pixelStride = planes[1].getPixelStride();
        if (pixelStride == 1) {
            int i2 = this.width;
            int i3 = this.height;
            int i4 = ((i2 / 2) * i3) / 2;
            buffer.get(bArr, 0, i);
            buffer2.get(bArr, i, i4);
            buffer3.get(bArr, i + i4, ((i2 / 2) * i3) / 2);
        } else if (pixelStride == 2) {
            int i5 = (((this.width / 2) * this.height) / 2) * 2;
            buffer.get(bArr, 0, i);
            buffer2.get(bArr, i, i5);
        }
        return bArr;
    }
}
