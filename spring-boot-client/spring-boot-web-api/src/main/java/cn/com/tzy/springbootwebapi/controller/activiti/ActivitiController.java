package cn.com.tzy.springbootwebapi.controller.activiti;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.activiti.DeployXml;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import cn.com.tzy.springbootentity.param.activiti.impl.TaskCompleteModelImpl;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.activiti.ActivitiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "工作流相关接口",position = 2)
@RestController("WebApiActivitiActivitiController")
@RequestMapping(value = "/webapi/activiti/activiti")
public class ActivitiController extends ApiController {

    @Autowired
    ActivitiService activitiService;

    @ApiOperation(value = "获取个人工作流统计", notes = "获取个人工作流统计")
    @GetMapping("stats_user_oa")
    public RestResult<?> statsUserOa(){
        return activitiService.statsUserOa();
    }


    @ApiOperation(value = "获取当前用户待办事项", notes = "获取个人工作流统计")
    @PostMapping("find_user_need_list")
    @ResponseBody
    public PageResult findUserNeedList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findUserNeedList(pageModel);
    }


    @ApiOperation(value = "获取当前用户发起事项", notes = "获取当前用户发起事项")
    @PostMapping("find_user_launch_list")
    @ResponseBody
    public PageResult findUserLaunchList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findUserLaunchList(pageModel);
    }

    @ApiOperation(value = "获取当前用户已办事项", notes = "获取当前用户已办事项")
    @PostMapping("find_user_already_list")
    @ResponseBody
    public PageResult findUserAlreadyList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findUserAlreadyList(pageModel);
    }

    @ApiOperation(value = "获取流程部署实例", notes = "获取流程部署实例")
    @PostMapping("find_repository_list")
    @ResponseBody
    public PageResult findRepositoryList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findRepositoryList(pageModel);
    }


    @ApiOperation(value = "总待办事项", notes = "总待办事项")
    @PostMapping("find_need_list")
    @ResponseBody
    public PageResult findNeedList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findNeedList(pageModel);
    }

    @ApiOperation(value = "总历史记录", notes = "总历史记录")
    @PostMapping("find_already_list")
    @ResponseBody
    public PageResult findAlreadyList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findAlreadyList(pageModel);
    }


    @ApiOperation(value = "根据流程编号获取审批历史记录", notes = "根据流程编号获取审批历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="instanceId", value="流程编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("find_historical_instance_id_list")
    public RestResult<?> findHistoricalInstanceIdList(@RequestParam("instanceId") String instanceId){
        return activitiService.findHistoricalInstanceIdList(instanceId);
    }


    @ApiOperation(value = "获取流程定义 xml", notes = "获取流程定义 xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name="processDefinitionId", value="流程定义编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("find_repository_xml")
    @ResponseBody
    public RestResult<?> findRepositoryXml(@RequestParam("processDefinitionId") String processDefinitionId){
        return activitiService.findRepositoryXml(processDefinitionId);
    }


    @ApiOperation(value = "部署流程引擎实例(以参数传输方式)", notes = "部署流程引擎实例(以参数传输方式)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="name", value="流程名称", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="xml", value="流程xml字符", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @PostMapping("deploy_process_parameter")
    @ResponseBody
    public RestResult<?> deployProcessParameter(@Validated @RequestBody DeployXml xml){
        return activitiService.deployProcessParameter(xml);
    }


    /**
     *
     * @param name 流程名称
     * @return 流程实例
     * @see StartProcessModel
     */
    @ApiOperation(value = "部署流程引擎实例(以文本传输方式)", notes = "部署流程引擎实例(以文本传输方式)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="name", value="流程名称", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="multipartFile", value="流程文件", required=true, paramType="query", dataType="__file", defaultValue="")
    })
    @PostMapping("deploy_process")
    @ResponseBody
    public RestResult<?> deployProcess(@RequestParam("name")String name,@RequestParam("multipartFile") MultipartFile multipartFile){
        return activitiService.deployProcess(name,multipartFile);
    }

    @ApiOperation(value = "删除流程引擎实例", notes = "删除流程引擎实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deploymentId", value="流程引擎编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="stats", value="1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("delete_process")
    @ResponseBody
    public RestResult<?> deleteProcess(@RequestParam("deploymentId")String deploymentId,@RequestParam("stats")Integer stats){
        return activitiService.deleteProcess(deploymentId,stats);
    }

    @ApiOperation(value = "删除流程实例-只能删除当前用户的实例", notes = "删除流程实例-只能删除当前用户的实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name="processInstanceId", value="流程引擎编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="memo", value="备注",  paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("delete_process_instance")
    @ResponseBody
    public RestResult<?> deleteProcessInstance(@RequestParam("processInstanceId")String processInstanceId,@RequestParam("memo")String memo){
        return activitiService.deleteProcessInstance(processInstanceId,memo);
    }


    @ApiOperation(value = "完成任务", notes = "完成任务")
    @PostMapping("complete")
    @ResponseBody
    public RestResult<?> complete(@Validated @RequestBody TaskCompleteModelImpl model){
        return activitiService.complete(model);
    }


    @ApiOperation(value = "签收任务", notes = "签收任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="任务编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("claim")
    @ResponseBody
    public RestResult<?> claim(@RequestParam("taskId") String taskId){
        return activitiService.claim(taskId);
    }

    @ApiOperation(value = "指定用户签收任务", notes = "指定用户签收任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="任务编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="userId", value="签收用户编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("appoint_claim")
    @ResponseBody
    public RestResult<?> appointClaim(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId){
        return activitiService.appointClaim(taskId,userId);
    }

    @ApiOperation(value = "任务跳转", notes = "任务跳转")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="当前的任务", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="targetTaskKey", value="目前任务的定义key", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("jump")
    @ResponseBody
    public RestResult<?> jump(@RequestParam("taskId") String taskId, @RequestParam("targetTaskKey")String targetTaskKey){
        return activitiService.jump(taskId,targetTaskKey);
    }


    @ApiOperation(value = "挂起 - 激活 流程实例流程", notes = "挂起 - 激活 流程实例流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name="instanceId", value="流程编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("suspended_instance")
    @ResponseBody
    public RestResult<?> suspendedInstance(@RequestParam("instanceId") String instanceId){
        return activitiService.suspendedInstance(instanceId);
    }

    @ApiOperation(value = "挂起 - 激活 流程定义", notes = "挂起 - 激活 流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name="processDefinitionId", value="流程定义编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("suspended_process_definition")
    @ResponseBody
    public RestResult<?> suspendedProcessDefinition(@RequestParam("processDefinitionId")String processDefinitionId){
        return activitiService.suspendedProcessDefinition(processDefinitionId);
    }

    @ApiOperation(value = "驳回上一流程", notes = "驳回上一流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="审核节点id", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("back_process")
    public RestResult<?> backProcess(@RequestParam("taskId") String taskId){
        return activitiService.backProcess(taskId);
    }


    @ApiOperation(value = "根据节点编号获取节点信息及实例信息", notes = "根据节点编号获取节点信息及实例信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="instanceId", value="流程编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("find_instance_id_detail")
    @ResponseBody
    public RestResult<?> findInstanceIdDetail(@RequestParam("instanceId") String instanceId){
        return activitiService.findInstanceIdDetail(instanceId);
    }


    @ApiOperation(value = "根据流程实例Id,获取实时流程图片", notes = "根据流程实例Id,获取实时流程图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name="instanceId", value="流程编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="useCustomColor", value="是否自定义颜色", required=true, paramType="query", dataType="Boolean", defaultValue="true")
    })
    @GetMapping("get_flow_img_by_instance_id")
    @ResponseBody
    public RestResult<?> getFlowImgByInstanceId(@RequestParam("instanceId") String instanceId,@RequestParam("useCustomColor")Boolean useCustomColor){
        return activitiService.getFlowImgByInstanceId(instanceId,useCustomColor);
    }
}
