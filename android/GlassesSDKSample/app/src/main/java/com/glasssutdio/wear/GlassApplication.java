package com.glasssutdio.wear;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Process;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDexApplication;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.Flattener2;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.PrinterSet;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.glasssutdio.wear.all.Localization;
import com.glasssutdio.wear.all.lifecycle.GlassLifeCycle;
import com.glasssutdio.wear.all.pref.MMKVConfig;
import com.glasssutdio.wear.all.pref.SpUtils;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.DateUtil;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.ble.receiver.BluetoothReceiver;
import com.glasssutdio.wear.ble.receiver.MyBluetoothReceiver;
import com.glasssutdio.wear.ble.receiver.SystemLocaleChangeReceiver;
import com.glasssutdio.wear.manager.KoinModuleKt;
import com.google.firebase.crashlytics.buildtools.ndk.internal.elf.EMachine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.IOUtils;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;
import com.iflytek.sparkchain.core.common.ApiType;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.OkDownloadProvider;
import com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.oudmon.ble.base.bluetooth.BleAction;
import com.oudmon.ble.base.bluetooth.BleBaseControl;
import com.oudmon.ble.base.bluetooth.BleOperateManager;
import com.oudmon.ble.base.communication.LargeDataHandler;
import com.tencent.mmkv.MMKV;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;
import org.koin.android.ext.koin.KoinExtKt;
import org.koin.core.KoinApplication;
import org.koin.core.context.GlobalContextKt;

/* compiled from: GlassApplication.kt */
@Metadata(m606d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010%\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 &2\u00020\u0001:\u0001&B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0005J\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u0015J\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u0018H\u0002J\b\u0010\u001c\u001a\u00020\u001aH\u0002J\u0006\u0010\u001d\u001a\u00020\u001aJ\b\u0010\u001e\u001a\u00020\u0018H\u0002J\u000e\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00180 H\u0002J\u0011\u0010!\u001a\u00020\u0018H\u0082@ø\u0001\u0000¢\u0006\u0002\u0010\"J\b\u0010#\u001a\u00020\u001aH\u0002J\b\u0010$\u001a\u00020\u001aH\u0016J\b\u0010%\u001a\u00020\rH\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006'"}, m607d2 = {"Lcom/glasssutdio/wear/GlassApplication;", "Landroidx/multidex/MultiDexApplication;", "()V", "aiLanguageMap", "", "", "filterPrefixMap", "", "gLifeCycle", "Lcom/glasssutdio/wear/all/lifecycle/GlassLifeCycle;", "job", "Lkotlinx/coroutines/CoroutineScope;", "pingGoogle", "", "getPingGoogle", "()Z", "setPingGoogle", "(Z)V", "getAiLanguageValueByKey", "appLanguage", "getDeviceKeys", "", "getKeysMap", "initAppParams", "", "initBle", "", "initOkDownLoad", "initReceiver", "initSpark", "initSparkChain", "initSparkChainInCoroutine", "Lkotlinx/coroutines/Deferred;", "initSparkChainSync", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "initXLog", "onCreate", "shouldInit", "Companion", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class GlassApplication extends MultiDexApplication {
    private static GlassApplication instance;
    private boolean pingGoogle;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final ReadWriteProperty<Object, Context> CONTEXT$delegate = Delegates.INSTANCE.notNull();
    private static final Lazy<GlassApplication> getInstance$delegate = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, (Function0) new Function0<GlassApplication>() { // from class: com.glasssutdio.wear.GlassApplication$Companion$getInstance$2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final GlassApplication invoke() {
            return new GlassApplication();
        }
    });
    private Map<String, String> filterPrefixMap = new LinkedHashMap();
    private final GlassLifeCycle gLifeCycle = new GlassLifeCycle();
    private final CoroutineScope job = CoroutineScopeKt.CoroutineScope(Dispatchers.getMain().plus(SupervisorKt.SupervisorJob$default((Job) null, 1, (Object) null)));
    private final Map<String, String> aiLanguageMap = MapsKt.mapOf(TuplesKt.m614to("cn", "告诉我你看到了什么？"), TuplesKt.m614to("en", "Tell me what you see."), TuplesKt.m614to(Localization.language, "Dis-moi ce que tu vois."), TuplesKt.m614to("de", "Sag mir, was du siehst."), TuplesKt.m614to("es", "Dime qué ves."), TuplesKt.m614to("ja", "何が見えるか教えてください。"), TuplesKt.m614to("it", "Dimmi cosa vedi."), TuplesKt.m614to("hi", "तुम्हें क्या दिख रहा है बताओ।"), TuplesKt.m614to("ko", "무엇이 보이는지 말해 주세요."), TuplesKt.m614to("th", "บอกฉันหน่อยว่าคุณเห็นอะไร"), TuplesKt.m614to("ru", "Скажи мне, что ты видишь."), TuplesKt.m614to("vi", "Hãy nói cho tôi biết bạn thấy gì."), TuplesKt.m614to("ms", "Beritahu saya apa yang anda lihat."), TuplesKt.m614to(BreakpointSQLiteKey.f521ID, "Katakan padaku apa yang kamu lihat."), TuplesKt.m614to("el", "Πες μου τι βλέπεις."), TuplesKt.m614to("cs", "Řekni mi, co vidíš."), TuplesKt.m614to("ro", "Spune-mi ce vezi."), TuplesKt.m614to("sv", "Berätta för mig vad du ser."), TuplesKt.m614to("nl", "Vertel me wat je ziet."), TuplesKt.m614to("pl", "Powiedz mi, co widzisz."), TuplesKt.m614to("pt", "Diz-me o que vês."), TuplesKt.m614to("ar", "أخبرني ماذا ترى."), TuplesKt.m614to("fa", "به من بگو چه می\u200cبینی."), TuplesKt.m614to("ur", "مجھے بتاؤ کہ تم کیا دیکھ رہے ہو۔"), TuplesKt.m614to("tr", "Ne gördüğünü bana söyle."));

    public final boolean getPingGoogle() {
        return this.pingGoogle;
    }

    public final void setPingGoogle(boolean z) {
        this.pingGoogle = z;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (shouldInit()) {
            long jInitAppParams = initAppParams();
            long jInitOkDownLoad = initOkDownLoad();
            if (UserConfig.INSTANCE.getInstance().getUniqueIdHw().length() > 0) {
                BuildersKt__Builders_commonKt.launch$default(this.job, null, null, new C07681(jInitAppParams, jInitOkDownLoad, null), 3, null);
            }
        }
    }

    /* compiled from: GlassApplication.kt */
    @Metadata(m606d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, m607d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.GlassApplication$onCreate$1", m620f = "GlassApplication.kt", m621i = {}, m622l = {EMachine.EM_ALTERA_NIOS2}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.GlassApplication$onCreate$1 */
    static final class C07681 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ long $downTime;
        final /* synthetic */ long $initTime;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C07681(long j, long j2, Continuation<? super C07681> continuation) {
            super(2, continuation);
            this.$initTime = j;
            this.$downTime = j2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return GlassApplication.this.new C07681(this.$initTime, this.$downTime, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C07681) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                obj = GlassApplication.this.initSparkChainInCoroutine().await(this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            XLog.m137i("初始化耗时：" + this.$initTime + ",down:" + this.$downTime + ",spark:" + ((Number) obj).longValue());
            CoroutineScopeKt.cancel$default(GlassApplication.this.job, null, 1, null);
            return Unit.INSTANCE;
        }
    }

    /* compiled from: GlassApplication.kt */
    @Metadata(m606d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, m607d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.GlassApplication$initSpark$1", m620f = "GlassApplication.kt", m621i = {}, m622l = {123}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.GlassApplication$initSpark$1 */
    static final class C07671 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;

        C07671(Continuation<? super C07671> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return GlassApplication.this.new C07671(continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C07671) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                obj = GlassApplication.this.initSparkChainInCoroutine().await(this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            XLog.m137i("初始化耗时：spark:" + ((Number) obj).longValue());
            CoroutineScopeKt.cancel$default(GlassApplication.this.job, null, 1, null);
            return Unit.INSTANCE;
        }
    }

    public final void initSpark() {
        BuildersKt__Builders_commonKt.launch$default(this.job, null, null, new C07671(null), 3, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long initSparkChain() {
        String str;
        long jCurrentTimeMillis = System.currentTimeMillis();
        int iInit = SparkChain.getInst().init(INSTANCE.getCONTEXT(), SparkChainConfig.builder().appID("51437164").apiKey("cece7673de5f4b110064bf00d68adfce").apiSecret("MzYwYzE0YzE4M2U2MmRkMGU3NjY0ZmU1").uid(UserConfig.INSTANCE.getInstance().getUniqueIdHw()).apiType(ApiType.TYPE_SG).logPath(GFileUtilKt.getLogDirFile().getAbsolutePath() + IOUtils.DIR_SEPARATOR_UNIX + new DateUtil().getY_M_D() + "_1.txt").logLevel(666));
        if (iInit == 0) {
            str = "SDK初始化成功,请选择相应的功能点击体验。";
        } else {
            str = "SDK初始化失败,错误码:" + iInit;
        }
        XLog.m137i(str);
        return System.currentTimeMillis() - jCurrentTimeMillis;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Deferred<Long> initSparkChainInCoroutine() {
        return BuildersKt__Builders_commonKt.async$default(CoroutineScopeKt.CoroutineScope(Dispatchers.getIO()), null, null, new GlassApplication$initSparkChainInCoroutine$deferred$1(this, null), 3, null);
    }

    private final boolean shouldInit() {
        Object systemService = getSystemService("activity");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.ActivityManager");
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) systemService).getRunningAppProcesses();
        String packageName = getPackageName();
        int iMyPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == iMyPid && Intrinsics.areEqual(packageName, runningAppProcessInfo.processName)) {
                return true;
            }
        }
        return false;
    }

    private final long initAppParams() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        GlassesWearJavaApplication.getInstance().setApplication(this);
        initXLog();
        XLog.m132e("mmkv root: " + MMKV.initialize(this));
        MMKVConfig.INSTANCE.getInstance();
        SpUtils.getInstance();
        GlobalContextKt.startKoin(new Function1<KoinApplication, Unit>() { // from class: com.glasssutdio.wear.GlassApplication.initAppParams.1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(KoinApplication koinApplication) {
                invoke2(koinApplication);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(KoinApplication startKoin) {
                Intrinsics.checkNotNullParameter(startKoin, "$this$startKoin");
                Companion companion = GlassApplication.INSTANCE;
                Context applicationContext = GlassApplication.this.getApplicationContext();
                Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
                companion.setCONTEXT(applicationContext);
                KoinExtKt.androidContext(startKoin, GlassApplication.this);
                startKoin.modules(KoinModuleKt.getAppModule());
            }
        });
        initBle();
        registerActivityLifecycleCallbacks(this.gLifeCycle);
        UserConfig.INSTANCE.getInstance().setScanKeyFilter("QGlasses,Glasses,HeyCyan,G100,V03,A08,AM01");
        return System.currentTimeMillis() - jCurrentTimeMillis;
    }

    private final void initBle() {
        initReceiver();
        IntentFilter intentFilter = BleAction.getIntentFilter();
        MyBluetoothReceiver myBluetoothReceiver = new MyBluetoothReceiver();
        Companion companion = INSTANCE;
        LocalBroadcastManager.getInstance(companion.getCONTEXT()).registerReceiver(myBluetoothReceiver, intentFilter);
        BleBaseControl.getInstance(companion.getCONTEXT()).setmContext(this);
    }

    private final void initReceiver() {
        LargeDataHandler.getInstance();
        GlassApplication glassApplication = this;
        BleOperateManager.getInstance(glassApplication);
        BleOperateManager.getInstance().setApplication(glassApplication);
        BleOperateManager.getInstance().init();
        IntentFilter deviceIntentFilter = BleAction.getDeviceIntentFilter();
        Intrinsics.checkNotNullExpressionValue(deviceIntentFilter, "getDeviceIntentFilter(...)");
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        if (Build.VERSION.SDK_INT >= 26) {
            registerReceiver(bluetoothReceiver, deviceIntentFilter, 2);
        } else {
            registerReceiver(bluetoothReceiver, deviceIntentFilter);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        if (Build.VERSION.SDK_INT >= 26) {
            registerReceiver(new SystemLocaleChangeReceiver(), intentFilter, 2);
        } else {
            registerReceiver(new SystemLocaleChangeReceiver(), intentFilter);
        }
    }

    private final void initXLog() {
        AndroidPrinter androidPrinter = new AndroidPrinter();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        File externalFilesDir = getExternalFilesDir(null);
        XLog.init(new LogConfiguration.Builder().logLevel(Integer.MIN_VALUE).tag("Glass").enableThreadInfo().enableStackTrace(1).enableBorder().build(), new PrinterSet(androidPrinter, new FilePrinter.Builder(sb.append(externalFilesDir != null ? externalFilesDir.getAbsolutePath() : null).append("/logs").toString()).fileNameGenerator(new DateFileNameGenerator()).cleanStrategy(new FileLastModifiedCleanStrategy(259200000L)).flattener(new Flattener2() { // from class: com.glasssutdio.wear.GlassApplication$$ExternalSyntheticLambda0
            @Override // com.elvishew.xlog.flattener.Flattener2
            public final CharSequence flatten(long j, int i, String str, String str2) {
                return GlassApplication.initXLog$lambda$1(simpleDateFormat, j, i, str, str2);
            }
        }).build()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence initXLog$lambda$1(SimpleDateFormat dateFormat, long j, int i, String str, String str2) {
        Intrinsics.checkNotNullParameter(dateFormat, "$dateFormat");
        return "[" + dateFormat.format(new Date(j)) + "][" + i + "][" + str + "] " + str2;
    }

    private final long initOkDownLoad() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        OkDownload.setSingletonInstance(new OkDownload.Builder(this).build());
        OkDownloadProvider.context = INSTANCE.getCONTEXT();
        DownloadDispatcher.setMaxParallelRunningCount(10);
        return System.currentTimeMillis() - jCurrentTimeMillis;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x005f A[LOOP:1: B:15:0x005d->B:16:0x005f, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Map<String, String> getKeysMap() {
        List listEmptyList;
        String scanKeyFilter = UserConfig.INSTANCE.getInstance().getScanKeyFilter();
        if (scanKeyFilter.length() > 0) {
            List<String> listSplit = new Regex(",").split(scanKeyFilter, 0);
            if (!listSplit.isEmpty()) {
                ListIterator<String> listIterator = listSplit.listIterator(listSplit.size());
                while (listIterator.hasPrevious()) {
                    if (listIterator.previous().length() != 0) {
                        listEmptyList = CollectionsKt.take(listSplit, listIterator.nextIndex() + 1);
                        break;
                    }
                }
                listEmptyList = CollectionsKt.emptyList();
                for (String str : (String[]) listEmptyList.toArray(new String[0])) {
                    this.filterPrefixMap.put(str, str);
                }
            } else {
                listEmptyList = CollectionsKt.emptyList();
                while (i < r1) {
                }
            }
        }
        return this.filterPrefixMap;
    }

    public final List<String> getDeviceKeys() {
        List listEmptyList;
        String scanKeyFilter = UserConfig.INSTANCE.getInstance().getScanKeyFilter();
        if (scanKeyFilter.length() > 0) {
            List<String> listSplit = new Regex(",").split(scanKeyFilter, 0);
            if (!listSplit.isEmpty()) {
                ListIterator<String> listIterator = listSplit.listIterator(listSplit.size());
                while (listIterator.hasPrevious()) {
                    if (listIterator.previous().length() != 0) {
                        listEmptyList = CollectionsKt.take(listSplit, listIterator.nextIndex() + 1);
                        break;
                    }
                }
                listEmptyList = CollectionsKt.emptyList();
            } else {
                listEmptyList = CollectionsKt.emptyList();
            }
            return ArraysKt.toList((String[]) listEmptyList.toArray(new String[0]));
        }
        return new ArrayList();
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x002a A[PHI: r0
      0x002a: PHI (r0v42 java.lang.String) = 
      (r0v10 java.lang.String)
      (r0v11 java.lang.String)
      (r0v12 java.lang.String)
      (r0v13 java.lang.String)
      (r0v14 java.lang.String)
      (r0v15 java.lang.String)
      (r0v16 java.lang.String)
      (r0v17 java.lang.String)
      (r0v18 java.lang.String)
      (r0v19 java.lang.String)
      (r0v20 java.lang.String)
      (r0v21 java.lang.String)
      (r0v22 java.lang.String)
      (r0v23 java.lang.String)
      (r0v24 java.lang.String)
      (r0v25 java.lang.String)
      (r0v26 java.lang.String)
      (r0v27 java.lang.String)
     binds: [B:10:0x0028, B:13:0x0033, B:16:0x003c, B:19:0x0045, B:22:0x004e, B:25:0x0057, B:28:0x0060, B:31:0x0069, B:34:0x0072, B:37:0x007b, B:40:0x0084, B:43:0x008d, B:46:0x0096, B:49:0x009f, B:52:0x00a8, B:55:0x00b2, B:58:0x00bc, B:61:0x00c6] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final String getAiLanguageValueByKey(String appLanguage) {
        Intrinsics.checkNotNullParameter(appLanguage, "appLanguage");
        if (StringsKt.startsWith$default(appLanguage, "zh", false, 2, (Object) null) || StringsKt.startsWith$default(appLanguage, "cn", false, 2, (Object) null) || StringsKt.startsWith$default(appLanguage, "hk", false, 2, (Object) null)) {
            appLanguage = "cn";
        } else {
            String str = "en";
            if (StringsKt.startsWith$default(appLanguage, "en", false, 2, (Object) null)) {
                appLanguage = str;
            } else {
                str = Localization.language;
                if (!StringsKt.startsWith$default(appLanguage, Localization.language, false, 2, (Object) null)) {
                    str = "de";
                    if (!StringsKt.startsWith$default(appLanguage, "de", false, 2, (Object) null)) {
                        str = "ja";
                        if (!StringsKt.startsWith$default(appLanguage, "ja", false, 2, (Object) null)) {
                            str = "ko";
                            if (!StringsKt.startsWith$default(appLanguage, "ko", false, 2, (Object) null)) {
                                str = "es";
                                if (!StringsKt.startsWith$default(appLanguage, "es", false, 2, (Object) null)) {
                                    str = "pt";
                                    if (!StringsKt.startsWith$default(appLanguage, "pt", false, 2, (Object) null)) {
                                        str = "ru";
                                        if (!StringsKt.startsWith$default(appLanguage, "ru", false, 2, (Object) null)) {
                                            str = "ar";
                                            if (!StringsKt.startsWith$default(appLanguage, "ar", false, 2, (Object) null)) {
                                                str = "hi";
                                                if (!StringsKt.startsWith$default(appLanguage, "hi", false, 2, (Object) null)) {
                                                    str = "th";
                                                    if (!StringsKt.startsWith$default(appLanguage, "th", false, 2, (Object) null)) {
                                                        str = BreakpointSQLiteKey.f521ID;
                                                        if (!StringsKt.startsWith$default(appLanguage, BreakpointSQLiteKey.f521ID, false, 2, (Object) null)) {
                                                            str = "it";
                                                            if (!StringsKt.startsWith$default(appLanguage, "it", false, 2, (Object) null)) {
                                                                str = "vi";
                                                                if (!StringsKt.startsWith$default(appLanguage, "vi", false, 2, (Object) null)) {
                                                                    str = "ms";
                                                                    if (!StringsKt.startsWith$default(appLanguage, "ms", false, 2, (Object) null)) {
                                                                        str = "tr";
                                                                        if (!StringsKt.startsWith$default(appLanguage, "tr", false, 2, (Object) null)) {
                                                                            str = "fa";
                                                                            if (!StringsKt.startsWith$default(appLanguage, "fa", false, 2, (Object) null)) {
                                                                                str = "pl";
                                                                                if (!StringsKt.startsWith$default(appLanguage, "pl", false, 2, (Object) null)) {
                                                                                    if (StringsKt.startsWith$default(appLanguage, "nl", false, 2, (Object) null)) {
                                                                                        appLanguage = "nl";
                                                                                    } else if (StringsKt.startsWith$default(appLanguage, "sv", false, 2, (Object) null)) {
                                                                                        appLanguage = "sv";
                                                                                    } else if (StringsKt.startsWith$default(appLanguage, "cs", false, 2, (Object) null)) {
                                                                                        appLanguage = "cs";
                                                                                    } else if (StringsKt.startsWith$default(appLanguage, "ro", false, 2, (Object) null)) {
                                                                                        appLanguage = "ro";
                                                                                    } else if (StringsKt.startsWith$default(appLanguage, "ur", false, 2, (Object) null)) {
                                                                                        appLanguage = "ur";
                                                                                    } else if (StringsKt.startsWith$default(appLanguage, "hu", false, 2, (Object) null)) {
                                                                                        appLanguage = "hu";
                                                                                    } else if (StringsKt.startsWith$default(appLanguage, "el", false, 2, (Object) null)) {
                                                                                        appLanguage = "el";
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        XLog.m137i("resultKey: " + appLanguage);
        String str2 = this.aiLanguageMap.get(appLanguage);
        return str2 == null ? "" : str2;
    }

    /* compiled from: GlassApplication.kt */
    @Metadata(m606d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\f\u001a\u00020\rR+\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\n\u0010\u000b\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001b\u0010\f\u001a\u00020\r8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0012\u001a\u00020\rX\u0082.¢\u0006\u0002\n\u0000¨\u0006\u0013"}, m607d2 = {"Lcom/glasssutdio/wear/GlassApplication$Companion;", "", "()V", "<set-?>", "Landroid/content/Context;", "CONTEXT", "getCONTEXT", "()Landroid/content/Context;", "setCONTEXT", "(Landroid/content/Context;)V", "CONTEXT$delegate", "Lkotlin/properties/ReadWriteProperty;", "getInstance", "Lcom/glasssutdio/wear/GlassApplication;", "getGetInstance", "()Lcom/glasssutdio/wear/GlassApplication;", "getInstance$delegate", "Lkotlin/Lazy;", "instance", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.mutableProperty1(new MutablePropertyReference1Impl(Companion.class, "CONTEXT", "getCONTEXT()Landroid/content/Context;", 0))};

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Context getCONTEXT() {
            return (Context) GlassApplication.CONTEXT$delegate.getValue(this, $$delegatedProperties[0]);
        }

        public final void setCONTEXT(Context context) {
            Intrinsics.checkNotNullParameter(context, "<set-?>");
            GlassApplication.CONTEXT$delegate.setValue(this, $$delegatedProperties[0], context);
        }

        public final GlassApplication getInstance() {
            GlassApplication glassApplication = GlassApplication.instance;
            if (glassApplication != null) {
                return glassApplication;
            }
            Intrinsics.throwUninitializedPropertyAccessException("instance");
            return null;
        }

        public final GlassApplication getGetInstance() {
            return (GlassApplication) GlassApplication.getInstance$delegate.getValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object initSparkChainSync(Continuation<? super Long> continuation) {
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellableContinuationImpl.initCancellability();
        Long lBoxLong = Boxing.boxLong(initSparkChain());
        Result.Companion companion = Result.INSTANCE;
        cancellableContinuationImpl.resumeWith(Result.m903constructorimpl(lBoxLong));
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }
}
