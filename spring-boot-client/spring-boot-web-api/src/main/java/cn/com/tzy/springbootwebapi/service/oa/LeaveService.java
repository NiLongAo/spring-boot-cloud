package cn.com.tzy.springbootwebapi.service.oa;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.oa.Leave;
import cn.com.tzy.springbootentity.param.activiti.impl.CommentEntity;
import cn.com.tzy.springbootentity.param.activiti.impl.StartProcessModelImpl;
import cn.com.tzy.springbootentity.param.oa.LeaveParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignacitiviti.api.activiti.ActivitiServiceFeign;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootfeignoa.api.oa.LeaveServiceFeign;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;

/**
 * 请假流程
 */
@Service
public class LeaveService {

    @Autowired
    private LeaveServiceFeign leaveServiceFeign;
    @Autowired
    private ActivitiServiceFeign activitiServiceFeign;
    @Autowired
    private UserServiceFeign userServiceFeign;

    public RestResult<?> find(Long id){
        return leaveServiceFeign.find(id);
    }
    /**
     * 创建请假单
     * @param param
     * @return
     */
    @SneakyThrows
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> insert(LeaveParam param){
        RestResult<?> result = leaveServiceFeign.insert(param);
        Leave leave = AppUtils.convertValue2(result.getData(), Leave.class);
        StartProcessModelImpl startProcessModel = StartProcessModelImpl.builder().processDefineKey("Leave")
                .userId(String.valueOf(leave.getUserId()))
                .name("请假流程申请")
                .businessKey(String.valueOf(leave.getId()))
                .comment(CommentEntity.builder()
                        .memo(leave.getMemo())
                        .build()
                )
                .variables(new HashMap<String, Object>() {{
                    put("day", leave.getDay());
                }}).build();
        RestResult<?> starProcess = activitiServiceFeign.starProcess(startProcessModel);
        String processInstanceId = AppUtils.convertValue2(starProcess.getData(), String.class);
        leaveServiceFeign.updateProcessInstanceId(LeaveParam.builder().id(leave.getId()).processInstanceId(processInstanceId).build());
        return RestResult.result(RespCode.CODE_0.getValue(),"创建成功");
    }

    /**
     * 结束流程需要
     * @param param
     * @return
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> updateState(LeaveParam param){
        RestResult<?> result = leaveServiceFeign.find(param.getId());
        if(ObjectUtils.isEmpty(result.getData())){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取请假单");
        }
        if(StringUtils.isEmpty(ConstEnum.ReviewStateEnum.getName(param.getState()))){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前状态错误");
        }
        return leaveServiceFeign.updateState(param);
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> delete(LeaveParam param){
        RestResult<?> result = leaveServiceFeign.find(param.getId());
        if(ObjectUtils.isEmpty(result.getData())){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取请假单");
        }
        Leave leave = AppUtils.convertValue2(result.getCode(), Leave.class);
        if(ConstEnum.ReviewStateEnum.IS_REVIEW.getValue() != leave.getState()){
            return RestResult.result(RespCode.CODE_2.getValue(),"当前状态不是审核中");
        }
        if(StringUtils.isNotEmpty(leave.getProcessInstanceId())){
            activitiServiceFeign.deleteProcessInstance(leave.getProcessInstanceId(),false,leave.getMemo());
        }
        return leaveServiceFeign.delete(param);
    }

}
