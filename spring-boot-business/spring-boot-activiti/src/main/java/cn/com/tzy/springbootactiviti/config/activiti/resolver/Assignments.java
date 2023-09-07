package cn.com.tzy.springbootactiviti.config.activiti.resolver;

import cn.com.tzy.springbootactiviti.config.init.SpringContextConfig;
import cn.com.tzy.springbootactiviti.exception.WorkflowException;
import cn.com.tzy.springbootactiviti.model.impl.WorkflowContextImpl;
import cn.com.tzy.springbootactiviti.service.impl.ActivitiServcieImpl;
import cn.com.tzy.springbootactiviti.utils.LambdaUtils;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理人表达式解析器
 *
 * @author yiuman
 * @date 2021/4/8
 */
@Component("assignments")
@Log4j2
public class Assignments implements ExpressionResolver<List<String>> {

    private final TaskCandidateResolver taskCandidateResolver;

    private final ObjectMapper objectMapper;

    public Assignments(TaskCandidateResolver taskCandidateResolver, ObjectMapper objectMapper) {
        this.taskCandidateResolver = taskCandidateResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> resolve(DelegateExecution execution, String expressionStr) throws WorkflowException {
        ActivitiServcieImpl workflowService = SpringContextConfig.getBean(ActivitiServcieImpl.class);
        ProcessEngine processEngine = workflowService.getProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //找到当前用于的ID
        String currentUserId = String.valueOf(JwtUtils.getUserId());
        //构建流程上下文
        WorkflowContextImpl workflowContext = WorkflowContextImpl.builder()
                .processEngine(workflowService.getProcessEngine())
                .processInstance(
                        runtimeService
                                .createProcessInstanceQuery()
                                .processInstanceId(execution.getProcessInstanceId())
                                .singleResult()
                )
                .executionId(execution.getId())
                .flowElement(execution.getCurrentFlowElement())
                .currentUserId(currentUserId)
                .build();
        final List<Object> stringArrayList = new ArrayList<>();
        try {
            List<?> list = objectMapper.readValue(expressionStr, List.class);
            list.forEach(LambdaUtils.consumerWrapper(item -> stringArrayList.add(objectMapper.writeValueAsString(item))));
        } catch (Exception ex) {
            stringArrayList.add(expressionStr);
            log.info("Assignments resolver exception: ", ex);
        }

        return taskCandidateResolver.resolve(workflowContext, stringArrayList);

    }
}
