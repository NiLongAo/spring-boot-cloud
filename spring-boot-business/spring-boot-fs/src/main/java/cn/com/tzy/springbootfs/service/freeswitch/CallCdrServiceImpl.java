package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallDeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallLogInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.CallCdrService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallCdrServiceImpl implements CallCdrService {
    @Override
    public void saveOrUpdateCallLog(CallLogInfo callLog) {

    }

    @Override
    public void saveCallDevice(CallDeviceInfo callDevice) {

    }

    @Override
    public void saveCallDetail(List<CallDetail> callDetails) {

    }
}
