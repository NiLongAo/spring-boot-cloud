package cn.com.tzy.springbootactiviti.model.impl;

import cn.com.tzy.springbootactiviti.model.WorkflowContext;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * 流程上下文实现
 *
 * @author yiuman
 * @date 2020/12/29
 */
@SuperBuilder(toBuilder = true)
@Data
public class WorkflowContextImpl implements WorkflowContext {

    private ProcessEngine processEngine;

    private ProcessInstance processInstance;

    private String executionId;

    private Task task;

    private String currentUserId;

    private FlowElement flowElement;


}
