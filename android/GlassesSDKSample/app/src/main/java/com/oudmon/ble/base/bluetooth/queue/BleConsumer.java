package com.oudmon.ble.base.bluetooth.queue;

import com.oudmon.ble.base.bluetooth.BleOperateManager;
import com.oudmon.ble.base.communication.utils.CRC16;
import com.oudmon.ble.base.request.WriteRequest;
import com.oudmon.qc_utils.bytes.DataTransferUtils;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/* loaded from: classes2.dex */
public class BleConsumer extends Thread {
    BlockingQueue<BleDataBean> blockingQueue;
    private static final UUID SERIAL_PORT_SERVICE = UUID.fromString("de5bf728-d711-4e47-af26-65e3012a5dc7");
    private static final UUID SERIAL_PORT_CHARACTER_NOTIFY = UUID.fromString("de5bf729-d711-4e47-af26-65e3012a5dc7");
    private static final UUID SERIAL_PORT_CHARACTER_WRITE = UUID.fromString("de5bf72a-d711-4e47-af26-65e3012a5dc7");

    public BleConsumer(String str, BlockingQueue<BleDataBean> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws InterruptedException {
        while (true) {
            try {
                BleDataBean bleDataBeanTake = this.blockingQueue.take();
                if (bleDataBeanTake.getSleepTime() > 0) {
                    Thread.sleep(bleDataBeanTake.getSleepTime());
                }
                int subLength = bleDataBeanTake.getSubLength();
                int i = 0;
                while (true) {
                    int i2 = i * subLength;
                    if (i2 < bleDataBeanTake.getData().length) {
                        BleOperateManager.getInstance().execute(getWriteRequest(Arrays.copyOfRange(bleDataBeanTake.getData(), i2, Math.min(subLength, bleDataBeanTake.getData().length - i2) + i2)));
                        i++;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private byte[] addHeader(int i, byte[] bArr) {
        byte[] bArr2 = new byte[(bArr == null ? 0 : bArr.length) + 6];
        bArr2[0] = -68;
        bArr2[1] = (byte) i;
        if (bArr != null && bArr.length > 0) {
            System.arraycopy(DataTransferUtils.shortToBytes((short) bArr.length), 0, bArr2, 2, 2);
            System.arraycopy(DataTransferUtils.shortToBytes((short) CRC16.calcCrc16(bArr)), 0, bArr2, 4, 2);
            System.arraycopy(bArr, 0, bArr2, 6, bArr.length);
        } else {
            bArr2[4] = -1;
            bArr2[5] = -1;
        }
        return bArr2;
    }

    public WriteRequest getWriteRequest(byte[] bArr) {
        WriteRequest noRspInstance = WriteRequest.getNoRspInstance(SERIAL_PORT_SERVICE, SERIAL_PORT_CHARACTER_WRITE);
        noRspInstance.setValue(bArr);
        return noRspInstance;
    }
}
