package cn.com.tzy.springbootactiviti.config.activiti.resolver;


import cn.com.tzy.springbootactiviti.exception.WorkflowException;
import cn.com.tzy.springbootactiviti.model.WorkflowContext;

import java.util.List;

/**
 * 任务的候选人解析器<br/>
 * 根据任务定义的候选人的字符串解析成相关候选人用户ID
 *
 * @author yiuman
 * @date 2020/12/18
 */
@FunctionalInterface
public interface TaskCandidateResolver {

    /**
     * 根据流程上下文与候选人集合解析真正的候选人信息
     *
     * @param workflowContext     流程上下文
     * @param taskCandidateDefine 候选人定义
     * @return 真正的候选人信息
     * @throws WorkflowException 工作流程运行时异常
     */
    List<String> resolve(WorkflowContext workflowContext, List<Object> taskCandidateDefine) throws WorkflowException;
}