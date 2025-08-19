package com.glasssutdio.wear.home.album;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.core.content.FileProvider;
import com.glasssutdio.wear.all.GlobalKt;
import com.glasssutdio.wear.all.ThreadExtKt;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.databinding.ActivityDebugBinding;
import com.glasssutdio.wear.manager.BaseSettingActivity;
import com.liulishuo.okdownload.OkDownloadProvider;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.p014io.ByteStreamsKt;
import kotlin.p014io.CloseableKt;
import kotlin.text.StringsKt;

/* compiled from: DebugActivity.kt */
@Metadata(m606d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\u0012\u0010\u0007\u001a\u00020\u00062\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\u001e\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0016\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000eJ \u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\u0018H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u0019"}, m607d2 = {"Lcom/glasssutdio/wear/home/album/DebugActivity;", "Lcom/glasssutdio/wear/manager/BaseSettingActivity;", "()V", "bindding", "Lcom/glasssutdio/wear/databinding/ActivityDebugBinding;", "initView", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "shareZipFile", "context", "Landroid/content/Context;", "zipFile", "Ljava/io/File;", "authority", "", "zipDirectory", "sourceDir", "zipFileRecursive", "zos", "Ljava/util/zip/ZipOutputStream;", "file", "basePathLength", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class DebugActivity extends BaseSettingActivity {
    private ActivityDebugBinding bindding;

    @Override // com.glasssutdio.wear.manager.BaseSettingActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        ActivityDebugBinding activityDebugBinding = null;
        EdgeToEdge.enable$default(this, null, null, 3, null);
        ActivityDebugBinding activityDebugBindingInflate = ActivityDebugBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(activityDebugBindingInflate, "inflate(...)");
        this.bindding = activityDebugBindingInflate;
        if (activityDebugBindingInflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bindding");
        } else {
            activityDebugBinding = activityDebugBindingInflate;
        }
        setContentView(activityDebugBinding.getRoot());
        initView();
    }

    private final void initView() {
        final ActivityDebugBinding activityDebugBinding = this.bindding;
        if (activityDebugBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bindding");
            activityDebugBinding = null;
        }
        activityDebugBinding.tvAppLog.setText("APP日志开关：" + UserConfig.INSTANCE.getInstance().getDebug());
        activityDebugBinding.tvLog.setText(String.valueOf(UserConfig.INSTANCE.getInstance().getGlassesLogs()));
        activityDebugBinding.tvLog.setText("眼镜日志开关：" + UserConfig.INSTANCE.getInstance().getGlassesLogs());
        activityDebugBinding.tvLog.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.album.DebugActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DebugActivity.initView$lambda$3$lambda$0(activityDebugBinding, view);
            }
        });
        activityDebugBinding.tvAppLog.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.album.DebugActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DebugActivity.initView$lambda$3$lambda$1(activityDebugBinding, view);
            }
        });
        activityDebugBinding.tvLogShare.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.album.DebugActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DebugActivity.initView$lambda$3$lambda$2(activityDebugBinding, this, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initView$lambda$3$lambda$0(ActivityDebugBinding this_run, View view) {
        Intrinsics.checkNotNullParameter(this_run, "$this_run");
        UserConfig.INSTANCE.getInstance().setGlassesLogs(!UserConfig.INSTANCE.getInstance().getGlassesLogs());
        GlobalKt.showToast$default("导入眼镜日志开关打开,一次有效", 0, 1, null);
        this_run.tvLog.setText("眼镜日志开关：" + UserConfig.INSTANCE.getInstance().getGlassesLogs());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initView$lambda$3$lambda$1(ActivityDebugBinding this_run, View view) {
        Intrinsics.checkNotNullParameter(this_run, "$this_run");
        UserConfig.INSTANCE.getInstance().setDebug(!UserConfig.INSTANCE.getInstance().getDebug());
        this_run.tvAppLog.setText("APP日志开关：" + UserConfig.INSTANCE.getInstance().getDebug());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initView$lambda$3$lambda$2(ActivityDebugBinding this_run, final DebugActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this_run, "$this_run");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        ThreadExtKt.ktxRunOnBgSingle(this_run, new Function1<ActivityDebugBinding, Unit>() { // from class: com.glasssutdio.wear.home.album.DebugActivity$initView$1$3$1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(ActivityDebugBinding activityDebugBinding) {
                invoke2(activityDebugBinding);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(ActivityDebugBinding ktxRunOnBgSingle) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
                File file = new File(GFileUtilKt.getLogDirFile().getAbsolutePath());
                File file2 = new File(OkDownloadProvider.context.getCacheDir(), "glasses_log.zip");
                this.this$0.zipDirectory(file, file2);
                DebugActivity debugActivity = this.this$0;
                Context context = OkDownloadProvider.context;
                Intrinsics.checkNotNullExpressionValue(context, "context");
                debugActivity.shareZipFile(context, file2, OkDownloadProvider.context.getPackageName() + ".provider");
            }
        });
    }

    public final void zipDirectory(File sourceDir, File zipFile) {
        Intrinsics.checkNotNullParameter(sourceDir, "sourceDir");
        Intrinsics.checkNotNullParameter(zipFile, "zipFile");
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            zipFileRecursive(zipOutputStream, sourceDir, sourceDir.getAbsolutePath().length() + 1);
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(zipOutputStream, null);
        } finally {
        }
    }

    public final void shareZipFile(Context context, File zipFile, String authority) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(zipFile, "zipFile");
        Intrinsics.checkNotNullParameter(authority, "authority");
        Uri uriForFile = FileProvider.getUriForFile(context, authority, zipFile);
        Intrinsics.checkNotNullExpressionValue(uriForFile, "getUriForFile(...)");
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("application/zip");
        intent.putExtra("android.intent.extra.STREAM", uriForFile);
        intent.addFlags(1);
        List<ResolveInfo> listQueryIntentActivities = getPackageManager().queryIntentActivities(intent, 65536);
        Intrinsics.checkNotNullExpressionValue(listQueryIntentActivities, "queryIntentActivities(...)");
        Iterator<ResolveInfo> it = listQueryIntentActivities.iterator();
        while (it.hasNext()) {
            grantUriPermission(it.next().activityInfo.packageName, uriForFile, 1);
        }
        startActivity(Intent.createChooser(intent, "Share zip file"));
    }

    private final void zipFileRecursive(ZipOutputStream zos, File file, int basePathLength) {
        if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles != null) {
                for (File file2 : fileArrListFiles) {
                    Intrinsics.checkNotNull(file2);
                    zipFileRecursive(zos, file2, basePathLength);
                }
                return;
            }
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            String absolutePath = file.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
            String strSubstring = absolutePath.substring(basePathLength);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            zos.putNextEntry(new ZipEntry(StringsKt.replace$default(strSubstring, "\\", "/", false, 4, (Object) null)));
            ByteStreamsKt.copyTo(fileInputStream, zos, 1024);
            zos.closeEntry();
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(fileInputStream, null);
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(fileInputStream, th);
                throw th2;
            }
        }
    }
}
