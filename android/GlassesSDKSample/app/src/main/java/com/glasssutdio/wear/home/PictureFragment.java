package com.glasssutdio.wear.home;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.C0775R;
import com.glasssutdio.wear.GlassesWearJavaApplication;
import com.glasssutdio.wear.all.GlobalKt;
import com.glasssutdio.wear.all.TextViewExtKt;
import com.glasssutdio.wear.all.ThreadExtKt;
import com.glasssutdio.wear.all.ViewKt;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.DateUtil;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.all.utils.GsonInstance;
import com.glasssutdio.wear.all.utils.PermissionUtilKt;
import com.glasssutdio.wear.all.utils.time.Interval;
import com.glasssutdio.wear.all.view.ConstraintRadioGroup;
import com.glasssutdio.wear.ble.connect.BindGuideActivity;
import com.glasssutdio.wear.ble.glass.thread.ThreadManager;
import com.glasssutdio.wear.bus.AlbumDownloadSuccessfullyEvent;
import com.glasssutdio.wear.bus.AlbumPageIndexEvent;
import com.glasssutdio.wear.bus.AlbumRefreshEvent;
import com.glasssutdio.wear.bus.BluetoothEvent;
import com.glasssutdio.wear.bus.BusEvent;
import com.glasssutdio.wear.bus.EventType;
import com.glasssutdio.wear.bus.RecordingToPcmSuccessfullyEvent;
import com.glasssutdio.wear.bus.VideoEisSuccessfullyEvent;
import com.glasssutdio.wear.database.entity.GlassAlbumEntity;
import com.glasssutdio.wear.databinding.FragmentPictureBinding;
import com.glasssutdio.wear.depository.AlbumDepository;
import com.glasssutdio.wear.home.PictureFragment;
import com.glasssutdio.wear.home.activity.AlbumSettingActivity;
import com.glasssutdio.wear.home.adapter.PagerGuideAdapter;
import com.glasssutdio.wear.home.album.DebugActivity;
import com.glasssutdio.wear.home.album.EditAlbumActivity;
import com.glasssutdio.wear.home.album.p005vm.AlbumListViewModel;
import com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton;
import com.google.android.gms.location.DeviceOrientationRequest;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.FilenameUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.IOUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.oudmon.ble.base.bluetooth.BleOperateManager;
import com.oudmon.ble.base.communication.ILargeDataResponse;
import com.oudmon.ble.base.communication.LargeDataHandler;
import com.oudmon.ble.base.communication.bigData.resp.BaseResponse;
import com.oudmon.ble.base.communication.bigData.resp.BatteryResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassModelControlResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyListener;
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyRsp;
import com.oudmon.ble.base.communication.utils.ByteUtil;
import com.oudmon.qc_utils.date.LanguageUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.koin.androidx.viewmodel.ext.android.LifecycleOwnerExtKt;
import org.koin.core.qualifier.Qualifier;

/* compiled from: PictureFragment.kt */
@Metadata(m606d1 = {"\u0000¸\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0016\u0018\u0000 v2\u00020\u00012\u00020\u00022\u00020\u0003:\u0005vwxyzB\u0005¢\u0006\u0002\u0010\u0004J\b\u00100\u001a\u000201H\u0016J\u0010\u00102\u001a\u0002012\u0006\u00103\u001a\u00020\u0011H\u0016J\b\u00104\u001a\u000201H\u0016J\b\u00105\u001a\u000201H\u0002J\u0018\u00106\u001a\u0002012\u0006\u00107\u001a\u00020\u00132\u0006\u00108\u001a\u00020\u0013H\u0016J \u00109\u001a\u0002012\u0006\u00107\u001a\u00020\u00132\u0006\u0010:\u001a\u00020\u00132\u0006\u0010;\u001a\u00020\u0013H\u0016J\u0018\u0010<\u001a\u0002012\u0006\u0010=\u001a\u00020\u00112\u0006\u0010>\u001a\u00020\u0011H\u0016J\b\u0010?\u001a\u000201H\u0016J\u0018\u0010@\u001a\u0002012\u0006\u0010A\u001a\u00020\u00112\u0006\u0010B\u001a\u00020\u0011H\u0016J\u0018\u0010C\u001a\u0002012\u0006\u00107\u001a\u00020\u00132\u0006\u0010D\u001a\u00020\u0011H\u0016J\u0010\u0010E\u001a\u0002012\u0006\u0010F\u001a\u00020GH\u0016J\u000e\u0010H\u001a\u0002012\u0006\u0010I\u001a\u00020\u000eJ\b\u0010J\u001a\u000201H\u0003J\b\u0010K\u001a\u000201H\u0002J\b\u0010L\u001a\u000201H\u0002J\b\u0010M\u001a\u000201H\u0016J\u0010\u0010N\u001a\u0002012\u0006\u00103\u001a\u00020\u0011H\u0016J\b\u0010O\u001a\u000201H\u0016J\u0012\u0010P\u001a\u0002012\b\u0010Q\u001a\u0004\u0018\u00010RH\u0016J$\u0010S\u001a\u00020T2\u0006\u0010U\u001a\u00020V2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010Y\u001a\u0004\u0018\u00010ZH\u0016J\b\u0010[\u001a\u000201H\u0016J\b\u0010\\\u001a\u000201H\u0016J\u0010\u0010]\u001a\u0002012\u0006\u0010^\u001a\u00020_H\u0007J\u0010\u0010`\u001a\u0002012\u0006\u00103\u001a\u00020\u0011H\u0016J\b\u0010a\u001a\u000201H\u0016J\u0016\u0010b\u001a\u0002012\f\u0010c\u001a\b\u0012\u0004\u0012\u00020e0dH\u0016J\b\u0010f\u001a\u000201H\u0016J\u0012\u0010g\u001a\u0002012\b\u0010h\u001a\u0004\u0018\u00010eH\u0016J\b\u0010i\u001a\u000201H\u0016J\b\u0010j\u001a\u000201H\u0016J\u0006\u0010k\u001a\u000201J\b\u0010l\u001a\u000201H\u0002J \u0010m\u001a\u0002012\u0006\u00107\u001a\u00020\u00132\u0006\u00108\u001a\u00020\u00132\u0006\u0010n\u001a\u00020\u0011H\u0016J\u0018\u0010o\u001a\u0002012\u0006\u00107\u001a\u00020\u00132\u0006\u0010;\u001a\u00020\u0013H\u0016J\b\u0010p\u001a\u000201H\u0016J\b\u0010q\u001a\u000201H\u0002J\b\u0010r\u001a\u000201H\u0002J\u0010\u0010s\u001a\u0002012\u0006\u0010t\u001a\u00020\u0011H\u0002J\u0010\u0010u\u001a\u0002012\u0006\u0010u\u001a\u00020\u0013H\u0016R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00060\u0016R\u00020\u0000X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0011X\u0082D¢\u0006\u0002\n\u0000R\u0012\u0010!\u001a\u00060\"R\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082.¢\u0006\u0002\n\u0000R(\u0010%\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u0013 (*\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010'0'0&X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00130'X\u0082\u0004¢\u0006\u0004\n\u0002\u0010*R\u000e\u0010+\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010,\u001a\u00060-R\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020/X\u0082.¢\u0006\u0002\n\u0000¨\u0006{"}, m607d2 = {"Lcom/glasssutdio/wear/home/PictureFragment;", "Lcom/glasssutdio/wear/home/BaseFragment;", "Lcom/glasssutdio/wear/depository/AlbumDepository$WifiFilesDownloadListener;", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$WifiP2pCallback;", "()V", "albumViewModel", "Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel;", "getAlbumViewModel", "()Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel;", "albumViewModel$delegate", "Lkotlin/Lazy;", "binding", "Lcom/glasssutdio/wear/databinding/FragmentPictureBinding;", "bleCallbackSuccess", "", "charging", "click_title_times", "", "configFileName", "", "currTabIndex", "deviceNotifyListener", "Lcom/glasssutdio/wear/home/PictureFragment$MyDeviceNotifyListener;", "failRetry", "handler", "Landroid/os/Handler;", "importing", "interval", "Lcom/glasssutdio/wear/all/utils/time/Interval;", "isP2PConnecting", "is_title_clicked", "logFileName", "max_click_times", "p2pConnectFailRunnable", "Lcom/glasssutdio/wear/home/PictureFragment$P2pConnectFailRunnable;", "pagerAdapter", "Lcom/glasssutdio/wear/home/adapter/PagerGuideAdapter;", "requestPermissionLaunch", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "requestedPermissions", "[Ljava/lang/String;", "systemSuccess", "timeoutRunnable", "Lcom/glasssutdio/wear/home/PictureFragment$MyRunnable;", "wifiManager", "Landroid/net/wifi/WifiManager;", "cancelConnect", "", "cancelConnectFail", "reason", "connecting", "downloadMediaConfig", "eisEnd", "fileName", "filePath", "eisError", "sourcePath", "errorInfo", "fileCount", "index", "total", "fileDownloadComplete", "fileDownloadError", "fileType", "errorType", "fileProgress", "progress", "fileWasDownloadSuccessfully", "entity", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "imageEdit", "enable", "importAlbum", "initDevice", "initHeartBeat", "loadDataData", "onConnectRequestFailed", "onConnectRequestSent", "onConnected", "info", "Landroid/net/wifi/p2p/WifiP2pInfo;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onDisconnected", "onMessageEvent", "messageEvent", "Lcom/glasssutdio/wear/bus/BusEvent;", "onPeerDiscoveryFailed", "onPeerDiscoveryStarted", "onPeersChanged", "peers", "", "Landroid/net/wifi/p2p/WifiP2pDevice;", "onResume", "onThisDeviceChanged", "device", "onWifiP2pDisabled", "onWifiP2pEnabled", "readAlbumCounts", "readDeviceBattery", "recordingToPcm", TypedValues.TransitionType.S_DURATION, "recordingToPcmError", "retryAlsoFailed", "tvImportBtnReset", "tvImportBtnRetry", "updateIndicator", "position", "wifiSpeed", "Companion", "LocationPermissionCallback", "MyDeviceNotifyListener", "MyRunnable", "P2pConnectFailRunnable", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class PictureFragment extends BaseFragment implements AlbumDepository.WifiFilesDownloadListener, WifiP2pManagerSingleton.WifiP2pCallback {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);

    /* renamed from: albumViewModel$delegate, reason: from kotlin metadata */
    private final Lazy albumViewModel;
    private FragmentPictureBinding binding;
    private boolean bleCallbackSuccess;
    private boolean charging;
    private int click_title_times;
    private String configFileName;
    private int currTabIndex;
    private MyDeviceNotifyListener deviceNotifyListener;
    private int failRetry;
    private final Handler handler;
    private boolean importing;
    private Interval interval;
    private boolean isP2PConnecting;
    private boolean is_title_clicked;
    private String logFileName;
    private final int max_click_times;
    private P2pConnectFailRunnable p2pConnectFailRunnable;
    private PagerGuideAdapter pagerAdapter;
    private final ActivityResultLauncher<String[]> requestPermissionLaunch;
    private final String[] requestedPermissions;
    private boolean systemSuccess;
    private MyRunnable timeoutRunnable;
    private WifiManager wifiManager;

    /* JADX INFO: Access modifiers changed from: private */
    public static final void fileDownloadComplete$lambda$15(int i, GlassModelControlResponse glassModelControlResponse) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$11$lambda$4(View view) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$11$lambda$5(View view) {
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void connecting() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public PictureFragment() {
        final PictureFragment pictureFragment = this;
        final Qualifier qualifier = null;
        final Object[] objArr = 0 == true ? 1 : 0;
        this.albumViewModel = LazyKt.lazy(new Function0<AlbumListViewModel>() { // from class: com.glasssutdio.wear.home.PictureFragment$special$$inlined$viewModel$default$1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            /* JADX WARN: Type inference failed for: r0v1, types: [androidx.lifecycle.ViewModel, com.glasssutdio.wear.home.album.vm.AlbumListViewModel] */
            @Override // kotlin.jvm.functions.Function0
            public final AlbumListViewModel invoke() {
                return LifecycleOwnerExtKt.getViewModel(pictureFragment, Reflection.getOrCreateKotlinClass(AlbumListViewModel.class), qualifier, objArr);
            }
        });
        this.configFileName = "media.config";
        this.logFileName = "log.list";
        this.max_click_times = 5;
        List listCreateListBuilder = CollectionsKt.createListBuilder();
        listCreateListBuilder.add("android.permission.INTERNET");
        listCreateListBuilder.add("android.permission.ACCESS_WIFI_STATE");
        listCreateListBuilder.add("android.permission.CHANGE_WIFI_STATE");
        listCreateListBuilder.add("android.permission.ACCESS_NETWORK_STATE");
        listCreateListBuilder.add("android.permission.CHANGE_NETWORK_STATE");
        if (Build.VERSION.SDK_INT >= 33) {
            listCreateListBuilder.add(Permission.NEARBY_WIFI_DEVICES);
        } else {
            listCreateListBuilder.add(Permission.ACCESS_COARSE_LOCATION);
            listCreateListBuilder.add(Permission.ACCESS_FINE_LOCATION);
        }
        this.requestedPermissions = (String[]) CollectionsKt.build(listCreateListBuilder).toArray(new String[0]);
        ActivityResultLauncher<String[]> activityResultLauncherRegisterForActivityResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda2
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                PictureFragment.requestPermissionLaunch$lambda$2(this.f$0, (Map) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(activityResultLauncherRegisterForActivityResult, "registerForActivityResult(...)");
        this.requestPermissionLaunch = activityResultLauncherRegisterForActivityResult;
        this.p2pConnectFailRunnable = new P2pConnectFailRunnable();
        this.timeoutRunnable = new MyRunnable();
        final Looper mainLooper = Looper.getMainLooper();
        this.handler = new Handler(mainLooper) { // from class: com.glasssutdio.wear.home.PictureFragment$handler$1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                Intrinsics.checkNotNullParameter(msg, "msg");
                super.handleMessage(msg);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final AlbumListViewModel getAlbumViewModel() {
        return (AlbumListViewModel) this.albumViewModel.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void downloadMediaConfig() {
        if (GlassesWearJavaApplication.getInstance().isOtaUpgrading()) {
            XLog.m137i("正在OTA...");
            return;
        }
        if (this.systemSuccess && this.bleCallbackSuccess) {
            if (UserConfig.INSTANCE.getInstance().getDebug()) {
                ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.downloadMediaConfig.1
                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                        invoke2(pictureFragment);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(PictureFragment ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        GlobalKt.showToast$default("连接成功,开始下载", 0, 1, null);
                    }
                });
            }
            Interval interval = this.interval;
            if (interval != null) {
                interval.cancel();
            }
            WifiP2pManagerSingleton.INSTANCE.getInstance().setConnect(true);
            this.importing = true;
            this.failRetry = 0;
            final String glassDeviceWifiIP = UserConfig.INSTANCE.getInstance().getGlassDeviceWifiIP();
            this.handler.removeCallbacks(this.p2pConnectFailRunnable);
            this.systemSuccess = false;
            this.bleCallbackSuccess = false;
            ThreadExtKt.ktxRunOnUiDelay(this, 1000L, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.downloadMediaConfig.2
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                    invoke2(pictureFragment);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(PictureFragment ktxRunOnUiDelay) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUiDelay, "$this$ktxRunOnUiDelay");
                    if (UserConfig.INSTANCE.getInstance().getGlassesLogs()) {
                        AlbumListViewModel albumViewModel = ktxRunOnUiDelay.getAlbumViewModel();
                        String str = "http://" + glassDeviceWifiIP + "/files/log/" + ktxRunOnUiDelay.logFileName;
                        String absolutePath = GFileUtilKt.getDCIMFile().getAbsolutePath();
                        Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
                        albumViewModel.getPhotoTextFile(str, absolutePath, ktxRunOnUiDelay.logFileName);
                        return;
                    }
                    AlbumListViewModel albumViewModel2 = ktxRunOnUiDelay.getAlbumViewModel();
                    String str2 = "http://" + glassDeviceWifiIP + "/files/" + ktxRunOnUiDelay.configFileName;
                    String absolutePath2 = GFileUtilKt.getDCIMFile().getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath2, "getAbsolutePath(...)");
                    albumViewModel2.getPhotoTextFile(str2, absolutePath2, ktxRunOnUiDelay.configFileName);
                }
            });
            return;
        }
        XLog.m137i("systemSuccess:" + this.systemSuccess + ",bleCallbackSuccess:" + this.bleCallbackSuccess);
    }

    @Override // com.glasssutdio.wear.home.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intrinsics.checkNotNullParameter(inflater, "inflater");
        FragmentPictureBinding fragmentPictureBindingInflate = FragmentPictureBinding.inflate(inflater, container, false);
        Intrinsics.checkNotNullExpressionValue(fragmentPictureBindingInflate, "inflate(...)");
        this.binding = fragmentPictureBindingInflate;
        if (fragmentPictureBindingInflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBindingInflate = null;
        }
        ConstraintLayout root = fragmentPictureBindingInflate.getRoot();
        Intrinsics.checkNotNullExpressionValue(root, "getRoot(...)");
        return root;
    }

    private final void initDevice() {
        WifiP2pManagerSingleton.INSTANCE.getInstance().addCallback(this);
    }

    private final void initHeartBeat() {
        Interval interval = this.interval;
        if (interval == null) {
            Interval intervalLife$default = Interval.life$default(new Interval(-1L, 20L, TimeUnit.SECONDS, 0L, 0L, 16, null), (Fragment) this, (Lifecycle.Event) null, 2, (Object) null);
            this.interval = intervalLife$default;
            if (intervalLife$default != null) {
                intervalLife$default.subscribe(new Function2<Interval, Long, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$initHeartBeat$1$1
                    {
                        super(2);
                    }

                    @Override // kotlin.jvm.functions.Function2
                    public /* bridge */ /* synthetic */ Unit invoke(Interval interval2, Long l) {
                        invoke(interval2, l.longValue());
                        return Unit.INSTANCE;
                    }

                    public final void invoke(Interval subscribe, long j) {
                        Intrinsics.checkNotNullParameter(subscribe, "$this$subscribe");
                        if (!BleOperateManager.getInstance().isConnected()) {
                            Interval interval2 = this.this$0.interval;
                            if (interval2 != null) {
                                interval2.cancel();
                                return;
                            }
                            return;
                        }
                        LargeDataHandler.getInstance().syncHeartBeat(4);
                    }
                }).finish(new Function2<Interval, Long, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$initHeartBeat$1$2
                    public final void invoke(Interval finish, long j) {
                        Intrinsics.checkNotNullParameter(finish, "$this$finish");
                    }

                    @Override // kotlin.jvm.functions.Function2
                    public /* bridge */ /* synthetic */ Unit invoke(Interval interval2, Long l) {
                        invoke(interval2, l.longValue());
                        return Unit.INSTANCE;
                    }
                });
            }
            Interval interval2 = this.interval;
            if (interval2 != null) {
                interval2.start();
                return;
            }
            return;
        }
        if (interval != null) {
            interval.reset();
        }
        Interval interval3 = this.interval;
        if (interval3 != null) {
            interval3.start();
        }
    }

    @Override // com.glasssutdio.wear.home.BaseFragment
    public void loadDataData() {
        super.loadDataData();
        Object systemService = getActivity().getSystemService("wifi");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.WifiManager");
        this.wifiManager = (WifiManager) systemService;
        initDevice();
        Activity activity = getActivity();
        Intrinsics.checkNotNull(activity, "null cannot be cast to non-null type androidx.fragment.app.FragmentActivity");
        this.pagerAdapter = new PagerGuideAdapter((FragmentActivity) activity);
        final FragmentPictureBinding fragmentPictureBinding = this.binding;
        FragmentPictureBinding fragmentPictureBinding2 = null;
        if (fragmentPictureBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding = null;
        }
        fragmentPictureBinding.noBindDevice.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PictureFragment.loadDataData$lambda$11$lambda$4(view);
            }
        });
        ConstraintLayout titleBar = fragmentPictureBinding.titleBar;
        Intrinsics.checkNotNullExpressionValue(titleBar, "titleBar");
        ViewKt.statusMargin$default(titleBar, false, 0, 3, null);
        TextView tvTitle = fragmentPictureBinding.tvTitle;
        Intrinsics.checkNotNullExpressionValue(tvTitle, "tvTitle");
        TextViewExtKt.setupMarquee(tvTitle);
        TextView tvTitle2 = fragmentPictureBinding.tvTitle2;
        Intrinsics.checkNotNullExpressionValue(tvTitle2, "tvTitle2");
        TextViewExtKt.setupMarquee(tvTitle2);
        TextView tvTitle22 = fragmentPictureBinding.tvTitle2;
        Intrinsics.checkNotNullExpressionValue(tvTitle22, "tvTitle2");
        ViewKt.statusMargin$default(tvTitle22, false, 0, 3, null);
        TextView tvImportBtn = fragmentPictureBinding.tvImportBtn;
        Intrinsics.checkNotNullExpressionValue(tvImportBtn, "tvImportBtn");
        TextViewExtKt.setupMarqueeWithClick(tvImportBtn);
        ViewPager2 viewPager2 = fragmentPictureBinding.viewPager1;
        PagerGuideAdapter pagerGuideAdapter = this.pagerAdapter;
        if (pagerGuideAdapter == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pagerAdapter");
            pagerGuideAdapter = null;
        }
        viewPager2.setAdapter(pagerGuideAdapter);
        fragmentPictureBinding.viewPager1.setOffscreenPageLimit(5);
        fragmentPictureBinding.glassStatus1.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PictureFragment.loadDataData$lambda$11$lambda$5(view);
            }
        });
        fragmentPictureBinding.tvImportBtn.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PictureFragment.loadDataData$lambda$11$lambda$6(this.f$0, view);
            }
        });
        fragmentPictureBinding.tvToBind.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) throws NoSuchMethodException, SecurityException {
                PictureFragment.loadDataData$lambda$11$lambda$7(this.f$0, view);
            }
        });
        fragmentPictureBinding.imageSetting.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PictureFragment.loadDataData$lambda$11$lambda$8(this.f$0, view);
            }
        });
        fragmentPictureBinding.viewPager1.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.glasssutdio.wear.home.PictureFragment$loadDataData$1$6
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                this.this$0.updateIndicator(position);
            }
        });
        fragmentPictureBinding.diyRadioGroup.setCheckedChangeListener(new ConstraintRadioGroup.OnCheckedChangeListener() { // from class: com.glasssutdio.wear.home.PictureFragment$loadDataData$1$7
            @Override // com.glasssutdio.wear.all.view.ConstraintRadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(ConstraintRadioGroup group, CompoundButton checkedButton) {
                Intrinsics.checkNotNullParameter(group, "group");
                Intrinsics.checkNotNullParameter(checkedButton, "checkedButton");
                int id = checkedButton.getId();
                if (id == C0775R.id.rb_1) {
                    this.this$0.currTabIndex = 0;
                    fragmentPictureBinding.viewPager1.setCurrentItem(0);
                    return;
                }
                if (id == C0775R.id.rb_2) {
                    this.this$0.currTabIndex = 1;
                    fragmentPictureBinding.viewPager1.setCurrentItem(1);
                    return;
                }
                if (id == C0775R.id.rb_3) {
                    this.this$0.currTabIndex = 2;
                    fragmentPictureBinding.viewPager1.setCurrentItem(2);
                } else if (id == C0775R.id.rb_5) {
                    this.this$0.currTabIndex = 3;
                    fragmentPictureBinding.viewPager1.setCurrentItem(3);
                } else if (id == C0775R.id.rb_4) {
                    this.this$0.currTabIndex = 4;
                    fragmentPictureBinding.viewPager1.setCurrentItem(4);
                }
            }
        });
        fragmentPictureBinding.imageEdit.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PictureFragment.loadDataData$lambda$11$lambda$10(this.f$0, view);
            }
        });
        readAlbumCounts();
        getAlbumViewModel().getAlbumDepository().setWifiDownloadListener(this);
        FragmentPictureBinding fragmentPictureBinding3 = this.binding;
        if (fragmentPictureBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            fragmentPictureBinding2 = fragmentPictureBinding3;
        }
        fragmentPictureBinding2.tvTitle.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PictureFragment.loadDataData$lambda$12(this.f$0, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$11$lambda$6(PictureFragment this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        XLog.m137i("import click");
        if (!BleOperateManager.getInstance().isConnected()) {
            String string = this$0.getString(C0775R.string.ble_glass_18);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            GlobalKt.showToast$default(string, 0, 1, null);
            return;
        }
        if (UserConfig.INSTANCE.getInstance().getBattery() <= 15 && !this$0.charging) {
            String string2 = this$0.getString(C0775R.string.album_glass_41);
            Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
            GlobalKt.showToast$default(string2, 0, 1, null);
            return;
        }
        WifiManager wifiManager = this$0.wifiManager;
        if (wifiManager == null) {
            Intrinsics.throwUninitializedPropertyAccessException("wifiManager");
            wifiManager = null;
        }
        if (!wifiManager.isWifiEnabled()) {
            this$0.startActivity(new Intent("android.net.wifi.PICK_WIFI_NETWORK"));
            String string3 = this$0.getString(C0775R.string.album_glass_35);
            Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
            GlobalKt.showToast$default(string3, 0, 1, null);
            return;
        }
        this$0.requestPermissionLaunch.launch(this$0.requestedPermissions);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$11$lambda$7(PictureFragment this$0, View view) throws NoSuchMethodException, SecurityException {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Activity activity = this$0.getActivity();
        Intrinsics.checkNotNull(activity, "null cannot be cast to non-null type androidx.fragment.app.FragmentActivity");
        PermissionUtilKt.requestLocationPermission((FragmentActivity) activity, this$0.new LocationPermissionCallback());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$11$lambda$8(PictureFragment this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        PictureFragment pictureFragment = this$0;
        FragmentActivity activity = pictureFragment.getActivity();
        if (activity != null) {
            ArrayList<Pair> arrayList = new ArrayList();
            Intrinsics.checkNotNull(activity);
            Intent intent = new Intent(activity, (Class<?>) AlbumSettingActivity.class);
            for (Pair pair : arrayList) {
                if (pair != null) {
                    String str = (String) pair.getFirst();
                    Object second = pair.getSecond();
                    if (second instanceof Integer) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).intValue()), "putExtra(...)");
                    } else if (second instanceof Byte) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).byteValue()), "putExtra(...)");
                    } else if (second instanceof Character) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Character) second).charValue()), "putExtra(...)");
                    } else if (second instanceof Short) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).shortValue()), "putExtra(...)");
                    } else if (second instanceof Boolean) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Boolean) second).booleanValue()), "putExtra(...)");
                    } else if (second instanceof Long) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).longValue()), "putExtra(...)");
                    } else if (second instanceof Float) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).floatValue()), "putExtra(...)");
                    } else if (second instanceof Double) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).doubleValue()), "putExtra(...)");
                    } else if (second instanceof String) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (String) second), "putExtra(...)");
                    } else if (second instanceof CharSequence) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (CharSequence) second), "putExtra(...)");
                    } else if (second instanceof Parcelable) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                    } else if (second instanceof Object[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                    } else if (second instanceof ArrayList) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                    } else if (second instanceof Serializable) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                    } else if (second instanceof boolean[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (boolean[]) second), "putExtra(...)");
                    } else if (second instanceof byte[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (byte[]) second), "putExtra(...)");
                    } else if (second instanceof short[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (short[]) second), "putExtra(...)");
                    } else if (second instanceof char[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (char[]) second), "putExtra(...)");
                    } else if (second instanceof int[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (int[]) second), "putExtra(...)");
                    } else if (second instanceof long[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (long[]) second), "putExtra(...)");
                    } else if (second instanceof float[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (float[]) second), "putExtra(...)");
                    } else if (second instanceof double[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (double[]) second), "putExtra(...)");
                    } else if (second instanceof Bundle) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Bundle) second), "putExtra(...)");
                    } else if (second instanceof Intent) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                    } else {
                        Unit unit = Unit.INSTANCE;
                    }
                }
            }
            pictureFragment.startActivity(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$11$lambda$10(PictureFragment this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Bundle bundle = new Bundle();
        bundle.putInt("input_type", this$0.currTabIndex);
        PictureFragment pictureFragment = this$0;
        FragmentActivity activity = pictureFragment.getActivity();
        if (activity != null) {
            ArrayList<Pair> arrayList = new ArrayList();
            Intrinsics.checkNotNull(activity);
            Intent intent = new Intent(activity, (Class<?>) EditAlbumActivity.class);
            intent.setFlags(1);
            intent.putExtras(bundle);
            for (Pair pair : arrayList) {
                if (pair != null) {
                    String str = (String) pair.getFirst();
                    Object second = pair.getSecond();
                    if (second instanceof Integer) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).intValue()), "putExtra(...)");
                    } else if (second instanceof Byte) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).byteValue()), "putExtra(...)");
                    } else if (second instanceof Character) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Character) second).charValue()), "putExtra(...)");
                    } else if (second instanceof Short) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).shortValue()), "putExtra(...)");
                    } else if (second instanceof Boolean) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Boolean) second).booleanValue()), "putExtra(...)");
                    } else if (second instanceof Long) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).longValue()), "putExtra(...)");
                    } else if (second instanceof Float) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).floatValue()), "putExtra(...)");
                    } else if (second instanceof Double) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).doubleValue()), "putExtra(...)");
                    } else if (second instanceof String) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (String) second), "putExtra(...)");
                    } else if (second instanceof CharSequence) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (CharSequence) second), "putExtra(...)");
                    } else if (second instanceof Parcelable) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                    } else if (second instanceof Object[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                    } else if (second instanceof ArrayList) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                    } else if (second instanceof Serializable) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                    } else if (second instanceof boolean[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (boolean[]) second), "putExtra(...)");
                    } else if (second instanceof byte[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (byte[]) second), "putExtra(...)");
                    } else if (second instanceof short[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (short[]) second), "putExtra(...)");
                    } else if (second instanceof char[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (char[]) second), "putExtra(...)");
                    } else if (second instanceof int[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (int[]) second), "putExtra(...)");
                    } else if (second instanceof long[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (long[]) second), "putExtra(...)");
                    } else if (second instanceof float[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (float[]) second), "putExtra(...)");
                    } else if (second instanceof double[]) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (double[]) second), "putExtra(...)");
                    } else if (second instanceof Bundle) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Bundle) second), "putExtra(...)");
                    } else if (second instanceof Intent) {
                        Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                    } else {
                        Unit unit = Unit.INSTANCE;
                    }
                }
            }
            pictureFragment.startActivity(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadDataData$lambda$12(final PictureFragment this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (!this$0.is_title_clicked) {
            this$0.is_title_clicked = true;
            this$0.click_title_times = 1;
            new Timer().schedule(new TimerTask() { // from class: com.glasssutdio.wear.home.PictureFragment$loadDataData$2$1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    this.this$0.is_title_clicked = false;
                    this.this$0.click_title_times = 0;
                }
            }, 2000L);
            return;
        }
        int i = this$0.click_title_times + 1;
        this$0.click_title_times = i;
        if (i == this$0.max_click_times) {
            this$0.is_title_clicked = false;
            this$0.click_title_times = 0;
            GlobalKt.showToast$default("It's not a bug, it's a feature", 0, 1, null);
            PictureFragment pictureFragment = this$0;
            FragmentActivity activity = pictureFragment.getActivity();
            if (activity != null) {
                ArrayList<Pair> arrayList = new ArrayList();
                Intrinsics.checkNotNull(activity);
                Intent intent = new Intent(activity, (Class<?>) DebugActivity.class);
                for (Pair pair : arrayList) {
                    if (pair != null) {
                        String str = (String) pair.getFirst();
                        Object second = pair.getSecond();
                        if (second instanceof Integer) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).intValue()), "putExtra(...)");
                        } else if (second instanceof Byte) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).byteValue()), "putExtra(...)");
                        } else if (second instanceof Character) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Character) second).charValue()), "putExtra(...)");
                        } else if (second instanceof Short) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).shortValue()), "putExtra(...)");
                        } else if (second instanceof Boolean) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Boolean) second).booleanValue()), "putExtra(...)");
                        } else if (second instanceof Long) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).longValue()), "putExtra(...)");
                        } else if (second instanceof Float) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).floatValue()), "putExtra(...)");
                        } else if (second instanceof Double) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).doubleValue()), "putExtra(...)");
                        } else if (second instanceof String) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (String) second), "putExtra(...)");
                        } else if (second instanceof CharSequence) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (CharSequence) second), "putExtra(...)");
                        } else if (second instanceof Parcelable) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                        } else if (second instanceof Object[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                        } else if (second instanceof ArrayList) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                        } else if (second instanceof Serializable) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                        } else if (second instanceof boolean[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (boolean[]) second), "putExtra(...)");
                        } else if (second instanceof byte[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (byte[]) second), "putExtra(...)");
                        } else if (second instanceof short[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (short[]) second), "putExtra(...)");
                        } else if (second instanceof char[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (char[]) second), "putExtra(...)");
                        } else if (second instanceof int[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (int[]) second), "putExtra(...)");
                        } else if (second instanceof long[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (long[]) second), "putExtra(...)");
                        } else if (second instanceof float[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (float[]) second), "putExtra(...)");
                        } else if (second instanceof double[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (double[]) second), "putExtra(...)");
                        } else if (second instanceof Bundle) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Bundle) second), "putExtra(...)");
                        } else if (second instanceof Intent) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                        } else {
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                }
                pictureFragment.startActivity(intent);
            }
        }
    }

    /* compiled from: PictureFragment.kt */
    @Metadata(m606d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0016J\u001e\u0010\n\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u000b\u001a\u00020\tH\u0016¨\u0006\f"}, m607d2 = {"Lcom/glasssutdio/wear/home/PictureFragment$LocationPermissionCallback;", "Lcom/hjq/permissions/OnPermissionCallback;", "(Lcom/glasssutdio/wear/home/PictureFragment;)V", "onDenied", "", "permissions", "", "", "never", "", "onGranted", "all", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class LocationPermissionCallback implements OnPermissionCallback {
        public LocationPermissionCallback() {
        }

        @Override // com.hjq.permissions.OnPermissionCallback
        public void onGranted(List<String> permissions, boolean all) {
            Intrinsics.checkNotNullParameter(permissions, "permissions");
            XLog.m136i(permissions);
            PictureFragment pictureFragment = PictureFragment.this;
            FragmentActivity activity = pictureFragment.getActivity();
            if (activity != null) {
                ArrayList<Pair> arrayList = new ArrayList();
                Intrinsics.checkNotNull(activity);
                Intent intent = new Intent(activity, (Class<?>) BindGuideActivity.class);
                for (Pair pair : arrayList) {
                    if (pair != null) {
                        String str = (String) pair.getFirst();
                        Object second = pair.getSecond();
                        if (second instanceof Integer) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).intValue()), "putExtra(...)");
                        } else if (second instanceof Byte) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).byteValue()), "putExtra(...)");
                        } else if (second instanceof Character) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Character) second).charValue()), "putExtra(...)");
                        } else if (second instanceof Short) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).shortValue()), "putExtra(...)");
                        } else if (second instanceof Boolean) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Boolean) second).booleanValue()), "putExtra(...)");
                        } else if (second instanceof Long) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).longValue()), "putExtra(...)");
                        } else if (second instanceof Float) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).floatValue()), "putExtra(...)");
                        } else if (second instanceof Double) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).doubleValue()), "putExtra(...)");
                        } else if (second instanceof String) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (String) second), "putExtra(...)");
                        } else if (second instanceof CharSequence) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (CharSequence) second), "putExtra(...)");
                        } else if (second instanceof Parcelable) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                        } else if (second instanceof Object[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                        } else if (second instanceof ArrayList) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                        } else if (second instanceof Serializable) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                        } else if (second instanceof boolean[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (boolean[]) second), "putExtra(...)");
                        } else if (second instanceof byte[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (byte[]) second), "putExtra(...)");
                        } else if (second instanceof short[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (short[]) second), "putExtra(...)");
                        } else if (second instanceof char[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (char[]) second), "putExtra(...)");
                        } else if (second instanceof int[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (int[]) second), "putExtra(...)");
                        } else if (second instanceof long[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (long[]) second), "putExtra(...)");
                        } else if (second instanceof float[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (float[]) second), "putExtra(...)");
                        } else if (second instanceof double[]) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (double[]) second), "putExtra(...)");
                        } else if (second instanceof Bundle) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Bundle) second), "putExtra(...)");
                        } else if (second instanceof Intent) {
                            Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                        } else {
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                }
                pictureFragment.startActivity(intent);
            }
        }

        @Override // com.hjq.permissions.OnPermissionCallback
        public void onDenied(List<String> permissions, boolean never) {
            Intrinsics.checkNotNullParameter(permissions, "permissions");
            super.onDenied(permissions, never);
            String string = PictureFragment.this.getString(C0775R.string.ble_glass_20);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            GlobalKt.showToast$default(string, 0, 1, null);
            XLog.m136i(permissions);
            XLog.m136i(Boolean.valueOf(never));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public final void onMessageEvent(BusEvent messageEvent) {
        Intrinsics.checkNotNullParameter(messageEvent, "messageEvent");
        if (messageEvent instanceof BluetoothEvent) {
            FragmentPictureBinding fragmentPictureBinding = null;
            if (!((BluetoothEvent) messageEvent).getConnect()) {
                if (!UserConfig.INSTANCE.getInstance().getDeviceBind()) {
                    FragmentPictureBinding fragmentPictureBinding2 = this.binding;
                    if (fragmentPictureBinding2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        fragmentPictureBinding2 = null;
                    }
                    ViewKt.visible(fragmentPictureBinding2.noBindDevice);
                    if (UserConfig.INSTANCE.getInstance().getDeviceAddress().length() == 0) {
                        FragmentPictureBinding fragmentPictureBinding3 = this.binding;
                        if (fragmentPictureBinding3 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                        } else {
                            fragmentPictureBinding = fragmentPictureBinding3;
                        }
                        ViewKt.visible(fragmentPictureBinding.noBindDevice);
                        return;
                    }
                    FragmentPictureBinding fragmentPictureBinding4 = this.binding;
                    if (fragmentPictureBinding4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                    } else {
                        fragmentPictureBinding = fragmentPictureBinding4;
                    }
                    ViewKt.gone(fragmentPictureBinding.noBindDevice);
                    return;
                }
                FragmentPictureBinding fragmentPictureBinding5 = this.binding;
                if (fragmentPictureBinding5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    fragmentPictureBinding = fragmentPictureBinding5;
                }
                ViewKt.gone(fragmentPictureBinding.noBindDevice);
                return;
            }
            FragmentPictureBinding fragmentPictureBinding6 = this.binding;
            if (fragmentPictureBinding6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                fragmentPictureBinding = fragmentPictureBinding6;
            }
            ViewKt.gone(fragmentPictureBinding.noBindDevice);
        }
    }

    /* compiled from: PictureFragment.kt */
    @Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, m607d2 = {"Lcom/glasssutdio/wear/home/PictureFragment$MyRunnable;", "Ljava/lang/Runnable;", "(Lcom/glasssutdio/wear/home/PictureFragment;)V", "run", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class MyRunnable implements Runnable {
        public MyRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (UserConfig.INSTANCE.getInstance().getDebug()) {
                ThreadExtKt.ktxRunOnUi(this, new Function1<MyRunnable, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$MyRunnable$run$1
                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(PictureFragment.MyRunnable myRunnable) {
                        invoke2(myRunnable);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(PictureFragment.MyRunnable ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        GlobalKt.showToast$default("开wifi 失败，指令没有返回", 0, 1, null);
                    }
                });
            }
            PictureFragment.this.tvImportBtnReset();
            FragmentPictureBinding fragmentPictureBinding = PictureFragment.this.binding;
            if (fragmentPictureBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding = null;
            }
            fragmentPictureBinding.tvCount.setText(PictureFragment.this.getString(C0775R.string.album_glass_14));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void tvImportBtnReset() {
        this.importing = false;
        FragmentPictureBinding fragmentPictureBinding = this.binding;
        FragmentPictureBinding fragmentPictureBinding2 = null;
        if (fragmentPictureBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding = null;
        }
        fragmentPictureBinding.tvImportBtn.setText(getString(C0775R.string.h_glass_107));
        FragmentPictureBinding fragmentPictureBinding3 = this.binding;
        if (fragmentPictureBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding3 = null;
        }
        fragmentPictureBinding3.tvSpeed.setText("");
        FragmentPictureBinding fragmentPictureBinding4 = this.binding;
        if (fragmentPictureBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            fragmentPictureBinding2 = fragmentPictureBinding4;
        }
        fragmentPictureBinding2.tvImportBtn.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void tvImportBtnRetry() {
        this.importing = false;
        FragmentPictureBinding fragmentPictureBinding = this.binding;
        FragmentPictureBinding fragmentPictureBinding2 = null;
        if (fragmentPictureBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding = null;
        }
        fragmentPictureBinding.tvImportBtn.setText(getString(C0775R.string.album_glass_42));
        FragmentPictureBinding fragmentPictureBinding3 = this.binding;
        if (fragmentPictureBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding3 = null;
        }
        fragmentPictureBinding3.tvSpeed.setText("");
        FragmentPictureBinding fragmentPictureBinding4 = this.binding;
        if (fragmentPictureBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            fragmentPictureBinding2 = fragmentPictureBinding4;
        }
        fragmentPictureBinding2.tvImportBtn.setEnabled(true);
    }

    /* compiled from: PictureFragment.kt */
    @Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, m607d2 = {"Lcom/glasssutdio/wear/home/PictureFragment$P2pConnectFailRunnable;", "Ljava/lang/Runnable;", "(Lcom/glasssutdio/wear/home/PictureFragment;)V", "run", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class P2pConnectFailRunnable implements Runnable {
        public P2pConnectFailRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            PictureFragment.this.importing = false;
            FragmentPictureBinding fragmentPictureBinding = null;
            if (PictureFragment.this.failRetry < 1) {
                try {
                    XLog.m137i("p2p连接超时,准备再次走流程");
                    if (UserConfig.INSTANCE.getInstance().getDebug()) {
                        ThreadExtKt.ktxRunOnUi(this, new Function1<P2pConnectFailRunnable, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$P2pConnectFailRunnable$run$1
                            @Override // kotlin.jvm.functions.Function1
                            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment.P2pConnectFailRunnable p2pConnectFailRunnable) {
                                invoke2(p2pConnectFailRunnable);
                                return Unit.INSTANCE;
                            }

                            /* renamed from: invoke, reason: avoid collision after fix types in other method */
                            public final void invoke2(PictureFragment.P2pConnectFailRunnable ktxRunOnUi) {
                                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                                GlobalKt.showToast$default("p2p连接超时,准备再次走流程", 0, 1, null);
                            }
                        });
                    }
                    FragmentPictureBinding fragmentPictureBinding2 = PictureFragment.this.binding;
                    if (fragmentPictureBinding2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        fragmentPictureBinding2 = null;
                    }
                    fragmentPictureBinding2.tvCount.setText(PictureFragment.this.getString(C0775R.string.album_glass_33));
                    PictureFragment.this.systemSuccess = false;
                    PictureFragment.this.bleCallbackSuccess = false;
                    PictureFragment.this.importAlbum();
                    FragmentPictureBinding fragmentPictureBinding3 = PictureFragment.this.binding;
                    if (fragmentPictureBinding3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        fragmentPictureBinding3 = null;
                    }
                    fragmentPictureBinding3.tvImportBtn.setText(PictureFragment.this.getString(C0775R.string.h_glass_119));
                    FragmentPictureBinding fragmentPictureBinding4 = PictureFragment.this.binding;
                    if (fragmentPictureBinding4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                    } else {
                        fragmentPictureBinding = fragmentPictureBinding4;
                    }
                    fragmentPictureBinding.tvImportBtn.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PictureFragment.this.failRetry++;
                return;
            }
            if (UserConfig.INSTANCE.getInstance().getDebug()) {
                ThreadExtKt.ktxRunOnUi(this, new Function1<P2pConnectFailRunnable, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$P2pConnectFailRunnable$run$2
                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(PictureFragment.P2pConnectFailRunnable p2pConnectFailRunnable) {
                        invoke2(p2pConnectFailRunnable);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(PictureFragment.P2pConnectFailRunnable ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        GlobalKt.showToast$default("p2p连接失败", 0, 1, null);
                    }
                });
            }
            PictureFragment.this.handler.removeCallbacks(PictureFragment.this.p2pConnectFailRunnable);
            PictureFragment.this.tvImportBtnRetry();
            FragmentPictureBinding fragmentPictureBinding5 = PictureFragment.this.binding;
            if (fragmentPictureBinding5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                fragmentPictureBinding = fragmentPictureBinding5;
            }
            fragmentPictureBinding.tvCount.setText(PictureFragment.this.getString(C0775R.string.album_glass_14));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void importAlbum() {
        this.importing = true;
        this.handler.postDelayed(this.timeoutRunnable, DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
        WifiP2pManagerSingleton.INSTANCE.getInstance().startPeerDiscovery();
        LargeDataHandler.getInstance().glassesControl(new byte[]{2, 1, 4}, new ILargeDataResponse() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda5
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public final void parseData(int i, BaseResponse baseResponse) {
                PictureFragment.importAlbum$lambda$13(this.f$0, i, (GlassModelControlResponse) baseResponse);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0070  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void importAlbum$lambda$13(PictureFragment this$0, int i, GlassModelControlResponse glassModelControlResponse) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (glassModelControlResponse.getDataType() == 1 && glassModelControlResponse.getGlassWorkType() == 4) {
            if (glassModelControlResponse.getErrorCode() == 0) {
                int workTypeIng = glassModelControlResponse.getWorkTypeIng();
                if (workTypeIng == 1) {
                    String string = this$0.getString(C0775R.string.album_glass_40);
                    Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                    GlobalKt.showToast$default(string, 0, 1, null);
                } else if (workTypeIng == 2) {
                    String string2 = this$0.getString(C0775R.string.album_glass_36);
                    Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                    GlobalKt.showToast$default(string2, 0, 1, null);
                } else if (workTypeIng == 5) {
                    String string3 = this$0.getString(C0775R.string.album_glass_38);
                    Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                    GlobalKt.showToast$default(string3, 0, 1, null);
                } else if (workTypeIng != 6) {
                    if (workTypeIng == 7) {
                        String string4 = this$0.getString(C0775R.string.album_glass_37);
                        Intrinsics.checkNotNullExpressionValue(string4, "getString(...)");
                        GlobalKt.showToast$default(string4, 0, 1, null);
                    } else if (workTypeIng == 8) {
                        String string5 = this$0.getString(C0775R.string.album_glass_39);
                        Intrinsics.checkNotNullExpressionValue(string5, "getString(...)");
                        GlobalKt.showToast$default(string5, 0, 1, null);
                    }
                }
                this$0.handler.removeCallbacks(this$0.timeoutRunnable);
                this$0.tvImportBtnReset();
                return;
            }
            this$0.handler.removeCallbacks(this$0.timeoutRunnable);
            ThreadExtKt.ktxRunOnUi(this$0, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$importAlbum$1$1
                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                    invoke2(pictureFragment);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(PictureFragment ktxRunOnUi) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                    FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                    FragmentPictureBinding fragmentPictureBinding2 = null;
                    if (fragmentPictureBinding == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        fragmentPictureBinding = null;
                    }
                    fragmentPictureBinding.tvSpeed.setText("");
                    FragmentPictureBinding fragmentPictureBinding3 = ktxRunOnUi.binding;
                    if (fragmentPictureBinding3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                    } else {
                        fragmentPictureBinding2 = fragmentPictureBinding3;
                    }
                    fragmentPictureBinding2.tvCount.setText(ktxRunOnUi.getString(C0775R.string.album_glass_4));
                    ktxRunOnUi.handler.postDelayed(ktxRunOnUi.p2pConnectFailRunnable, 40000L);
                    WifiP2pManagerSingleton.INSTANCE.getInstance().resetFailCount();
                }
            });
            this$0.initHeartBeat();
            LargeDataHandler.getInstance().removeGlassesControlCallback();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateIndicator(int position) {
        this.currTabIndex = position;
        EventBus.getDefault().post(new AlbumPageIndexEvent(position));
        FragmentPictureBinding fragmentPictureBinding = null;
        if (position == 0) {
            FragmentPictureBinding fragmentPictureBinding2 = this.binding;
            if (fragmentPictureBinding2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding2 = null;
            }
            ConstraintRadioGroup constraintRadioGroup = fragmentPictureBinding2.diyRadioGroup;
            FragmentPictureBinding fragmentPictureBinding3 = this.binding;
            if (fragmentPictureBinding3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                fragmentPictureBinding = fragmentPictureBinding3;
            }
            RadioButton rb1 = fragmentPictureBinding.rb1;
            Intrinsics.checkNotNullExpressionValue(rb1, "rb1");
            constraintRadioGroup.setCheckedStateForView(rb1, true);
            return;
        }
        if (position == 1) {
            FragmentPictureBinding fragmentPictureBinding4 = this.binding;
            if (fragmentPictureBinding4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding4 = null;
            }
            ConstraintRadioGroup constraintRadioGroup2 = fragmentPictureBinding4.diyRadioGroup;
            FragmentPictureBinding fragmentPictureBinding5 = this.binding;
            if (fragmentPictureBinding5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                fragmentPictureBinding = fragmentPictureBinding5;
            }
            RadioButton rb2 = fragmentPictureBinding.rb2;
            Intrinsics.checkNotNullExpressionValue(rb2, "rb2");
            constraintRadioGroup2.setCheckedStateForView(rb2, true);
            return;
        }
        if (position == 2) {
            FragmentPictureBinding fragmentPictureBinding6 = this.binding;
            if (fragmentPictureBinding6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding6 = null;
            }
            ConstraintRadioGroup constraintRadioGroup3 = fragmentPictureBinding6.diyRadioGroup;
            FragmentPictureBinding fragmentPictureBinding7 = this.binding;
            if (fragmentPictureBinding7 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                fragmentPictureBinding = fragmentPictureBinding7;
            }
            RadioButton rb3 = fragmentPictureBinding.rb3;
            Intrinsics.checkNotNullExpressionValue(rb3, "rb3");
            constraintRadioGroup3.setCheckedStateForView(rb3, true);
            return;
        }
        if (position == 3) {
            FragmentPictureBinding fragmentPictureBinding8 = this.binding;
            if (fragmentPictureBinding8 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding8 = null;
            }
            ConstraintRadioGroup constraintRadioGroup4 = fragmentPictureBinding8.diyRadioGroup;
            FragmentPictureBinding fragmentPictureBinding9 = this.binding;
            if (fragmentPictureBinding9 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                fragmentPictureBinding = fragmentPictureBinding9;
            }
            RadioButton rb5 = fragmentPictureBinding.rb5;
            Intrinsics.checkNotNullExpressionValue(rb5, "rb5");
            constraintRadioGroup4.setCheckedStateForView(rb5, true);
            return;
        }
        if (position != 4) {
            return;
        }
        FragmentPictureBinding fragmentPictureBinding10 = this.binding;
        if (fragmentPictureBinding10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding10 = null;
        }
        ConstraintRadioGroup constraintRadioGroup5 = fragmentPictureBinding10.diyRadioGroup;
        FragmentPictureBinding fragmentPictureBinding11 = this.binding;
        if (fragmentPictureBinding11 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            fragmentPictureBinding = fragmentPictureBinding11;
        }
        RadioButton rb4 = fragmentPictureBinding.rb4;
        Intrinsics.checkNotNullExpressionValue(rb4, "rb4");
        constraintRadioGroup5.setCheckedStateForView(rb4, true);
    }

    public final void imageEdit(final boolean enable) {
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.imageEdit.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                fragmentPictureBinding.imageEdit.setEnabled(!enable);
            }
        });
    }

    /* compiled from: PictureFragment.kt */
    @Metadata(m606d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016¨\u0006\t"}, m607d2 = {"Lcom/glasssutdio/wear/home/PictureFragment$MyDeviceNotifyListener;", "Lcom/oudmon/ble/base/communication/bigData/resp/GlassesDeviceNotifyListener;", "(Lcom/glasssutdio/wear/home/PictureFragment;)V", "parseData", "", "cmdType", "", "response", "Lcom/oudmon/ble/base/communication/bigData/resp/GlassesDeviceNotifyRsp;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class MyDeviceNotifyListener extends GlassesDeviceNotifyListener {
        public MyDeviceNotifyListener() {
        }

        @Override // com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyListener, com.oudmon.ble.base.communication.ILargeDataResponse
        public void parseData(int cmdType, GlassesDeviceNotifyRsp response) {
            Intrinsics.checkNotNull(response);
            byte b = response.getLoadData()[6];
            if (b == 1) {
                final int iBytesToInt = ByteUtil.bytesToInt(Arrays.copyOfRange(response.getLoadData(), 7, 9)) + ByteUtil.bytesToInt(Arrays.copyOfRange(response.getLoadData(), 9, 11)) + ByteUtil.bytesToInt(Arrays.copyOfRange(response.getLoadData(), 11, 13));
                final PictureFragment pictureFragment = PictureFragment.this;
                ThreadExtKt.ktxRunOnUi(this, new Function1<MyDeviceNotifyListener, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$MyDeviceNotifyListener$parseData$1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(PictureFragment.MyDeviceNotifyListener myDeviceNotifyListener) {
                        invoke2(myDeviceNotifyListener);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(PictureFragment.MyDeviceNotifyListener ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        FragmentPictureBinding fragmentPictureBinding = null;
                        if (iBytesToInt > 0) {
                            FragmentPictureBinding fragmentPictureBinding2 = pictureFragment.binding;
                            if (fragmentPictureBinding2 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                fragmentPictureBinding2 = null;
                            }
                            ViewKt.visible(fragmentPictureBinding2.glassStatus1);
                            FragmentPictureBinding fragmentPictureBinding3 = pictureFragment.binding;
                            if (fragmentPictureBinding3 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                fragmentPictureBinding3 = null;
                            }
                            ViewKt.gone(fragmentPictureBinding3.mediaProgress);
                            pictureFragment.tvImportBtnReset();
                            FragmentPictureBinding fragmentPictureBinding4 = pictureFragment.binding;
                            if (fragmentPictureBinding4 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                            } else {
                                fragmentPictureBinding = fragmentPictureBinding4;
                            }
                            TextView textView = fragmentPictureBinding.tvCount;
                            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                            String string = pictureFragment.getString(C0775R.string.h_glass_106);
                            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                            String str = String.format(string, Arrays.copyOf(new Object[]{String.valueOf(iBytesToInt)}, 1));
                            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                            textView.setText(str);
                            return;
                        }
                        FragmentPictureBinding fragmentPictureBinding5 = pictureFragment.binding;
                        if (fragmentPictureBinding5 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                        } else {
                            fragmentPictureBinding = fragmentPictureBinding5;
                        }
                        ViewKt.gone(fragmentPictureBinding.glassStatus1);
                    }
                });
                return;
            }
            if (b == 5) {
                PictureFragment.this.charging = response.getLoadData()[8] == 1;
                return;
            }
            if (b == 11) {
                final int iBytesToInt2 = ByteUtil.bytesToInt(Arrays.copyOfRange(response.getLoadData(), 7, 8));
                final PictureFragment pictureFragment2 = PictureFragment.this;
                ThreadExtKt.ktxRunOnUi(this, new Function1<MyDeviceNotifyListener, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$MyDeviceNotifyListener$parseData$3
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(PictureFragment.MyDeviceNotifyListener myDeviceNotifyListener) {
                        invoke2(myDeviceNotifyListener);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(PictureFragment.MyDeviceNotifyListener ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        FragmentPictureBinding fragmentPictureBinding = pictureFragment2.binding;
                        if (fragmentPictureBinding == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            fragmentPictureBinding = null;
                        }
                        fragmentPictureBinding.tvTemperature.setText(String.valueOf(iBytesToInt2));
                    }
                });
                return;
            }
            if (b == 8) {
                final String string = new StringBuilder().append(ByteUtil.byteToInt(response.getLoadData()[7])).append(FilenameUtils.EXTENSION_SEPARATOR).append(ByteUtil.byteToInt(response.getLoadData()[8])).append(FilenameUtils.EXTENSION_SEPARATOR).append(ByteUtil.byteToInt(response.getLoadData()[9])).append(FilenameUtils.EXTENSION_SEPARATOR).append(ByteUtil.byteToInt(response.getLoadData()[10])).toString();
                XLog.m137i("蓝牙返回ip:" + string);
                if (UserConfig.INSTANCE.getInstance().getDebug()) {
                    ThreadExtKt.ktxRunOnUi(this, new Function1<MyDeviceNotifyListener, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$MyDeviceNotifyListener$parseData$2
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        {
                            super(1);
                        }

                        @Override // kotlin.jvm.functions.Function1
                        public /* bridge */ /* synthetic */ Unit invoke(PictureFragment.MyDeviceNotifyListener myDeviceNotifyListener) {
                            invoke2(myDeviceNotifyListener);
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2(PictureFragment.MyDeviceNotifyListener ktxRunOnUi) {
                            Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                            GlobalKt.showToast$default("返回ip:" + string, 0, 1, null);
                        }
                    });
                }
                UserConfig.INSTANCE.getInstance().setGlassDeviceWifiIP(string);
                PictureFragment.this.bleCallbackSuccess = true;
                PictureFragment.this.downloadMediaConfig();
                return;
            }
            if (b != 9) {
                return;
            }
            int iBytesToInt3 = ByteUtil.bytesToInt(Arrays.copyOfRange(response.getLoadData(), 7, 8));
            XLog.m137i("蓝牙返回错误:" + iBytesToInt3);
            if (iBytesToInt3 == 255) {
                WifiP2pManagerSingleton.INSTANCE.getInstance().resetDeviceP2p();
                PictureFragment.this.handler.removeCallbacks(PictureFragment.this.p2pConnectFailRunnable);
                FragmentPictureBinding fragmentPictureBinding = PictureFragment.this.binding;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                fragmentPictureBinding.tvCount.setText(PictureFragment.this.getString(C0775R.string.album_glass_33));
                PictureFragment.this.tvImportBtnRetry();
            }
        }
    }

    public final void readAlbumCounts() {
        if (this.importing) {
            XLog.m137i("正在同步照片。。。");
        } else {
            LargeDataHandler.getInstance().glassesControl(new byte[]{2, 4}, new ILargeDataResponse() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda0
                @Override // com.oudmon.ble.base.communication.ILargeDataResponse
                public final void parseData(int i, BaseResponse baseResponse) {
                    PictureFragment.readAlbumCounts$lambda$14(this.f$0, i, (GlassModelControlResponse) baseResponse);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void readAlbumCounts$lambda$14(PictureFragment this$0, int i, GlassModelControlResponse glassModelControlResponse) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (glassModelControlResponse.getDataType() == 4) {
            final int imageCount = glassModelControlResponse.getImageCount() + glassModelControlResponse.getVideoCount() + glassModelControlResponse.getRecordCount();
            XLog.m137i("mediaCount:" + imageCount);
            if (imageCount > 0) {
                ThreadExtKt.ktxRunOnUi(this$0, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$readAlbumCounts$1$1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                        invoke2(pictureFragment);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(PictureFragment ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                        FragmentPictureBinding fragmentPictureBinding2 = null;
                        if (fragmentPictureBinding == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            fragmentPictureBinding = null;
                        }
                        ViewKt.visible(fragmentPictureBinding.glassStatus1);
                        FragmentPictureBinding fragmentPictureBinding3 = ktxRunOnUi.binding;
                        if (fragmentPictureBinding3 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            fragmentPictureBinding3 = null;
                        }
                        ViewKt.gone(fragmentPictureBinding3.mediaProgress);
                        ktxRunOnUi.tvImportBtnReset();
                        FragmentPictureBinding fragmentPictureBinding4 = ktxRunOnUi.binding;
                        if (fragmentPictureBinding4 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                        } else {
                            fragmentPictureBinding2 = fragmentPictureBinding4;
                        }
                        TextView textView = fragmentPictureBinding2.tvCount;
                        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                        String string = ktxRunOnUi.getString(C0775R.string.h_glass_106);
                        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                        String str = String.format(string, Arrays.copyOf(new Object[]{String.valueOf(imageCount)}, 1));
                        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                        textView.setText(str);
                    }
                });
                return;
            }
            FragmentPictureBinding fragmentPictureBinding = this$0.binding;
            if (fragmentPictureBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding = null;
            }
            ViewKt.gone(fragmentPictureBinding.glassStatus1);
        }
    }

    @Override // com.glasssutdio.wear.home.BaseFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        MyDeviceNotifyListener myDeviceNotifyListener = null;
        if (!UserConfig.INSTANCE.getInstance().getDeviceBind()) {
            FragmentPictureBinding fragmentPictureBinding = this.binding;
            if (fragmentPictureBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding = null;
            }
            ViewKt.visible(fragmentPictureBinding.noBindDevice);
        } else {
            FragmentPictureBinding fragmentPictureBinding2 = this.binding;
            if (fragmentPictureBinding2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding2 = null;
            }
            ViewKt.gone(fragmentPictureBinding2.noBindDevice);
        }
        if (LanguageUtil.isChinaReal()) {
            FragmentPictureBinding fragmentPictureBinding3 = this.binding;
            if (fragmentPictureBinding3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding3 = null;
            }
            fragmentPictureBinding3.ivNoPicture.setImageResource(C0775R.mipmap.ic_picture_no_bind);
        } else {
            FragmentPictureBinding fragmentPictureBinding4 = this.binding;
            if (fragmentPictureBinding4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                fragmentPictureBinding4 = null;
            }
            fragmentPictureBinding4.ivNoPicture.setImageResource(C0775R.mipmap.ic_picture_no_bind_hw);
        }
        WifiP2pManagerSingleton.INSTANCE.getInstance().registerReceiver();
        ThreadManager.getInstance().wakeUp();
        this.deviceNotifyListener = new MyDeviceNotifyListener();
        LargeDataHandler largeDataHandler = LargeDataHandler.getInstance();
        MyDeviceNotifyListener myDeviceNotifyListener2 = this.deviceNotifyListener;
        if (myDeviceNotifyListener2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("deviceNotifyListener");
        } else {
            myDeviceNotifyListener = myDeviceNotifyListener2;
        }
        largeDataHandler.addOutDeviceListener(2, myDeviceNotifyListener);
        if (!new DateUtil(UserConfig.INSTANCE.getInstance().getAppLastUserTime(), true).isToday()) {
            UserConfig.INSTANCE.getInstance().setAppLastUserTime(new DateUtil().getUnixTimestamp());
            EventBus.getDefault().post(new AlbumRefreshEvent());
        }
        if (UserConfig.INSTANCE.getInstance().getBattery() <= 15) {
            readDeviceBattery();
        }
    }

    @Override // com.glasssutdio.wear.home.BaseFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        try {
            WifiP2pManagerSingleton.INSTANCE.getInstance().unregisterReceiver();
        } catch (Exception unused) {
        }
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void fileProgress(String fileName, int progress) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        FragmentPictureBinding fragmentPictureBinding = this.binding;
        FragmentPictureBinding fragmentPictureBinding2 = null;
        if (fragmentPictureBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding = null;
        }
        ViewKt.visible(fragmentPictureBinding.mediaProgress);
        FragmentPictureBinding fragmentPictureBinding3 = this.binding;
        if (fragmentPictureBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            fragmentPictureBinding2 = fragmentPictureBinding3;
        }
        fragmentPictureBinding2.mediaProgress.setProgress(progress);
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.fileProgress.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                FragmentPictureBinding fragmentPictureBinding4 = ktxRunOnUi.binding;
                FragmentPictureBinding fragmentPictureBinding5 = null;
                if (fragmentPictureBinding4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding4 = null;
                }
                fragmentPictureBinding4.tvImportBtn.setText(ktxRunOnUi.getString(C0775R.string.h_glass_119));
                FragmentPictureBinding fragmentPictureBinding6 = ktxRunOnUi.binding;
                if (fragmentPictureBinding6 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    fragmentPictureBinding5 = fragmentPictureBinding6;
                }
                fragmentPictureBinding5.tvImportBtn.setEnabled(false);
            }
        });
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void wifiSpeed(final String wifiSpeed) {
        Intrinsics.checkNotNullParameter(wifiSpeed, "wifiSpeed");
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.wifiSpeed.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                fragmentPictureBinding.tvSpeed.setText(wifiSpeed);
            }
        });
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void fileWasDownloadSuccessfully(GlassAlbumEntity entity) {
        Intrinsics.checkNotNullParameter(entity, "entity");
        if (entity.getFileType() == 2) {
            entity.setEisInProgress(true);
        }
        Interval interval = this.interval;
        if (interval != null) {
            interval.cancel();
        }
        EventBus.getDefault().post(new AlbumDownloadSuccessfullyEvent(entity));
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void fileCount(final int index, final int total) {
        XLog.m137i(total + "files curr fileName index:" + index);
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.fileCount.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                FragmentPictureBinding fragmentPictureBinding2 = null;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                TextView textView = fragmentPictureBinding.tvCount;
                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                String string = ktxRunOnUi.getString(C0775R.string.h_glass_106);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                String str = String.format(string, Arrays.copyOf(new Object[]{new StringBuilder().append(index).append(IOUtils.DIR_SEPARATOR_UNIX).append(total).toString()}, 1));
                Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                textView.setText(str);
                FragmentPictureBinding fragmentPictureBinding3 = ktxRunOnUi.binding;
                if (fragmentPictureBinding3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    fragmentPictureBinding2 = fragmentPictureBinding3;
                }
                fragmentPictureBinding2.tvSpeed.setText("");
            }
        });
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void fileDownloadComplete() {
        XLog.m137i("全部文件下载完成了");
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.fileDownloadComplete.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                ViewKt.gone(fragmentPictureBinding.glassStatus1);
                ktxRunOnUi.tvImportBtnReset();
                WifiP2pManagerSingleton.INSTANCE.getInstance().cancelP2pConnection();
            }
        });
        LargeDataHandler.getInstance().glassesControl(new byte[]{2, 1, 9}, new ILargeDataResponse() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda3
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public final void parseData(int i, BaseResponse baseResponse) {
                PictureFragment.fileDownloadComplete$lambda$15(i, (GlassModelControlResponse) baseResponse);
            }
        });
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.fileDownloadComplete.3
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                ktxRunOnUi.imageEdit(false);
                FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                fragmentPictureBinding.tvSpeed.setText("");
            }
        });
        EventBus.getDefault().post(new EventType(6));
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void fileDownloadError(final int fileType, final int errorType) {
        XLog.m137i("下载出错:" + errorType + "-->文件类型：" + fileType);
        if (UserConfig.INSTANCE.getInstance().getDebug()) {
            ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.fileDownloadError.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                    invoke2(pictureFragment);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(PictureFragment ktxRunOnUi) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                    GlobalKt.showToast$default("下载出错:" + errorType + "-->文件类型：" + fileType, 0, 1, null);
                }
            });
        }
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.fileDownloadError.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                try {
                    FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                    if (fragmentPictureBinding == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        fragmentPictureBinding = null;
                    }
                    fragmentPictureBinding.tvSpeed.setText("");
                    if (fileType == 1) {
                        XLog.m137i(String.valueOf(errorType));
                        ktxRunOnUi.tvImportBtnRetry();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void eisEnd(String fileName, String filePath) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(filePath, "filePath");
        EventBus.getDefault().post(new VideoEisSuccessfullyEvent(fileName, filePath, false));
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void eisError(String fileName, String sourcePath, final String errorInfo) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(sourcePath, "sourcePath");
        Intrinsics.checkNotNullParameter(errorInfo, "errorInfo");
        EventBus.getDefault().post(new VideoEisSuccessfullyEvent(fileName, sourcePath, false));
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.eisError.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                XLog.m137i("防抖处理失败：" + errorInfo);
            }
        });
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void recordingToPcm(String fileName, String filePath, int duration) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(filePath, "filePath");
        EventBus.getDefault().post(new RecordingToPcmSuccessfullyEvent(fileName, filePath, duration));
    }

    @Override // com.glasssutdio.wear.depository.AlbumDepository.WifiFilesDownloadListener
    public void recordingToPcmError(String fileName, final String errorInfo) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(errorInfo, "errorInfo");
        ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.recordingToPcmError.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                XLog.m137i("录音转失败：" + errorInfo);
            }
        });
    }

    /* compiled from: PictureFragment.kt */
    @Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\u0005"}, m607d2 = {"Lcom/glasssutdio/wear/home/PictureFragment$Companion;", "", "()V", "newInstance", "Lcom/glasssutdio/wear/home/PictureFragment;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final PictureFragment newInstance() {
            return new PictureFragment();
        }
    }

    @Override // com.glasssutdio.wear.home.BaseFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onWifiP2pEnabled() {
        XLog.m137i("onWifiP2pEnabled");
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onWifiP2pDisabled() {
        XLog.m137i("onWifiP2pDisabled");
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onPeersChanged(Collection<? extends WifiP2pDevice> peers) {
        Intrinsics.checkNotNullParameter(peers, "peers");
        if (peers.isEmpty()) {
            return;
        }
        XLog.m137i("wifi p2p设备:" + peers.size() + "-----" + (!this.isP2PConnecting));
        if (this.isP2PConnecting) {
            return;
        }
        for (WifiP2pDevice wifiP2pDevice : peers) {
            XLog.m137i("扫描到设备:" + wifiP2pDevice.deviceName);
            if (!StringsKt.equals(wifiP2pDevice.deviceName, UserConfig.INSTANCE.getInstance().getGlassDeviceWifiName(), true)) {
                String deviceName = wifiP2pDevice.deviceName;
                Intrinsics.checkNotNullExpressionValue(deviceName, "deviceName");
                if (StringsKt.endsWith$default(deviceName, UserConfig.INSTANCE.getInstance().getDeviceAddressNoClear(), false, 2, (Object) null)) {
                }
            }
            XLog.m137i("匹配到设备:" + wifiP2pDevice.deviceName);
            WifiP2pManagerSingleton.INSTANCE.getInstance().connectToDevice(wifiP2pDevice);
            this.isP2PConnecting = true;
            return;
        }
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onConnected(WifiP2pInfo info) {
        String hostAddress;
        if (info != null) {
            StringBuilder sb = new StringBuilder("onConnectionInfoAvailable\n");
            sb.append("groupFormed: " + info.groupFormed);
            sb.append(IOUtils.LINE_SEPARATOR_UNIX);
            sb.append("isGroupOwner: " + info.isGroupOwner);
            sb.append(IOUtils.LINE_SEPARATOR_UNIX);
            sb.append("groupOwnerAddress hostAddress: " + info.groupOwnerAddress.getHostAddress());
            String string = sb.toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            XLog.m137i(string);
            XLog.m137i("isGroupOwner:" + info.isGroupOwner);
            this.systemSuccess = true;
            if (info.isGroupOwner || (hostAddress = info.groupOwnerAddress.getHostAddress()) == null) {
                return;
            }
            UserConfig.INSTANCE.getInstance().setGlassDeviceWifiIP(hostAddress);
            downloadMediaConfig();
        }
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onDisconnected() {
        XLog.m137i("P2P组网不可用:onDisconnected");
        this.isP2PConnecting = false;
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onThisDeviceChanged(WifiP2pDevice device) {
        XLog.m137i(GsonInstance.INSTANCE.getGson().toJson(device));
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onPeerDiscoveryStarted() {
        this.isP2PConnecting = false;
        XLog.m137i("P2P 开始扫描");
        if (UserConfig.INSTANCE.getInstance().getDebug()) {
            ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.onPeerDiscoveryStarted.1
                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                    invoke2(pictureFragment);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(PictureFragment ktxRunOnUi) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                    GlobalKt.showToast$default("P2P 开始扫描", 0, 1, null);
                }
            });
        }
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onPeerDiscoveryFailed(final int reason) {
        XLog.m137i("P2P 扫描失败原因:" + reason);
        if (UserConfig.INSTANCE.getInstance().getDebug()) {
            ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.onPeerDiscoveryFailed.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                    invoke2(pictureFragment);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(PictureFragment ktxRunOnUi) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                    GlobalKt.showToast$default("P2P 扫描失败原因:" + reason, 0, 1, null);
                }
            });
        }
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onConnectRequestSent() {
        XLog.m137i("P2P 发送连接请求成功");
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onConnectRequestFailed(final int reason) {
        XLog.m137i("P2P 发送连接请求失败:" + reason);
        if (UserConfig.INSTANCE.getInstance().getDebug()) {
            ThreadExtKt.ktxRunOnUi(this, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment.onConnectRequestFailed.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                    invoke2(pictureFragment);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(PictureFragment ktxRunOnUi) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                    GlobalKt.showToast$default("P2P 发送连接请求失败:" + reason, 0, 1, null);
                }
            });
        }
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void cancelConnect() {
        XLog.m137i("P2P cancelConnect success");
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void cancelConnectFail(int reason) {
        XLog.m137i("P2P cancelConnectFail: " + reason);
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void retryAlsoFailed() {
        XLog.m137i("P2P retryAlsoFailed");
        FragmentPictureBinding fragmentPictureBinding = this.binding;
        if (fragmentPictureBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            fragmentPictureBinding = null;
        }
        fragmentPictureBinding.tvCount.setText(getString(C0775R.string.album_glass_14));
        tvImportBtnRetry();
    }

    private final void readDeviceBattery() {
        LargeDataHandler.getInstance().addBatteryCallBack("picture", new ILargeDataResponse() { // from class: com.glasssutdio.wear.home.PictureFragment$$ExternalSyntheticLambda4
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public final void parseData(int i, BaseResponse baseResponse) {
                PictureFragment.readDeviceBattery$lambda$17(this.f$0, i, (BatteryResponse) baseResponse);
            }
        });
        LargeDataHandler.getInstance().syncBattery();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void readDeviceBattery$lambda$17(PictureFragment this$0, int i, BatteryResponse batteryResponse) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        UserConfig.INSTANCE.getInstance().setBattery(batteryResponse.getBattery());
        UserConfig companion = UserConfig.INSTANCE.getInstance();
        int battery = batteryResponse.getBattery();
        boolean z = false;
        if (1 <= battery && battery < 16) {
            z = true;
        }
        companion.setLowBattery(z);
        this$0.charging = batteryResponse.isCharging();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void requestPermissionLaunch$lambda$2(PictureFragment this$0, Map it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(it, "it");
        if (!it.isEmpty()) {
            Iterator it2 = it.entrySet().iterator();
            while (it2.hasNext()) {
                if (!((Boolean) ((Map.Entry) it2.next()).getValue()).booleanValue()) {
                    XLog.m137i("拒绝了权限");
                    String string = this$0.getString(C0775R.string.h_glass_101);
                    Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                    GlobalKt.showToast$default(string, 0, 1, null);
                    return;
                }
            }
        }
        ThreadExtKt.ktxRunOnUi(this$0, new Function1<PictureFragment, Unit>() { // from class: com.glasssutdio.wear.home.PictureFragment$requestPermissionLaunch$1$2
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(PictureFragment pictureFragment) {
                invoke2(pictureFragment);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(PictureFragment ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                FragmentPictureBinding fragmentPictureBinding = ktxRunOnUi.binding;
                FragmentPictureBinding fragmentPictureBinding2 = null;
                if (fragmentPictureBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    fragmentPictureBinding = null;
                }
                fragmentPictureBinding.tvImportBtn.setText(ktxRunOnUi.getString(C0775R.string.h_glass_119));
                FragmentPictureBinding fragmentPictureBinding3 = ktxRunOnUi.binding;
                if (fragmentPictureBinding3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    fragmentPictureBinding2 = fragmentPictureBinding3;
                }
                fragmentPictureBinding2.tvImportBtn.setEnabled(false);
            }
        });
        XLog.m137i("已获得全部权限");
        this$0.systemSuccess = false;
        this$0.bleCallbackSuccess = false;
        WifiP2pManagerSingleton.INSTANCE.getInstance().registerReceiver();
        this$0.importAlbum();
    }
}
