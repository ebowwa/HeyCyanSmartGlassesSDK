package com.glasssutdio.wear.wifi.utils;

import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import java.util.Objects;

/* loaded from: classes.dex */
public final class Elvis<T> {
    private final T mObject;

    private Elvis(T t) {
        this.mObject = t;
    }

    private Elvis() {
        this.mObject = null;
    }

    /* renamed from: of */
    public static <T> Elvis<T> m176of(T t) {
        return new Elvis<>(t);
    }

    public static <T> Elvis<T> ofNonNull(T t) {
        return new Elvis<>(Objects.requireNonNull(t, "SHOULD NOT BE NULL"));
    }

    public static <T> Elvis<T> empty() {
        return new Elvis<>();
    }

    public <S> Elvis<S> next(Function<? super T, ? extends S> function) {
        T t = this.mObject;
        return new Elvis<>(t == null ? null : function.apply(t));
    }

    public T get() {
        return this.mObject;
    }

    public boolean getBoolean() {
        T t = this.mObject;
        if (t != null && (t instanceof Boolean)) {
            return ((Boolean) t).booleanValue();
        }
        return false;
    }

    public int getInt() {
        T t = this.mObject;
        if (t != null && (t instanceof Integer)) {
            return ((Integer) t).intValue();
        }
        return 0;
    }

    public long getLong() {
        T t = this.mObject;
        if (t != null && (t instanceof Long)) {
            return ((Long) t).longValue();
        }
        return 0L;
    }

    public double getDouble() {
        T t = this.mObject;
        if (t != null && (t instanceof Double)) {
            return ((Double) t).doubleValue();
        }
        return 0.0d;
    }

    public T orElse(T other) {
        T t = this.mObject;
        return t == null ? other : t;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T t = this.mObject;
        if (t != null) {
            consumer.accept(t);
        }
    }

    public boolean isPresent() {
        return this.mObject != null;
    }
}
