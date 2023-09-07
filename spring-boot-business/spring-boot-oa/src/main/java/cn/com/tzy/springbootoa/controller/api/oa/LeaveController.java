package cn.com.tzy.springbootoa.controller.api.oa;

import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.dome.oa.Leave;
import cn.com.tzy.springbootentity.param.oa.LeaveParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootoa.service.LeaveService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 请假流程
 */
@RestController("ApiOaLeaveController")
@RequestMapping(value = "/api/oa/leave")
public class LeaveController extends ApiController {

    @Autowired
    LeaveService leaveService;
    @Autowired
    UserServiceFeign userServiceFeign;

    @GetMapping("find")
    @ResponseBody
    public RestResult<?> find(@RequestParam("id")Long id){
        Leave leave = leaveService.getById(id);
        return RestResult.result(RespCode.CODE_0.getValue(),"操作成功",leave);
    }


    @SneakyThrows
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@Validated(BaseModel.add.class) @RequestBody LeaveParam param){
        Long userId = JwtUtils.getUserId();
        RestResult<?> userInfo = userServiceFeign.getInfo(userId);
        RestResult<?> result = userServiceFeign.findUserConnectDepartment(userId);
        User user = null;
        List<Map> departmentList = null;
        try {
            user = (User) AppUtils.decodeJson2(AppUtils.encodeJson(userInfo.getData()), User.class);
            if(result.getData() != null && !ObjectUtils.isEmpty(result.getData())){
                departmentList = (List<Map>) AppUtils.convertValue2(result.getData(),List.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Leave leave = new Leave();
        BeanUtils.copyProperties(param,leave);
        leave.setUserId(user.getId());
        leave.setUserName(user.getUserName());
        if(departmentList != null){
            Map map = departmentList.get(0);
            leave.setDepartmentId(Long.valueOf(String.valueOf(map.get("departmentId"))));
            leave.setDepartmentName(String.valueOf(map.get("departmentName")));
        }else {
            leave.setDepartmentId(1l);
            leave.setDepartmentName("无");
        }
        leave.setStartTime(DateUtils.parseDate(param.getStartTime(), new String[]{Constant.DATE_TIME_FORMAT,Constant.DATE_FORMAT}));
        leave.setEndTime(DateUtils.parseDate(param.getEndTime(), new String[]{Constant.DATE_TIME_FORMAT,Constant.DATE_FORMAT}));
        leaveService.save(leave);
        Leave byId = leaveService.getById(leave);
        return RestResult.result(RespCode.CODE_0.getValue(),"操作成功",byId);
    }

    @PostMapping("updateState")
    @ResponseBody
    public RestResult<?> updateState(@Validated(LeaveParam.editState.class) @RequestBody LeaveParam param){
        Leave leave = new Leave();
        leave.setId(param.getId());
        leave.setState(param.getState());
        leaveService.updateById(leave);
        return RestResult.result(RespCode.CODE_0.getValue(),"操作成功");
    }

    @PostMapping("update_process_instance_id")
    @ResponseBody
    public RestResult<?> updateProcessInstanceId(@Validated(LeaveParam.editProcessInstanceId.class) @RequestBody LeaveParam param){
        Leave leave = new Leave();
        leave.setId(param.getId());
        leave.setProcessInstanceId(param.getProcessInstanceId());
        leaveService.updateById(leave);
        return RestResult.result(RespCode.CODE_0.getValue(),"操作成功");
    }

    @PostMapping("delete")
    @ResponseBody
    public RestResult<?> delete(@Validated(BaseModel.delete.class) @RequestBody LeaveParam param){
        leaveService.removeById(param.getId());
        return RestResult.result(RespCode.CODE_0.getValue(),"操作成功");
    }

}
