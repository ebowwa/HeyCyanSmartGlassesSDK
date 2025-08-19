package com.glasssutdio.wear.ble.glass.thread;

import com.elvishew.xlog.XLog;
import com.oudmon.ble.base.bluetooth.BleOperateManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class ThreadManager {
    private static ThreadManager instance;
    private Condition mCondition;
    private Lock mLock;
    private Queue<IDo> queue;
    private WakeupThread wakeupThread;
    private WorkThread workThread;

    private ThreadManager() {
        try {
            this.queue = new Queue<>();
            ReentrantLock reentrantLock = new ReentrantLock();
            this.mLock = reentrantLock;
            this.mCondition = reentrantLock.newCondition();
            if (this.workThread == null) {
                WorkThread workThread = new WorkThread("ble-background-thread-1", this.mLock, this.mCondition);
                this.workThread = workThread;
                workThread.start();
            }
            if (this.wakeupThread == null) {
                WakeupThread wakeupThread = new WakeupThread("ble-background-wakeup-thread-1", this.queue);
                this.wakeupThread = wakeupThread;
                wakeupThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ThreadManager getInstance() {
        ThreadManager threadManager;
        ThreadManager threadManager2 = instance;
        if (threadManager2 != null) {
            return threadManager2;
        }
        synchronized (ThreadManager.class) {
            if (instance == null) {
                instance = new ThreadManager();
            }
            threadManager = instance;
        }
        return threadManager;
    }

    public void wakeUp() {
        if (BleOperateManager.getInstance().isConnected()) {
            this.queue.clear();
            needWait();
            XLog.m137i("已经连接上，清除队列里的唤醒重连操作");
        } else {
            removeAllWait();
            addTask(new WakeUpTask(this.workThread));
            wakeupThreadWakeUp();
        }
    }

    public void wakeUpNotWait() {
        if (BleOperateManager.getInstance().isConnected()) {
            this.queue.clear();
            needWait();
            XLog.m137i("已经连接上，清除队列里的唤醒重连操作");
        } else {
            removeAllWait();
            WakeUpTask wakeUpTask = new WakeUpTask(this.workThread);
            wakeUpTask.setNoWait(true);
            addTask(wakeUpTask);
            wakeupThreadWakeUp();
        }
    }

    public void addTask(IDo task) {
        this.queue.addTail(task);
    }

    public void needWait() {
        if (this.queue.size() > 0) {
            if (this.queue.get() instanceof SleepTask) {
                return;
            }
            this.queue.addFirst(new SleepTask(this.wakeupThread.getLock(), this.wakeupThread.getCondition()));
            return;
        }
        this.queue.addFirst(new SleepTask(this.wakeupThread.getLock(), this.wakeupThread.getCondition()));
    }

    public void removeAllWait() {
        LinkedList<IDo> allTask = this.queue.getAllTask();
        if (allTask == null || allTask.size() == 0) {
            return;
        }
        synchronized (allTask) {
            if (allTask.size() == 0) {
                return;
            }
            LinkedList linkedList = new LinkedList();
            Iterator<IDo> it = allTask.iterator();
            while (it.hasNext()) {
                IDo next = it.next();
                if (next instanceof SleepTask) {
                    linkedList.add(next);
                }
            }
            Iterator it2 = linkedList.iterator();
            while (it2.hasNext()) {
                this.queue.remove((IDo) it2.next());
            }
        }
    }

    private void wakeupThreadWakeUp() {
        this.wakeupThread.wakeUp();
    }

    public void setSleepMin() {
        this.workThread.setSleepTimeMin();
    }

    public void reSetLastConnectTime(int time) {
        this.workThread.setLastConnectTime(time);
    }
}
