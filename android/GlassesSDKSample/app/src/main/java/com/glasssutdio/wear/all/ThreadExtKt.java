package com.glasssutdio.wear.all;

import android.os.Handler;
import android.os.Looper;
import androidx.exifinterface.media.ExifInterface;
import com.google.firebase.crashlytics.buildtools.ndk.internal.elf.EMachine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__BuildersKt;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.ExecutorsKt;
import kotlinx.coroutines.Job;

/* compiled from: ThreadExt.kt */
@Metadata(m606d1 = {"\u00004\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\t\n\u0002\b\u0002\u001a.\u0010\f\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0013\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0014\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0015\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0016\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0017\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0018\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a.\u0010\u0019\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u0012\u001a6\u0010\u001a\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\u0002H\u000e2\u0006\u0010\u001b\u001a\u00020\u001c2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\r0\u0010¢\u0006\u0002\b\u0011¢\u0006\u0002\u0010\u001d\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, m607d2 = {"cache", "Ljava/util/concurrent/ExecutorService;", "coreSize", "", "fix", "handler", "Landroid/os/Handler;", "scheduled", "single", "singleAnother", "singleBle", "singleNetWork", "ktxRunOnBgCache", "", ExifInterface.GPS_DIRECTION_TRUE, "block", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)V", "ktxRunOnBgFix", "ktxRunOnBgSingle", "ktxRunOnBgSingleBle", "ktxRunOnBgSingleDao", "ktxRunOnBgSingleNetWork", "ktxRunOnSparkSingle", "ktxRunOnUi", "ktxRunOnUiDelay", "delayMillis", "", "(Ljava/lang/Object;JLkotlin/jvm/functions/Function1;)V", "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class ThreadExtKt {
    private static final ExecutorService cache;
    private static final int coreSize;
    private static final ExecutorService fix;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final ExecutorService scheduled;
    private static final ExecutorService single;
    private static final ExecutorService singleAnother;
    private static final ExecutorService singleBle;
    private static final ExecutorService singleNetWork;

    static {
        int iAvailableProcessors = Runtime.getRuntime().availableProcessors() + 1;
        coreSize = iAvailableProcessors;
        ExecutorService executorServiceNewFixedThreadPool = Executors.newFixedThreadPool(iAvailableProcessors);
        Intrinsics.checkNotNullExpressionValue(executorServiceNewFixedThreadPool, "newFixedThreadPool(...)");
        fix = executorServiceNewFixedThreadPool;
        ExecutorService executorServiceNewCachedThreadPool = Executors.newCachedThreadPool();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewCachedThreadPool, "newCachedThreadPool(...)");
        cache = executorServiceNewCachedThreadPool;
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor, "newSingleThreadExecutor(...)");
        single = executorServiceNewSingleThreadExecutor;
        ExecutorService executorServiceNewSingleThreadExecutor2 = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor2, "newSingleThreadExecutor(...)");
        singleAnother = executorServiceNewSingleThreadExecutor2;
        ExecutorService executorServiceNewSingleThreadExecutor3 = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor3, "newSingleThreadExecutor(...)");
        singleBle = executorServiceNewSingleThreadExecutor3;
        ExecutorService executorServiceNewSingleThreadExecutor4 = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor4, "newSingleThreadExecutor(...)");
        singleNetWork = executorServiceNewSingleThreadExecutor4;
        ScheduledExecutorService scheduledExecutorServiceNewScheduledThreadPool = Executors.newScheduledThreadPool(iAvailableProcessors);
        Intrinsics.checkNotNullExpressionValue(scheduledExecutorServiceNewScheduledThreadPool, "newScheduledThreadPool(...)");
        scheduled = scheduledExecutorServiceNewScheduledThreadPool;
    }

    public static final <T> void ktxRunOnUi(final T t, final Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        handler.post(new Runnable() { // from class: com.glasssutdio.wear.all.ThreadExtKt$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ThreadExtKt.ktxRunOnUi$lambda$0(block, t);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void ktxRunOnUi$lambda$0(Function1 block, Object obj) {
        Intrinsics.checkNotNullParameter(block, "$block");
        block.invoke(obj);
    }

    public static final <T> void ktxRunOnUiDelay(final T t, long j, final Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        handler.postDelayed(new Runnable() { // from class: com.glasssutdio.wear.all.ThreadExtKt$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ThreadExtKt.ktxRunOnUiDelay$lambda$1(block, t);
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void ktxRunOnUiDelay$lambda$1(Function1 block, Object obj) {
        Intrinsics.checkNotNullParameter(block, "$block");
        block.invoke(obj);
    }

    public static final <T> void ktxRunOnBgSingle(T t, Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor, "newSingleThreadExecutor(...)");
        Object objRunBlocking$default = BuildersKt__BuildersKt.runBlocking$default(null, new C08011(BuildersKt__Builders_commonKt.launch$default(CoroutineScopeKt.CoroutineScope(ExecutorsKt.from(executorServiceNewSingleThreadExecutor)), null, null, new ThreadExtKt$ktxRunOnBgSingle$job$1(block, t, null), 3, null), null), 1, null);
        try {
            Result.Companion companion = Result.INSTANCE;
            Result.m903constructorimpl(((Unit) objRunBlocking$default).toString());
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            Result.m903constructorimpl(ResultKt.createFailure(th));
        }
    }

    /* compiled from: ThreadExt.kt */
    @Metadata(m606d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u0003H\u008a@"}, m607d2 = {"<anonymous>", "", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnBgSingle$1", m620f = "ThreadExt.kt", m621i = {}, m622l = {61}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnBgSingle$1 */
    static final class C08011 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Job $job;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C08011(Job job, Continuation<? super C08011> continuation) {
            super(2, continuation);
            this.$job = job;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new C08011(this.$job, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C08011) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                if (this.$job.join(this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public static final <T> void ktxRunOnSparkSingle(T t, Function1<? super T, Unit> block) throws InterruptedException {
        Intrinsics.checkNotNullParameter(block, "block");
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor, "newSingleThreadExecutor(...)");
        Object objRunBlocking$default = BuildersKt__BuildersKt.runBlocking$default(null, new C08071(BuildersKt__Builders_commonKt.launch$default(CoroutineScopeKt.CoroutineScope(ExecutorsKt.from(executorServiceNewSingleThreadExecutor)), null, null, new ThreadExtKt$ktxRunOnSparkSingle$job$1(block, t, null), 3, null), null), 1, null);
        try {
            Result.Companion companion = Result.INSTANCE;
            Result.m903constructorimpl(((Unit) objRunBlocking$default).toString());
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            Result.m903constructorimpl(ResultKt.createFailure(th));
        }
    }

    /* compiled from: ThreadExt.kt */
    @Metadata(m606d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u0003H\u008a@"}, m607d2 = {"<anonymous>", "", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnSparkSingle$1", m620f = "ThreadExt.kt", m621i = {}, m622l = {76}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnSparkSingle$1 */
    static final class C08071 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Job $job;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C08071(Job job, Continuation<? super C08071> continuation) {
            super(2, continuation);
            this.$job = job;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new C08071(this.$job, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C08071) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                if (this.$job.join(this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public static final <T> void ktxRunOnBgSingleDao(T t, Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor, "newSingleThreadExecutor(...)");
        BuildersKt__BuildersKt.runBlocking$default(null, new C08031(BuildersKt__Builders_commonKt.launch$default(CoroutineScopeKt.CoroutineScope(ExecutorsKt.from(executorServiceNewSingleThreadExecutor)), null, null, new ThreadExtKt$ktxRunOnBgSingleDao$job$1(block, t, null), 3, null), null), 1, null);
    }

    /* compiled from: ThreadExt.kt */
    @Metadata(m606d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u0003H\u008a@"}, m607d2 = {"<anonymous>", "", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnBgSingleDao$1", m620f = "ThreadExt.kt", m621i = {}, m622l = {EMachine.EM_XTENSA}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnBgSingleDao$1 */
    static final class C08031 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Job $job;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C08031(Job job, Continuation<? super C08031> continuation) {
            super(2, continuation);
            this.$job = job;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new C08031(this.$job, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C08031) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                if (this.$job.join(this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public static final <T> void ktxRunOnBgSingleBle(final T t, final Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        singleBle.execute(new Runnable() { // from class: com.glasssutdio.wear.all.ThreadExtKt$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ThreadExtKt.ktxRunOnBgSingleBle$lambda$4(block, t);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void ktxRunOnBgSingleBle$lambda$4(Function1 block, Object obj) {
        Intrinsics.checkNotNullParameter(block, "$block");
        block.invoke(obj);
    }

    public static final <T> void ktxRunOnBgSingleNetWork(T t, Function1<? super T, Unit> block) throws InterruptedException {
        Intrinsics.checkNotNullParameter(block, "block");
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(executorServiceNewSingleThreadExecutor, "newSingleThreadExecutor(...)");
        BuildersKt__BuildersKt.runBlocking$default(null, new C08051(BuildersKt__Builders_commonKt.launch$default(CoroutineScopeKt.CoroutineScope(ExecutorsKt.from(executorServiceNewSingleThreadExecutor)), null, null, new ThreadExtKt$ktxRunOnBgSingleNetWork$job$1(block, t, null), 3, null), null), 1, null);
    }

    /* compiled from: ThreadExt.kt */
    @Metadata(m606d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u0003H\u008a@"}, m607d2 = {"<anonymous>", "", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnBgSingleNetWork$1", m620f = "ThreadExt.kt", m621i = {}, m622l = {EMachine.EM_CRX}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.all.ThreadExtKt$ktxRunOnBgSingleNetWork$1 */
    static final class C08051 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Job $job;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C08051(Job job, Continuation<? super C08051> continuation) {
            super(2, continuation);
            this.$job = job;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new C08051(this.$job, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C08051) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                this.label = 1;
                if (this.$job.join(this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public static final <T> void ktxRunOnBgFix(final T t, final Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        fix.execute(new Runnable() { // from class: com.glasssutdio.wear.all.ThreadExtKt$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ThreadExtKt.ktxRunOnBgFix$lambda$5(block, t);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void ktxRunOnBgFix$lambda$5(Function1 block, Object obj) {
        Intrinsics.checkNotNullParameter(block, "$block");
        block.invoke(obj);
    }

    public static final <T> void ktxRunOnBgCache(final T t, final Function1<? super T, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        cache.execute(new Runnable() { // from class: com.glasssutdio.wear.all.ThreadExtKt$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ThreadExtKt.ktxRunOnBgCache$lambda$6(block, t);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void ktxRunOnBgCache$lambda$6(Function1 block, Object obj) {
        Intrinsics.checkNotNullParameter(block, "$block");
        block.invoke(obj);
    }
}
