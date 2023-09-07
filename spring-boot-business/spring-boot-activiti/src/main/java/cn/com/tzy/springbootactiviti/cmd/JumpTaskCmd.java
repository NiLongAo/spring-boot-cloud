package cn.com.tzy.springbootactiviti.cmd;

import cn.com.tzy.springbootactiviti.exception.WorkflowException;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.Objects;

/**
 * 任务跳转命令
 *
 * @author yiuman
 * @date 2021/3/30
 */
@SuperBuilder(toBuilder = true)
public class JumpTaskCmd implements Command<Void> {

    /**
     * 当前的执行ID
     */
    private final String executionId;

    /**
     * 目标任务定义的key
     */
    private final String targetTaskKey;

    public JumpTaskCmd(String executionId, String targetTaskKey) {
        this.executionId = executionId;
        this.targetTaskKey = targetTaskKey;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        //1.找到当前执行实例
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        //2.找到当前的流程
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        FlowElement targetFlowElement = process.getFlowElement(targetTaskKey);
        if (Objects.isNull(targetFlowElement)) {
            throw new WorkflowException(String.format("can not found FlowElement with key `%s`", targetTaskKey));
        }

        //3.将当前的执行实例活动删除，删除原因为jump
        commandContext.getHistoryManager().recordActivityEnd(executionEntity, "jump");
        //4.将目标节点设置到当前的执行实例中
        executionEntity.setCurrentFlowElement(targetFlowElement);

        //5.删除当前执行中的任务
        commandContext.getTaskEntityManager().findTasksByExecutionId(executionId)
                .forEach(task -> commandContext.getTaskEntityManager().deleteTask(task, "jump", false, true));

        //6.触发实例流转
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessInCompensation(executionEntity);

        return null;
    }
}
