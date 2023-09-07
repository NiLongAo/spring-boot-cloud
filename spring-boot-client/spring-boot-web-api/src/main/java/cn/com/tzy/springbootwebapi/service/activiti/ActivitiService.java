package cn.com.tzy.springbootwebapi.service.activiti;

import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.activiti.DeployXml;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import cn.com.tzy.springbootentity.param.activiti.TaskCompleteModel;
import cn.com.tzy.springbootentity.param.activiti.impl.TaskCompleteModelImpl;
import cn.com.tzy.springbootfeignacitiviti.api.activiti.ActivitiServiceFeign;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工作流通用接口
 */
@Service
public class ActivitiService {

    @Autowired
    private ActivitiServiceFeign activitiServiceFeign;
    @Autowired
    private UserServiceFeign userServiceFeign;

    /**
     * 获取个人工作流统计
     * @return
     */
    public RestResult<?> statsUserOa(){
        return activitiServiceFeign.statsUserOa() ;
    }

    /**
     * 获取当前用户待办事项
     * @return
     */
    public PageResult findUserNeedList(PageModel pageModel){
        return activitiServiceFeign.findUserNeedList(pageModel) ;
    }

    /**
     * 获取当前用户发起事项
     * @return
     */
    public PageResult findUserLaunchList(PageModel pageModel){
        return activitiServiceFeign.findUserLaunchList(pageModel) ;
    }

    /**
     * 获取当前用户已办事项
     * @return
     */
    public PageResult findUserAlreadyList(PageModel pageModel){
        return activitiServiceFeign.findUserAlreadyList(pageModel) ;
    }

    /**
     * 获取流程部署实例
     */
    public PageResult findRepositoryList(PageModel pageModel){
        return activitiServiceFeign.findRepositoryList(pageModel) ;
    }

    /**
     * 待办事项
     */
    public PageResult findNeedList(PageModel pageModel){
        return activitiServiceFeign.findNeedList(pageModel) ;
    }
    /**
     * 历史记录
     */
    public PageResult findAlreadyList(PageModel pageModel){
        return activitiServiceFeign.findAlreadyList(pageModel) ;
    }


    /**
     * 根据流程编号获取审批历史记录
     */
    public RestResult<?> findHistoricalInstanceIdList(String instanceId){
        return activitiServiceFeign.findHistoricalInstanceIdList(instanceId) ;
    }

    /**
     * 获取流程定义 xml
     */
    public RestResult<?> findRepositoryXml(String processDefinitionId){
        return activitiServiceFeign.findRepositoryXml(processDefinitionId) ;
    }

    /**
     * 部署流程引擎实例(以参数传输方式)
     * @param xml 流程xml
     * @return 流程实例
     * @see StartProcessModel
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> deployProcessParameter(DeployXml xml){
        return activitiServiceFeign.deployProcessParameter(xml) ;
    }


    /**
     * 部署流程引擎实例(以文本传输方式)
     * @param name 流程名称
     * @return 流程实例
     * @see StartProcessModel
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> deployProcess(String name, MultipartFile multipartFile){
        return activitiServiceFeign.deployProcess(name,multipartFile) ;
    }

    /**
     * 删除流程引擎实例
     * @param deploymentId
     * @param stats 1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除
     * @see StartProcessModel
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> deleteProcess(String deploymentId,Integer stats){
        return activitiServiceFeign.deleteProcess(deploymentId,stats) ;
    }

    /**
     * 删除流程实例-只能删除当前用户的实例
     * @param processInstanceId 流程实例编号
     * @param memo 删除备注
     * @see StartProcessModel
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> deleteProcessInstance(String processInstanceId,String memo){
        return activitiServiceFeign.deleteProcessInstance(processInstanceId,true,memo) ;
    }

    /**
     * 完成任务
     *
     * @param model 任务完成模型
     * @see TaskCompleteModel
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> complete(TaskCompleteModelImpl model){
        return activitiServiceFeign.complete(model);
    }

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> claim(String taskId){
        Long userId = JwtUtils.getUserId();
        return activitiServiceFeign.claim(taskId,String.valueOf(userId));
    }

    /**
     * 指定用户签收任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> appointClaim(String taskId, String userId){
        return activitiServiceFeign.appointClaim(taskId,userId);
    }

    /**
     * 任务跳转
     *
     * @param taskId        当前的任务
     * @param targetTaskKey 目前任务的定义key
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> jump(String taskId,String targetTaskKey){
        return activitiServiceFeign.jump(taskId,targetTaskKey);
    }

    /**
     * 挂起 - 激活 流程实例流程
     *
     * @param instanceId 流程实例ID
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> suspendedInstance( String instanceId){
        return activitiServiceFeign.suspendedInstance(instanceId);
    }

    /**
     * 挂起 - 激活 流程定义
     *
     * @param processDefinitionId 流程定义ID
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> suspendedProcessDefinition(String processDefinitionId){
        return activitiServiceFeign.suspendedProcessDefinition(processDefinitionId);
    }

    /**
     * 驳回上一流程
     * @param taskId 任务节点id
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> backProcess(String taskId){
        return activitiServiceFeign.backProcess(taskId);
    }


    /**
     * 根据节点编号获取节点信息及实例信息
     * @param instanceId 流程实例ID
     */
    public RestResult<?> findInstanceIdDetail(String instanceId){
        return activitiServiceFeign.findInstanceIdDetail(instanceId);
    }


    /**
     * 根据流程实例Id,获取实时流程图片
     * @param instanceId 任务模型
     * @param useCustomColor 是否自定义颜色
     */
    public RestResult<?> getFlowImgByInstanceId( String instanceId, Boolean useCustomColor){
        return activitiServiceFeign.getFlowImgByInstanceId(instanceId,useCustomColor);
    }

}
