package com.oudmon.ble.base.communication;

import com.oudmon.ble.base.communication.bigData.resp.BaseResponse;

/* loaded from: classes2.dex */
public interface ILargeDataResponse<T extends BaseResponse> {
    void parseData(int i, T t);
}
