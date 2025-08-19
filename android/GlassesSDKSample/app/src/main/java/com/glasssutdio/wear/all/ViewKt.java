package com.glasssutdio.wear.all;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.glasssutdio.wear.C0775R;
import com.glasssutdio.wear.all.ViewKt;
import com.glasssutdio.wear.all.utils.bar.StatusBarUtil;
import com.glasssutdio.wear.all.view.CustomScrollbarHelper;
import com.glasssutdio.wear.all.view.ScrollbarConfig;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;

/* compiled from: View.kt */
@Metadata(m606d1 = {"\u0000h\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\u001a<\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00052\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0005H\u0007\u001a#\u0010\u0007\u001a\u00020\b*\u00020\t2\u0017\u0010\n\u001a\u0013\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b¢\u0006\u0002\b\r\u001a\u0014\u0010\u0007\u001a\u00020\b*\u00020\t2\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u001a$\u0010\u0010\u001a\u00020\u0001*\u00020\t2\u0018\u0010\u0011\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u0012\u001a7\u0010\u0014\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u0015\u001a\u00020\u00162!\u0010\u0017\u001a\u001d\u0012\u0013\u0012\u00110\u0002¢\u0006\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\u00010\u000b\u001a\f\u0010\u001b\u001a\u00020\u0001*\u0004\u0018\u00010\u0002\u001a\u0016\u0010\u001c\u001a\u00020\u0001*\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u001d\u001a\u00020\u0016\u001a\u0016\u0010\u001e\u001a\u00020\u0001*\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a\f\u0010!\u001a\u00020\u0001*\u0004\u0018\u00010\u0002\u001a\u0016\u0010\"\u001a\u00020\u0001*\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u001d\u001a\u00020\u0016\u001aP\u0010#\u001a\u00020$*\u00020\u00022\b\b\u0002\u0010\u001d\u001a\u00020\u00162\b\b\u0002\u0010%\u001a\u00020\u00132\b\b\u0002\u0010&\u001a\u00020 2\n\b\u0002\u0010'\u001a\u0004\u0018\u00010(2\b\b\u0002\u0010)\u001a\u00020\u00162\u0010\b\u0002\u0010*\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0005\u001a(\u0010+\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010,\u001a\u00020\u00162\u0012\u0010-\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00010\u000b\u001a?\u0010.\u001a\u00020\u0001*\u00020\u00022\n\b\u0002\u0010/\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u00100\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u00101\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u00102\u001a\u0004\u0018\u00010\u0013¢\u0006\u0002\u00103\u001a4\u00104\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010/\u001a\u00020\u00132\b\b\u0002\u00100\u001a\u00020\u00132\b\b\u0002\u00101\u001a\u00020\u00132\b\b\u0002\u00102\u001a\u00020\u0013H\u0007\u001a \u00105\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u00106\u001a\u00020 2\b\b\u0002\u00107\u001a\u00020\u0013H\u0007\u001a\f\u00108\u001a\u00020\u0001*\u0004\u0018\u00010\u0002\u001a\u0016\u00109\u001a\u00020\u0001*\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u001d\u001a\u00020\u0016¨\u0006:"}, m607d2 = {"addClickAnimation", "", "Landroid/view/View;", "animatorView", "actionDown", "Lkotlin/Function0;", "actionUp", "addCustomScrollbar", "Lcom/glasssutdio/wear/all/view/CustomScrollbarHelper;", "Landroidx/recyclerview/widget/RecyclerView;", "block", "Lkotlin/Function1;", "Lcom/glasssutdio/wear/all/view/CustomScrollbarHelper$Builder;", "Lkotlin/ExtensionFunctionType;", "config", "Lcom/glasssutdio/wear/all/view/ScrollbarConfig;", "addOnItemClickListener", "onClickListener", "Lkotlin/Function2;", "", "click", "interval", "", "action", "Lkotlin/ParameterName;", "name", "view", "gone", "goneAlphaAnimation", TypedValues.TransitionType.S_DURATION, "goneOrVisible", "isShow", "", "invisible", "invisibleAlphaAnimation", "rotate", "Landroid/animation/ObjectAnimator;", "repeatCount", "clockwise", "lifecycle", "Landroidx/lifecycle/Lifecycle;", "startDelay", "onAnimationEnd", "setDebouncedClickListener", "delayMillis", "onClick", "setMargin", "left", "top", "right", "bottom", "(Landroid/view/View;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)V", "setMarginAdd", "statusMargin", "remove", "addMargin", "visible", "visibleAlphaAnimation", "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class ViewKt {
    public static final void setMarginAdd(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        setMarginAdd$default(view, 0, 0, 0, 0, 15, null);
    }

    public static final void setMarginAdd(View view, int i) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        setMarginAdd$default(view, i, 0, 0, 0, 14, null);
    }

    public static final void setMarginAdd(View view, int i, int i2) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        setMarginAdd$default(view, i, i2, 0, 0, 12, null);
    }

    public static final void setMarginAdd(View view, int i, int i2, int i3) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        setMarginAdd$default(view, i, i2, i3, 0, 8, null);
    }

    public static final void statusMargin(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        statusMargin$default(view, false, 0, 3, null);
    }

    public static final void statusMargin(View view, boolean z) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        statusMargin$default(view, z, 0, 2, null);
    }

    public static final void visible(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(0);
    }

    public static /* synthetic */ void visibleAlphaAnimation$default(View view, long j, int i, Object obj) {
        if ((i & 1) != 0) {
            j = 500;
        }
        visibleAlphaAnimation(view, j);
    }

    public static final void visibleAlphaAnimation(View view, long j) {
        if (view != null) {
            view.setVisibility(0);
        }
        if (view != null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(j);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
    }

    public static /* synthetic */ CustomScrollbarHelper addCustomScrollbar$default(RecyclerView recyclerView, ScrollbarConfig scrollbarConfig, int i, Object obj) {
        if ((i & 1) != 0) {
            scrollbarConfig = ScrollbarConfig.INSTANCE.m779default();
        }
        return addCustomScrollbar(recyclerView, scrollbarConfig);
    }

    public static final CustomScrollbarHelper addCustomScrollbar(RecyclerView recyclerView, ScrollbarConfig config) {
        Intrinsics.checkNotNullParameter(recyclerView, "<this>");
        Intrinsics.checkNotNullParameter(config, "config");
        return CustomScrollbarHelper.INSTANCE.create(recyclerView).trackWidth(config.getTrackWidth()).trackColor(config.getTrackColor()).trackMarginEnd(config.getTrackMarginEnd()).thumbWidth(config.getThumbWidth()).thumbHeight(config.getThumbHeight()).thumbColor(config.getThumbColor()).thumbRadius(config.getThumbRadius()).thumbMarginEnd(config.getThumbMarginEnd()).autoHide(config.getAutoHide()).fadeDelay(config.getFadeDelay()).fadeDuration(config.getFadeDuration()).initiallyHidden(config.getInitiallyHidden()).build();
    }

    public static final CustomScrollbarHelper addCustomScrollbar(RecyclerView recyclerView, Function1<? super CustomScrollbarHelper.Builder, Unit> block) {
        Intrinsics.checkNotNullParameter(recyclerView, "<this>");
        Intrinsics.checkNotNullParameter(block, "block");
        CustomScrollbarHelper.Builder builderCreate = CustomScrollbarHelper.INSTANCE.create(recyclerView);
        block.invoke(builderCreate);
        return builderCreate.build();
    }

    public static final void gone(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(8);
    }

    public static /* synthetic */ void goneOrVisible$default(View view, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            z = true;
        }
        goneOrVisible(view, z);
    }

    public static final void goneOrVisible(View view, boolean z) {
        if (view == null) {
            return;
        }
        view.setVisibility(z ? 0 : 8);
    }

    public static /* synthetic */ void goneAlphaAnimation$default(View view, long j, int i, Object obj) {
        if ((i & 1) != 0) {
            j = 500;
        }
        goneAlphaAnimation(view, j);
    }

    public static final void goneAlphaAnimation(View view, long j) {
        if (view != null) {
            view.setVisibility(8);
        }
        if (view != null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(j);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
    }

    public static final void invisible(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(4);
    }

    public static /* synthetic */ void invisibleAlphaAnimation$default(View view, long j, int i, Object obj) {
        if ((i & 1) != 0) {
            j = 500;
        }
        invisibleAlphaAnimation(view, j);
    }

    public static final void invisibleAlphaAnimation(View view, long j) {
        if (view != null) {
            view.setVisibility(4);
        }
        if (view != null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(j);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
    }

    /* compiled from: View.kt */
    @Metadata(m606d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0007"}, m607d2 = {"com/glasssutdio/wear/all/ViewKt$addOnItemClickListener$1", "Landroidx/recyclerview/widget/RecyclerView$OnChildAttachStateChangeListener;", "onChildViewAttachedToWindow", "", "view", "Landroid/view/View;", "onChildViewDetachedFromWindow", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    /* renamed from: com.glasssutdio.wear.all.ViewKt$addOnItemClickListener$1 */
    public static final class C08091 implements RecyclerView.OnChildAttachStateChangeListener {
        final /* synthetic */ Function2<View, Integer, Unit> $onClickListener;
        final /* synthetic */ RecyclerView $this_addOnItemClickListener;

        /* JADX WARN: Multi-variable type inference failed */
        C08091(RecyclerView recyclerView, Function2<? super View, ? super Integer, Unit> function2) {
            this.$this_addOnItemClickListener = recyclerView;
            this.$onClickListener = function2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
        public void onChildViewAttachedToWindow(final View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            final RecyclerView recyclerView = this.$this_addOnItemClickListener;
            final Function2<View, Integer, Unit> function2 = this.$onClickListener;
            view.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.all.ViewKt$addOnItemClickListener$1$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ViewKt.C08091.onChildViewAttachedToWindow$lambda$0(recyclerView, view, function2, view2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onChildViewAttachedToWindow$lambda$0(RecyclerView this_addOnItemClickListener, View view, Function2 onClickListener, View view2) {
            Intrinsics.checkNotNullParameter(this_addOnItemClickListener, "$this_addOnItemClickListener");
            Intrinsics.checkNotNullParameter(view, "$view");
            Intrinsics.checkNotNullParameter(onClickListener, "$onClickListener");
            onClickListener.invoke(view, Integer.valueOf(this_addOnItemClickListener.getChildViewHolder(view).getAdapterPosition()));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
        public void onChildViewDetachedFromWindow(View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            view.setOnClickListener(null);
        }
    }

    public static final void addOnItemClickListener(RecyclerView recyclerView, Function2<? super View, ? super Integer, Unit> onClickListener) {
        Intrinsics.checkNotNullParameter(recyclerView, "<this>");
        Intrinsics.checkNotNullParameter(onClickListener, "onClickListener");
        recyclerView.addOnChildAttachStateChangeListener(new C08091(recyclerView, onClickListener));
    }

    public static /* synthetic */ void statusMargin$default(View view, boolean z, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            z = false;
        }
        if ((i2 & 2) != 0) {
            i = 0;
        }
        statusMargin(view, z, i);
    }

    public static final void statusMargin(View view, boolean z, int i) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(view.getContext());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Intrinsics.checkNotNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        if (z) {
            if (marginLayoutParams.topMargin < statusBarHeight) {
                return;
            }
            marginLayoutParams.topMargin -= statusBarHeight;
            marginLayoutParams.topMargin += i;
            view.setLayoutParams(marginLayoutParams);
            return;
        }
        if (marginLayoutParams.topMargin >= statusBarHeight) {
            return;
        }
        marginLayoutParams.topMargin += statusBarHeight;
        marginLayoutParams.topMargin += i;
        view.setLayoutParams(marginLayoutParams);
    }

    public static /* synthetic */ void setMarginAdd$default(View view, int i, int i2, int i3, int i4, int i5, Object obj) {
        if ((i5 & 1) != 0) {
            i = 0;
        }
        if ((i5 & 2) != 0) {
            i2 = 0;
        }
        if ((i5 & 4) != 0) {
            i3 = 0;
        }
        if ((i5 & 8) != 0) {
            i4 = 0;
        }
        setMarginAdd(view, i, i2, i3, i4);
    }

    public static final void setMarginAdd(View view, int i, int i2, int i3, int i4) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Intrinsics.checkNotNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        marginLayoutParams.leftMargin += i;
        marginLayoutParams.topMargin += i2;
        marginLayoutParams.rightMargin += i3;
        marginLayoutParams.bottomMargin += i4;
        view.setLayoutParams(marginLayoutParams);
    }

    public static /* synthetic */ void setMargin$default(View view, Integer num, Integer num2, Integer num3, Integer num4, int i, Object obj) {
        if ((i & 1) != 0) {
            num = null;
        }
        if ((i & 2) != 0) {
            num2 = null;
        }
        if ((i & 4) != 0) {
            num3 = null;
        }
        if ((i & 8) != 0) {
            num4 = null;
        }
        setMargin(view, num, num2, num3, num4);
    }

    public static final void setMargin(View view, Integer num, Integer num2, Integer num3, Integer num4) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Intrinsics.checkNotNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        if (num != null) {
            marginLayoutParams.leftMargin = num.intValue();
        }
        if (num2 != null) {
            marginLayoutParams.topMargin = num2.intValue();
        }
        if (num3 != null) {
            marginLayoutParams.rightMargin = num3.intValue();
        }
        if (num4 != null) {
            marginLayoutParams.bottomMargin = num4.intValue();
        }
        view.setLayoutParams(marginLayoutParams);
    }

    public static /* synthetic */ void addClickAnimation$default(View view, View view2, Function0 function0, Function0 function02, int i, Object obj) {
        if ((i & 1) != 0) {
            view2 = null;
        }
        if ((i & 2) != 0) {
            function0 = null;
        }
        if ((i & 4) != 0) {
            function02 = null;
        }
        addClickAnimation(view, view2, function0, function02);
    }

    public static final void addClickAnimation(final View view, View view2, final Function0<Unit> function0, final Function0<Unit> function02) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        final View view3 = view2 == null ? view : view2;
        final Ref.BooleanRef booleanRef = new Ref.BooleanRef();
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.glasssutdio.wear.all.ViewKt$$ExternalSyntheticLambda1
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view4, MotionEvent motionEvent) {
                return ViewKt.addClickAnimation$lambda$11(objectRef, booleanRef, view, view3, function02, function0, view4, motionEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r9v3, types: [T, com.glasssutdio.wear.all.ViewKt$$ExternalSyntheticLambda2] */
    public static final boolean addClickAnimation$lambda$11(Ref.ObjectRef longPressRunnable, final Ref.BooleanRef enableLong, View this_addClickAnimation, View realAnimatorView, Function0 function0, final Function0 function02, View view, MotionEvent motionEvent) {
        Intrinsics.checkNotNullParameter(longPressRunnable, "$longPressRunnable");
        Intrinsics.checkNotNullParameter(enableLong, "$enableLong");
        Intrinsics.checkNotNullParameter(this_addClickAnimation, "$this_addClickAnimation");
        Intrinsics.checkNotNullParameter(realAnimatorView, "$realAnimatorView");
        int action = motionEvent.getAction();
        if (action == 0) {
            Runnable runnable = (Runnable) longPressRunnable.element;
            if (runnable != null) {
                this_addClickAnimation.removeCallbacks(runnable);
            }
            enableLong.element = false;
            longPressRunnable.element = new Runnable() { // from class: com.glasssutdio.wear.all.ViewKt$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ViewKt.addClickAnimation$lambda$11$lambda$8(enableLong, function02);
                }
            };
            this_addClickAnimation.postDelayed((Runnable) longPressRunnable.element, 100L);
            realAnimatorView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100L).start();
        } else if (action == 1) {
            Runnable runnable2 = (Runnable) longPressRunnable.element;
            if (runnable2 != null) {
                this_addClickAnimation.removeCallbacks(runnable2);
            }
            if (enableLong.element && function0 != null) {
                function0.invoke();
            }
            realAnimatorView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100L).start();
            float width = view.getWidth();
            float x = motionEvent.getX();
            if (0.0f <= x && x <= width) {
                float height = view.getHeight();
                float y = motionEvent.getY();
                if (0.0f <= y && y <= height) {
                    view.performClick();
                }
            }
        } else {
            if (action != 3) {
                return false;
            }
            Runnable runnable3 = (Runnable) longPressRunnable.element;
            if (runnable3 != null) {
                this_addClickAnimation.removeCallbacks(runnable3);
            }
            if (enableLong.element && function0 != null) {
                function0.invoke();
            }
            realAnimatorView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100L).start();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addClickAnimation$lambda$11$lambda$8(Ref.BooleanRef enableLong, Function0 function0) {
        Intrinsics.checkNotNullParameter(enableLong, "$enableLong");
        enableLong.element = true;
        if (function0 != null) {
            function0.invoke();
        }
    }

    public static /* synthetic */ void click$default(View view, long j, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            j = 500;
        }
        click(view, j, function1);
    }

    public static final void click(final View view, final long j, final Function1<? super View, Unit> action) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        Intrinsics.checkNotNullParameter(action, "action");
        view.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.all.ViewKt$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ViewKt.click$lambda$12(view, j, action, view2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void click$lambda$12(View this_click, long j, Function1 action, View view) {
        Intrinsics.checkNotNullParameter(this_click, "$this_click");
        Intrinsics.checkNotNullParameter(action, "$action");
        Object tag = this_click.getTag(C0775R.id.last_click_time);
        Long l = tag instanceof Long ? (Long) tag : null;
        long jLongValue = l != null ? l.longValue() : 0L;
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - jLongValue > j) {
            this_click.setTag(C0775R.id.last_click_time, Long.valueOf(jCurrentTimeMillis));
            Intrinsics.checkNotNull(view);
            action.invoke(view);
        }
    }

    public static /* synthetic */ void setDebouncedClickListener$default(View view, long j, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            j = 500;
        }
        setDebouncedClickListener(view, j, function1);
    }

    public static final void setDebouncedClickListener(View view, final long j, final Function1<? super View, Unit> onClick) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        Intrinsics.checkNotNullParameter(onClick, "onClick");
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        view.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.all.ViewKt$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ViewKt.setDebouncedClickListener$lambda$13(objectRef, onClick, j, view2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r9v1, types: [T, kotlinx.coroutines.Job] */
    public static final void setDebouncedClickListener$lambda$13(Ref.ObjectRef job, Function1 onClick, long j, View view) {
        Intrinsics.checkNotNullParameter(job, "$job");
        Intrinsics.checkNotNullParameter(onClick, "$onClick");
        Job job2 = (Job) job.element;
        if (job2 != null) {
            Job.DefaultImpls.cancel$default(job2, (CancellationException) null, 1, (Object) null);
        }
        job.element = BuildersKt__Builders_commonKt.launch$default(CoroutineScopeKt.CoroutineScope(Dispatchers.getMain()), null, null, new ViewKt$setDebouncedClickListener$1$1(onClick, view, j, null), 3, null);
    }

    public static final ObjectAnimator rotate(final View view, long j, int i, boolean z, final Lifecycle lifecycle, long j2, final Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        view.clearAnimation();
        if (view.isLaidOut() && !view.isLayoutRequested()) {
            view.setPivotX(view.getWidth() / 2.0f);
            view.setPivotY(view.getHeight() / 2.0f);
        } else {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.glasssutdio.wear.all.ViewKt$rotate$$inlined$doOnLayout$1
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    view2.removeOnLayoutChangeListener(this);
                    view.setPivotX(r1.getWidth() / 2.0f);
                    view.setPivotY(r1.getHeight() / 2.0f);
                }
            });
        }
        final ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ROTATION, z ? 0.0f : 360.0f, z ? 360.0f : 0.0f);
        objectAnimatorOfFloat.setDuration(j);
        objectAnimatorOfFloat.setRepeatCount(i);
        objectAnimatorOfFloat.setStartDelay(j2);
        objectAnimatorOfFloat.setInterpolator(new LinearInterpolator());
        if (i != -1 && function0 != null) {
            objectAnimatorOfFloat.addListener(new Animator.AnimatorListener() { // from class: com.glasssutdio.wear.all.ViewKt$rotate$rotationAnimator$1$1
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    Intrinsics.checkNotNullParameter(animation, "animation");
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animation) {
                    Intrinsics.checkNotNullParameter(animation, "animation");
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    Intrinsics.checkNotNullParameter(animation, "animation");
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    Intrinsics.checkNotNullParameter(animation, "animation");
                    function0.invoke();
                }
            });
        }
        if (lifecycle != null) {
            lifecycle.addObserver(new DefaultLifecycleObserver() { // from class: com.glasssutdio.wear.all.ViewKt.rotate.2
                @Override // androidx.lifecycle.DefaultLifecycleObserver
                public void onDestroy(LifecycleOwner owner) {
                    Intrinsics.checkNotNullParameter(owner, "owner");
                    objectAnimatorOfFloat.cancel();
                    view.clearAnimation();
                    lifecycle.removeObserver(this);
                }
            });
        }
        objectAnimatorOfFloat.start();
        Intrinsics.checkNotNull(objectAnimatorOfFloat);
        return objectAnimatorOfFloat;
    }
}
