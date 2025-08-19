package com.oudmon.ble.base.bluetooth.queue;

import com.elvishew.xlog.XLog;
import com.oudmon.ble.base.bluetooth.BleOperateManager;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/* loaded from: classes2.dex */
public class BleThreadManager {
    private static BleThreadManager instance;
    private BleConsumer bleConsumer;
    private BlockingDeque<BleDataBean> queue = new LinkedBlockingDeque(20);

    private BleThreadManager() {
        if (this.bleConsumer == null) {
            BleConsumer bleConsumer = new BleConsumer("bleConsumer-" + new Random().nextInt(), this.queue);
            this.bleConsumer = bleConsumer;
            bleConsumer.start();
        }
    }

    public static BleThreadManager getInstance() {
        BleThreadManager bleThreadManager;
        BleThreadManager bleThreadManager2 = instance;
        if (bleThreadManager2 != null) {
            return bleThreadManager2;
        }
        synchronized (BleThreadManager.class) {
            if (instance == null) {
                instance = new BleThreadManager();
            }
            bleThreadManager = instance;
        }
        return bleThreadManager;
    }

    public void addData(BleDataBean bleDataBean) {
        try {
            if (!BleOperateManager.getInstance().isConnected()) {
                XLog.m137i("设备断开");
            } else {
                this.queue.putLast(bleDataBean);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        this.queue.clear();
    }
}
