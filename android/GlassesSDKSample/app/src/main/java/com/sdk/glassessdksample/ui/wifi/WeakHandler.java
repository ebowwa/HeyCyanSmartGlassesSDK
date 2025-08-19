package com.glasssutdio.wear.wifi;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class WeakHandler {
    private final Handler.Callback mCallback;
    private final ExecHandler mExec;
    private Lock mLock;
    final ChainedRef mRunnables;

    public WeakHandler() {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = null;
        this.mExec = new ExecHandler();
    }

    public WeakHandler(Handler.Callback callback) {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = callback;
        this.mExec = new ExecHandler((WeakReference<Handler.Callback>) new WeakReference(callback));
    }

    public WeakHandler(Looper looper) {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = null;
        this.mExec = new ExecHandler(looper);
    }

    public WeakHandler(Looper looper, Handler.Callback callback) {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = callback;
        this.mExec = new ExecHandler(looper, new WeakReference(callback));
    }

    public final boolean post(Runnable r) {
        return this.mExec.post(wrapRunnable(r));
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return this.mExec.postAtTime(wrapRunnable(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return this.mExec.postAtTime(wrapRunnable(r), token, uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return this.mExec.postDelayed(wrapRunnable(r), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return this.mExec.postAtFrontOfQueue(wrapRunnable(r));
    }

    public final void removeCallbacks(Runnable r) {
        WeakRunnable weakRunnableRemove = this.mRunnables.remove(r);
        if (weakRunnableRemove != null) {
            this.mExec.removeCallbacks(weakRunnableRemove);
        }
    }

    public final void removeCallbacks(Runnable r, Object token) {
        WeakRunnable weakRunnableRemove = this.mRunnables.remove(r);
        if (weakRunnableRemove != null) {
            this.mExec.removeCallbacks(weakRunnableRemove, token);
        }
    }

    public final boolean sendMessage(Message msg) {
        return this.mExec.sendMessage(msg);
    }

    public final boolean sendEmptyMessage(int what) {
        return this.mExec.sendEmptyMessage(what);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        return this.mExec.sendEmptyMessageDelayed(what, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        return this.mExec.sendEmptyMessageAtTime(what, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        return this.mExec.sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        return this.mExec.sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        return this.mExec.sendMessageAtFrontOfQueue(msg);
    }

    public final void removeMessages(int what) {
        this.mExec.removeMessages(what);
    }

    public final void removeMessages(int what, Object object) {
        this.mExec.removeMessages(what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        this.mExec.removeCallbacksAndMessages(token);
    }

    public final boolean hasMessages(int what) {
        return this.mExec.hasMessages(what);
    }

    public final boolean hasMessages(int what, Object object) {
        return this.mExec.hasMessages(what, object);
    }

    public final Looper getLooper() {
        return this.mExec.getLooper();
    }

    private WeakRunnable wrapRunnable(Runnable r) {
        if (r == null) {
            throw new NullPointerException("Runnable can't be null");
        }
        ChainedRef chainedRef = new ChainedRef(this.mLock, r);
        this.mRunnables.insertAfter(chainedRef);
        return chainedRef.wrapper;
    }

    private static class ExecHandler extends Handler {
        private final WeakReference<Handler.Callback> mCallback;

        ExecHandler() {
            this.mCallback = null;
        }

        ExecHandler(WeakReference<Handler.Callback> callback) {
            this.mCallback = callback;
        }

        ExecHandler(Looper looper) {
            super(looper);
            this.mCallback = null;
        }

        ExecHandler(Looper looper, WeakReference<Handler.Callback> callback) {
            super(looper);
            this.mCallback = callback;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Handler.Callback callback;
            WeakReference<Handler.Callback> weakReference = this.mCallback;
            if (weakReference == null || (callback = weakReference.get()) == null) {
                return;
            }
            callback.handleMessage(msg);
        }
    }

    static class WeakRunnable implements Runnable {
        private final WeakReference<Runnable> mDelegate;
        private final WeakReference<ChainedRef> mReference;

        WeakRunnable(WeakReference<Runnable> delegate, WeakReference<ChainedRef> reference) {
            this.mDelegate = delegate;
            this.mReference = reference;
        }

        @Override // java.lang.Runnable
        public void run() {
            Runnable runnable = this.mDelegate.get();
            ChainedRef chainedRef = this.mReference.get();
            if (chainedRef != null) {
                chainedRef.remove();
            }
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    static class ChainedRef {
        Lock lock;
        ChainedRef next;
        ChainedRef prev;
        final Runnable runnable;
        final WeakRunnable wrapper;

        public ChainedRef(Lock lock, Runnable r) {
            this.runnable = r;
            this.lock = lock;
            this.wrapper = new WeakRunnable(new WeakReference(r), new WeakReference(this));
        }

        public WeakRunnable remove() {
            this.lock.lock();
            try {
                ChainedRef chainedRef = this.prev;
                if (chainedRef != null) {
                    chainedRef.next = this.next;
                }
                ChainedRef chainedRef2 = this.next;
                if (chainedRef2 != null) {
                    chainedRef2.prev = chainedRef;
                }
                this.prev = null;
                this.next = null;
                this.lock.unlock();
                return this.wrapper;
            } catch (Throwable th) {
                this.lock.unlock();
                throw th;
            }
        }

        public void insertAfter(ChainedRef candidate) {
            this.lock.lock();
            try {
                ChainedRef chainedRef = this.next;
                if (chainedRef != null) {
                    chainedRef.prev = candidate;
                }
                candidate.next = chainedRef;
                this.next = candidate;
                candidate.prev = this;
            } finally {
                this.lock.unlock();
            }
        }

        public WeakRunnable remove(Runnable obj) {
            this.lock.lock();
            try {
                for (ChainedRef chainedRef = this.next; chainedRef != null; chainedRef = chainedRef.next) {
                    if (chainedRef.runnable == obj) {
                        return chainedRef.remove();
                    }
                }
                this.lock.unlock();
                return null;
            } finally {
                this.lock.unlock();
            }
        }
    }
}
