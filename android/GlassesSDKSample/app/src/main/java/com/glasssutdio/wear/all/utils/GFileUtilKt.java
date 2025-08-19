package com.glasssutdio.wear.all.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.GlassApplication;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.IOUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.cookie.ClientCookie;
import com.liulishuo.okdownload.OkDownloadProvider;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.p014io.CloseableKt;
import kotlin.text.StringsKt;

/* compiled from: GFileUtil.kt */
@Metadata(m606d1 = {"\u0000B\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\u001a\u0006\u0010\u0000\u001a\u00020\u0001\u001a\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0001\u001a\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0001\u001a\u0010\u0010\b\u001a\u00020\u00032\b\u0010\t\u001a\u0004\u0018\u00010\n\u001a\u0010\u0010\b\u001a\u00020\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0001\u001a\u000e\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0001\u001a\u0006\u0010\f\u001a\u00020\n\u001a\u000e\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f\u001a\u000e\u0010\u0010\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f\u001a\u0006\u0010\u0011\u001a\u00020\n\u001a\u0006\u0010\u0012\u001a\u00020\n\u001a\u0006\u0010\u0013\u001a\u00020\n\u001a\u0006\u0010\u0014\u001a\u00020\n\u001a\u0006\u0010\u0015\u001a\u00020\n\u001a\u001a\u0010\u0016\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018\u001a\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0001\u001a\u000e\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u0001\u001a\u0016\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\u0007\u001a\u00020\u0001\u001a\u0006\u0010!\u001a\u00020\"\u001a\u0006\u0010#\u001a\u00020\"\u001a\u001e\u0010$\u001a\u00020\"2\u0006\u0010%\u001a\u00020&2\u0006\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0001¨\u0006'"}, m607d2 = {"androidCameraFilePath", "", "createDirs", "", "folder", "createFile", ClientCookie.PATH_ATTR, "fileName", "deleteFile", "file", "Ljava/io/File;", "fileExists", "getAlbumDirFile", "getAppCacheRootFile", "context", "Landroid/content/Context;", "getAppRootFile", "getBinDirFile", "getCacheFolder", "getDCIMFile", "getGPTDirFile", "getLogDirFile", "getRealPathFromUri", "contentUri", "Landroid/net/Uri;", "getVideoDuration", "", "videoPath", "identifyFileType", "filePath", "saveBitmapToFolder", "bitmap", "Landroid/graphics/Bitmap;", "test", "", "testFile", "writeToFile1", "data", "", "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class GFileUtilKt {
    public static final void test() {
    }

    public static final File getAppRootFile(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (context.getExternalFilesDir("") != null) {
            File externalFilesDir = context.getExternalFilesDir("");
            Intrinsics.checkNotNull(externalFilesDir);
            Intrinsics.checkNotNull(externalFilesDir);
            return externalFilesDir;
        }
        File externalCacheDir = context.getExternalCacheDir();
        File cacheDir = externalCacheDir == null ? context.getCacheDir() : externalCacheDir;
        Intrinsics.checkNotNull(cacheDir);
        return cacheDir;
    }

    public static final File getAppCacheRootFile(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (context.getExternalCacheDir() != null) {
            File externalCacheDir = context.getExternalCacheDir();
            Intrinsics.checkNotNull(externalCacheDir);
            Intrinsics.checkNotNull(externalCacheDir);
            return externalCacheDir;
        }
        File externalCacheDir2 = context.getExternalCacheDir();
        File cacheDir = externalCacheDir2 == null ? context.getCacheDir() : externalCacheDir2;
        Intrinsics.checkNotNull(cacheDir);
        return cacheDir;
    }

    public static final File getAlbumDirFile() {
        return new File(getAppRootFile(GlassApplication.INSTANCE.getCONTEXT()), "DCIM_1");
    }

    public static final File getCacheFolder() {
        File file = new File(getAppRootFile(GlassApplication.INSTANCE.getCONTEXT()), "qc_cache");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static final File getBinDirFile() {
        return new File(getAppRootFile(GlassApplication.INSTANCE.getCONTEXT()), "dfu");
    }

    public static final File getGPTDirFile() {
        return new File(getAppRootFile(GlassApplication.INSTANCE.getCONTEXT()), "ai");
    }

    public static final File getLogDirFile() {
        return new File(getAppRootFile(GlassApplication.INSTANCE.getCONTEXT()), "logs");
    }

    public static final boolean createFile(String path, String fileName) throws IOException {
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        File file = new File(path + IOUtils.DIR_SEPARATOR_UNIX + fileName);
        if (file.exists()) {
            return false;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static final boolean deleteFile(File file) {
        return file != null && file.delete();
    }

    public static final void writeToFile1(byte[] data, String path, String fileName) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        File parentFile;
        Intrinsics.checkNotNullParameter(data, "data");
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        File file = new File(path + IOUtils.DIR_SEPARATOR_UNIX + fileName);
        File parentFile2 = file.getParentFile();
        BufferedOutputStream bufferedOutputStream2 = null;
        Boolean boolValueOf = parentFile2 != null ? Boolean.valueOf(parentFile2.exists()) : null;
        Intrinsics.checkNotNull(boolValueOf);
        if (!boolValueOf.booleanValue() && (parentFile = file.getParentFile()) != null) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            createFile(path, fileName);
        }
        try {
            try {
                try {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Exception e) {
                e = e;
            }
            try {
                bufferedOutputStream.write(data);
                bufferedOutputStream.close();
            } catch (Exception e2) {
                e = e2;
                bufferedOutputStream2 = bufferedOutputStream;
                e.printStackTrace();
                if (bufferedOutputStream2 != null) {
                    bufferedOutputStream2.close();
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedOutputStream2 = bufferedOutputStream;
                if (bufferedOutputStream2 != null) {
                    try {
                        bufferedOutputStream2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    public static final String saveBitmapToFolder(Bitmap bitmap, String fileName) {
        Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        File albumDirFile = getAlbumDirFile();
        if (!albumDirFile.exists() && !albumDirFile.mkdirs()) {
            return "";
        }
        File file = new File(albumDirFile, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                FileOutputStream fileOutputStream2 = fileOutputStream;
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream2);
                fileOutputStream2.flush();
                String absolutePath = file.getAbsolutePath();
                Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
                CloseableKt.closeFinally(fileOutputStream, null);
                return absolutePath;
            } finally {
            }
        } catch (IOException e) {
            XLog.m132e("Error saving bitmap: " + e.getMessage());
            return "";
        }
    }

    public static final boolean fileExists(String path) {
        Intrinsics.checkNotNullParameter(path, "path");
        return new File(path).exists();
    }

    public static final boolean deleteFile(String str) {
        File file = new File(str);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        file.delete();
        return true;
    }

    public static final boolean createDirs(String folder) {
        Intrinsics.checkNotNullParameter(folder, "folder");
        File file = new File(folder);
        if (file.exists()) {
            return false;
        }
        file.mkdirs();
        return true;
    }

    public static final void testFile() throws IOException {
        OkDownloadProvider.context.getExternalFilesDir(null);
        try {
            new File(OkDownloadProvider.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "1.txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int getVideoDuration(String videoPath) throws IOException {
        Integer intOrNull;
        Integer intOrNull2;
        Intrinsics.checkNotNullParameter(videoPath, "videoPath");
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(videoPath);
            String strExtractMetadata = mediaMetadataRetriever.extractMetadata(9);
            String strExtractMetadata2 = mediaMetadataRetriever.extractMetadata(18);
            String strExtractMetadata3 = mediaMetadataRetriever.extractMetadata(19);
            int iIntValue = (strExtractMetadata2 == null || (intOrNull2 = StringsKt.toIntOrNull(strExtractMetadata2)) == null) ? 0 : intOrNull2.intValue();
            int iIntValue2 = (strExtractMetadata3 == null || (intOrNull = StringsKt.toIntOrNull(strExtractMetadata3)) == null) ? 0 : intOrNull.intValue();
            XLog.m137i("width:" + iIntValue + ",height:" + iIntValue2);
            if (iIntValue > 1920 || iIntValue2 > 1080) {
                UserConfig.INSTANCE.getInstance().setCamera8Mp(true);
            } else {
                UserConfig.INSTANCE.getInstance().setCamera8Mp(false);
            }
            Intrinsics.checkNotNull(strExtractMetadata);
            Integer intOrNull3 = StringsKt.toIntOrNull(strExtractMetadata);
            Intrinsics.checkNotNull(intOrNull3);
            if (intOrNull3.intValue() <= 1000) {
                return 1;
            }
            Integer intOrNull4 = StringsKt.toIntOrNull(strExtractMetadata);
            Intrinsics.checkNotNull(intOrNull4);
            return intOrNull4.intValue() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            mediaMetadataRetriever.release();
        }
    }

    public static final int identifyFileType(String filePath) {
        Intrinsics.checkNotNullParameter(filePath, "filePath");
        if (!StringsKt.endsWith$default(filePath, "jpg", false, 2, (Object) null) && !StringsKt.endsWith$default(filePath, "jpeg", false, 2, (Object) null)) {
            if (StringsKt.endsWith$default(filePath, "mp4", false, 2, (Object) null) || StringsKt.endsWith$default(filePath, "avi", false, 2, (Object) null)) {
                return 2;
            }
            if (StringsKt.endsWith$default(filePath, "opus", false, 2, (Object) null)) {
                return 3;
            }
        }
        return 1;
    }

    public static final File getDCIMFile() {
        return new File(getAppRootFile(GlassApplication.INSTANCE.getCONTEXT()), "DCIM_1");
    }

    public static final String androidCameraFilePath() {
        String absolutePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera").getAbsolutePath();
        Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
        return absolutePath;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0040  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final String getRealPathFromUri(Context context, Uri uri) throws Throwable {
        Cursor cursorQuery;
        Intrinsics.checkNotNullParameter(context, "context");
        Cursor cursor = null;
        string = null;
        String string = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Intrinsics.checkNotNull(uri);
            cursorQuery = contentResolver.query(uri, new String[]{"_data"}, null, null, null);
            try {
                try {
                    Intrinsics.checkNotNull(cursorQuery);
                    int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("_data");
                    cursorQuery.moveToFirst();
                    string = cursorQuery.getString(columnIndexOrThrow);
                } catch (Exception e) {
                    e = e;
                    e.printStackTrace();
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return string;
                }
            } catch (Throwable th) {
                th = th;
                cursor = cursorQuery;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Exception e2) {
            e = e2;
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
            if (cursor != null) {
            }
            throw th;
        }
        cursorQuery.close();
        return string;
    }
}
