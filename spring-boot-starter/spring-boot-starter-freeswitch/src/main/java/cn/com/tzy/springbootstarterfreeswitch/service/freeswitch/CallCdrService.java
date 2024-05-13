package cn.com.tzy.springbootstarterfreeswitch.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallDeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallLogInfo;

import java.util.List;

public interface CallCdrService {
    /**
     * 保存话单信息
     */
    void saveOrUpdateCallLog(CallLogInfo callLog);

    /**
     * 保存话单设备信息
     */
    void saveCallDevice(CallDeviceInfo callDevice);

    /**
     * 保存话单明细详情
     */
    void saveCallDetail(List<CallDetail> callDetails);
}
