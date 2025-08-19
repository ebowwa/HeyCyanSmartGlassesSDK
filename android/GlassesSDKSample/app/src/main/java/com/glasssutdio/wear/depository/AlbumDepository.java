package com.glasssutdio.wear.depository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.content.ContextCompat;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.GlassApplication;
import com.glasssutdio.wear.all.GlobalKt;
import com.glasssutdio.wear.all.ThreadExtKt;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.DateUtil;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.all.utils.GsonInstance;
import com.glasssutdio.wear.database.GlassDatabase;
import com.glasssutdio.wear.database.dao.GlassAlbumDao;
import com.glasssutdio.wear.database.entity.GlassAlbumEntity;
import com.glasssutdio.wear.depository.AlbumDepository;
import com.glasssutdio.wear.depository.bean.DownloadSpeedCalculator;
import com.glasssutdio.wear.depository.bean.PictureDownloadBean;
import com.glasssutdio.wear.home.album.update.PcmToMp3Kt;
import com.glasssutdio.wear.stabilization.Mp4Decode;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.FilenameUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.IOUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.cookie.ClientCookie;
import com.hjq.permissions.Permission;
import com.jieli.jl_audio_decode.callback.OnStateCallback;
import com.jieli.jl_audio_decode.opus.OpusManager;
import com.jieli.jl_audio_decode.opus.model.OpusOption;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref;
import kotlin.math.MathKt;
import kotlin.p014io.ByteStreamsKt;
import kotlin.p014io.CloseableKt;
import kotlin.p014io.FilesKt;
import kotlin.text.StringsKt;
import p000.ImageProcessor;

/* compiled from: AlbumDepository.kt */
@Metadata(m606d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 O2\u00020\u0001:\u0002OPB\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\u0012J\u0018\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'H\u0002J\u000e\u0010(\u001a\u00020#2\u0006\u0010)\u001a\u00020\u0015J\u000e\u0010*\u001a\u00020#2\u0006\u0010+\u001a\u00020\u0015J\b\u0010,\u001a\u00020#H\u0002J\u0015\u0010-\u001a\u0004\u0018\u00010\r2\u0006\u0010.\u001a\u00020\u0010¢\u0006\u0002\u0010/J\u0010\u00100\u001a\u00020\u00102\u0006\u00101\u001a\u000202H\u0002J\u001e\u00103\u001a\u00020#2\u0006\u00104\u001a\u00020\u00102\u0006\u00105\u001a\u00020\u00102\u0006\u0010.\u001a\u00020\u0010J(\u00106\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u0014072\f\u00108\u001a\b\u0012\u0004\u0012\u00020\u001509H\u0002J\u0014\u0010:\u001a\u00020#2\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014J\u0012\u0010<\u001a\u0004\u0018\u00010=2\u0006\u0010>\u001a\u00020\u0010H\u0002J\b\u0010?\u001a\u00020#H\u0002J\u0018\u0010@\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u001407J\u0010\u0010A\u001a\u0004\u0018\u00010\u00152\u0006\u0010.\u001a\u00020\u0010J \u0010B\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u0014072\u0006\u0010C\u001a\u00020\rJ\u0018\u0010D\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u001407J\u0010\u0010E\u001a\u00020#2\u0006\u0010F\u001a\u00020\u0010H\u0002J\u000e\u0010G\u001a\u00020#2\u0006\u0010+\u001a\u00020\u0015J\u0018\u0010H\u001a\u00020#2\u0006\u0010I\u001a\u00020J2\u0006\u0010K\u001a\u000202H\u0002J\u0018\u0010L\u001a\u00020#2\u0006\u0010I\u001a\u00020J2\u0006\u0010M\u001a\u000202H\u0002J\u000e\u0010N\u001a\u00020#2\u0006\u0010\u001e\u001a\u00020\u001fR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\r0\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R \u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00150\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006Q"}, m607d2 = {"Lcom/glasssutdio/wear/depository/AlbumDepository;", "", "()V", "eisCallback", "Lcom/glasssutdio/wear/stabilization/Mp4Decode$Mp4DecodeCallback;", "getEisCallback", "()Lcom/glasssutdio/wear/stabilization/Mp4Decode$Mp4DecodeCallback;", "fileQueue", "Ljava/util/concurrent/BlockingDeque;", "Lcom/glasssutdio/wear/depository/bean/PictureDownloadBean;", "glassAlbumDao", "Lcom/glasssutdio/wear/database/dao/GlassAlbumDao;", "intervalTime", "", "keyMap", "", "", "lastUpdateTime", "", "listData", "", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "getListData", "()Ljava/util/List;", "setListData", "(Ljava/util/List;)V", "opusToPcmIng", "", "recordQueue", "totalFiles", "wifiFilesDownloadListener", "Lcom/glasssutdio/wear/depository/AlbumDepository$WifiFilesDownloadListener;", "calculatePCMPlaybackDuration", "totalBytes", "copyFile", "", "inputStream", "Ljava/io/InputStream;", "outputStream", "Ljava/io/OutputStream;", "decodeOpusStream", "entity", "deleteFile", "album", "downloadGlassFile", "getIndexByFileName", "fileName", "(Ljava/lang/String;)Ljava/lang/Integer;", "getMimeType", "file", "Ljava/io/File;", "getPhotoTextFile", "url", "dirPath", "groupAlbumsByDate", "Ljava/util/TreeMap;", "albums", "", "initDetailList", "list", "loadVideoFirstFrame", "Landroid/graphics/Bitmap;", "videoPath", "opusToPcm", "queryAllMedia", "queryFileByName", "queryImageMedia", "fileType", "queryLikeMedia", "readPhotoFile", ClientCookie.PATH_ATTR, "saveAlbum", "saveFileToAppGalleryFolder", "context", "Landroid/content/Context;", "sourceFile", "savePcmAsWavAndAddToMediaStore", "pcmFile", "setWifiDownloadListener", "Companion", "WifiFilesDownloadListener", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class AlbumDepository {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final Lazy<AlbumDepository> getInstance$delegate = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, (Function0) new Function0<AlbumDepository>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$Companion$getInstance$2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final AlbumDepository invoke() {
            return new AlbumDepository();
        }
    });
    private long lastUpdateTime;
    private boolean opusToPcmIng;
    private int totalFiles;
    private WifiFilesDownloadListener wifiFilesDownloadListener;
    private final GlassAlbumDao glassAlbumDao = GlassDatabase.INSTANCE.getDatabase(GlassApplication.INSTANCE.getCONTEXT()).glassAlbumDao();
    private List<GlassAlbumEntity> listData = new ArrayList();
    private Map<String, Integer> keyMap = new LinkedHashMap();
    private BlockingDeque<PictureDownloadBean> fileQueue = new LinkedBlockingDeque(100);
    private final BlockingDeque<GlassAlbumEntity> recordQueue = new LinkedBlockingDeque(50);
    private int intervalTime = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    private final Mp4Decode.Mp4DecodeCallback eisCallback = new Mp4Decode.Mp4DecodeCallback() { // from class: com.glasssutdio.wear.depository.AlbumDepository$eisCallback$1
        @Override // com.glasssutdio.wear.stabilization.Mp4Decode.Mp4DecodeCallback
        public void eisStart(String fileName, String filePath) {
            Intrinsics.checkNotNullParameter(fileName, "fileName");
            Intrinsics.checkNotNullParameter(filePath, "filePath");
            XLog.m137i("eisStart" + filePath);
        }

        @Override // com.glasssutdio.wear.stabilization.Mp4Decode.Mp4DecodeCallback
        public void eisError(final String fileName, final String sourcePath, final String eisFilePath, String errorInfo) {
            Intrinsics.checkNotNullParameter(fileName, "fileName");
            Intrinsics.checkNotNullParameter(sourcePath, "sourcePath");
            Intrinsics.checkNotNullParameter(eisFilePath, "eisFilePath");
            Intrinsics.checkNotNullParameter(errorInfo, "errorInfo");
            XLog.m137i("eisError" + errorInfo + sourcePath + "-----" + eisFilePath);
            final AlbumDepository albumDepository = this.this$0;
            ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<AlbumDepository$eisCallback$1, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$eisCallback$1$eisError$1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository$eisCallback$1 albumDepository$eisCallback$1) {
                    invoke2(albumDepository$eisCallback$1);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(AlbumDepository$eisCallback$1 ktxRunOnBgSingleDao) {
                    Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                    GFileUtilKt.deleteFile(eisFilePath);
                    GlassAlbumEntity glassAlbumEntityQueryFileByName = albumDepository.queryFileByName(fileName);
                    if (glassAlbumEntityQueryFileByName != null) {
                        glassAlbumEntityQueryFileByName.setFilePath(sourcePath);
                        glassAlbumEntityQueryFileByName.setEisInProgress(false);
                        albumDepository.saveAlbum(glassAlbumEntityQueryFileByName);
                    }
                }
            });
            if (this.this$0.wifiFilesDownloadListener != null) {
                AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener = this.this$0.wifiFilesDownloadListener;
                Intrinsics.checkNotNull(wifiFilesDownloadListener);
                wifiFilesDownloadListener.eisError(fileName, sourcePath, errorInfo);
            }
        }

        @Override // com.glasssutdio.wear.stabilization.Mp4Decode.Mp4DecodeCallback
        public void eisEnd(final String fileName, final String sourceFilePath, final String eisFilePath) {
            Intrinsics.checkNotNullParameter(fileName, "fileName");
            Intrinsics.checkNotNullParameter(sourceFilePath, "sourceFilePath");
            Intrinsics.checkNotNullParameter(eisFilePath, "eisFilePath");
            XLog.m137i("eisEnd:" + fileName + "-filePath:" + eisFilePath + "-----" + sourceFilePath);
            final AlbumDepository albumDepository = this.this$0;
            ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<AlbumDepository$eisCallback$1, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$eisCallback$1$eisEnd$1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository$eisCallback$1 albumDepository$eisCallback$1) {
                    invoke2(albumDepository$eisCallback$1);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(AlbumDepository$eisCallback$1 ktxRunOnBgSingleDao) {
                    Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                    if (new File(eisFilePath).length() <= 10) {
                        XLog.m137i("防抖空文件");
                        final AlbumDepository albumDepository2 = albumDepository;
                        final String str = fileName;
                        final String str2 = sourceFilePath;
                        ThreadExtKt.ktxRunOnBgSingleDao(ktxRunOnBgSingleDao, new Function1<AlbumDepository$eisCallback$1, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$eisCallback$1$eisEnd$1.1
                            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                            {
                                super(1);
                            }

                            @Override // kotlin.jvm.functions.Function1
                            public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository$eisCallback$1 albumDepository$eisCallback$1) {
                                invoke2(albumDepository$eisCallback$1);
                                return Unit.INSTANCE;
                            }

                            /* renamed from: invoke, reason: avoid collision after fix types in other method */
                            public final void invoke2(AlbumDepository$eisCallback$1 ktxRunOnBgSingleDao2) {
                                Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao2, "$this$ktxRunOnBgSingleDao");
                                GlassAlbumEntity glassAlbumEntityQueryFileByName = albumDepository2.queryFileByName(str);
                                if (glassAlbumEntityQueryFileByName != null) {
                                    glassAlbumEntityQueryFileByName.setFilePath(str2);
                                    glassAlbumEntityQueryFileByName.setEisInProgress(false);
                                    albumDepository2.saveAlbum(glassAlbumEntityQueryFileByName);
                                }
                            }
                        });
                        if (albumDepository.wifiFilesDownloadListener != null) {
                            AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener = albumDepository.wifiFilesDownloadListener;
                            Intrinsics.checkNotNull(wifiFilesDownloadListener);
                            wifiFilesDownloadListener.eisEnd(fileName, sourceFilePath);
                            return;
                        }
                        return;
                    }
                    GlassAlbumEntity glassAlbumEntityQueryFileByName = albumDepository.queryFileByName(fileName);
                    if (glassAlbumEntityQueryFileByName != null) {
                        glassAlbumEntityQueryFileByName.setEisInProgress(false);
                        glassAlbumEntityQueryFileByName.setFilePath(eisFilePath);
                        albumDepository.saveAlbum(glassAlbumEntityQueryFileByName);
                    }
                    if (albumDepository.wifiFilesDownloadListener != null) {
                        AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener2 = albumDepository.wifiFilesDownloadListener;
                        Intrinsics.checkNotNull(wifiFilesDownloadListener2);
                        wifiFilesDownloadListener2.eisEnd(fileName, eisFilePath);
                    }
                }
            });
            final AlbumDepository albumDepository2 = this.this$0;
            ThreadExtKt.ktxRunOnBgFix(this, new Function1<AlbumDepository$eisCallback$1, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$eisCallback$1$eisEnd$2
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository$eisCallback$1 albumDepository$eisCallback$1) throws InterruptedException, Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                    invoke2(albumDepository$eisCallback$1);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(AlbumDepository$eisCallback$1 ktxRunOnBgFix) throws InterruptedException, Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                    Intrinsics.checkNotNullParameter(ktxRunOnBgFix, "$this$ktxRunOnBgFix");
                    Thread.sleep(2000L);
                    albumDepository2.saveFileToAppGalleryFolder(GlassApplication.INSTANCE.getCONTEXT(), new File(eisFilePath));
                }
            });
        }
    };

    /* compiled from: AlbumDepository.kt */
    @Metadata(m606d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H&J \u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0005H&J\u0018\u0010\n\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH&J\b\u0010\u000e\u001a\u00020\u0003H&J\u0018\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\fH&J\u0018\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\fH&J\u0010\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016H&J \u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\fH&J\u0018\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0005H&J\u0010\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u0005H&¨\u0006\u001b"}, m607d2 = {"Lcom/glasssutdio/wear/depository/AlbumDepository$WifiFilesDownloadListener;", "", "eisEnd", "", "fileName", "", "filePath", "eisError", "sourcePath", "errorInfo", "fileCount", "index", "", "total", "fileDownloadComplete", "fileDownloadError", "fileType", "errorType", "fileProgress", "progress", "fileWasDownloadSuccessfully", "entity", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "recordingToPcm", TypedValues.TransitionType.S_DURATION, "recordingToPcmError", "wifiSpeed", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public interface WifiFilesDownloadListener {
        void eisEnd(String fileName, String filePath);

        void eisError(String fileName, String sourcePath, String errorInfo);

        void fileCount(int index, int total);

        void fileDownloadComplete();

        void fileDownloadError(int fileType, int errorType);

        void fileProgress(String fileName, int progress);

        void fileWasDownloadSuccessfully(GlassAlbumEntity entity);

        void recordingToPcm(String fileName, String filePath, int duration);

        void recordingToPcmError(String fileName, String errorInfo);

        void wifiSpeed(String wifiSpeed);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void savePcmAsWavAndAddToMediaStore$lambda$6$lambda$5(String str, Uri uri) {
    }

    public final List<GlassAlbumEntity> getListData() {
        return this.listData;
    }

    public final void setListData(List<GlassAlbumEntity> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.listData = list;
    }

    public final void setWifiDownloadListener(WifiFilesDownloadListener wifiFilesDownloadListener) {
        Intrinsics.checkNotNullParameter(wifiFilesDownloadListener, "wifiFilesDownloadListener");
        this.wifiFilesDownloadListener = wifiFilesDownloadListener;
    }

    public final void initDetailList(List<GlassAlbumEntity> list) {
        Intrinsics.checkNotNullParameter(list, "list");
        this.listData = list;
        int i = 0;
        for (GlassAlbumEntity glassAlbumEntity : list) {
            int i2 = i + 1;
            this.keyMap.put(glassAlbumEntity.getFileName(), Integer.valueOf(i));
            i = i2;
        }
    }

    public final Integer getIndexByFileName(String fileName) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        return this.keyMap.get(fileName);
    }

    private final TreeMap<Integer, List<GlassAlbumEntity>> groupAlbumsByDate(List<GlassAlbumEntity> albums) {
        TreeMap<Integer, List<GlassAlbumEntity>> treeMap = new TreeMap<>();
        for (GlassAlbumEntity glassAlbumEntity : albums) {
            TreeMap<Integer, List<GlassAlbumEntity>> treeMap2 = treeMap;
            Integer numValueOf = Integer.valueOf(DateUtil.dateY_M_D2StampSecond(glassAlbumEntity.getFileDate()));
            ArrayList arrayList = treeMap2.get(numValueOf);
            if (arrayList == null) {
                arrayList = new ArrayList();
                treeMap2.put(numValueOf, arrayList);
            }
            arrayList.add(glassAlbumEntity);
        }
        return treeMap;
    }

    public final TreeMap<Integer, List<GlassAlbumEntity>> queryAllMedia() {
        return groupAlbumsByDate(this.glassAlbumDao.queryAllFile(UserConfig.INSTANCE.getInstance().getDeviceAddressNoClear()));
    }

    public final TreeMap<Integer, List<GlassAlbumEntity>> queryImageMedia(int fileType) {
        return groupAlbumsByDate(this.glassAlbumDao.queryImageFileByteType(UserConfig.INSTANCE.getInstance().getDeviceAddressNoClear(), fileType));
    }

    public final TreeMap<Integer, List<GlassAlbumEntity>> queryLikeMedia() {
        return groupAlbumsByDate(this.glassAlbumDao.queryLikeMedia(UserConfig.INSTANCE.getInstance().getDeviceAddressNoClear()));
    }

    public final void saveAlbum(GlassAlbumEntity album) {
        Intrinsics.checkNotNullParameter(album, "album");
        XLog.m137i(GsonInstance.INSTANCE.getGson().toJson(album));
        this.glassAlbumDao.insert(album);
    }

    public final GlassAlbumEntity queryFileByName(String fileName) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        return this.glassAlbumDao.queryAlbumFileByName(fileName, UserConfig.INSTANCE.getInstance().getDeviceAddressNoClear());
    }

    public final void deleteFile(GlassAlbumEntity album) {
        Intrinsics.checkNotNullParameter(album, "album");
        this.glassAlbumDao.delete(album);
    }

    /* compiled from: AlbumDepository.kt */
    @Metadata(m606d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n¢\u0006\u0002\b\u0003"}, m607d2 = {"<anonymous>", "", "Lcom/glasssutdio/wear/depository/AlbumDepository;", "invoke"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    /* renamed from: com.glasssutdio.wear.depository.AlbumDepository$getPhotoTextFile$1 */
    static final class C09011 extends Lambda implements Function1<AlbumDepository, Unit> {
        final /* synthetic */ String $dirPath;
        final /* synthetic */ String $fileName;
        final /* synthetic */ String $url;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C09011(String str, String str2, String str3) {
            super(1);
            this.$url = str;
            this.$dirPath = str2;
            this.$fileName = str3;
        }

        @Override // kotlin.jvm.functions.Function1
        public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository albumDepository) {
            invoke2(albumDepository);
            return Unit.INSTANCE;
        }

        /* renamed from: invoke, reason: avoid collision after fix types in other method */
        public final void invoke2(AlbumDepository ktxRunOnBgSingle) {
            Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
            XLog.m137i(this.$url);
            if (GFileUtilKt.fileExists(this.$dirPath + IOUtils.DIR_SEPARATOR_UNIX + this.$fileName)) {
                GFileUtilKt.deleteFile(this.$dirPath + IOUtils.DIR_SEPARATOR_UNIX + this.$fileName);
            }
            invoke$startDownload(this.$url, this.$dirPath, this.$fileName, ktxRunOnBgSingle, new Ref.IntRef(), 1);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void invoke$startDownload(final String str, final String str2, final String str3, final AlbumDepository albumDepository, final Ref.IntRef intRef, final int i) {
            AndroidNetworking.download(str, str2, str3).setTag((Object) "photo.txt").setPriority(Priority.MEDIUM).build().startDownload(new DownloadListener() { // from class: com.glasssutdio.wear.depository.AlbumDepository$getPhotoTextFile$1$startDownload$1
                @Override // com.androidnetworking.interfaces.DownloadListener
                public void onDownloadComplete() throws InterruptedException {
                    XLog.m137i("download photo text success");
                    albumDepository.fileQueue.clear();
                    albumDepository.readPhotoFile(str2 + IOUtils.DIR_SEPARATOR_UNIX + str3);
                }

                @Override // com.androidnetworking.interfaces.DownloadListener
                public void onError(ANError error) {
                    Intrinsics.checkNotNullParameter(error, "error");
                    XLog.m137i(String.valueOf(error.getErrorCode()));
                    XLog.m137i(error.getErrorDetail());
                    if (intRef.element >= i) {
                        if (albumDepository.wifiFilesDownloadListener != null) {
                            AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener = albumDepository.wifiFilesDownloadListener;
                            Intrinsics.checkNotNull(wifiFilesDownloadListener);
                            wifiFilesDownloadListener.fileDownloadError(1, error.getErrorCode());
                            return;
                        }
                        return;
                    }
                    intRef.element++;
                    XLog.m137i("Download failed, retrying (" + intRef.element + IOUtils.DIR_SEPARATOR_UNIX + i + ')');
                    AlbumDepository.C09011.invoke$startDownload(str, str2, str3, albumDepository, intRef, i);
                }
            });
        }
    }

    public final void getPhotoTextFile(String url, String dirPath, String fileName) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(dirPath, "dirPath");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        ThreadExtKt.ktxRunOnBgSingle(this, new C09011(url, dirPath, fileName));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void readPhotoFile(String path) throws InterruptedException {
        try {
            List<String> lines$default = FilesKt.readLines$default(new File(path), null, 1, null);
            XLog.m137i("总文件数:" + lines$default.size());
            this.fileQueue = new LinkedBlockingDeque(lines$default.size() + 10);
            for (String str : lines$default) {
                String str2 = "http://" + UserConfig.INSTANCE.getInstance().getGlassDeviceWifiIP() + "/files/" + str;
                if (UserConfig.INSTANCE.getInstance().getGlassesLogs()) {
                    str2 = "http://" + UserConfig.INSTANCE.getInstance().getGlassDeviceWifiIP() + "/files/log/" + str;
                }
                this.fileQueue.putLast(new PictureDownloadBean(str2, str));
            }
            this.totalFiles = lines$default.size();
            ThreadExtKt.ktxRunOnBgSingle(this, new Function1<AlbumDepository, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository.readPhotoFile.1
                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository albumDepository) throws InterruptedException {
                    invoke2(albumDepository);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(AlbumDepository ktxRunOnBgSingle) throws InterruptedException {
                    Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
                    ktxRunOnBgSingle.downloadGlassFile();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void downloadGlassFile() throws InterruptedException {
        XLog.m137i("还剩下几个文件:" + this.fileQueue.size());
        if (this.fileQueue.isEmpty()) {
            WifiFilesDownloadListener wifiFilesDownloadListener = this.wifiFilesDownloadListener;
            Intrinsics.checkNotNull(wifiFilesDownloadListener);
            wifiFilesDownloadListener.fileDownloadComplete();
            UserConfig.INSTANCE.getInstance().setGlassesLogs(false);
            return;
        }
        WifiFilesDownloadListener wifiFilesDownloadListener2 = this.wifiFilesDownloadListener;
        Intrinsics.checkNotNull(wifiFilesDownloadListener2);
        wifiFilesDownloadListener2.fileCount((this.totalFiles - this.fileQueue.size()) + 1, this.totalFiles);
        final PictureDownloadBean pictureDownloadBeanTake = this.fileQueue.take();
        final Ref.IntRef intRef = new Ref.IntRef();
        intRef.element = -1;
        final DownloadSpeedCalculator downloadSpeedCalculator = new DownloadSpeedCalculator();
        downloadSpeedCalculator.start();
        this.lastUpdateTime = 0L;
        XLog.m137i(pictureDownloadBeanTake.getPath());
        AndroidNetworking.download(pictureDownloadBeanTake.getPath(), GFileUtilKt.getAlbumDirFile().getAbsolutePath(), pictureDownloadBeanTake.getFileName()).setTag((Object) "download_file").setPriority(Priority.MEDIUM).build().setDownloadProgressListener(new DownloadProgressListener() { // from class: com.glasssutdio.wear.depository.AlbumDepository$$ExternalSyntheticLambda1
            @Override // com.androidnetworking.interfaces.DownloadProgressListener
            public final void onProgress(long j, long j2) {
                AlbumDepository.downloadGlassFile$lambda$1(this.f$0, downloadSpeedCalculator, intRef, pictureDownloadBeanTake, j, j2);
            }
        }).startDownload(new DownloadListener() { // from class: com.glasssutdio.wear.depository.AlbumDepository.downloadGlassFile.2
            /* JADX WARN: Removed duplicated region for block: B:22:0x0115  */
            /* JADX WARN: Removed duplicated region for block: B:31:0x012f A[EXC_TOP_SPLITTER, SYNTHETIC] */
            @Override // com.androidnetworking.interfaces.DownloadListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onDownloadComplete() throws InterruptedException, IOException {
                int i;
                boolean z;
                String strSaveBitmapToFolder;
                if (StringsKt.endsWith$default(pictureDownloadBeanTake.getFileName(), ".log", false, 2, (Object) null)) {
                    this.downloadGlassFile();
                    return;
                }
                XLog.m137i("download photo text success:" + pictureDownloadBeanTake.getFileName());
                final File file = new File(GFileUtilKt.getAlbumDirFile().getAbsolutePath(), pictureDownloadBeanTake.getFileName());
                if (((int) file.length()) == 0) {
                    GFileUtilKt.deleteFile(file.getAbsolutePath());
                } else {
                    String absolutePath = file.getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
                    final int iIdentifyFileType = GFileUtilKt.identifyFileType(absolutePath);
                    if (iIdentifyFileType == 1) {
                        ThreadExtKt.ktxRunOnBgFix(this, new Function1<C08982, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$downloadGlassFile$2$onDownloadComplete$1
                            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                            {
                                super(1);
                            }

                            @Override // kotlin.jvm.functions.Function1
                            public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08982 c08982) throws Throwable {
                                invoke2(c08982);
                                return Unit.INSTANCE;
                            }

                            /* renamed from: invoke, reason: avoid collision after fix types in other method */
                            public final void invoke2(AlbumDepository.C08982 ktxRunOnBgFix) throws Throwable {
                                Intrinsics.checkNotNullParameter(ktxRunOnBgFix, "$this$ktxRunOnBgFix");
                                ImageProcessor imageProcessor = ImageProcessor.INSTANCE;
                                String absolutePath2 = file.getAbsolutePath();
                                Intrinsics.checkNotNullExpressionValue(absolutePath2, "getAbsolutePath(...)");
                                imageProcessor.processAndReplace(absolutePath2);
                            }
                        });
                    } else {
                        if (iIdentifyFileType == 2) {
                            String absolutePath2 = file.getAbsolutePath();
                            Intrinsics.checkNotNullExpressionValue(absolutePath2, "getAbsolutePath(...)");
                            int videoDuration = GFileUtilKt.getVideoDuration(absolutePath2);
                            AlbumDepository albumDepository = this;
                            String absolutePath3 = file.getAbsolutePath();
                            Intrinsics.checkNotNullExpressionValue(absolutePath3, "getAbsolutePath(...)");
                            Bitmap bitmapLoadVideoFirstFrame = albumDepository.loadVideoFirstFrame(absolutePath3);
                            if (bitmapLoadVideoFirstFrame == null) {
                                z = true;
                                strSaveBitmapToFolder = "";
                            } else {
                                strSaveBitmapToFolder = GFileUtilKt.saveBitmapToFolder(bitmapLoadVideoFirstFrame, "first_frame_" + ((String) StringsKt.split$default((CharSequence) pictureDownloadBeanTake.getFileName(), new String[]{"."}, false, 0, 6, (Object) null).get(0)) + ".png");
                                z = true;
                            }
                            i = videoDuration;
                        }
                        String name = file.getName();
                        String deviceAddress = UserConfig.INSTANCE.getInstance().getDeviceAddress();
                        String absolutePath4 = file.getAbsolutePath();
                        String y_m_d = new DateUtil(file.lastModified(), false).getY_M_D();
                        long timestamp = new DateUtil(file.lastModified(), false).getTimestamp();
                        Intrinsics.checkNotNull(name);
                        Intrinsics.checkNotNull(absolutePath4);
                        Intrinsics.checkNotNull(y_m_d);
                        final GlassAlbumEntity glassAlbumEntity = new GlassAlbumEntity(name, deviceAddress, absolutePath4, strSaveBitmapToFolder, iIdentifyFileType, i, y_m_d, timestamp, 0, 0, z, false);
                        if (iIdentifyFileType == 3) {
                            final AlbumDepository albumDepository2 = this;
                            ThreadExtKt.ktxRunOnBgFix(this, new Function1<C08982, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$downloadGlassFile$2$onDownloadComplete$2
                                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                {
                                    super(1);
                                }

                                @Override // kotlin.jvm.functions.Function1
                                public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08982 c08982) throws InterruptedException {
                                    invoke2(c08982);
                                    return Unit.INSTANCE;
                                }

                                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                                public final void invoke2(AlbumDepository.C08982 ktxRunOnBgFix) throws InterruptedException {
                                    Intrinsics.checkNotNullParameter(ktxRunOnBgFix, "$this$ktxRunOnBgFix");
                                    albumDepository2.recordQueue.put(glassAlbumEntity);
                                    final AlbumDepository albumDepository3 = albumDepository2;
                                    ThreadExtKt.ktxRunOnUiDelay(ktxRunOnBgFix, 2000L, new Function1<AlbumDepository.C08982, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$downloadGlassFile$2$onDownloadComplete$2.1
                                        {
                                            super(1);
                                        }

                                        @Override // kotlin.jvm.functions.Function1
                                        public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08982 c08982) throws InterruptedException {
                                            invoke2(c08982);
                                            return Unit.INSTANCE;
                                        }

                                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                                        public final void invoke2(AlbumDepository.C08982 ktxRunOnUiDelay) throws InterruptedException {
                                            Intrinsics.checkNotNullParameter(ktxRunOnUiDelay, "$this$ktxRunOnUiDelay");
                                            albumDepository3.opusToPcm();
                                        }
                                    });
                                }
                            });
                        }
                        final AlbumDepository albumDepository3 = this;
                        ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<C08982, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$downloadGlassFile$2$onDownloadComplete$3
                            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                            {
                                super(1);
                            }

                            @Override // kotlin.jvm.functions.Function1
                            public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08982 c08982) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                                invoke2(c08982);
                                return Unit.INSTANCE;
                            }

                            /* renamed from: invoke, reason: avoid collision after fix types in other method */
                            public final void invoke2(AlbumDepository.C08982 ktxRunOnBgSingleDao) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                                Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                                albumDepository3.saveAlbum(glassAlbumEntity);
                                if (iIdentifyFileType == 1) {
                                    albumDepository3.saveFileToAppGalleryFolder(GlassApplication.INSTANCE.getCONTEXT(), file);
                                }
                                if (albumDepository3.wifiFilesDownloadListener != null) {
                                    AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener3 = albumDepository3.wifiFilesDownloadListener;
                                    Intrinsics.checkNotNull(wifiFilesDownloadListener3);
                                    wifiFilesDownloadListener3.fileWasDownloadSuccessfully(glassAlbumEntity);
                                }
                            }
                        });
                        if (iIdentifyFileType == 2) {
                            try {
                                final PictureDownloadBean pictureDownloadBean = pictureDownloadBeanTake;
                                final AlbumDepository albumDepository4 = this;
                                ThreadExtKt.ktxRunOnBgFix(this, new Function1<C08982, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$downloadGlassFile$2$onDownloadComplete$4
                                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                    {
                                        super(1);
                                    }

                                    @Override // kotlin.jvm.functions.Function1
                                    public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08982 c08982) throws MediaCodec.CryptoException, IOException {
                                        invoke2(c08982);
                                        return Unit.INSTANCE;
                                    }

                                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                                    public final void invoke2(AlbumDepository.C08982 ktxRunOnBgFix) throws MediaCodec.CryptoException, IOException {
                                        Intrinsics.checkNotNullParameter(ktxRunOnBgFix, "$this$ktxRunOnBgFix");
                                        String str = "eis_" + pictureDownloadBean.getFileName();
                                        Mp4Decode mp4Decode = new Mp4Decode();
                                        mp4Decode.eisInit();
                                        mp4Decode.eisYuv2Mp4(file, new File(GFileUtilKt.getAlbumDirFile().getAbsolutePath(), str), albumDepository4.getEisCallback());
                                        XLog.m137i("eis fileName:" + str);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    i = 0;
                    z = false;
                    strSaveBitmapToFolder = "";
                    String name2 = file.getName();
                    String deviceAddress2 = UserConfig.INSTANCE.getInstance().getDeviceAddress();
                    String absolutePath42 = file.getAbsolutePath();
                    String y_m_d2 = new DateUtil(file.lastModified(), false).getY_M_D();
                    long timestamp2 = new DateUtil(file.lastModified(), false).getTimestamp();
                    Intrinsics.checkNotNull(name2);
                    Intrinsics.checkNotNull(absolutePath42);
                    Intrinsics.checkNotNull(y_m_d2);
                    final GlassAlbumEntity glassAlbumEntity2 = new GlassAlbumEntity(name2, deviceAddress2, absolutePath42, strSaveBitmapToFolder, iIdentifyFileType, i, y_m_d2, timestamp2, 0, 0, z, false);
                    if (iIdentifyFileType == 3) {
                    }
                    final AlbumDepository albumDepository32 = this;
                    ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<C08982, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$downloadGlassFile$2$onDownloadComplete$3
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        {
                            super(1);
                        }

                        @Override // kotlin.jvm.functions.Function1
                        public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08982 c08982) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                            invoke2(c08982);
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2(AlbumDepository.C08982 ktxRunOnBgSingleDao) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                            Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                            albumDepository32.saveAlbum(glassAlbumEntity2);
                            if (iIdentifyFileType == 1) {
                                albumDepository32.saveFileToAppGalleryFolder(GlassApplication.INSTANCE.getCONTEXT(), file);
                            }
                            if (albumDepository32.wifiFilesDownloadListener != null) {
                                AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener3 = albumDepository32.wifiFilesDownloadListener;
                                Intrinsics.checkNotNull(wifiFilesDownloadListener3);
                                wifiFilesDownloadListener3.fileWasDownloadSuccessfully(glassAlbumEntity2);
                            }
                        }
                    });
                    if (iIdentifyFileType == 2) {
                    }
                }
                this.downloadGlassFile();
            }

            @Override // com.androidnetworking.interfaces.DownloadListener
            public void onError(ANError error) throws InterruptedException {
                Intrinsics.checkNotNullParameter(error, "error");
                XLog.m137i(String.valueOf(error.getErrorCode()));
                XLog.m137i(error.getErrorDetail().toString());
                if (this.wifiFilesDownloadListener != null) {
                    WifiFilesDownloadListener wifiFilesDownloadListener3 = this.wifiFilesDownloadListener;
                    Intrinsics.checkNotNull(wifiFilesDownloadListener3);
                    wifiFilesDownloadListener3.fileDownloadError(2, error.getErrorCode());
                }
                this.downloadGlassFile();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void downloadGlassFile$lambda$1(AlbumDepository this$0, DownloadSpeedCalculator calculator, Ref.IntRef lastReportedProgress, PictureDownloadBean pictureDownloadBean, long j, long j2) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(calculator, "$calculator");
        Intrinsics.checkNotNullParameter(lastReportedProgress, "$lastReportedProgress");
        if (j2 > 0) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            boolean z = j == j2 / ((long) 2);
            if (jCurrentTimeMillis - this$0.lastUpdateTime >= this$0.intervalTime || z || j == j2) {
                this$0.lastUpdateTime = jCurrentTimeMillis;
                String strCalculate = calculator.calculate(j);
                if (this$0.wifiFilesDownloadListener != null && !Intrinsics.areEqual(strCalculate, "-1")) {
                    WifiFilesDownloadListener wifiFilesDownloadListener = this$0.wifiFilesDownloadListener;
                    Intrinsics.checkNotNull(wifiFilesDownloadListener);
                    wifiFilesDownloadListener.wifiSpeed(strCalculate);
                }
            }
            int i = (int) ((j * 100) / j2);
            if (i > lastReportedProgress.element || i == 100) {
                lastReportedProgress.element = i;
                WifiFilesDownloadListener wifiFilesDownloadListener2 = this$0.wifiFilesDownloadListener;
                if (wifiFilesDownloadListener2 != null) {
                    Intrinsics.checkNotNull(wifiFilesDownloadListener2);
                    wifiFilesDownloadListener2.fileProgress(pictureDownloadBean.getFileName(), i);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Bitmap loadVideoFirstFrame(String videoPath) throws IOException {
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                try {
                    mediaMetadataRetriever.setDataSource(videoPath);
                    return mediaMetadataRetriever.getFrameAtTime(1L, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    mediaMetadataRetriever.release();
                    return null;
                }
            } finally {
                mediaMetadataRetriever.release();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public final Mp4Decode.Mp4DecodeCallback getEisCallback() {
        return this.eisCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void opusToPcm() throws InterruptedException {
        if (this.recordQueue.isEmpty()) {
            return;
        }
        if (!this.opusToPcmIng) {
            XLog.m137i("opusToPcm");
            this.opusToPcmIng = true;
            GlassAlbumEntity glassAlbumEntityTake = this.recordQueue.take();
            Intrinsics.checkNotNull(glassAlbumEntityTake);
            decodeOpusStream(glassAlbumEntityTake);
            return;
        }
        XLog.m137i("opusToPcm 正在进行中");
    }

    public final void decodeOpusStream(final GlassAlbumEntity entity) {
        Intrinsics.checkNotNullParameter(entity, "entity");
        final String str = ((String) StringsKt.split$default((CharSequence) entity.getFileName(), new String[]{"."}, false, 0, 6, (Object) null).get(0)) + ".pcm";
        String str2 = GFileUtilKt.getDCIMFile().getAbsolutePath() + IOUtils.DIR_SEPARATOR_UNIX + str;
        OpusOption opusOption = new OpusOption();
        opusOption.setHasHead(false);
        opusOption.setSampleRate(16000);
        opusOption.setPacketSize(40);
        opusOption.setChannel(1);
        final OpusManager opusManager = new OpusManager();
        opusManager.decodeFile(entity.getFilePath(), str2, opusOption, new OnStateCallback() { // from class: com.glasssutdio.wear.depository.AlbumDepository.decodeOpusStream.1
            @Override // com.jieli.jl_audio_decode.callback.OnStateCallback
            public void onStart() {
                XLog.m137i("开始解码");
            }

            @Override // com.jieli.jl_audio_decode.callback.OnStateCallback
            public void onComplete(String p0) throws InterruptedException {
                Intrinsics.checkNotNullParameter(p0, "p0");
                XLog.m137i("解码结束 >> " + p0);
                AlbumDepository.this.opusToPcmIng = false;
                entity.setFilePath(GFileUtilKt.getDCIMFile().getAbsolutePath() + IOUtils.DIR_SEPARATOR_UNIX + str);
                final int iCalculatePCMPlaybackDuration = AlbumDepository.this.calculatePCMPlaybackDuration(new File(entity.getFilePath()).length());
                XLog.m137i("recording length:" + iCalculatePCMPlaybackDuration);
                final GlassAlbumEntity glassAlbumEntity = entity;
                final AlbumDepository albumDepository = AlbumDepository.this;
                ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<C08971, Unit>() { // from class: com.glasssutdio.wear.depository.AlbumDepository$decodeOpusStream$1$onComplete$1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(AlbumDepository.C08971 c08971) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                        invoke2(c08971);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(AlbumDepository.C08971 ktxRunOnBgSingleDao) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                        Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                        glassAlbumEntity.setVideoLength(iCalculatePCMPlaybackDuration);
                        albumDepository.saveAlbum(glassAlbumEntity);
                        AlbumDepository.WifiFilesDownloadListener wifiFilesDownloadListener = albumDepository.wifiFilesDownloadListener;
                        if (wifiFilesDownloadListener != null) {
                            wifiFilesDownloadListener.recordingToPcm(glassAlbumEntity.getFileName(), glassAlbumEntity.getFilePath(), iCalculatePCMPlaybackDuration);
                        }
                        albumDepository.saveFileToAppGalleryFolder(GlassApplication.INSTANCE.getCONTEXT(), new File(glassAlbumEntity.getFilePath()));
                    }
                });
                opusManager.stopEncodeStream();
                AlbumDepository.this.opusToPcm();
            }

            @Override // com.jieli.jl_audio_decode.callback.OnStateCallback
            public void onError(int code, String message) throws InterruptedException {
                Intrinsics.checkNotNullParameter(message, "message");
                try {
                    AlbumDepository.this.opusToPcmIng = false;
                    XLog.m137i(code + ", " + message);
                    AlbumDepository.this.recordQueue.put(entity);
                    WifiFilesDownloadListener wifiFilesDownloadListener = AlbumDepository.this.wifiFilesDownloadListener;
                    if (wifiFilesDownloadListener != null) {
                        wifiFilesDownloadListener.recordingToPcmError(entity.getFileName(), message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private final void savePcmAsWavAndAddToMediaStore(Context context, File pcmFile) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
        File fileConvertPcmToWav = PcmToMp3Kt.convertPcmToWav(pcmFile);
        String appName = GlobalKt.getAppName(context);
        if (fileConvertPcmToWav != null) {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", fileConvertPcmToWav.getName());
                contentValues.put("mime_type", "audio/x-wav");
                contentValues.put("relative_path", Environment.DIRECTORY_MUSIC + IOUtils.DIR_SEPARATOR_UNIX + appName);
                ContentResolver contentResolver = context.getContentResolver();
                Uri uriInsert = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (uriInsert != null) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileConvertPcmToWav);
                        OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(uriInsert);
                        if (outputStreamOpenOutputStream != null) {
                            OutputStream outputStream = outputStreamOpenOutputStream;
                            try {
                                Long.valueOf(ByteStreamsKt.copyTo$default(fileInputStream, outputStream, 0, 2, null));
                                CloseableKt.closeFinally(outputStream, null);
                            } finally {
                            }
                        }
                        fileInputStream.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                return;
            }
            MediaScannerConnection.scanFile(context, new String[]{fileConvertPcmToWav.getAbsolutePath()}, new String[]{"audio/x-wav"}, new MediaScannerConnection.OnScanCompletedListener() { // from class: com.glasssutdio.wear.depository.AlbumDepository$$ExternalSyntheticLambda0
                @Override // android.media.MediaScannerConnection.OnScanCompletedListener
                public final void onScanCompleted(String str, Uri uri) {
                    AlbumDepository.savePcmAsWavAndAddToMediaStore$lambda$6$lambda$5(str, uri);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void saveFileToAppGalleryFolder(Context context, File sourceFile) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
        FileOutputStream fileInputStream;
        Pair pairM614to;
        if (!UserConfig.INSTANCE.getInstance().getPictureAutoSave()) {
            return;
        }
        String appName = GlobalKt.getAppName(context);
        String mimeType = getMimeType(sourceFile);
        if (StringsKt.startsWith$default(mimeType, "audio/", false, 2, (Object) null)) {
            savePcmAsWavAndAddToMediaStore(context, sourceFile);
            return;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            if (StringsKt.startsWith$default(mimeType, "image/", false, 2, (Object) null)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", sourceFile.getName());
                contentValues.put("mime_type", mimeType);
                contentValues.put("relative_path", Environment.DIRECTORY_DCIM + IOUtils.DIR_SEPARATOR_UNIX + appName);
                pairM614to = TuplesKt.m614to(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else if (StringsKt.startsWith$default(mimeType, "video/", false, 2, (Object) null)) {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("_display_name", sourceFile.getName());
                contentValues2.put("mime_type", mimeType);
                contentValues2.put("relative_path", Environment.DIRECTORY_DCIM + IOUtils.DIR_SEPARATOR_UNIX + appName);
                pairM614to = TuplesKt.m614to(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues2);
            } else {
                XLog.m137i("不支持的文件类型");
                return;
            }
            Uri uri = (Uri) pairM614to.component1();
            ContentValues contentValues3 = (ContentValues) pairM614to.component2();
            ContentResolver contentResolver = context.getContentResolver();
            Uri uriInsert = contentResolver.insert(uri, contentValues3);
            if (uriInsert != null) {
                try {
                    fileInputStream = new FileInputStream(sourceFile);
                    try {
                        FileInputStream fileInputStream2 = fileInputStream;
                        OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(uriInsert);
                        if (outputStreamOpenOutputStream != null) {
                            fileInputStream = outputStreamOpenOutputStream;
                            try {
                                OutputStream outputStream = fileInputStream;
                                Intrinsics.checkNotNull(outputStream);
                                copyFile(fileInputStream2, outputStream);
                                Unit unit = Unit.INSTANCE;
                                CloseableKt.closeFinally(fileInputStream, null);
                                Unit unit2 = Unit.INSTANCE;
                            } finally {
                                try {
                                    throw th;
                                } finally {
                                }
                            }
                        }
                        CloseableKt.closeFinally(fileInputStream, null);
                        XLog.m137i("文件保存成功");
                        return;
                    } finally {
                        try {
                            throw th;
                        } finally {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    XLog.m137i("文件保存失败");
                    return;
                }
            }
            return;
        }
        if (ContextCompat.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE) != 0) {
            XLog.m137i("缺少写入外部存储权限");
            return;
        }
        if (StringsKt.startsWith$default(mimeType, "image/", false, 2, (Object) null) || StringsKt.startsWith$default(mimeType, "video/", false, 2, (Object) null)) {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(externalStoragePublicDirectory, appName);
            if (!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(file, sourceFile.getName());
            try {
                fileInputStream = new FileInputStream(sourceFile);
                try {
                    FileInputStream fileInputStream3 = fileInputStream;
                    fileInputStream = new FileOutputStream(file2);
                    try {
                        copyFile(fileInputStream3, fileInputStream);
                        Unit unit3 = Unit.INSTANCE;
                        CloseableKt.closeFinally(fileInputStream, null);
                        Unit unit4 = Unit.INSTANCE;
                        CloseableKt.closeFinally(fileInputStream, null);
                        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                    } finally {
                    }
                } finally {
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                XLog.m137i("文件保存失败");
            }
        } else {
            XLog.m137i("不支持的文件类型");
        }
    }

    private final void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int i = inputStream.read(bArr);
            if (i <= 0) {
                return;
            } else {
                outputStream.write(bArr, 0, i);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue
    java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because the return value of "jadx.core.dex.visitors.regions.SwitchOverStringVisitor$SwitchData.getNewCases()" is null
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.restoreSwitchOverString(SwitchOverStringVisitor.java:109)
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visitRegion(SwitchOverStringVisitor.java:66)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:77)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:82)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:31)
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visit(SwitchOverStringVisitor.java:60)
     */
    /* JADX WARN: Removed duplicated region for block: B:33:0x007c A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0088 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x008b A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final String getMimeType(File file) {
        String name = file.getName();
        Intrinsics.checkNotNull(name);
        String strSubstringAfterLast = StringsKt.substringAfterLast(name, FilenameUtils.EXTENSION_SEPARATOR, "");
        Locale ROOT = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue(ROOT, "ROOT");
        String lowerCase = strSubstringAfterLast.toLowerCase(ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        switch (lowerCase.hashCode()) {
            case 96980:
                if (lowerCase.equals("avi")) {
                    return "video/*";
                }
                return "*/*";
            case 97669:
                if (lowerCase.equals("bmp")) {
                    return "image/*";
                }
                break;
            case 102340:
                if (!lowerCase.equals("gif")) {
                }
                break;
            case 105441:
                if (!lowerCase.equals("jpg")) {
                }
                break;
            case 108184:
                if (!lowerCase.equals("mkv")) {
                }
                break;
            case 108273:
                if (!lowerCase.equals("mp4")) {
                }
                break;
            case 108308:
                if (!lowerCase.equals("mov")) {
                }
                break;
            case 110810:
                if (lowerCase.equals("pcm")) {
                    return "audio/*";
                }
                break;
            case 111145:
                if (!lowerCase.equals("png")) {
                }
                break;
            case 3268712:
                if (!lowerCase.equals("jpeg")) {
                }
                break;
        }
    }

    public final int calculatePCMPlaybackDuration(long totalBytes) {
        return MathKt.roundToInt((totalBytes / 32000) * 1000);
    }

    /* compiled from: AlbumDepository.kt */
    @Metadata(m606d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006\t"}, m607d2 = {"Lcom/glasssutdio/wear/depository/AlbumDepository$Companion;", "", "()V", "getInstance", "Lcom/glasssutdio/wear/depository/AlbumDepository;", "getGetInstance", "()Lcom/glasssutdio/wear/depository/AlbumDepository;", "getInstance$delegate", "Lkotlin/Lazy;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final AlbumDepository getGetInstance() {
            return (AlbumDepository) AlbumDepository.getInstance$delegate.getValue();
        }
    }
}
