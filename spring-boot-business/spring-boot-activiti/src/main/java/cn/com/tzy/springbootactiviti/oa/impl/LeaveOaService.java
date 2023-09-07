package cn.com.tzy.springbootactiviti.oa.impl;

import cn.com.tzy.springbootactiviti.oa.OaInterface;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.oa.Leave;
import cn.com.tzy.springbootentity.param.oa.LeaveParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignoa.api.oa.LeaveServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("LeaveOaService")
public class LeaveOaService implements OaInterface{
    @Autowired
    LeaveServiceFeign leaveServiceFeign;

    @Override
    public Map findObject(String id) {
        Leave leave = findLeave(Long.valueOf(id));
        return AppUtils.convertValue2(leave,Map.class);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        leaveServiceFeign.updateState(LeaveParam.builder().id(Long.valueOf(id)).state(status).build());
    }

    @Override
    public void deleteId(String id) {
        leaveServiceFeign.delete(LeaveParam.builder().id(Long.valueOf(id)).build());
    }

    public Leave findLeave(Long id){
        RestResult<?> result = leaveServiceFeign.find(id);
        return AppUtils.convertValue2(result.getData(), Leave.class);
    }


}
