package cn.com.tzy.springbootactiviti.service.impl;

import cn.com.tzy.spingbootstartermybatis.core.tenant.context.TenantContextHolder;
import cn.com.tzy.springbootactiviti.cmd.JumpTaskCmd;
import cn.com.tzy.springbootactiviti.config.activiti.engine.WorkflowEngineGetter;
import cn.com.tzy.springbootactiviti.config.activiti.engine.WorkflowEngineGetterImpl;
import cn.com.tzy.springbootactiviti.config.activiti.resolver.TaskCandidateResolver;
import cn.com.tzy.springbootactiviti.config.custom.DefaultProcessDiagramGenerator;
import cn.com.tzy.springbootactiviti.config.init.SpringContextConfig;
import cn.com.tzy.springbootactiviti.exception.WorkflowException;
import cn.com.tzy.springbootactiviti.model.impl.WorkflowContextImpl;
import cn.com.tzy.springbootactiviti.service.WorkflowService;
import cn.com.tzy.springbootactiviti.utils.LambdaUtils;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.ImgConstant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.activiti.DeployXml;
import cn.com.tzy.springbootentity.param.activiti.ProcessPersonalModel;
import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import cn.com.tzy.springbootentity.param.activiti.TaskCompleteModel;
import cn.com.tzy.springbootentity.param.activiti.impl.CommentEntity;
import cn.com.tzy.springbootentity.param.activiti.impl.TaskCompleteModelImpl;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * 流程处理抽象类<br/>
 * 开启流程、完成任务、签收任务、挂起、激活
 *
 * @author yiuman
 * @date 2020/12/11
 */
@Log4j2
public abstract class BaseWorkflowService implements WorkflowService {

    private WorkflowEngineGetter workflowEngineGetter;

    private TaskCandidateResolver taskCandidateResolver;


    @Override
    public ProcessEngine getProcessEngine() {
        workflowEngineGetter = Optional.ofNullable(workflowEngineGetter)
                .orElse(SpringContextConfig.getBean(WorkflowEngineGetterImpl.class));
        return workflowEngineGetter.getProcessEngine();
    }

    public TaskCandidateResolver getTaskCandidateResolver() {
        return taskCandidateResolver = Optional.ofNullable(taskCandidateResolver)
                .orElse(SpringContextConfig.getBean(TaskCandidateResolver.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> deployProcessParameter(DeployXml deployXml) {
        if(ObjectUtils.isEmpty(deployXml)){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到要部署内容");
        }
        RestResult<?> result =null;
        try {
            Deployment deployment = getProcessEngine().getRepositoryService().createDeployment()//初始化流程
                    .tenantId(TenantContextHolder.getTenantId().toString())
                    .addString(deployXml.getId(), deployXml.getXml())
                    .name(deployXml.getName())
                    .deploy();
            result = RestResult.result(RespCode.CODE_0.getValue(),null,deployment.getId());
        } catch (Exception e) {
            log.error("部署流程失败:",e);
            throw new WorkflowException(e.getMessage());
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> deployProcess(String name, MultipartFile multipartFile) {
        // 获取上传的文件名
        String fileName = multipartFile.getOriginalFilename();
        RestResult<?> result =null;
        try {
            // 得到输入流（字节流）对象
            InputStream fileInputStream = multipartFile.getInputStream();

            // 文件的扩展名
            String extension = FilenameUtils.getExtension(fileName);
            Deployment deployment = null;
            if (extension.equals("zip")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = getProcessEngine().getRepositoryService().createDeployment()//初始化流程
                        .addZipInputStream(zip)
                        .tenantId(TenantContextHolder.getTenantId().toString())
                        .name(name)
                        .deploy();
            } else {
                deployment = getProcessEngine().getRepositoryService().createDeployment()//初始化流程
                        .addInputStream(fileName, fileInputStream)
                        .tenantId(TenantContextHolder.getTenantId().toString())
                        .name(name)
                        .deploy();
            }
            result = RestResult.result(RespCode.CODE_0.getValue(),null,deployment.getId());


        } catch (Exception e) {
            log.error("部署流程失败:",e);
            throw new WorkflowException(e.getMessage());
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> deleteProcess(String deploymentId,Integer stats) {
        if(StringUtils.isEmpty(deploymentId)){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到要流程部署实例编号");
        }
        RestResult<?> result =null;
        try {
            if(stats != null && stats == 2){
                getProcessEngine().getRepositoryService().deleteDeployment(deploymentId,true);
            }else {
                getProcessEngine().getRepositoryService().deleteDeployment(deploymentId,false);
            }
            result = RestResult.result(RespCode.CODE_0.getValue(),"流程部署实例删除成功");
        } catch (Exception e) {
            log.error("部署实例删除失败:",e);
            throw new WorkflowException(e.getMessage());
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> deleteProcessInstance(String processInstanceId,String memo) {
        if(StringUtils.isEmpty(processInstanceId)){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到要流程实例编号");
        }
        RestResult<?> result =null;
        try {
            getProcessEngine().getRuntimeService().deleteProcessInstance(processInstanceId,memo);
            getProcessEngine().getHistoryService().deleteHistoricProcessInstance(processInstanceId);
            result = RestResult.result(RespCode.CODE_0.getValue(),"流程实例删除成功");
        } catch (Exception e) {
            log.error("部署实例删除失败:",e);
            throw new WorkflowException(e.getMessage());
        }
        return result;
    }

    @SneakyThrows
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> starProcess(StartProcessModel model) {
        String processDefineId = model.getProcessDefineKey();
        //找到流程定义
        ProcessDefinition definition = Optional.ofNullable(getProcessEngine().getRepositoryService()
                        .createProcessDefinitionQuery()
                        .processDefinitionTenantId(TenantContextHolder.getTenantId().toString())
                        .processDefinitionKey(processDefineId)
                        .latestVersion()
                        .singleResult()
        ).orElseThrow(() -> new IllegalArgumentException(String.format("can not find ProcessDefinition for key:[%s]", processDefineId)));
        //开起流程
        Map<String, Object> processInstanceVars = model.getVariables();
        if(processInstanceVars == null){
            processInstanceVars = new HashMap<>();
        }
        processInstanceVars.put("status", ConstEnum.ReviewStateEnum.IS_REVIEW.getValue());
        processInstanceVars.put("statusName", ConstEnum.ReviewStateEnum.IS_REVIEW.getName());
        ProcessInstance processInstance = getProcessEngine().getRuntimeService().startProcessInstanceById(
                definition.getId(),
                model.getBusinessKey(),
                processInstanceVars
        );
        //1.找到当前流程的任务节点。
        getProcessEngine().getRuntimeService().setProcessInstanceName(processInstance.getProcessInstanceId(),model.getName());
        //2.若任务处理人与申请人一致，则自动完成任务，直接进入下一步
        //3.如请假申请为流程的第一步，则此任务自动完成
        if (StringUtils.isNotBlank(model.getUserId())) {
            addCommand(processInstance.getId(),null,model.getUserId(),model.getComment());
            List<Task> list = getProcessEngine().getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).active().list();
            for (Task  task: list) {
                if(StringUtils.isNotEmpty(task.getAssignee())){
                    addCommand(null,task.getId(),task.getAssignee(),null);
                }
            }
            Task applyUserTask = getProcessEngine().getTaskService().createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .taskCandidateOrAssigned(model.getUserId())
                    .active()
                    .singleResult();
            if (Objects.nonNull(applyUserTask)) {
                processInstanceVars.put("examineStatus", ConstEnum.ReviewStateEnum.REVIEW_ADOPT.getValue());
                processInstanceVars.put("examineMemo", ConstEnum.ReviewStateEnum.REVIEW_ADOPT.getName());
                complete(TaskCompleteModelImpl.builder()
                        .taskId(applyUserTask.getId())
                        .taskVariables(processInstanceVars)
                        .comment(CommentEntity.builder().status(ConstEnum.ReviewStateEnum.REVIEW_ADOPT.getValue()).statusName(ConstEnum.ReviewStateEnum.REVIEW_ADOPT.getName()).build())
                        .userId(model.getUserId())
                        .candidateOrAssigned(model.getCandidateOrAssigned())
                        .build());

            }
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"部署成功",processInstance.getId());
    }

    @SneakyThrows
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> complete(TaskCompleteModel model) {
        Assert.notNull(model.getTaskId(), "The taskId of the process can not be empty!");
        //扎到当前用户与任务编号匹配的任务
        TaskService taskService = getProcessEngine().getTaskService();
        Task task = Optional.ofNullable(taskService.createTaskQuery()
                .taskId(model.getTaskId())
                .taskCandidateOrAssigned(model.getUserId())
                .active()
                .singleResult())
                .orElseThrow(() -> new WorkflowException(String.format("cannot find Task for userId:[%s] taskId:[%s]", model.getUserId(), model.getTaskId())));
        //2.任务模型中用户不存在那则当前人接受当前任务
        String assignee = task.getAssignee();
        Assert.notNull(assignee, String.format("Task for taskId:[%s] did not claimed", task.getId()));
        if (StringUtils.isEmpty(assignee)) {
            claim(task.getId(),model.getUserId());
        }else if(!assignee.equals(model.getUserId())){
            return RestResult.result(RespCode.CODE_2.getValue(),"任务指派人与当前人不符，请重新筛选,或重新指派当前任务执行人");
        }

        taskService.setVariables(task.getId(), model.getVariables());
        taskService.setVariablesLocal(task.getId(), model.getTaskVariables());
        //更新操作
        addCommand(null,task.getId(),model.getUserId(),model.getComment());
        taskService.complete(task.getId());
        //如果有设置目标任务关键字则进行任务跳转
        if (StringUtils.isNotBlank(model.getTargetTaskKey())) {
            jump(task.getId(), model.getTargetTaskKey());
        }
        //完成此环节后，检查有没下个环节，有的话且是未设置办理人或候选人的情况下，使用模型进行设置
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId(task.getProcessInstanceId())
                .active()
                .list();

        if (!taskList.isEmpty()) {
            //设置任务的候选人
            taskList.forEach(LambdaUtils.consumerWrapper(nextTask -> setCandidateOrAssigned(nextTask, model)));
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"任务完成成功");
    }

    /**
     * 设置候选人或处理人
     *
     * @param task  当前的任务
     * @param model 流程人员模型
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    protected void setCandidateOrAssigned(Task task, ProcessPersonalModel model) throws Exception {
        //查询当前任务是否已经有候选人或办理人
        RepositoryService repositoryService = getProcessEngine().getRepositoryService();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            List<String> taskCandidateUsersDefine = userTask.getCandidateUsers();
            //没有负责人，则用解析器解析流程任务定义的候选人或参数传入来的候选人
            if (StringUtils.isBlank(task.getAssignee())) {
                List<Object> allCandidateOrAssigned = new ArrayList<>();
                List<String> modelCandidateOrAssigned = model.getCandidateOrAssigned();
                if (!CollectionUtils.isEmpty(modelCandidateOrAssigned)) {
                    allCandidateOrAssigned.addAll(modelCandidateOrAssigned);
                }

                allCandidateOrAssigned.addAll(taskCandidateUsersDefine);

                //删除任务候选人
                allCandidateOrAssigned.forEach(candidateDefine -> getProcessEngine().getTaskService().deleteCandidateUser(task.getId(), String.valueOf(candidateDefine)));

                RuntimeService runtimeService = getProcessEngine().getRuntimeService();

                WorkflowContextImpl workflowContext = WorkflowContextImpl.builder()
                        .processEngine(getProcessEngine())
                        .processInstance(
                                runtimeService
                                        .createProcessInstanceQuery()
                                        .processInstanceId(task.getProcessInstanceId())
                                        .singleResult()
                        )
                        .task(task)
                        .flowElement(flowElement)
                        .currentUserId(model.getUserId())
                        .build();
                //解析器解析完成后，把真正的候选人添加到任务中去
                Optional.ofNullable(getTaskCandidateResolver().resolve(workflowContext, allCandidateOrAssigned))
                        .ifPresent(resolvedCandidates -> {
                            if (resolvedCandidates.size() == 1) {
                                addCommand(null,task.getId(),model.getUserId(),null);
                                getProcessEngine().getTaskService().setAssignee(task.getId(), resolvedCandidates.get(1));
                            } else {
                                resolvedCandidates.stream().filter(Objects::nonNull).forEach(realUserId -> {
                                    getProcessEngine().getTaskService().addCandidateUser(task.getId(), realUserId);
                                    addCommand(null,task.getId(),realUserId,null);
                                });
                            }
                        });
            }else {
                addCommand(null,task.getId(),task.getAssignee(),null);
            }
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> claim(String taskId, String userId) {
        TaskService taskService = getProcessEngine().getTaskService();
        Task task = Optional.ofNullable(taskService.createTaskQuery()
                .taskId(taskId)
                .taskTenantId(TenantContextHolder.getTenantId().toString())
                .taskCandidateOrAssigned(userId)
                .singleResult())
                .orElseThrow(() -> new WorkflowException(String.format("can not claim Task for taskId:[%s]", taskId)));

        String assignee = task.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            throw new WorkflowException(String.format("Task for taskId:[%s] has been claimed", taskId));
        }
        taskService.claim(taskId, userId);
        addCommand(null,task.getId(),userId,null);
        return RestResult.result(RespCode.CODE_0.getValue(),"任务签收成功");
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> appointClaim(String taskId, String userId) {
        TaskService taskService = getProcessEngine().getTaskService();
        Task task = Optional.ofNullable(taskService.createTaskQuery()
                .taskId(taskId)
                .taskTenantId(TenantContextHolder.getTenantId().toString())
                .singleResult())
                .orElseThrow(() -> new WorkflowException(String.format("can not claim Task for taskId:[%s]", taskId)));

        taskService.setAssignee(taskId, userId);
        addCommand(null,task.getId(),userId,null);
        return RestResult.result(RespCode.CODE_0.getValue(),"任务签收成功");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> jump(String taskId, String targetTaskKey) {
        TaskService taskService = getProcessEngine().getTaskService();

        Task task = Optional.ofNullable(taskService.createTaskQuery().taskId(taskId).taskTenantId(TenantContextHolder.getTenantId().toString()).singleResult())
                .orElseThrow(() -> new WorkflowException(String.format("cannot find Task for taskId:[%s]", taskId)));

        Optional.ofNullable(getProcessEngine().getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceTenantId(TenantContextHolder.getTenantId().toString())
                .processInstanceId(task.getProcessInstanceId()).
                        active().singleResult()).orElseThrow(() -> new WorkflowException("This ProcessInstance is not active,cannot do jump"));

        //构建跳转命令并执行
        getProcessEngine().getManagementService().executeCommand(JumpTaskCmd.builder()
                .executionId(task.getExecutionId())
                .targetTaskKey(targetTaskKey)
                .build());
        if(StringUtils.isNotEmpty(task.getAssignee())){
            addCommand(null,task.getId(),task.getAssignee(),null);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"任务跳转成功");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> suspendedInstance(String instanceId) {
        ProcessInstance processInstance = getProcessEngine().getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId).processInstanceTenantId(TenantContextHolder.getTenantId().toString()).singleResult();
        if(processInstance.isSuspended()){
            getProcessEngine().getRuntimeService().activateProcessInstanceById(instanceId);
            return RestResult.result(RespCode.CODE_0.getValue(),"任务激活成功");
        }else {
            getProcessEngine().getRuntimeService().suspendProcessInstanceById(instanceId);
            return RestResult.result(RespCode.CODE_0.getValue(),"任务挂起成功");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> suspendedProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = getProcessEngine().getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).processDefinitionTenantId(TenantContextHolder.getTenantId().toString()).singleResult();
        if(processDefinition.isSuspended()){
            getProcessEngine().getRepositoryService().activateProcessDefinitionById(processDefinition.getId(),true,null);
            return RestResult.result(RespCode.CODE_0.getValue(),"流程定义激活成功");
        }else {
            getProcessEngine().getRepositoryService().suspendProcessDefinitionById(processDefinition.getId(),true,null);
            return RestResult.result(RespCode.CODE_0.getValue(),"流程定义挂起成功");
        }

    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RestResult<?> backProcess(String taskId){
        Long userId = JwtUtils.getUserId();
        TaskService taskService = getProcessEngine().getTaskService();

        //扎到相关任务
        Task task = Optional.ofNullable(taskService.createTaskQuery()
                .taskId(taskId)
                .taskTenantId(TenantContextHolder.getTenantId().toString())
                .active()
                .singleResult())
                .orElseThrow(() -> new WorkflowException(String.format("cannot find Task for taskId:[%s]", taskId)));

        String processInstanceId = task.getProcessInstanceId();
        // 取得所有历史任务按时间降序排序
        List<HistoricTaskInstance> htiList = getProcessEngine().getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskTenantId(TenantContextHolder.getTenantId().toString())
                .orderByTaskCreateTime()
                .desc()
                .list();

        Integer size = 2;

        if (ObjectUtils.isEmpty(htiList) || htiList.size() < size) {
            return RestResult.result(RespCode.CODE_2.getValue(),"驳回条件不符");
        }

        // list里的第二条代表上一个任务
        HistoricTaskInstance lastTask = htiList.get(1);
        // list里第一条代表当前任务
        HistoricTaskInstance curTask = htiList.get(0);
        // 当前节点的executionId
        String curExecutionId = curTask.getExecutionId();
        // 上个节点的taskId
        String lastTaskId = lastTask.getId();
        // 上个节点的executionId
        String lastExecutionId = lastTask.getExecutionId();
        if (null == lastTaskId) {
            throw new WorkflowException("LAST TASK IS NULL");
        }
        String processDefinitionId = lastTask.getProcessDefinitionId();
        BpmnModel bpmnModel = getProcessEngine().getRepositoryService().getBpmnModel(processDefinitionId);

        String lastActivityId = null;
        List<HistoricActivityInstance> haiFinishedList = getProcessEngine().getHistoryService().createHistoricActivityInstanceQuery()
                .executionId(lastExecutionId).finished().list();

        for (HistoricActivityInstance hai : haiFinishedList) {
            if (lastTaskId.equals(hai.getTaskId())) {
                // 得到ActivityId，只有HistoricActivityInstance对象里才有此方法
                lastActivityId = hai.getActivityId();
                break;
            }
        }

        // 得到上个节点的信息
        FlowNode lastFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(lastActivityId);

        // 取得当前节点的信息
        Execution execution = getProcessEngine().getRuntimeService().createExecutionQuery().executionId(curExecutionId).singleResult();
        String curActivityId = execution.getActivityId();
        FlowNode curFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(curActivityId);

        //记录当前节点的原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
        oriSequenceFlows.addAll(curFlowNode.getOutgoingFlows());

        //清理活动方向
        curFlowNode.getOutgoingFlows().clear();

        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(curFlowNode);
        newSequenceFlow.setTargetFlowElement(lastFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        curFlowNode.setOutgoingFlows(newSequenceFlowList);

        //更新操作
        addCommand(null,task.getId(),String.valueOf(userId),CommentEntity.builder().status(ConstEnum.ReviewStateEnum.REVIEW_RETURN.getValue()).statusName(ConstEnum.ReviewStateEnum.REVIEW_RETURN.getName()).build());
        // 完成任务
        getProcessEngine().getTaskService().complete(task.getId());

        //恢复原方向
        curFlowNode.setOutgoingFlows(oriSequenceFlows);

        org.activiti.engine.task.Task nextTask = taskService
                .createTaskQuery().processInstanceId(processInstanceId).taskTenantId(TenantContextHolder.getTenantId().toString()).singleResult();
        // 设置执行人
        if (nextTask != null) {
            getProcessEngine().getTaskService().setAssignee(nextTask.getId(), lastTask.getAssignee());
            addCommand(null,nextTask.getId(),lastTask.getAssignee(),null);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"任务驳回成功");
    }

    /**
     * 根据流程实例Id,获取实时流程图片
     *
     * @param processInstanceId
     * @param useCustomColor    true:用自定义的颜色（完成节点绿色，当前节点红色），default:用默认的颜色（红色）
     * @return
     */
    @Override
    public RestResult<?> getFlowImgByInstanceId(String processInstanceId,  boolean useCustomColor) {
        HistoryService historyService = getProcessEngine().getHistoryService();
        RepositoryService repositoryService = getProcessEngine().getRepositoryService();

        try {
            if (StringUtils.isEmpty(processInstanceId)) {
                return null;
            }
            // 获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .processInstanceTenantId(TenantContextHolder.getTenantId().toString())
                    .singleResult();

            // 获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstances = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .activityTenantId(TenantContextHolder.getTenantId().toString())
                    .orderByHistoricActivityInstanceId()
                    .asc().list();
            // 高亮已经执行流程节点ID集合
            List<String> highLightedActivitiIds = new ArrayList<>();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                // 用默认颜色
                highLightedActivitiIds.add(historicActivityInstance.getActivityId());
            }

            List<String> currIds = historicActivityInstances.stream()
                    .filter(item -> org.springframework.util.StringUtils.isEmpty(item.getEndTime()))
                    .map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());

            // 获得流程引擎配置
            ProcessEngineConfiguration processEngineConfiguration = getProcessEngine().getProcessEngineConfiguration();

            BpmnModel bpmnModel = repositoryService
                    .getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 高亮流程已发生流转的线id集合
            List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstances);

            InputStream imageStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel,
                    "png",
                    highLightedActivitiIds,
                    currIds,
                    highLightedFlowIds,
                    "宋体", "宋体", "宋体",
                    processEngineConfiguration.getClassLoader(),
                    1.0);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
            IOUtils.copy(imageStream, baos);
            byte[] bytes = baos.toByteArray();//转换成字节
            Base64.Encoder encoder = Base64.getMimeEncoder();
            String png_base64 = encoder.encodeToString(bytes).trim();//转换成base64串
            return RestResult.result(RespCode.CODE_0.getValue(),null, ImgConstant.IMAGE_SVG_XML +png_base64);
        } catch (Exception e) {
            log.error("processInstanceId" + processInstanceId + "生成流程图失败，原因：" + e.getMessage(), e);
            throw new WorkflowException(e.getMessage());
        }

    }


    /**
     *  获取已经流转的线
     *  @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转：
             * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType())
                    || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(sequenceFlow.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp == highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }

                    highLightedFlowIds.add(highLightedFlowId);
                }

            }

        }
        return highLightedFlowIds;
    }

    abstract void addCommand(String instances, String task, String userId, CommentEntity build);


}