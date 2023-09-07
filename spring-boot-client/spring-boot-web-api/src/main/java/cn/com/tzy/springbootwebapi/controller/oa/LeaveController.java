package cn.com.tzy.springbootwebapi.controller.oa;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.oa.LeaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.oa.LeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "请假相关接口",position = 2)
@RestController("WebApiOaLeaveController")
@RequestMapping(value = "/webapi/oa/leave")
public class LeaveController extends ApiController {

    @Autowired
    LeaveService leaveService;


    @ApiOperation(value = "根据编号查询请假信息", notes = "根据编号查询请假信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("find")
    public RestResult<?> find(@RequestParam("id")Long id){
        return leaveService.find(id);
    }


    @ApiOperation(value = "新增请假数据", notes = "新增请假数据")
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@Validated(BaseModel.add.class) @RequestBody LeaveParam param){
        return leaveService.insert(param);
    }

    @ApiOperation(value = "删除请假数据", notes = "删除请假数据")
    @PostMapping("delete")
    @ResponseBody
    public RestResult<?> delete(@Validated(BaseModel.delete.class) @RequestBody LeaveParam param){
        return leaveService.delete(param);
    }
}
