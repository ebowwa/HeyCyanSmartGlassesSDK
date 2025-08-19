package com.glasssutdio.wear.all.utils.time;

import android.os.Handler;
import android.os.Looper;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import java.io.Closeable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.channels.ChannelIterator;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.channels.TickerChannelsKt;
import kotlinx.coroutines.channels.TickerMode;

/* compiled from: Interval.kt */
@Metadata(m606d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u00012\u00020\u0002B!\b\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0004¢\u0006\u0002\u0010\bB3\b\u0007\u0012\u0006\u0010\t\u001a\u00020\u0004\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\n\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0004¢\u0006\u0002\u0010\u000bJ\u0006\u0010$\u001a\u00020\u0018J\b\u0010%\u001a\u00020\u0018H\u0016J%\u0010&\u001a\u00020\u00002\u001d\u0010'\u001a\u0019\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00180\u0017¢\u0006\u0002\b\u0019J\u0012\u0010(\u001a\u00020\u00182\b\b\u0002\u0010\u0012\u001a\u00020\u0004H\u0002J\u001a\u0010)\u001a\u00020\u00002\u0006\u0010*\u001a\u00020+2\b\b\u0002\u0010,\u001a\u00020-H\u0007J\u001a\u0010)\u001a\u00020\u00002\u0006\u0010.\u001a\u00020/2\b\b\u0002\u0010,\u001a\u00020-H\u0007J\u000e\u00100\u001a\u00020\u00002\u0006\u0010.\u001a\u00020/J\u0006\u00101\u001a\u00020\u0018J\u0006\u00102\u001a\u00020\u0018J\u0006\u00103\u001a\u00020\u0018J\u0016\u00104\u001a\u00020\u00182\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u001805H\u0002J\u0006\u0010\n\u001a\u00020\u0000J\u0006\u00106\u001a\u00020\u0018J%\u00107\u001a\u00020\u00002\u001d\u0010'\u001a\u0019\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00180\u0017¢\u0006\u0002\b\u0019J\u0006\u00108\u001a\u00020\u0018R\u001a\u0010\f\u001a\u00020\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u00020\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u000e\"\u0004\b\u0014\u0010\u0010R+\u0010\u0015\u001a\u001f\u0012\u001b\u0012\u0019\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00180\u0017¢\u0006\u0002\b\u00190\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001c\u001a\u00020\u001d@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R+\u0010!\u001a\u001f\u0012\u001b\u0012\u0019\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00180\u0017¢\u0006\u0002\b\u00190\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00180#X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00069"}, m607d2 = {"Lcom/glasssutdio/wear/all/utils/time/Interval;", "Ljava/io/Serializable;", "Ljava/io/Closeable;", TypedValues.CycleType.S_WAVE_PERIOD, "", "unit", "Ljava/util/concurrent/TimeUnit;", "initialDelay", "(JLjava/util/concurrent/TimeUnit;J)V", "end", "start", "(JJLjava/util/concurrent/TimeUnit;JJ)V", "count", "getCount", "()J", "setCount", "(J)V", "countTime", "delay", "getEnd", "setEnd", "finishList", "", "Lkotlin/Function2;", "", "Lkotlin/ExtensionFunctionType;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "<set-?>", "Lcom/glasssutdio/wear/all/utils/time/IntervalStatus;", "state", "getState", "()Lcom/glasssutdio/wear/all/utils/time/IntervalStatus;", "subscribeList", "ticker", "Lkotlinx/coroutines/channels/ReceiveChannel;", "cancel", "close", "finish", "block", "launch", "life", "fragment", "Landroidx/fragment/app/Fragment;", "lifeEvent", "Landroidx/lifecycle/Lifecycle$Event;", "lifecycleOwner", "Landroidx/lifecycle/LifecycleOwner;", "onlyResumed", "pause", "reset", "resume", "runMain", "Lkotlin/Function0;", "stop", "subscribe", "switch", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public class Interval implements Serializable, Closeable {
    private long count;
    private long countTime;
    private long delay;
    private long end;
    private final List<Function2<Interval, Long, Unit>> finishList;
    private final long initialDelay;
    private final long period;
    private CoroutineScope scope;
    private final long start;
    private IntervalStatus state;
    private final List<Function2<Interval, Long, Unit>> subscribeList;
    private ReceiveChannel<Unit> ticker;
    private final TimeUnit unit;

    /* compiled from: Interval.kt */
    @Metadata(m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[IntervalStatus.values().length];
            try {
                iArr[IntervalStatus.STATE_ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[IntervalStatus.STATE_IDLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[IntervalStatus.STATE_PAUSE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public Interval(long j, long j2, TimeUnit unit) {
        this(j, j2, unit, 0L, 0L, 24, null);
        Intrinsics.checkNotNullParameter(unit, "unit");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public Interval(long j, long j2, TimeUnit unit, long j3) {
        this(j, j2, unit, j3, 0L, 16, null);
        Intrinsics.checkNotNullParameter(unit, "unit");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public Interval(long j, TimeUnit unit) {
        this(j, unit, 0L, 4, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(unit, "unit");
    }

    public final Interval life(Fragment fragment) {
        Intrinsics.checkNotNullParameter(fragment, "fragment");
        return life$default(this, fragment, (Lifecycle.Event) null, 2, (Object) null);
    }

    public final Interval life(LifecycleOwner lifecycleOwner) {
        Intrinsics.checkNotNullParameter(lifecycleOwner, "lifecycleOwner");
        return life$default(this, lifecycleOwner, (Lifecycle.Event) null, 2, (Object) null);
    }

    public Interval(long j, long j2, TimeUnit unit, long j3, long j4) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        this.end = j;
        this.period = j2;
        this.unit = unit;
        this.start = j3;
        this.initialDelay = j4;
        this.subscribeList = new ArrayList();
        this.finishList = new ArrayList();
        this.count = j3;
        this.state = IntervalStatus.STATE_IDLE;
    }

    public /* synthetic */ Interval(long j, long j2, TimeUnit timeUnit, long j3, long j4, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(j, j2, timeUnit, (i & 8) != 0 ? 0L : j3, (i & 16) != 0 ? 0L : j4);
    }

    public final long getEnd() {
        return this.end;
    }

    public final void setEnd(long j) {
        this.end = j;
    }

    public /* synthetic */ Interval(long j, TimeUnit timeUnit, long j2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(j, timeUnit, (i & 4) != 0 ? 0L : j2);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public Interval(long j, TimeUnit unit, long j2) {
        this(-1L, j, unit, 0L, j2);
        Intrinsics.checkNotNullParameter(unit, "unit");
    }

    public final long getCount() {
        return this.count;
    }

    public final void setCount(long j) {
        this.count = j;
    }

    public final IntervalStatus getState() {
        return this.state;
    }

    public final Interval subscribe(Function2<? super Interval, ? super Long, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        this.subscribeList.add(block);
        return this;
    }

    public final Interval finish(Function2<? super Interval, ? super Long, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        this.finishList.add(block);
        return this;
    }

    public final Interval start() {
        if (this.state == IntervalStatus.STATE_ACTIVE) {
            return this;
        }
        this.state = IntervalStatus.STATE_ACTIVE;
        this.count = this.start;
        launch$default(this, 0L, 1, null);
        return this;
    }

    public final void stop() {
        if (this.state == IntervalStatus.STATE_IDLE) {
            return;
        }
        CoroutineScope coroutineScope = this.scope;
        if (coroutineScope != null) {
            CoroutineScopeKt.cancel$default(coroutineScope, null, 1, null);
        }
        this.state = IntervalStatus.STATE_IDLE;
        Iterator<T> it = this.finishList.iterator();
        while (it.hasNext()) {
            ((Function2) it.next()).invoke(this, Long.valueOf(this.count));
        }
    }

    public final void cancel() {
        if (this.state == IntervalStatus.STATE_IDLE) {
            return;
        }
        CoroutineScope coroutineScope = this.scope;
        if (coroutineScope != null) {
            CoroutineScopeKt.cancel$default(coroutineScope, null, 1, null);
        }
        this.state = IntervalStatus.STATE_IDLE;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        cancel();
    }

    /* renamed from: switch, reason: not valid java name */
    public final void m777switch() {
        int i = WhenMappings.$EnumSwitchMapping$0[this.state.ordinal()];
        if (i == 1) {
            stop();
        } else if (i == 2) {
            start();
        } else {
            if (i != 3) {
                return;
            }
            resume();
        }
    }

    public final void pause() {
        if (this.state != IntervalStatus.STATE_ACTIVE) {
            return;
        }
        CoroutineScope coroutineScope = this.scope;
        if (coroutineScope != null) {
            CoroutineScopeKt.cancel$default(coroutineScope, null, 1, null);
        }
        this.state = IntervalStatus.STATE_PAUSE;
        this.delay = System.currentTimeMillis() - this.countTime;
    }

    public final void resume() {
        if (this.state != IntervalStatus.STATE_PAUSE) {
            return;
        }
        this.state = IntervalStatus.STATE_ACTIVE;
        launch(this.delay);
    }

    public final void reset() {
        this.count = this.start;
        this.delay = this.unit.toMillis(this.initialDelay);
        CoroutineScope coroutineScope = this.scope;
        if (coroutineScope != null) {
            CoroutineScopeKt.cancel$default(coroutineScope, null, 1, null);
        }
        if (this.state == IntervalStatus.STATE_ACTIVE) {
            launch$default(this, 0L, 1, null);
        }
    }

    public static /* synthetic */ Interval life$default(Interval interval, LifecycleOwner lifecycleOwner, Lifecycle.Event event, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: life");
        }
        if ((i & 2) != 0) {
            event = Lifecycle.Event.ON_DESTROY;
        }
        return interval.life(lifecycleOwner, event);
    }

    public final Interval life(final LifecycleOwner lifecycleOwner, final Lifecycle.Event lifeEvent) {
        Intrinsics.checkNotNullParameter(lifecycleOwner, "lifecycleOwner");
        Intrinsics.checkNotNullParameter(lifeEvent, "lifeEvent");
        runMain(new Function0<Unit>() { // from class: com.glasssutdio.wear.all.utils.time.Interval$life$1$1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                Lifecycle lifecycle = lifecycleOwner.getLifecycle();
                final Lifecycle.Event event = lifeEvent;
                final Interval interval = this;
                lifecycle.addObserver(new LifecycleEventObserver() { // from class: com.glasssutdio.wear.all.utils.time.Interval$life$1$1.1
                    @Override // androidx.lifecycle.LifecycleEventObserver
                    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event2) {
                        Intrinsics.checkNotNullParameter(source, "source");
                        Intrinsics.checkNotNullParameter(event2, "event");
                        if (event == event2) {
                            interval.cancel();
                        }
                    }
                });
            }
        });
        return this;
    }

    public static /* synthetic */ Interval life$default(Interval interval, Fragment fragment, Lifecycle.Event event, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: life");
        }
        if ((i & 2) != 0) {
            event = Lifecycle.Event.ON_DESTROY;
        }
        return interval.life(fragment, event);
    }

    public final Interval life(Fragment fragment, final Lifecycle.Event lifeEvent) {
        Intrinsics.checkNotNullParameter(fragment, "fragment");
        Intrinsics.checkNotNullParameter(lifeEvent, "lifeEvent");
        fragment.getViewLifecycleOwnerLiveData().observe(fragment, new Interval$sam$androidx_lifecycle_Observer$0(new Function1<LifecycleOwner, Unit>() { // from class: com.glasssutdio.wear.all.utils.time.Interval.life.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(LifecycleOwner lifecycleOwner) {
                invoke2(lifecycleOwner);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(LifecycleOwner lifecycleOwner) {
                Lifecycle lifecycle;
                if (lifecycleOwner == null || (lifecycle = lifecycleOwner.getLifecycle()) == null) {
                    return;
                }
                final Lifecycle.Event event = lifeEvent;
                final Interval interval = this;
                lifecycle.addObserver(new LifecycleEventObserver() { // from class: com.glasssutdio.wear.all.utils.time.Interval.life.2.1
                    @Override // androidx.lifecycle.LifecycleEventObserver
                    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event2) {
                        Intrinsics.checkNotNullParameter(source, "source");
                        Intrinsics.checkNotNullParameter(event2, "event");
                        if (event == event2) {
                            interval.cancel();
                        }
                    }
                });
            }
        }));
        return this;
    }

    public final Interval onlyResumed(final LifecycleOwner lifecycleOwner) {
        Intrinsics.checkNotNullParameter(lifecycleOwner, "lifecycleOwner");
        runMain(new Function0<Unit>() { // from class: com.glasssutdio.wear.all.utils.time.Interval$onlyResumed$1$1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                Lifecycle lifecycle = lifecycleOwner.getLifecycle();
                final Interval interval = this;
                lifecycle.addObserver(new LifecycleEventObserver() { // from class: com.glasssutdio.wear.all.utils.time.Interval$onlyResumed$1$1.1

                    /* compiled from: Interval.kt */
                    @Metadata(m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
                    /* renamed from: com.glasssutdio.wear.all.utils.time.Interval$onlyResumed$1$1$1$WhenMappings */
                    public /* synthetic */ class WhenMappings {
                        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

                        static {
                            int[] iArr = new int[Lifecycle.Event.values().length];
                            try {
                                iArr[Lifecycle.Event.ON_RESUME.ordinal()] = 1;
                            } catch (NoSuchFieldError unused) {
                            }
                            try {
                                iArr[Lifecycle.Event.ON_PAUSE.ordinal()] = 2;
                            } catch (NoSuchFieldError unused2) {
                            }
                            try {
                                iArr[Lifecycle.Event.ON_DESTROY.ordinal()] = 3;
                            } catch (NoSuchFieldError unused3) {
                            }
                            $EnumSwitchMapping$0 = iArr;
                        }
                    }

                    @Override // androidx.lifecycle.LifecycleEventObserver
                    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                        Intrinsics.checkNotNullParameter(source, "source");
                        Intrinsics.checkNotNullParameter(event, "event");
                        int i = WhenMappings.$EnumSwitchMapping$0[event.ordinal()];
                        if (i == 1) {
                            interval.resume();
                        } else if (i == 2) {
                            interval.pause();
                        } else {
                            if (i != 3) {
                                return;
                            }
                            interval.cancel();
                        }
                    }
                });
            }
        });
        return this;
    }

    static /* synthetic */ void launch$default(Interval interval, long j, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: launch");
        }
        if ((i & 1) != 0) {
            j = interval.unit.toMillis(interval.initialDelay);
        }
        interval.launch(j);
    }

    /* compiled from: Interval.kt */
    @Metadata(m606d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, m607d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.all.utils.time.Interval$launch$1", m620f = "Interval.kt", m621i = {}, m622l = {269}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.all.utils.time.Interval$launch$1 */
    static final class C08251 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ long $delay;
        Object L$0;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C08251(long j, Continuation<? super C08251> continuation) {
            super(2, continuation);
            this.$delay = j;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return Interval.this.new C08251(this.$delay, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C08251) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x005d A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:17:0x0066  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x011b  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:13:0x005b -> B:15:0x005e). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Object invokeSuspend(Object obj) {
            ChannelIterator it;
            Interval interval;
            long count;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                Interval interval2 = Interval.this;
                interval2.ticker = TickerChannelsKt.ticker$default(interval2.unit.toMillis(Interval.this.period), this.$delay, null, TickerMode.FIXED_DELAY, 4, null);
                ReceiveChannel receiveChannel = Interval.this.ticker;
                if (receiveChannel == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("ticker");
                    receiveChannel = null;
                }
                it = receiveChannel.iterator();
                this.L$0 = it;
                this.label = 1;
                obj = it.hasNext(this);
                if (obj == coroutine_suspended) {
                }
                if (((Boolean) obj).booleanValue()) {
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                it = (ChannelIterator) this.L$0;
                ResultKt.throwOnFailure(obj);
                if (((Boolean) obj).booleanValue()) {
                    it.next();
                    List list = Interval.this.subscribeList;
                    Interval interval3 = Interval.this;
                    Iterator it2 = list.iterator();
                    while (it2.hasNext()) {
                        ((Function2) it2.next()).invoke(interval3, Boxing.boxLong(interval3.getCount()));
                    }
                    long j = -1;
                    if (Interval.this.getEnd() != -1 && Interval.this.getCount() == Interval.this.getEnd()) {
                        CoroutineScope coroutineScope = Interval.this.scope;
                        if (coroutineScope != null) {
                            CoroutineScopeKt.cancel$default(coroutineScope, null, 1, null);
                        }
                        Interval.this.state = IntervalStatus.STATE_IDLE;
                        List list2 = Interval.this.finishList;
                        Interval interval4 = Interval.this;
                        Iterator it3 = list2.iterator();
                        while (it3.hasNext()) {
                            ((Function2) it3.next()).invoke(interval4, Boxing.boxLong(interval4.getCount()));
                        }
                    }
                    if (Interval.this.getEnd() == -1 || Interval.this.start <= Interval.this.getEnd()) {
                        interval = Interval.this;
                        count = interval.getCount();
                        j = 1;
                    } else {
                        interval = Interval.this;
                        count = interval.getCount();
                    }
                    interval.setCount(count + j);
                    Interval.this.countTime = System.currentTimeMillis();
                    this.L$0 = it;
                    this.label = 1;
                    obj = it.hasNext(this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    if (((Boolean) obj).booleanValue()) {
                        return Unit.INSTANCE;
                    }
                }
            }
        }
    }

    private final void launch(long delay) {
        CoroutineScope CoroutineScope = CoroutineScopeKt.CoroutineScope(Dispatchers.getMain());
        this.scope = CoroutineScope;
        if (CoroutineScope != null) {
            BuildersKt__Builders_commonKt.launch$default(CoroutineScope, null, null, new C08251(delay, null), 3, null);
        }
    }

    private final void runMain(final Function0<Unit> block) {
        if (Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            block.invoke();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.glasssutdio.wear.all.utils.time.Interval$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Interval.runMain$lambda$6(block);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void runMain$lambda$6(Function0 block) {
        Intrinsics.checkNotNullParameter(block, "$block");
        block.invoke();
    }
}
