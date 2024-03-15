package cn.com.tzy.springbootfeignacitiviti.api.activiti;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootentity.param.activiti.DeployXml;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import cn.com.tzy.springbootentity.param.activiti.TaskCompleteModel;
import cn.com.tzy.springbootentity.param.activiti.impl.StartProcessModelImpl;
import cn.com.tzy.springbootentity.param.activiti.impl.TaskCompleteModelImpl;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "activiti-server",contextId = "activiti-server",path = "/api/activiti/activiti",configuration = FeignConfiguration.class)
public interface ActivitiServiceFeign {

    /**
     * 获取个人工作流统计
     * @return
     */
    @RequestMapping(value = "stats_user_oa",method = RequestMethod.GET)
    public RestResult<?> statsUserOa();

    /**
     * 获取当前用户待办事项
     * @return
     */
    @RequestMapping(value = "find_user_need_list",method = RequestMethod.POST)
    public PageResult findUserNeedList( @RequestBody PageModel pageModel);

    /**
     * 获取当前用户发起事项
     * @return
     */
    @RequestMapping(value = "find_user_launch_list",method = RequestMethod.POST)
    public PageResult findUserLaunchList( @RequestBody PageModel pageModel);

    /**
     * 获取当前用户已办事项
     * @return
     */
    @RequestMapping(value = "find_user_already_list",method = RequestMethod.POST)
    public PageResult findUserAlreadyList( @RequestBody PageModel pageModel);

    /**
     * 获取流程部署实例
     * @return
     */
    @RequestMapping(value = "find_repository_list",method = RequestMethod.POST)
    public PageResult findRepositoryList( @RequestBody PageModel pageModel);

    /**
     * 待办事项
     */
    @RequestMapping(value = "find_need_list",method = RequestMethod.POST)
    public PageResult findNeedList( @RequestBody PageModel pageModel);
    /**
     * 历史记录
     */
    @RequestMapping(value = "find_already_list",method = RequestMethod.POST)
    public PageResult findAlreadyList( @RequestBody PageModel pageModel);


    /**
     * 根据流程编号获取审批历史记录
     */
    @RequestMapping(value = "find_historical_instance_id_list",method = RequestMethod.GET)
    public RestResult<?> findHistoricalInstanceIdList(@RequestParam("instanceId") String instanceId);

    /**
     * 获取流程定义 xml
     */
    @RequestMapping(value = "find_repository_xml",method = RequestMethod.GET)
    public RestResult<?> findRepositoryXml(@RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 部署流程引擎实例(以参数传输方式)
     * @param xml 流程xml
     * @return 流程实例
     * @see StartProcessModel
     */
    @RequestMapping(value = "deploy_process_parameter",method = RequestMethod.POST)
    public RestResult<?> deployProcessParameter(@RequestBody DeployXml xml);


    /**
     * 部署流程引擎实例(以文本传输方式)
     * @param name 流程名称
     * @return 流程实例
     * @see StartProcessModel
     */
    @RequestMapping(value = "deploy_process",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    public RestResult<?> deployProcess(@RequestParam("name")String name,@RequestPart("multipartFile") MultipartFile multipartFile);

    /**
     * 删除流程引擎实例
     * @param deploymentId
     * @param stats 1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除
     * @see StartProcessModel
     */
    @RequestMapping(value = "delete_process",method = RequestMethod.GET)
    public RestResult<?> deleteProcess(@RequestParam("deploymentId")String deploymentId,@RequestParam("stats")Integer stats);

    /**
     * 删除流程引擎实例
     * @param processInstanceId
     * @param memo 备注
     * @see StartProcessModel
     */
    @RequestMapping(value = "delete_process_instance",method = RequestMethod.GET)
    public RestResult<?> deleteProcessInstance(@RequestParam("processInstanceId")String processInstanceId,@RequestParam("isUser")Boolean isUser,@RequestParam("memo")String memo);


    /**
     * 开始一个流程
     *
     * @param model 开始流程模型
     * @return 流程实例
     * @see StartProcessModel
     */
    @RequestMapping(value = "star_process",method = RequestMethod.POST)
    public RestResult<?> starProcess( @RequestBody StartProcessModelImpl model);

    /**
     * 完成任务
     *
     * @param model 任务完成模型
     * @see TaskCompleteModel
     */
    @RequestMapping(value = "complete",method = RequestMethod.POST)
    public RestResult<?> complete( @RequestBody TaskCompleteModelImpl model);

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    @RequestMapping(value = "claim",method = RequestMethod.GET)
    public RestResult<?> claim(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId);

    /**
     * 指定用户签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    @RequestMapping(value = "appoint_claim",method = RequestMethod.GET)
    public RestResult<?> appointClaim(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId);

    /**
     * 任务跳转
     *
     * @param taskId        当前的任务
     * @param targetTaskKey 目前任务的定义key
     */
    @RequestMapping(value = "jump",method = RequestMethod.GET)
    public RestResult<?> jump(@RequestParam("taskId") String taskId, @RequestParam("targetTaskKey")String targetTaskKey);

    /**
     * 挂起 - 激活 流程实例流程
     *
     * @param instanceId 流程实例ID
     */
    @RequestMapping(value = "suspended_instance",method = RequestMethod.GET)
    public RestResult<?> suspendedInstance(@RequestParam("instanceId") String instanceId);

    /**
     * 挂起 - 激活 流程定义
     *
     * @param processDefinitionId 流程定义ID
     */
    @RequestMapping(value = "suspended_process_definition",method = RequestMethod.GET)
    public RestResult<?> suspendedProcessDefinition(@RequestParam("processDefinitionId")String processDefinitionId);

    /**
     * 驳回上一流程
     * @param taskId 任务节点id
     */
    @RequestMapping(value = "back_process",method = RequestMethod.GET)
    public RestResult<?> backProcess(@RequestParam("taskId") String taskId);

    /**
     * 根据节点编号获取节点信息及实例信息
     * @param instanceId 流程实例ID
     */
    @RequestMapping(value = "find_instance_id_detail",method = RequestMethod.GET)
    public RestResult<?> findInstanceIdDetail(@RequestParam("instanceId") String instanceId);

    /**
     * 根据流程实例Id,获取实时流程图片
     * @param instanceId 任务模型
     * @param useCustomColor 是否自定义颜色
     */
    @RequestMapping(value = "get_flow_img_by_instance_id",method = RequestMethod.GET)
    public RestResult<?> getFlowImgByInstanceId(@RequestParam("instanceId") String instanceId,@RequestParam("useCustomColor")Boolean useCustomColor);

}
