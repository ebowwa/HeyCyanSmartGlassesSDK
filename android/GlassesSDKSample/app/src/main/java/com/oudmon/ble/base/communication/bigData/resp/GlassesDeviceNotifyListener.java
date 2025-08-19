package com.oudmon.ble.base.communication.bigData.resp;

import com.oudmon.ble.base.communication.ILargeDataResponse;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public class GlassesDeviceNotifyListener implements ILargeDataResponse<GlassesDeviceNotifyRsp> {
    private ConcurrentHashMap<Integer, ILargeDataResponse<GlassesDeviceNotifyRsp>> respList = new ConcurrentHashMap<>();

    public void setOutRspIOdmOpResponse(int i, ILargeDataResponse<GlassesDeviceNotifyRsp> iLargeDataResponse) {
        this.respList.put(Integer.valueOf(i), iLargeDataResponse);
    }

    public void removeCallback(int i) {
        this.respList.remove(Integer.valueOf(i));
    }

    @Override // com.oudmon.ble.base.communication.ILargeDataResponse
    public void parseData(int i, GlassesDeviceNotifyRsp glassesDeviceNotifyRsp) {
        Iterator<ILargeDataResponse<GlassesDeviceNotifyRsp>> it = this.respList.values().iterator();
        while (it.hasNext()) {
            it.next().parseData(i, glassesDeviceNotifyRsp);
        }
    }
}
