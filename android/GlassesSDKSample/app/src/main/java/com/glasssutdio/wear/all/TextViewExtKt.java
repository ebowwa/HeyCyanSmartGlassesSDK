package com.glasssutdio.wear.all;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TextViewExt.kt */
@Metadata(m606d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a(\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0001\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u001a\n\u0010\b\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\t\u001a\u00020\u0001*\u00020\u0002¨\u0006\n"}, m607d2 = {"setDrawableWithPadding", "", "Landroid/widget/TextView;", "drawableRes", "", "paddingPx", "position", "Lcom/glasssutdio/wear/all/DrawablePosition;", "setupMarquee", "setupMarqueeWithClick", "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class TextViewExtKt {

    /* compiled from: TextViewExt.kt */
    @Metadata(m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[DrawablePosition.values().length];
            try {
                iArr[DrawablePosition.START.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[DrawablePosition.TOP.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[DrawablePosition.END.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[DrawablePosition.BOTTOM.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public static final void setupMarquee(final TextView textView) {
        Intrinsics.checkNotNullParameter(textView, "<this>");
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSingleLine(true);
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setOnTouchListener(new View.OnTouchListener() { // from class: com.glasssutdio.wear.all.TextViewExtKt$$ExternalSyntheticLambda2
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return TextViewExtKt.setupMarquee$lambda$0(textView, view, motionEvent);
            }
        });
        textView.setHorizontalFadingEdgeEnabled(true);
        textView.setFadingEdgeLength(GlobalKt.getDp((Number) 10));
        textView.post(new Runnable() { // from class: com.glasssutdio.wear.all.TextViewExtKt$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TextViewExtKt.setupMarquee$lambda$1(textView);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean setupMarquee$lambda$0(TextView this_setupMarquee, View view, MotionEvent motionEvent) {
        Intrinsics.checkNotNullParameter(this_setupMarquee, "$this_setupMarquee");
        if (motionEvent.getAction() == 0) {
            this_setupMarquee.requestFocus();
            this_setupMarquee.getParent().requestDisallowInterceptTouchEvent(true);
            this_setupMarquee.setSelected(true);
            this_setupMarquee.performClick();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setupMarquee$lambda$1(TextView this_setupMarquee) {
        Intrinsics.checkNotNullParameter(this_setupMarquee, "$this_setupMarquee");
        this_setupMarquee.requestFocus();
        this_setupMarquee.setSelected(true);
    }

    public static final void setupMarqueeWithClick(final TextView textView) {
        Intrinsics.checkNotNullParameter(textView, "<this>");
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSingleLine(true);
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setOnTouchListener(new View.OnTouchListener() { // from class: com.glasssutdio.wear.all.TextViewExtKt$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return TextViewExtKt.setupMarqueeWithClick$lambda$2(textView, view, motionEvent);
            }
        });
        textView.setHorizontalFadingEdgeEnabled(true);
        textView.setFadingEdgeLength(GlobalKt.getDp((Number) 10));
        textView.post(new Runnable() { // from class: com.glasssutdio.wear.all.TextViewExtKt$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TextViewExtKt.setupMarqueeWithClick$lambda$3(textView);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean setupMarqueeWithClick$lambda$2(TextView this_setupMarqueeWithClick, View view, MotionEvent motionEvent) {
        Intrinsics.checkNotNullParameter(this_setupMarqueeWithClick, "$this_setupMarqueeWithClick");
        if (motionEvent.getAction() == 0) {
            this_setupMarqueeWithClick.requestFocus();
            this_setupMarqueeWithClick.getParent().requestDisallowInterceptTouchEvent(true);
            this_setupMarqueeWithClick.setSelected(true);
            this_setupMarqueeWithClick.performClick();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setupMarqueeWithClick$lambda$3(TextView this_setupMarqueeWithClick) {
        Intrinsics.checkNotNullParameter(this_setupMarqueeWithClick, "$this_setupMarqueeWithClick");
        this_setupMarqueeWithClick.requestFocus();
        this_setupMarqueeWithClick.setSelected(true);
    }

    public static /* synthetic */ void setDrawableWithPadding$default(TextView textView, int i, int i2, DrawablePosition drawablePosition, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            i2 = 0;
        }
        if ((i3 & 4) != 0) {
            drawablePosition = DrawablePosition.START;
        }
        setDrawableWithPadding(textView, i, i2, drawablePosition);
    }

    public static final void setDrawableWithPadding(TextView textView, int i, int i2, DrawablePosition position) {
        Intrinsics.checkNotNullParameter(textView, "<this>");
        Intrinsics.checkNotNullParameter(position, "position");
        Drawable drawable = ContextCompat.getDrawable(textView.getContext(), i);
        textView.setCompoundDrawablePadding(i2);
        int i3 = WhenMappings.$EnumSwitchMapping$0[position.ordinal()];
        if (i3 == 1) {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        if (i3 == 2) {
            textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        } else if (i3 == 3) {
            textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, drawable, (Drawable) null);
        } else {
            if (i3 != 4) {
                return;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, drawable);
        }
    }
}
