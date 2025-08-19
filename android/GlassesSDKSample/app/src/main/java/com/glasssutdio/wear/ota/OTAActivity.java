package com.glasssutdio.wear.ota;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.motion.widget.Key;
import androidx.core.animation.LinearInterpolator;
import androidx.core.animation.ObjectAnimator;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.C0775R;
import com.glasssutdio.wear.GlassesWearJavaApplication;
import com.glasssutdio.wear.all.GlobalKt;
import com.glasssutdio.wear.all.ThreadExtKt;
import com.glasssutdio.wear.all.ViewKt;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.all.utils.GsonInstance;
import com.glasssutdio.wear.all.utils.NetWorkUtils;
import com.glasssutdio.wear.bus.BluetoothEvent;
import com.glasssutdio.wear.bus.BusEvent;
import com.glasssutdio.wear.bus.EventType;
import com.glasssutdio.wear.bus.OTAFileStatusEvent;
import com.glasssutdio.wear.databinding.ActivityOtaactivityBinding;
import com.glasssutdio.wear.manager.BaseSettingActivity;
import com.glasssutdio.wear.ota.OTAActivity;
import com.glasssutdio.wear.ota.OTAViewModel;
import com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton;
import com.google.android.gms.location.DeviceOrientationRequest;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.cli.HelpFormatter;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.FilenameUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.IOUtils;
import com.hjq.permissions.Permission;
import com.oudmon.ble.base.bluetooth.BleOperateManager;
import com.oudmon.ble.base.communication.DfuHandle;
import com.oudmon.ble.base.communication.ILargeDataResponse;
import com.oudmon.ble.base.communication.LargeDataHandler;
import com.oudmon.ble.base.communication.bigData.resp.BaseResponse;
import com.oudmon.ble.base.communication.bigData.resp.BatteryResponse;
import com.oudmon.ble.base.communication.bigData.resp.DeviceInfoResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassModelControlResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyListener;
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyRsp;
import com.oudmon.ble.base.communication.utils.ByteUtil;
import com.oudmon.ble.base.util.AppUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.koin.androidx.viewmodel.ext.android.LifecycleOwnerExtKt;
import org.koin.core.qualifier.Qualifier;

/* compiled from: OTAActivity.kt */
@Metadata(m606d1 = {"\u0000Ê\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u0006\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0012\u0018\u00002\u00020\u00012\u00020\u0002:\u0003rstB\u0005¢\u0006\u0002\u0010\u0003J\u0006\u00103\u001a\u000204J\b\u00105\u001a\u000204H\u0016J\u0010\u00106\u001a\u0002042\u0006\u00107\u001a\u00020\u0007H\u0016JF\u00108\u001a\u00020\u00072\u0006\u00109\u001a\u00020\u00072\u0006\u0010:\u001a\u00020\u00072\u0006\u0010;\u001a\u00020\u00072\u0006\u0010<\u001a\u00020\u00072\u0006\u0010=\u001a\u00020>2\u0006\u0010?\u001a\u00020>2\u0006\u0010@\u001a\u00020>2\u0006\u0010A\u001a\u00020>J&\u0010B\u001a\u00020\u00072\u0006\u0010:\u001a\u00020\u00072\u0006\u0010;\u001a\u00020\u00072\u0006\u0010?\u001a\u00020>2\u0006\u0010@\u001a\u00020>J\b\u0010C\u001a\u000204H\u0016J\u0012\u0010D\u001a\u0004\u0018\u00010\u001f2\u0006\u0010E\u001a\u00020\u001fH\u0002J\u0010\u0010F\u001a\u0002042\u0006\u0010G\u001a\u00020HH\u0002J\b\u0010I\u001a\u000204H\u0003J\"\u0010J\u001a\u00020\u000b2\u0006\u0010E\u001a\u00020\u001f2\u0006\u0010K\u001a\u00020\u001f2\b\u0010L\u001a\u0004\u0018\u00010\u001fH\u0002J\u0010\u0010M\u001a\u0002042\u0006\u00107\u001a\u00020\u0007H\u0016J\b\u0010N\u001a\u000204H\u0016J\u0012\u0010O\u001a\u0002042\b\u0010P\u001a\u0004\u0018\u00010QH\u0016J\u0012\u0010R\u001a\u0002042\b\u0010S\u001a\u0004\u0018\u00010TH\u0014J\b\u0010U\u001a\u000204H\u0014J\b\u0010V\u001a\u000204H\u0016J\u001a\u0010W\u001a\u00020\u000b2\u0006\u0010X\u001a\u00020\u00072\b\u0010Y\u001a\u0004\u0018\u00010ZH\u0016J\u0010\u0010[\u001a\u0002042\u0006\u0010\\\u001a\u00020]H\u0017J\u0010\u0010^\u001a\u0002042\u0006\u00107\u001a\u00020\u0007H\u0016J\b\u0010_\u001a\u000204H\u0016J\u0016\u0010`\u001a\u0002042\f\u0010a\u001a\b\u0012\u0004\u0012\u00020c0bH\u0016J\b\u0010d\u001a\u000204H\u0014J\u0012\u0010e\u001a\u0002042\b\u0010f\u001a\u0004\u0018\u00010cH\u0016J\b\u0010g\u001a\u000204H\u0016J\b\u0010h\u001a\u000204H\u0016J\b\u0010i\u001a\u000204H\u0002J\b\u0010j\u001a\u000204H\u0016J\b\u0010k\u001a\u000204H\u0002J\u0010\u0010l\u001a\u0002042\u0006\u0010m\u001a\u00020\u001fH\u0002J\u0010\u0010)\u001a\u0002042\u0006\u0010n\u001a\u00020\u001fH\u0002J\b\u0010o\u001a\u000204H\u0003J\u0010\u0010p\u001a\u0002042\u0006\u0010q\u001a\u00020\u001fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0014\u001a\u00060\u0015R\u00020\u0000X\u0082.¢\u0006\u0002\n\u0000R\u001b\u0010\u0016\u001a\u00020\u00178BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u0018\u0010\u0019R\u0012\u0010\u001c\u001a\u00060\u001dR\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\"0!X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010#\u001a\b\u0012\u0004\u0012\u00020\u001f0\"X\u0082\u0004¢\u0006\u0004\n\u0002\u0010$R\u0012\u0010%\u001a\u00060&R\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020(X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010+\u001a\b\u0018\u00010,R\u00020-X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010/\u001a\u0004\u0018\u00010-*\u0002008F¢\u0006\u0006\u001a\u0004\b1\u00102¨\u0006u"}, m607d2 = {"Lcom/glasssutdio/wear/ota/OTAActivity;", "Lcom/glasssutdio/wear/manager/BaseSettingActivity;", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$WifiP2pCallback;", "()V", "FLAG_HOMEKEY_DISPATCHED", "", "PORT", "", "binding", "Lcom/glasssutdio/wear/databinding/ActivityOtaactivityBinding;", "bleCallbackSuccess", "", "dfuHandle", "Lcom/oudmon/ble/base/communication/DfuHandle;", "dfuOpResult", "Lcom/oudmon/ble/base/communication/DfuHandle$IOpResult;", "failRetry", "handler", "Landroid/os/Handler;", "isP2PConnecting", "otaListener", "Lcom/glasssutdio/wear/ota/OTAActivity$MyDeviceNotifyListener;", "otaViewModel", "Lcom/glasssutdio/wear/ota/OTAViewModel;", "getOtaViewModel", "()Lcom/glasssutdio/wear/ota/OTAViewModel;", "otaViewModel$delegate", "Lkotlin/Lazy;", "p2pConnectFailRunnable", "Lcom/glasssutdio/wear/ota/OTAActivity$P2pConnectFailRunnable;", "progressValue", "", "requestPermissionLaunch", "Landroidx/activity/result/ActivityResultLauncher;", "", "requestedPermissions", "[Ljava/lang/String;", "runnable", "Lcom/glasssutdio/wear/ota/OTAActivity$MyRunnable;", "serverSocket", "Ljava/net/ServerSocket;", "startServer", "systemSuccess", "wakeLock", "Landroid/os/PowerManager$WakeLock;", "Landroid/os/PowerManager;", "wifiFirmWareName", "powerManager", "Landroid/content/Context;", "getPowerManager", "(Landroid/content/Context;)Landroid/os/PowerManager;", "appDisconnect", "", "cancelConnect", "cancelConnectFail", "reason", "combineProgress", "progress0", "progress1", "progress2", "progress3", "weight0", "", "weight1", "weight2", "weight3", "combineProgressBle", "connecting", "getClientIPAddress", "ip1", "handleClient", "socket", "Ljava/net/Socket;", "initViews", "isSameSubnet", "ip2", "subnetMask", "onConnectRequestFailed", "onConnectRequestSent", "onConnected", "info", "Landroid/net/wifi/p2p/WifiP2pInfo;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onDisconnected", "onKeyDown", "keyCode", NotificationCompat.CATEGORY_EVENT, "Landroid/view/KeyEvent;", "onMessageEvent", "messageEvent", "Lcom/glasssutdio/wear/bus/BusEvent;", "onPeerDiscoveryFailed", "onPeerDiscoveryStarted", "onPeersChanged", "peers", "", "Landroid/net/wifi/p2p/WifiP2pDevice;", "onResume", "onThisDeviceChanged", "device", "onWifiP2pDisabled", "onWifiP2pEnabled", "otaFail", "retryAlsoFailed", "startBleOta", "startOta", "name", "ip", "startSocOta", "startSocOtaServer", "ipAddress", "MyDeviceNotifyListener", "MyRunnable", "P2pConnectFailRunnable", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class OTAActivity extends BaseSettingActivity implements WifiP2pManagerSingleton.WifiP2pCallback {
    private final long FLAG_HOMEKEY_DISPATCHED = 2147483648L;
    private final int PORT;
    private ActivityOtaactivityBinding binding;
    private boolean bleCallbackSuccess;
    private DfuHandle dfuHandle;
    private final DfuHandle.IOpResult dfuOpResult;
    private int failRetry;
    private final Handler handler;
    private boolean isP2PConnecting;
    private MyDeviceNotifyListener otaListener;

    /* renamed from: otaViewModel$delegate, reason: from kotlin metadata */
    private final Lazy otaViewModel;
    private P2pConnectFailRunnable p2pConnectFailRunnable;
    private String progressValue;
    private final ActivityResultLauncher<String[]> requestPermissionLaunch;
    private final String[] requestedPermissions;
    private MyRunnable runnable;
    private ServerSocket serverSocket;
    private boolean startServer;
    private boolean systemSuccess;
    private PowerManager.WakeLock wakeLock;
    private String wifiFirmWareName;

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void connecting() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public OTAActivity() {
        final OTAActivity oTAActivity = this;
        final Qualifier qualifier = null;
        final Object[] objArr = 0 == true ? 1 : 0;
        this.otaViewModel = LazyKt.lazy(new Function0<OTAViewModel>() { // from class: com.glasssutdio.wear.ota.OTAActivity$special$$inlined$viewModel$default$1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            /* JADX WARN: Type inference failed for: r0v1, types: [androidx.lifecycle.ViewModel, com.glasssutdio.wear.ota.OTAViewModel] */
            @Override // kotlin.jvm.functions.Function0
            public final OTAViewModel invoke() {
                return LifecycleOwnerExtKt.getViewModel(oTAActivity, Reflection.getOrCreateKotlinClass(OTAViewModel.class), qualifier, objArr);
            }
        });
        this.runnable = new MyRunnable();
        this.progressValue = "0";
        this.p2pConnectFailRunnable = new P2pConnectFailRunnable();
        this.handler = new Handler(Looper.getMainLooper());
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
        this.requestPermissionLaunch = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda2
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                OTAActivity.requestPermissionLaunch$lambda$2(this.f$0, (Map) obj);
            }
        });
        this.PORT = 8091;
        this.wifiFirmWareName = "";
        this.dfuOpResult = new OTAActivity$dfuOpResult$1(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final OTAViewModel getOtaViewModel() {
        return (OTAViewModel) this.otaViewModel.getValue();
    }

    @Override // com.glasssutdio.wear.manager.BaseSettingActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        ActivityOtaactivityBinding activityOtaactivityBindingInflate = ActivityOtaactivityBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(activityOtaactivityBindingInflate, "inflate(...)");
        this.binding = activityOtaactivityBindingInflate;
        if (activityOtaactivityBindingInflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityOtaactivityBindingInflate = null;
        }
        setContentView(activityOtaactivityBindingInflate.getRoot());
        getWindow().addFlags(128);
        initViews();
    }

    private final void initViews() {
        GlassesWearJavaApplication.getInstance().setOtaUpgrading(true);
        ActivityOtaactivityBinding activityOtaactivityBinding = this.binding;
        MyDeviceNotifyListener myDeviceNotifyListener = null;
        if (activityOtaactivityBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityOtaactivityBinding = null;
        }
        activityOtaactivityBinding.appBack.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                OTAActivity.initViews$lambda$4$lambda$3(this.f$0, view);
            }
        });
        DfuHandle dfuHandle = DfuHandle.getInstance();
        Intrinsics.checkNotNullExpressionValue(dfuHandle, "getInstance(...)");
        this.dfuHandle = dfuHandle;
        if (dfuHandle == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dfuHandle");
            dfuHandle = null;
        }
        dfuHandle.initCallback();
        WifiP2pManagerSingleton.INSTANCE.getInstance().removeCallback();
        WifiP2pManagerSingleton.INSTANCE.getInstance().registerReceiver();
        WifiP2pManagerSingleton.INSTANCE.getInstance().addCallback(this);
        OTAActivity oTAActivity = this;
        if (getPowerManager(oTAActivity) != null) {
            PowerManager powerManager = getPowerManager(oTAActivity);
            Intrinsics.checkNotNull(powerManager);
            PowerManager.WakeLock wakeLockNewWakeLock = powerManager.newWakeLock(536870913, "DeviceFirmwareUpdateActivity");
            this.wakeLock = wakeLockNewWakeLock;
            if (wakeLockNewWakeLock != null) {
                wakeLockNewWakeLock.acquire(600000L);
            }
        }
        this.handler.removeCallbacks(this.runnable);
        this.handler.postDelayed(this.runnable, DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
        getOtaViewModel().getUiState().observe(this, new OTAActivity$sam$androidx_lifecycle_Observer$0(new Function1<OTAViewModel.FirmwareUI, Unit>() { // from class: com.glasssutdio.wear.ota.OTAActivity.initViews.2
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(OTAViewModel.FirmwareUI firmwareUI) {
                invoke2(firmwareUI);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(final OTAViewModel.FirmwareUI firmwareUI) {
                int iCombineProgressBle;
                if (firmwareUI != null) {
                    ActivityOtaactivityBinding activityOtaactivityBinding2 = null;
                    if (firmwareUI.getRetCode() != 0) {
                        String string = OTAActivity.this.getString(C0775R.string.ble_glass_14);
                        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                        GlobalKt.showToast$default(string, 0, 1, null);
                        OTAActivity.this.finish();
                    }
                    OTAActivity.this.handler.removeCallbacks(OTAActivity.this.runnable);
                    OTAActivity.this.handler.postDelayed(OTAActivity.this.runnable, DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
                    OTAActivity.this.wifiFirmWareName = firmwareUI.getFileName();
                    final Ref.IntRef intRef = new Ref.IntRef();
                    if (OTAActivity.this.getOtaViewModel().getWifiNotBle()) {
                        iCombineProgressBle = OTAActivity.this.combineProgress((int) firmwareUI.getProgressBar(), 0, 0, 0, 1.0d, 0.0d, 0.0d, 0.0d);
                    } else {
                        iCombineProgressBle = OTAActivity.this.combineProgressBle((int) firmwareUI.getProgressBar(), 0, 1.0d, 0.0d);
                    }
                    intRef.element = iCombineProgressBle;
                    ActivityOtaactivityBinding activityOtaactivityBinding3 = OTAActivity.this.binding;
                    if (activityOtaactivityBinding3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                    } else {
                        activityOtaactivityBinding2 = activityOtaactivityBinding3;
                    }
                    activityOtaactivityBinding2.otaImage1.setImageResource(C0775R.mipmap.ota_file_download);
                    ThreadExtKt.ktxRunOnUi(OTAActivity.this, new Function1<OTAActivity, Unit>() { // from class: com.glasssutdio.wear.ota.OTAActivity.initViews.2.1
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        {
                            super(1);
                        }

                        @Override // kotlin.jvm.functions.Function1
                        public /* bridge */ /* synthetic */ Unit invoke(OTAActivity oTAActivity2) {
                            invoke2(oTAActivity2);
                            return Unit.INSTANCE;
                        }

                        /* renamed from: invoke, reason: avoid collision after fix types in other method */
                        public final void invoke2(OTAActivity ktxRunOnUi) {
                            Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                            XLog.m137i("progressBar>" + intRef.element + "---" + firmwareUI.getProgressBar());
                            ActivityOtaactivityBinding activityOtaactivityBinding4 = ktxRunOnUi.binding;
                            ActivityOtaactivityBinding activityOtaactivityBinding5 = null;
                            if (activityOtaactivityBinding4 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                activityOtaactivityBinding4 = null;
                            }
                            activityOtaactivityBinding4.progressValue.setProgress(intRef.element);
                            if (ktxRunOnUi.getOtaViewModel().getWifiNotBle()) {
                                ActivityOtaactivityBinding activityOtaactivityBinding6 = ktxRunOnUi.binding;
                                if (activityOtaactivityBinding6 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                                } else {
                                    activityOtaactivityBinding5 = activityOtaactivityBinding6;
                                }
                                Ref.IntRef intRef2 = intRef;
                                activityOtaactivityBinding5.tvTitle.setText(ktxRunOnUi.getString(C0775R.string.ble_glass_24));
                                TextView textView = activityOtaactivityBinding5.tvOta2;
                                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                                String string2 = ktxRunOnUi.getString(C0775R.string.ble_glass_25);
                                Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                                String str = String.format(string2, Arrays.copyOf(new Object[]{String.valueOf(intRef2.element)}, 1));
                                Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                                textView.setText(str);
                                return;
                            }
                            ActivityOtaactivityBinding activityOtaactivityBinding7 = ktxRunOnUi.binding;
                            if (activityOtaactivityBinding7 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                            } else {
                                activityOtaactivityBinding5 = activityOtaactivityBinding7;
                            }
                            Ref.IntRef intRef3 = intRef;
                            activityOtaactivityBinding5.tvTitle.setText(ktxRunOnUi.getString(C0775R.string.ble_glass_26));
                            TextView textView2 = activityOtaactivityBinding5.tvOta2;
                            StringCompanionObject stringCompanionObject2 = StringCompanionObject.INSTANCE;
                            String string3 = ktxRunOnUi.getString(C0775R.string.ble_glass_27);
                            Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                            String str2 = String.format(string3, Arrays.copyOf(new Object[]{String.valueOf(intRef3.element)}, 1));
                            Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
                            textView2.setText(str2);
                        }
                    });
                    OTAActivity.this.handler.removeCallbacks(OTAActivity.this.runnable);
                    OTAActivity.this.handler.postDelayed(OTAActivity.this.runnable, DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
                    if (firmwareUI.getSuccess()) {
                        XLog.m137i(firmwareUI.getFileName());
                        if (OTAActivity.this.getOtaViewModel().getWifiNotBle()) {
                            OTAActivity.this.handler.removeCallbacks(OTAActivity.this.runnable);
                            OTAActivity.this.wifiFirmWareName = firmwareUI.getFileName();
                            OTAActivity.this.requestPermissionLaunch.launch(OTAActivity.this.requestedPermissions);
                            return;
                        }
                        OTAActivity.this.startOta(firmwareUI.getFileName());
                    }
                }
            }
        }));
        Bundle extras = getIntent().getExtras();
        Integer numValueOf = extras != null ? Integer.valueOf(extras.getInt("background")) : null;
        if (numValueOf == null || numValueOf.intValue() != 1) {
            this.handler.postDelayed(new Runnable() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    OTAActivity.initViews$lambda$5(this.f$0);
                }
            }, 200L);
        }
        this.otaListener = new MyDeviceNotifyListener();
        LargeDataHandler largeDataHandler = LargeDataHandler.getInstance();
        MyDeviceNotifyListener myDeviceNotifyListener2 = this.otaListener;
        if (myDeviceNotifyListener2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("otaListener");
        } else {
            myDeviceNotifyListener = myDeviceNotifyListener2;
        }
        largeDataHandler.addOutDeviceListener(1, myDeviceNotifyListener);
        LargeDataHandler.getInstance().removeOutDeviceListener(100);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initViews$lambda$4$lambda$3(OTAActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initViews$lambda$5(OTAActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (UserConfig.INSTANCE.getInstance().getOtaDown()) {
            this$0.getOtaViewModel().checkOtaDown(UserConfig.INSTANCE.getInstance().getHwVersionWifi(), UserConfig.INSTANCE.getInstance().getFmVersionWifi(), true);
        } else {
            this$0.getOtaViewModel().checkOta(UserConfig.INSTANCE.getInstance().getHwVersionWifi(), UserConfig.INSTANCE.getInstance().getFmVersionWifi(), true);
        }
    }

    private final void startServer(final String ip) {
        this.startServer = true;
        new Thread(new Runnable() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() throws IOException {
                OTAActivity.startServer$lambda$8(ip, this);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startServer$lambda$8(String ip, final OTAActivity this$0) throws IOException {
        Intrinsics.checkNotNullParameter(ip, "$ip");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        try {
            final InetAddress byName = InetAddress.getByName(ip);
            this$0.serverSocket = new ServerSocket(this$0.PORT, 50, byName);
            this$0.runOnUiThread(new Runnable() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    OTAActivity.startServer$lambda$8$lambda$6(byName, this$0);
                }
            });
            while (true) {
                ServerSocket serverSocket = this$0.serverSocket;
                if (serverSocket == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("serverSocket");
                    serverSocket = null;
                }
                Socket socketAccept = serverSocket.accept();
                Intrinsics.checkNotNull(socketAccept);
                this$0.handleClient(socketAccept);
                XLog.m137i("111");
            }
        } catch (IOException e) {
            e.printStackTrace();
            this$0.otaFail();
            this$0.runOnUiThread(new Runnable() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    OTAActivity.startServer$lambda$8$lambda$7(e);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startServer$lambda$8$lambda$6(InetAddress inetAddress, OTAActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        XLog.m137i("Server started on ip $" + inetAddress);
        XLog.m137i("Server started on port " + this$0.PORT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startServer$lambda$8$lambda$7(IOException e) {
        Intrinsics.checkNotNullParameter(e, "$e");
        XLog.m137i("Server error: " + e.getMessage());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startSocOta() {
        this.handler.postDelayed(this.runnable, 15000L);
        ActivityOtaactivityBinding activityOtaactivityBinding = this.binding;
        ActivityOtaactivityBinding activityOtaactivityBinding2 = null;
        if (activityOtaactivityBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityOtaactivityBinding = null;
        }
        ViewKt.gone(activityOtaactivityBinding.appBack);
        ActivityOtaactivityBinding activityOtaactivityBinding3 = this.binding;
        if (activityOtaactivityBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityOtaactivityBinding3 = null;
        }
        ViewKt.visible(activityOtaactivityBinding3.cslOtaWifi);
        ActivityOtaactivityBinding activityOtaactivityBinding4 = this.binding;
        if (activityOtaactivityBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityOtaactivityBinding4 = null;
        }
        ViewKt.gone(activityOtaactivityBinding4.groupOta);
        ActivityOtaactivityBinding activityOtaactivityBinding5 = this.binding;
        if (activityOtaactivityBinding5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            activityOtaactivityBinding2 = activityOtaactivityBinding5;
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(activityOtaactivityBinding2.otaImage2, Key.ROTATION, 0.0f, 360.0f);
        objectAnimatorOfFloat.setDuration(2000L);
        objectAnimatorOfFloat.setInterpolator(new LinearInterpolator());
        objectAnimatorOfFloat.setRepeatCount(-1);
        objectAnimatorOfFloat.setRepeatMode(1);
        Intrinsics.checkNotNullExpressionValue(objectAnimatorOfFloat, "apply(...)");
        objectAnimatorOfFloat.start();
        LargeDataHandler.getInstance().glassesControl(new byte[]{2, 1, 5}, new ILargeDataResponse() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda3
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public final void parseData(int i, BaseResponse baseResponse) {
                OTAActivity.startSocOta$lambda$10(this.f$0, i, (GlassModelControlResponse) baseResponse);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startSocOta$lambda$10(OTAActivity this$0, int i, final GlassModelControlResponse glassModelControlResponse) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        XLog.m137i(GsonInstance.INSTANCE.getGson().toJson(glassModelControlResponse));
        if (glassModelControlResponse.getDataType() == 1 && glassModelControlResponse.getGlassWorkType() == 5) {
            this$0.handler.removeCallbacks(this$0.runnable);
            ThreadExtKt.ktxRunOnUi(this$0, new Function1<OTAActivity, Unit>() { // from class: com.glasssutdio.wear.ota.OTAActivity$startSocOta$1$1
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(OTAActivity oTAActivity) {
                    invoke2(oTAActivity);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke, reason: avoid collision after fix types in other method */
                public final void invoke2(OTAActivity ktxRunOnUi) {
                    Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                    ActivityOtaactivityBinding activityOtaactivityBinding = ktxRunOnUi.binding;
                    if (activityOtaactivityBinding == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        activityOtaactivityBinding = null;
                    }
                    activityOtaactivityBinding.tvTitle.setText(ktxRunOnUi.getString(C0775R.string.ble_glass_28));
                    if (glassModelControlResponse.getErrorCode() == 0) {
                        int workTypeIng = glassModelControlResponse.getWorkTypeIng();
                        if (workTypeIng != 1) {
                            if (workTypeIng == 2) {
                                String string = ktxRunOnUi.getString(C0775R.string.album_glass_36);
                                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                                GlobalKt.showToast$default(string, 0, 1, null);
                                return;
                            }
                            if (workTypeIng == 5) {
                                String string2 = ktxRunOnUi.getString(C0775R.string.album_glass_38);
                                Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                                GlobalKt.showToast$default(string2, 0, 1, null);
                                return;
                            } else if (workTypeIng != 6) {
                                if (workTypeIng == 7) {
                                    String string3 = ktxRunOnUi.getString(C0775R.string.album_glass_37);
                                    Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                                    GlobalKt.showToast$default(string3, 0, 1, null);
                                    return;
                                } else {
                                    if (workTypeIng != 8) {
                                        return;
                                    }
                                    String string4 = ktxRunOnUi.getString(C0775R.string.album_glass_38);
                                    Intrinsics.checkNotNullExpressionValue(string4, "getString(...)");
                                    GlobalKt.showToast$default(string4, 0, 1, null);
                                    return;
                                }
                            }
                        }
                        String string5 = ktxRunOnUi.getString(C0775R.string.album_glass_40);
                        Intrinsics.checkNotNullExpressionValue(string5, "getString(...)");
                        GlobalKt.showToast$default(string5, 0, 1, null);
                        return;
                    }
                    if (glassModelControlResponse.getOtaStatus() == 1) {
                        ktxRunOnUi.handler.postDelayed(ktxRunOnUi.p2pConnectFailRunnable, 40000L);
                        WifiP2pManagerSingleton.INSTANCE.getInstance().resetFailCount();
                        WifiP2pManagerSingleton.INSTANCE.getInstance().startPeerDiscovery();
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startSocOtaServer(String ipAddress) {
        XLog.m132e("systemSuccess" + this.systemSuccess + "bleCallbackSuccess" + this.bleCallbackSuccess);
        if (this.systemSuccess && this.bleCallbackSuccess) {
            if (this.startServer) {
                return;
            }
            WifiP2pManagerSingleton.INSTANCE.getInstance().setConnect(true);
            this.failRetry = 0;
            this.handler.removeCallbacks(this.p2pConnectFailRunnable);
            this.systemSuccess = false;
            this.bleCallbackSuccess = false;
            startServer(ipAddress);
            String str = "http://" + ipAddress + ':' + this.PORT + IOUtils.DIR_SEPARATOR_UNIX + this.wifiFirmWareName;
            XLog.m132e(str);
            ThreadExtKt.ktxRunOnUiDelay(this, 1000L, new C11161(str));
            return;
        }
        XLog.m132e("systemSuccess:" + this.systemSuccess + ",bleCallbackSuccess:" + this.bleCallbackSuccess);
    }

    /* compiled from: OTAActivity.kt */
    @Metadata(m606d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n¢\u0006\u0002\b\u0003"}, m607d2 = {"<anonymous>", "", "Lcom/glasssutdio/wear/ota/OTAActivity;", "invoke"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    /* renamed from: com.glasssutdio.wear.ota.OTAActivity$startSocOtaServer$1 */
    static final class C11161 extends Lambda implements Function1<OTAActivity, Unit> {
        final /* synthetic */ String $url;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C11161(String str) {
            super(1);
            this.$url = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void invoke$lambda$0(int i, BatteryResponse batteryResponse) {
        }

        @Override // kotlin.jvm.functions.Function1
        public /* bridge */ /* synthetic */ Unit invoke(OTAActivity oTAActivity) {
            invoke2(oTAActivity);
            return Unit.INSTANCE;
        }

        /* renamed from: invoke, reason: avoid collision after fix types in other method */
        public final void invoke2(OTAActivity ktxRunOnUiDelay) {
            Intrinsics.checkNotNullParameter(ktxRunOnUiDelay, "$this$ktxRunOnUiDelay");
            LargeDataHandler.getInstance().writeIpToSoc(this.$url, new ILargeDataResponse() { // from class: com.glasssutdio.wear.ota.OTAActivity$startSocOtaServer$1$$ExternalSyntheticLambda0
                @Override // com.oudmon.ble.base.communication.ILargeDataResponse
                public final void parseData(int i, BaseResponse baseResponse) {
                    OTAActivity.C11161.invoke$lambda$0(i, (BatteryResponse) baseResponse);
                }
            });
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (!BleOperateManager.getInstance().isConnected()) {
            this.progressValue = "0";
            this.handler.removeCallbacks(this.runnable);
            finish();
        }
        if (NetWorkUtils.INSTANCE.isNetworkAvailable(this)) {
            return;
        }
        String string = getString(C0775R.string.ai_glass_1);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        GlobalKt.showToast$default(string, 0, 1, null);
        finish();
    }

    @Override // com.glasssutdio.wear.manager.BaseSettingActivity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BusEvent messageEvent) {
        Intrinsics.checkNotNullParameter(messageEvent, "messageEvent");
        if (messageEvent instanceof BluetoothEvent) {
            if (((BluetoothEvent) messageEvent).getConnect()) {
                return;
            }
            this.progressValue = "0";
            this.handler.removeCallbacks(this.runnable);
            finish();
            return;
        }
        if (messageEvent instanceof OTAFileStatusEvent) {
            try {
                if (AppUtil.isBackground(this) || AppUtil.isApplicationBroughtToBackground(this)) {
                    this.handler.removeCallbacks(this.runnable);
                    this.handler.postDelayed(this.runnable, DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
                    if (((OTAFileStatusEvent) messageEvent).getType() == 3) {
                        ((OTAFileStatusEvent) messageEvent).getSuccess();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startOta(String name) {
        File file = new File(GFileUtilKt.getBinDirFile(), name);
        if (file.exists()) {
            String absolutePath = file.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
            if (absolutePath.length() == 0) {
                otaFail();
            }
            String absolutePath2 = file.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath2, "getAbsolutePath(...)");
            DfuHandle dfuHandle = null;
            if (!StringsKt.contains$default((CharSequence) absolutePath2, (CharSequence) "bin", false, 2, (Object) null)) {
                otaFail();
            }
            String absolutePath3 = file.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath3, "getAbsolutePath(...)");
            if (absolutePath3.length() > 0) {
                DfuHandle dfuHandle2 = this.dfuHandle;
                if (dfuHandle2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("dfuHandle");
                    dfuHandle2 = null;
                }
                if (dfuHandle2.checkFile(file.getAbsolutePath())) {
                    DfuHandle dfuHandle3 = this.dfuHandle;
                    if (dfuHandle3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("dfuHandle");
                    } else {
                        dfuHandle = dfuHandle3;
                    }
                    dfuHandle.start(this.dfuOpResult);
                    return;
                }
                return;
            }
            otaFail();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void otaFail() {
        ThreadExtKt.ktxRunOnUi(this, new Function1<OTAActivity, Unit>() { // from class: com.glasssutdio.wear.ota.OTAActivity.otaFail.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(OTAActivity oTAActivity) {
                invoke2(oTAActivity);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(OTAActivity ktxRunOnUi) {
                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                String string = ktxRunOnUi.getString(C0775R.string.ble_glass_13);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                GlobalKt.showToast$default(string, 0, 1, null);
                ktxRunOnUi.finish();
            }
        });
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 || keyCode == 3) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public final void appDisconnect() {
        if (BleOperateManager.getInstance().isConnected()) {
            BleOperateManager.getInstance().disconnect();
        }
        String string = getString(C0775R.string.ble_glass_14);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        GlobalKt.showToast$default(string, 0, 1, null);
        this.progressValue = "0";
        EventBus.getDefault().post(new EventType(11));
        finish();
    }

    @Override // com.glasssutdio.wear.manager.BaseSettingActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags((int) this.FLAG_HOMEKEY_DISPATCHED);
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null) {
            wakeLock.release();
        }
        this.handler.removeCallbacks(this.runnable);
        try {
            WifiP2pManagerSingleton.INSTANCE.getInstance().unregisterReceiver();
            WifiP2pManagerSingleton.INSTANCE.getInstance().removeCallback();
            LargeDataHandler.getInstance().syncDeviceInfo(new ILargeDataResponse() { // from class: com.glasssutdio.wear.ota.OTAActivity$$ExternalSyntheticLambda0
                @Override // com.oudmon.ble.base.communication.ILargeDataResponse
                public final void parseData(int i, BaseResponse baseResponse) {
                    OTAActivity.onDestroy$lambda$11(i, (DeviceInfoResponse) baseResponse);
                }
            });
            LargeDataHandler.getInstance().removeOutDeviceListener(1);
            GlassesWearJavaApplication.getInstance().setOtaUpgrading(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onDestroy$lambda$11(int i, DeviceInfoResponse deviceInfoResponse) {
        if (deviceInfoResponse != null) {
            UserConfig companion = UserConfig.INSTANCE.getInstance();
            String wifiFirmwareVersion = deviceInfoResponse.getWifiFirmwareVersion();
            Intrinsics.checkNotNullExpressionValue(wifiFirmwareVersion, "getWifiFirmwareVersion(...)");
            companion.setFmVersionWifi(wifiFirmwareVersion);
            UserConfig companion2 = UserConfig.INSTANCE.getInstance();
            String wifiHardwareVersion = deviceInfoResponse.getWifiHardwareVersion();
            Intrinsics.checkNotNullExpressionValue(wifiHardwareVersion, "getWifiHardwareVersion(...)");
            companion2.setHwVersionWifi(wifiHardwareVersion);
        }
    }

    /* compiled from: OTAActivity.kt */
    @Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, m607d2 = {"Lcom/glasssutdio/wear/ota/OTAActivity$MyRunnable;", "Ljava/lang/Runnable;", "(Lcom/glasssutdio/wear/ota/OTAActivity;)V", "run", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class MyRunnable implements Runnable {
        public MyRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            OTAActivity.this.otaFail();
        }
    }

    private final void handleClient(Socket socket) throws IOException {
        try {
            OutputStream outputStream = socket.getOutputStream();
            Intrinsics.checkNotNullExpressionValue(outputStream, "getOutputStream(...)");
            File file = new File(GFileUtilKt.getBinDirFile(), this.wifiFirmWareName);
            if (!file.exists()) {
                byte[] bytes = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\nContent-Length: 0\r\nConnection: close\r\n\r\n".getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                outputStream.write(bytes);
                return;
            }
            byte[] bytes2 = ("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + file.length() + "\r\nContent-Disposition: attachment; filename=\"" + file.getName() + "\"\r\nConnection: close\r\n\r\n").getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            outputStream.write(bytes2);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bArr = new byte[4096];
            while (true) {
                int i = fileInputStream.read(bArr);
                if (i == -1) {
                    return;
                } else {
                    outputStream.write(bArr, 0, i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String getClientIPAddress(String ip1) throws SocketException {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Intrinsics.checkNotNullExpressionValue(networkInterfaces, "getNetworkInterfaces(...)");
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterfaceNextElement = networkInterfaces.nextElement();
                Intrinsics.checkNotNullExpressionValue(networkInterfaceNextElement, "nextElement(...)");
                Enumeration<InetAddress> inetAddresses = networkInterfaceNextElement.getInetAddresses();
                Intrinsics.checkNotNullExpressionValue(inetAddresses, "getInetAddresses(...)");
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddressNextElement = inetAddresses.nextElement();
                    Intrinsics.checkNotNullExpressionValue(inetAddressNextElement, "nextElement(...)");
                    InetAddress inetAddress = inetAddressNextElement;
                    String hostAddress = inetAddress.getHostAddress();
                    Intrinsics.checkNotNull(hostAddress);
                    if (isSameSubnet(ip1, hostAddress.toString(), "255.255.255.0")) {
                        XLog.m137i(inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private final boolean isSameSubnet(String ip1, String ip2, String subnetMask) throws UnknownHostException {
        try {
            InetAddress byName = InetAddress.getByName(ip1);
            InetAddress byName2 = InetAddress.getByName(ip2);
            InetAddress byName3 = InetAddress.getByName(subnetMask);
            byte[] address = byName.getAddress();
            byte[] address2 = byName2.getAddress();
            byte[] address3 = byName3.getAddress();
            int length = address.length;
            for (int i = 0; i < length; i++) {
                byte b = address[i];
                byte b2 = address3[i];
                if ((b & b2) != (b2 & address2[i])) {
                    return false;
                }
            }
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startBleOta() {
        getOtaViewModel().checkOta(UserConfig.INSTANCE.getInstance().getHwVersion(), UserConfig.INSTANCE.getInstance().getFmVersion(), false);
    }

    /* compiled from: OTAActivity.kt */
    @Metadata(m606d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0017¨\u0006\t"}, m607d2 = {"Lcom/glasssutdio/wear/ota/OTAActivity$MyDeviceNotifyListener;", "Lcom/oudmon/ble/base/communication/bigData/resp/GlassesDeviceNotifyListener;", "(Lcom/glasssutdio/wear/ota/OTAActivity;)V", "parseData", "", "cmdType", "", "response", "Lcom/oudmon/ble/base/communication/bigData/resp/GlassesDeviceNotifyRsp;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class MyDeviceNotifyListener extends GlassesDeviceNotifyListener {
        public MyDeviceNotifyListener() {
        }

        @Override // com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyListener, com.oudmon.ble.base.communication.ILargeDataResponse
        public void parseData(int cmdType, GlassesDeviceNotifyRsp response) throws SocketException {
            Intrinsics.checkNotNullParameter(response, "response");
            byte b = response.getLoadData()[6];
            ActivityOtaactivityBinding activityOtaactivityBinding = null;
            if (b != 4) {
                if (b != 7) {
                    if (b != 8) {
                        return;
                    }
                    String string = new StringBuilder().append(ByteUtil.byteToInt(response.getLoadData()[7])).append(FilenameUtils.EXTENSION_SEPARATOR).append(ByteUtil.byteToInt(response.getLoadData()[8])).append(FilenameUtils.EXTENSION_SEPARATOR).append(ByteUtil.byteToInt(response.getLoadData()[9])).append(FilenameUtils.EXTENSION_SEPARATOR).append(ByteUtil.byteToInt(response.getLoadData()[10])).toString();
                    XLog.m132e("蓝牙返回ip:" + string);
                    OTAActivity.this.bleCallbackSuccess = true;
                    String clientIPAddress = OTAActivity.this.getClientIPAddress(string);
                    XLog.m132e("ipAddress:" + clientIPAddress);
                    if (clientIPAddress != null) {
                        OTAActivity.this.startSocOtaServer(clientIPAddress);
                        return;
                    } else {
                        OTAActivity.this.startSocOtaServer("");
                        return;
                    }
                }
                boolean z = response.getLoadData()[7] == 1;
                if (z) {
                    ActivityOtaactivityBinding activityOtaactivityBinding2 = OTAActivity.this.binding;
                    if (activityOtaactivityBinding2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        activityOtaactivityBinding2 = null;
                    }
                    activityOtaactivityBinding2.tvTitle.setText(OTAActivity.this.getString(C0775R.string.ble_glass_28));
                    ActivityOtaactivityBinding activityOtaactivityBinding3 = OTAActivity.this.binding;
                    if (activityOtaactivityBinding3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        activityOtaactivityBinding3 = null;
                    }
                    activityOtaactivityBinding3.progressValue.setProgress(100);
                    ActivityOtaactivityBinding activityOtaactivityBinding4 = OTAActivity.this.binding;
                    if (activityOtaactivityBinding4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                    } else {
                        activityOtaactivityBinding = activityOtaactivityBinding4;
                    }
                    TextView textView = activityOtaactivityBinding.tvOta2;
                    StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                    String string2 = OTAActivity.this.getString(C0775R.string.ble_glass_29);
                    Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                    String str = String.format(string2, Arrays.copyOf(new Object[]{"100"}, 1));
                    Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                    textView.setText(str);
                    OTAActivity.this.startBleOta();
                    EventBus.getDefault().post(new EventType(12));
                }
                XLog.m136i(Boolean.valueOf(z));
                return;
            }
            try {
                ActivityOtaactivityBinding activityOtaactivityBinding5 = OTAActivity.this.binding;
                if (activityOtaactivityBinding5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityOtaactivityBinding5 = null;
                }
                ViewKt.gone(activityOtaactivityBinding5.cslOtaWifi);
                ActivityOtaactivityBinding activityOtaactivityBinding6 = OTAActivity.this.binding;
                if (activityOtaactivityBinding6 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityOtaactivityBinding6 = null;
                }
                ViewKt.visible(activityOtaactivityBinding6.groupOta);
                ActivityOtaactivityBinding activityOtaactivityBinding7 = OTAActivity.this.binding;
                if (activityOtaactivityBinding7 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityOtaactivityBinding7 = null;
                }
                activityOtaactivityBinding7.otaImage1.setImageResource(C0775R.mipmap.ota_file_update_to_device);
                byte b2 = response.getLoadData()[7];
                byte b3 = response.getLoadData()[8];
                byte b4 = response.getLoadData()[9];
                XLog.m137i(((int) b2) + HelpFormatter.DEFAULT_LONG_OPT_PREFIX + ((int) b3) + "---" + ((int) b4));
                ActivityOtaactivityBinding activityOtaactivityBinding8 = OTAActivity.this.binding;
                if (activityOtaactivityBinding8 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityOtaactivityBinding8 = null;
                }
                activityOtaactivityBinding8.tvTitle.setText(OTAActivity.this.getString(C0775R.string.ble_glass_28));
                OTAActivity.this.handler.removeCallbacks(OTAActivity.this.runnable);
                OTAActivity.this.handler.postDelayed(OTAActivity.this.runnable, 60000L);
                int iCombineProgress = OTAActivity.this.combineProgress(100, b2, b3, b4, 0.0d, 0.29d, 0.01d, 0.7d);
                ActivityOtaactivityBinding activityOtaactivityBinding9 = OTAActivity.this.binding;
                if (activityOtaactivityBinding9 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityOtaactivityBinding9 = null;
                }
                activityOtaactivityBinding9.progressValue.setProgress(iCombineProgress);
                ActivityOtaactivityBinding activityOtaactivityBinding10 = OTAActivity.this.binding;
                if (activityOtaactivityBinding10 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    activityOtaactivityBinding = activityOtaactivityBinding10;
                }
                TextView textView2 = activityOtaactivityBinding.tvOta2;
                StringCompanionObject stringCompanionObject2 = StringCompanionObject.INSTANCE;
                String string3 = OTAActivity.this.getString(C0775R.string.ble_glass_29);
                Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                String str2 = String.format(string3, Arrays.copyOf(new Object[]{String.valueOf(iCombineProgress)}, 1));
                Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
                textView2.setText(str2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final int combineProgress(int progress0, int progress1, int progress2, int progress3, double weight0, double weight1, double weight2, double weight3) {
        if (weight0 + weight1 + weight2 + weight3 != 1.0d) {
            XLog.m137i("权重之和必须为 1");
            throw new IllegalArgumentException(Unit.INSTANCE.toString());
        }
        return (int) Math.round((progress0 * weight0) + (progress1 * weight1) + (progress2 * weight2) + (progress3 * weight3));
    }

    public final int combineProgressBle(int progress1, int progress2, double weight1, double weight2) {
        if (weight1 + weight2 != 1.0d) {
            XLog.m137i("权重之和必须为 1");
            throw new IllegalArgumentException(Unit.INSTANCE.toString());
        }
        return (int) Math.round((progress1 * weight1) + (progress2 * weight2));
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
        if (peers.isEmpty() || this.isP2PConnecting) {
            return;
        }
        for (WifiP2pDevice wifiP2pDevice : peers) {
            if (!StringsKt.equals(wifiP2pDevice.deviceName, UserConfig.INSTANCE.getInstance().getGlassDeviceWifiName(), true)) {
                String deviceName = wifiP2pDevice.deviceName;
                Intrinsics.checkNotNullExpressionValue(deviceName, "deviceName");
                if (StringsKt.endsWith$default(deviceName, UserConfig.INSTANCE.getInstance().getDeviceAddressNoClear(), false, 2, (Object) null)) {
                }
            }
            XLog.m137i(wifiP2pDevice.deviceName + " 1+++++ " + wifiP2pDevice.deviceAddress);
            WifiP2pManagerSingleton.INSTANCE.getInstance().connectToDevice(wifiP2pDevice);
            this.isP2PConnecting = true;
            return;
        }
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onConnected(WifiP2pInfo info) throws SocketException {
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
            XLog.m132e(string);
            XLog.m132e("isGroupOwner:" + info.isGroupOwner);
            this.systemSuccess = true;
            if (info.isGroupOwner || (hostAddress = info.groupOwnerAddress.getHostAddress()) == null) {
                return;
            }
            String clientIPAddress = getClientIPAddress(hostAddress);
            XLog.m132e("ipAddress112:" + clientIPAddress);
            if (clientIPAddress != null) {
                startSocOtaServer(clientIPAddress);
            } else {
                startSocOtaServer("");
            }
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
        XLog.m137i("P2P 开始扫描");
        this.isP2PConnecting = false;
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onPeerDiscoveryFailed(int reason) {
        XLog.m137i("P2P 扫描失败原因:" + reason);
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onConnectRequestSent() {
        XLog.m137i("P2P 发送连接请求成功");
    }

    @Override // com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.WifiP2pCallback
    public void onConnectRequestFailed(int reason) {
        XLog.m137i("P2P 发送连接请求失败:" + reason);
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
        XLog.m137i("retryAlsoFailed");
    }

    /* compiled from: OTAActivity.kt */
    @Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, m607d2 = {"Lcom/glasssutdio/wear/ota/OTAActivity$P2pConnectFailRunnable;", "Ljava/lang/Runnable;", "(Lcom/glasssutdio/wear/ota/OTAActivity;)V", "run", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class P2pConnectFailRunnable implements Runnable {
        public P2pConnectFailRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (OTAActivity.this.failRetry < 3) {
                try {
                    XLog.m137i("p2p连接超时,准备再次走流程");
                    if (UserConfig.INSTANCE.getInstance().getDebug()) {
                        ThreadExtKt.ktxRunOnUi(this, new Function1<P2pConnectFailRunnable, Unit>() { // from class: com.glasssutdio.wear.ota.OTAActivity$P2pConnectFailRunnable$run$1
                            @Override // kotlin.jvm.functions.Function1
                            public /* bridge */ /* synthetic */ Unit invoke(OTAActivity.P2pConnectFailRunnable p2pConnectFailRunnable) {
                                invoke2(p2pConnectFailRunnable);
                                return Unit.INSTANCE;
                            }

                            /* renamed from: invoke, reason: avoid collision after fix types in other method */
                            public final void invoke2(OTAActivity.P2pConnectFailRunnable ktxRunOnUi) {
                                Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                                GlobalKt.showToast$default("p2p连接超时,准备再次走流程", 0, 1, null);
                            }
                        });
                    }
                    OTAActivity.this.systemSuccess = false;
                    OTAActivity.this.bleCallbackSuccess = false;
                    OTAActivity.this.startSocOta();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OTAActivity.this.failRetry++;
                return;
            }
            if (UserConfig.INSTANCE.getInstance().getDebug()) {
                ThreadExtKt.ktxRunOnUi(this, new Function1<P2pConnectFailRunnable, Unit>() { // from class: com.glasssutdio.wear.ota.OTAActivity$P2pConnectFailRunnable$run$2
                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(OTAActivity.P2pConnectFailRunnable p2pConnectFailRunnable) {
                        invoke2(p2pConnectFailRunnable);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke, reason: avoid collision after fix types in other method */
                    public final void invoke2(OTAActivity.P2pConnectFailRunnable ktxRunOnUi) {
                        Intrinsics.checkNotNullParameter(ktxRunOnUi, "$this$ktxRunOnUi");
                        GlobalKt.showToast$default("p2p连接失败", 0, 1, null);
                    }
                });
            }
            OTAActivity.this.handler.removeCallbacks(OTAActivity.this.p2pConnectFailRunnable);
            OTAActivity.this.otaFail();
        }
    }

    public final PowerManager getPowerManager(Context context) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        return (PowerManager) ContextCompat.getSystemService(context, PowerManager.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void requestPermissionLaunch$lambda$2(OTAActivity this$0, Map it) {
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
        XLog.m137i("已获得全部权限");
        this$0.startSocOta();
    }
}
