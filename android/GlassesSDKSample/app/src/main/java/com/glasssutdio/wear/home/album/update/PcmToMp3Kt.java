package com.glasssutdio.wear.home.album.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.p014io.ByteStreamsKt;
import kotlin.p014io.FilesKt;
import kotlin.text.Charsets;

/* compiled from: PcmToMp3.kt */
@Metadata(m606d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\n\n\u0000\u001a\u0010\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0001\u001a\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u001a\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\bH\u0002¨\u0006\t"}, m607d2 = {"convertPcmToWav", "Ljava/io/File;", "pcmFile", "intToByteArray", "", "value", "", "shortToByteArray", "", "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class PcmToMp3Kt {
    public static final File convertPcmToWav(File pcmFile) throws IOException {
        Intrinsics.checkNotNullParameter(pcmFile, "pcmFile");
        FileInputStream fileInputStream = new FileInputStream(pcmFile);
        File file = new File(pcmFile.getParent(), FilesKt.getNameWithoutExtension(pcmFile) + ".wav");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        long length = pcmFile.length();
        byte[] bytes = "RIFF".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
        fileOutputStream.write(bytes);
        fileOutputStream.write(intToByteArray((int) (36 + length)));
        byte[] bytes2 = "WAVE".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
        fileOutputStream.write(bytes2);
        byte[] bytes3 = "fmt ".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
        fileOutputStream.write(bytes3);
        fileOutputStream.write(intToByteArray(16));
        fileOutputStream.write(shortToByteArray((short) 1));
        fileOutputStream.write(shortToByteArray((short) 1));
        fileOutputStream.write(intToByteArray(16000));
        fileOutputStream.write(intToByteArray(32000));
        fileOutputStream.write(shortToByteArray((short) 2));
        fileOutputStream.write(shortToByteArray((short) 16));
        byte[] bytes4 = "data".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
        fileOutputStream.write(bytes4);
        fileOutputStream.write(intToByteArray((int) length));
        ByteStreamsKt.copyTo$default(fileInputStream, fileOutputStream, 0, 2, null);
        fileInputStream.close();
        fileOutputStream.close();
        return file;
    }

    private static final byte[] intToByteArray(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)};
    }

    private static final byte[] shortToByteArray(short s) {
        return new byte[]{(byte) (s & 255), (byte) ((s >> 8) & 255)};
    }
}
