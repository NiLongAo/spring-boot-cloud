package cn.com.tzy.springbootactiviti.controller.activiti;

import cn.com.tzy.springbootactiviti.service.ActivitiService;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.activiti.DeployXml;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import cn.com.tzy.springbootentity.param.activiti.TaskCompleteModel;
import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.activiti.impl.StartProcessModelImpl;
import cn.com.tzy.springbootentity.param.activiti.impl.TaskCompleteModelImpl;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import io.seata.core.context.RootContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("ApiActivitiActivitiController")
@RequestMapping(value = "/api/activiti/activiti")
public class ActivitiController extends ApiController {

    @Autowired
    private ActivitiService activitiService;

    /**
     * 获取个人工作流统计
     * @return
     */
    @GetMapping("stats_user_oa")
    public RestResult<?> statsUserOa(){
        return activitiService.statsUserOa();
    }

    /**
     * 获取当前用户待办事项
     * @return
     */
    @PostMapping("find_user_need_list")
    @ResponseBody
    public PageResult findUserNeedList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findUserNeedList(pageModel);
    }

    /**
     * 获取当前用户发起事项
     * @return
     */
    @PostMapping("find_user_launch_list")
    @ResponseBody
    public PageResult findUserLaunchList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findUserLaunchList(pageModel);
    }

    /**
     * 获取当前用户已办事项
     * @return
     */
    @PostMapping("find_user_already_list")
    @ResponseBody
    public PageResult findUserAlreadyList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findUserAlreadyList(pageModel);
    }

    /**
     * 获取流程部署实例
     * @return
     */
    @PostMapping("find_repository_list")
    @ResponseBody
    public PageResult findRepositoryList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findRepositoryList(pageModel);
    }

    /**
     * 待办事项
     */
    @PostMapping("find_need_list")
    @ResponseBody
    public PageResult findNeedList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findNeedList(pageModel);
    }

    /**
     * 历史记录
     */
    @PostMapping("find_already_list")
    @ResponseBody
    public PageResult findAlreadyList(@Validated @RequestBody PageModel pageModel){
        return activitiService.findAlreadyList(pageModel);
    }


    /**
     * 根据节点编号获取节点信息及实例信息
     */
    @GetMapping("find_instance_id_detail")
    @ResponseBody
    public RestResult<?> findInstanceIdDetail(@RequestParam("instanceId") String instanceId){
        return activitiService.findInstanceIdDetail(instanceId);
    }

    /**
     * 根据流程编号获取审批历史记录
     */
    @GetMapping("find_historical_instance_id_list")
    @ResponseBody
    public RestResult<?> findHistoricalInstanceIdList(@RequestParam("instanceId") String instanceId){
        return activitiService.findHistoricalInstanceIdList(instanceId);
    }


    /**
     * 获取流程定义 xml
     */
    @GetMapping("find_repository_xml")
    @ResponseBody
    public RestResult<?> findRepositoryXml(@RequestParam("processDefinitionId") String processDefinitionId){
        return activitiService.findRepositoryXml(processDefinitionId);
    }

    /**
     * 部署流程引擎实例(以参数传输方式)
     * @param xml 流程xml
     * @return 流程实例
     * @see StartProcessModel
     */
    @PostMapping("deploy_process_parameter")
    @ResponseBody
    public RestResult<?> deployProcessParameter(@Validated @RequestBody DeployXml xml){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result =activitiService.deployProcessParameter(xml);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }


    /**
     * 部署流程引擎实例(以文本传输方式)
     * @param name 流程名称
     * @return 流程实例
     * @see StartProcessModel
     */
    @PostMapping("deploy_process")
    @ResponseBody
    public RestResult<?> deployProcess(@RequestParam("name")String name,@RequestParam("multipartFile") MultipartFile multipartFile){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result =activitiService.deployProcess(name,multipartFile);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 删除流程引擎实例
     * @param deploymentId
     * @param stats 1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除
     * @see StartProcessModel
     */
    @GetMapping("delete_process")
    @ResponseBody
    public RestResult<?> deleteProcess(@RequestParam("deploymentId")String deploymentId,@RequestParam("stats")Integer stats){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result =activitiService.deleteProcess(deploymentId,stats);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 删除流程实例
     * @param processInstanceId 流程实例编号
     * @param memo 备注
     * @see StartProcessModel
     */
    @GetMapping("delete_process_instance")
    @ResponseBody
    public RestResult<?> deleteProcessInstance(@RequestParam("processInstanceId")String processInstanceId,@RequestParam("isUser")Boolean isUser,@RequestParam("memo")String memo){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result =activitiService.deleteProcessInstance(processInstanceId,isUser,memo);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }


    /**
     * 开始一个流程
     *
     * @param model 开始流程模型
     * @return 流程实例
     * @see StartProcessModel
     */
    @PostMapping("star_process")
    @ResponseBody
    public RestResult<?> starProcess(@Validated @RequestBody StartProcessModelImpl model){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result =activitiService.starProcess(model);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 完成任务
     *
     * @param model 任务完成模型
     * @see TaskCompleteModel
     */
    @PostMapping("complete")
    @ResponseBody
    public RestResult<?> complete(@Validated @RequestBody TaskCompleteModelImpl model){
        String unbindXid = RootContext.unbind();
        model.setUserId(String.valueOf(JwtUtils.getUserId()));
        RestResult<?> result = null;
        try {
            result = activitiService.complete(model);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    @GetMapping("claim")
    @ResponseBody
    public RestResult<?> claim(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result = activitiService.claim(taskId, userId);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 指定用户签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    @GetMapping("appoint_claim")
    @ResponseBody
    public RestResult<?> appointClaim(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result = activitiService.appointClaim(taskId, userId);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 任务跳转
     *
     * @param taskId        当前的任务
     * @param targetTaskKey 目前任务的定义key
     */
    @GetMapping("jump")
    @ResponseBody
    public RestResult<?> jump(@RequestParam("taskId") String taskId, @RequestParam("targetTaskKey")String targetTaskKey){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result = activitiService.jump(taskId, targetTaskKey);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 挂起 - 激活 流程实例流程
     *
     * @param instanceId 流程实例ID
     */
    @GetMapping("suspended_instance")
    @ResponseBody
    public RestResult<?> suspendedInstance(@RequestParam("instanceId") String instanceId){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result = activitiService.suspendedInstance(instanceId);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 挂起 - 激活 流程定义
     *
     * @param processDefinitionId 流程实例ID
     */
    @GetMapping("suspended_process_definition")
    @ResponseBody
    public RestResult<?> suspendedProcessDefinition(@RequestParam("processDefinitionId")String processDefinitionId){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result = activitiService.suspendedProcessDefinition(processDefinitionId);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 驳回上一流程
     * @param taskId 审核节点id
     */
    @GetMapping("back_process")
    public RestResult<?> backProcess(@RequestParam("taskId") String taskId){
        String unbindXid = RootContext.unbind();
        RestResult<?> result = null;
        try {
            result =activitiService.backProcess(taskId);
        }catch (Exception e){
            throw e;
        }finally {
            RootContext.bind(unbindXid);
        }
        return result;
    }

    /**
     * 根据流程实例Id,获取实时流程图片
     * @param instanceId 任务模型
     * @param useCustomColor 是否自定义颜色
     */
    @GetMapping("get_flow_img_by_instance_id")
    @ResponseBody
    public RestResult<?> getFlowImgByInstanceId(@RequestParam("instanceId") String instanceId,@RequestParam("useCustomColor")Boolean useCustomColor){
        return activitiService.getFlowImgByInstanceId(instanceId,useCustomColor);
    }

}
