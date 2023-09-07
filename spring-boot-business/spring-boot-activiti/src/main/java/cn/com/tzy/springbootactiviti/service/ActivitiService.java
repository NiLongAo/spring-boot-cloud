package cn.com.tzy.springbootactiviti.service;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;

import java.util.Map;

public interface ActivitiService extends WorkflowService{



    RestResult<?> statsUserOa();

    /**
     * 获取当前用户待办事项
     * @return
     */
    PageResult findUserNeedList(PageModel pageModel);

    /**
     * 获取当前用户发起事项
     * @return
     */
    PageResult findUserLaunchList(PageModel pageModel);

    /**
     * 获取当前用户参与事项
     * @return
     */
    PageResult findUserAlreadyList(PageModel pageModel);

    /**
     * 获取流程部署实例
     * @return
     */
    PageResult findRepositoryList(PageModel pageModel);

    /**
     * 待办事项
     */
    PageResult findNeedList(PageModel pageModel);

    /**
     * 历史记录
     */
    PageResult findAlreadyList(PageModel pageModel);


    /**
     * 根据流程编号获取审批历史记录
     */
    RestResult<?> findHistoricalInstanceIdList(String instanceId);


    /**
     * 根据节点编号获取节点详情及实例详情
     */
    RestResult<?> findInstanceIdDetail(String instanceId);

    /**
     * 获取流程定义 xml
     * @return
     */
    RestResult<?> findRepositoryXml(String processDefinitionId);

    /**
     * 删除流程实例
     * @param processInstanceId
     * @param memo 1.如果启动流程实例中有正在进行中的则报错提示 2.有进行中的也删除
     * @see StartProcessModel
     */
    RestResult<?> deleteProcessInstance(String processInstanceId,Boolean isUser,String memo);


    /**
     * 获取当前任务节点的下一节点审批人
     * @param taskId 任务点编号
     * @see StartProcessModel
     */
    RestResult<?> findNextTaskUser(String taskId, Map<String, Object> vars);

}
