package cn.com.tzy.springbootactiviti.service;

import cn.com.tzy.springbootactiviti.config.activiti.engine.WorkflowEngineGetter;
import cn.com.tzy.springbootentity.param.activiti.DeployXml;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import cn.com.tzy.springbootentity.param.activiti.TaskCompleteModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程服务类
 *
 * @author yiuman
 * @date 2020/12/11
 */
public interface WorkflowService extends WorkflowEngineGetter {

    /**
     * 部署流程引擎实例(以参数传输方式)
     * @param xml 流程xml
     * @return 流程实例
     * @see StartProcessModel
     */
    RestResult<?> deployProcessParameter(DeployXml xml);


    /**
     * 部署流程引擎实例(以文本传输方式)
     * @param name 流程名称
     * @return 流程实例
     * @see StartProcessModel
     */
    RestResult<?> deployProcess(String name, MultipartFile multipartFile);

    /**
     * 删除流程部署实例
     * @param deploymentId
     * @param stats 1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除
     * @see StartProcessModel
     */
    RestResult<?> deleteProcess(String deploymentId,Integer stats);

    /**
     * 删除流程实例
     * @param processInstanceId
     * @param memo 1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除
     * @see StartProcessModel
     */
    RestResult<?> deleteProcessInstance(String processInstanceId,String memo);


    /**
     * 开始一个流程
     *
     * @param model 开始流程模型
     * @return 流程实例
     * @see StartProcessModel
     */
    RestResult<?> starProcess(StartProcessModel model);

    /**
     * 完成任务
     *
     * @param model 任务完成模型
     * @see TaskCompleteModel
     */
    RestResult<?> complete(TaskCompleteModel model);

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    RestResult<?> claim(String taskId, String userId);

    /**
     * 指定签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    RestResult<?> appointClaim(String taskId, String userId);

    /**
     * 任务跳转
     *
     * @param taskId        当前的任务
     * @param targetTaskKey 目前任务的定义key
     */
    RestResult<?> jump(String taskId, String targetTaskKey);

    /**
     * 挂起 - 激活 流程实例流程
     *
     * @param instanceId 流程实例ID
     */
    RestResult<?> suspendedInstance(String instanceId);

    /**
     * 挂起 - 激活 流程定义
     *
     * @param processDefinitionId 流程实例ID
     */
    RestResult<?> suspendedProcessDefinition(String processDefinitionId);

    /**
     * 驳回上一流程
     * @param taskId 审核节点id
     */
    RestResult<?> backProcess(String taskId);

    /**
     * 根据流程实例Id,获取实时流程图片
     * @param instanceId 流程实例编号
     * @param useCustomColor 流程实例编号
     */
    RestResult<?> getFlowImgByInstanceId(String instanceId,boolean useCustomColor);



}