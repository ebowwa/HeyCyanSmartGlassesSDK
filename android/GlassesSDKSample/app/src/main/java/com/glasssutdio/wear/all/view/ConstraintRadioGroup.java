package com.glasssutdio.wear.all.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import com.glasssutdio.wear.C0775R;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ConstraintRadioGroup.kt */
@Metadata(m606d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001,B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0002\u0010\u0006J$\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020\u00102\b\u0010!\u001a\u0004\u0018\u00010\"H\u0016J\b\u0010#\u001a\u00020\u001dH\u0014J\u000e\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\bJ\u0016\u0010&\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\b2\u0006\u0010'\u001a\u00020(J\u0010\u0010)\u001a\u00020\u001d2\u0006\u0010*\u001a\u00020+H\u0002R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\"\u0010\u0015\u001a\u0004\u0018\u00010\u00142\b\u0010\u000f\u001a\u0004\u0018\u00010\u0014@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001e\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013R\"\u0010\u001a\u001a\u0004\u0018\u00010\u00142\b\u0010\u000f\u001a\u0004\u0018\u00010\u0014@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0017¨\u0006-"}, m607d2 = {"Lcom/glasssutdio/wear/all/view/ConstraintRadioGroup;", "Landroidx/constraintlayout/widget/ConstraintLayout;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "checkedButton", "Landroid/widget/CompoundButton;", "checkedChangeListener", "Lcom/glasssutdio/wear/all/view/ConstraintRadioGroup$OnCheckedChangeListener;", "getCheckedChangeListener", "()Lcom/glasssutdio/wear/all/view/ConstraintRadioGroup$OnCheckedChangeListener;", "setCheckedChangeListener", "(Lcom/glasssutdio/wear/all/view/ConstraintRadioGroup$OnCheckedChangeListener;)V", "<set-?>", "", "selectedTextColor", "getSelectedTextColor", "()I", "Landroid/graphics/Typeface;", "selectedTextTypeface", "getSelectedTextTypeface", "()Landroid/graphics/Typeface;", "unSelectedTextColor", "getUnSelectedTextColor", "unSelectedTextTypeface", "getUnSelectedTextTypeface", "addView", "", "child", "Landroid/view/View;", "index", "params", "Landroid/view/ViewGroup$LayoutParams;", "onFinishInflate", "setCheckedButton", "compoundButton", "setCheckedStateForView", "checked", "", "setTypedArray", "typedArray", "Landroid/content/res/TypedArray;", "OnCheckedChangeListener", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class ConstraintRadioGroup extends ConstraintLayout {
    private CompoundButton checkedButton;
    private OnCheckedChangeListener checkedChangeListener;
    private int selectedTextColor;
    private Typeface selectedTextTypeface;
    private int unSelectedTextColor;
    private Typeface unSelectedTextTypeface;

    /* compiled from: ConstraintRadioGroup.kt */
    @Metadata(m606d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&¨\u0006\b"}, m607d2 = {"Lcom/glasssutdio/wear/all/view/ConstraintRadioGroup$OnCheckedChangeListener;", "", "onCheckedChanged", "", "group", "Lcom/glasssutdio/wear/all/view/ConstraintRadioGroup;", "checkedButton", "Landroid/widget/CompoundButton;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public interface OnCheckedChangeListener {
        void onCheckedChanged(ConstraintRadioGroup group, CompoundButton checkedButton);
    }

    public /* synthetic */ ConstraintRadioGroup(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ConstraintRadioGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
        this.selectedTextColor = ViewCompat.MEASURED_STATE_MASK;
        this.unSelectedTextColor = ViewCompat.MEASURED_STATE_MASK;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0775R.styleable.ConstraintRadioGroup);
        Intrinsics.checkNotNullExpressionValue(typedArrayObtainStyledAttributes, "obtainStyledAttributes(...)");
        setTypedArray(typedArrayObtainStyledAttributes);
    }

    public final OnCheckedChangeListener getCheckedChangeListener() {
        return this.checkedChangeListener;
    }

    public final void setCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.checkedChangeListener = onCheckedChangeListener;
    }

    public final int getSelectedTextColor() {
        return this.selectedTextColor;
    }

    public final int getUnSelectedTextColor() {
        return this.unSelectedTextColor;
    }

    public final Typeface getSelectedTextTypeface() {
        return this.selectedTextTypeface;
    }

    public final Typeface getUnSelectedTextTypeface() {
        return this.unSelectedTextTypeface;
    }

    private final void setTypedArray(TypedArray typedArray) {
        Typeface font;
        Typeface font2;
        this.selectedTextColor = typedArray.getColor(C0775R.styleable.ConstraintRadioGroup_selected_text_color, ViewCompat.MEASURED_STATE_MASK);
        this.unSelectedTextColor = typedArray.getColor(C0775R.styleable.ConstraintRadioGroup_unSelected_text_color, ViewCompat.MEASURED_STATE_MASK);
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                font = typedArray.getFont(C0775R.styleable.ConstraintRadioGroup_selected_font);
            } else {
                font = ResourcesCompat.getFont(getContext(), typedArray.getResourceId(C0775R.styleable.ConstraintRadioGroup_selected_font, 0));
            }
            this.selectedTextTypeface = font;
            if (Build.VERSION.SDK_INT >= 26) {
                font2 = typedArray.getFont(C0775R.styleable.ConstraintRadioGroup_unSelected_font);
            } else {
                font2 = ResourcesCompat.getFont(getContext(), typedArray.getResourceId(C0775R.styleable.ConstraintRadioGroup_unSelected_font, 0));
            }
            this.unSelectedTextTypeface = font2;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        typedArray.recycle();
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof CompoundButton) {
            CompoundButton compoundButton = (CompoundButton) child;
            if (compoundButton.isChecked()) {
                this.checkedButton = compoundButton;
            }
            setCheckedStateForView(compoundButton, false);
            compoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.glasssutdio.wear.all.view.ConstraintRadioGroup$$ExternalSyntheticLambda0
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton2, boolean z) {
                    ConstraintRadioGroup.addView$lambda$0(this.f$0, compoundButton2, z);
                }
            });
        }
        super.addView(child, index, params);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addView$lambda$0(ConstraintRadioGroup this$0, CompoundButton compoundButton, boolean z) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNull(compoundButton);
        this$0.setCheckedButton(compoundButton);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        CompoundButton compoundButton = this.checkedButton;
        if (compoundButton != null) {
            setCheckedStateForView(compoundButton, true);
        }
    }

    public final void setCheckedStateForView(CompoundButton compoundButton, boolean checked) {
        Intrinsics.checkNotNullParameter(compoundButton, "compoundButton");
        if (checked) {
            compoundButton.setTypeface(this.selectedTextTypeface);
            compoundButton.setTextColor(this.selectedTextColor);
        } else {
            compoundButton.setTypeface(this.unSelectedTextTypeface);
            compoundButton.setTextColor(this.unSelectedTextColor);
        }
        compoundButton.setChecked(checked);
    }

    public final void setCheckedButton(CompoundButton compoundButton) {
        Intrinsics.checkNotNullParameter(compoundButton, "compoundButton");
        if (Intrinsics.areEqual(this.checkedButton, compoundButton)) {
            return;
        }
        OnCheckedChangeListener onCheckedChangeListener = this.checkedChangeListener;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, compoundButton);
        }
        setCheckedStateForView(compoundButton, true);
        CompoundButton compoundButton2 = this.checkedButton;
        if (compoundButton2 != null) {
            setCheckedStateForView(compoundButton2, false);
        }
        this.checkedButton = compoundButton;
    }
}
