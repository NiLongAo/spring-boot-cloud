package cn.com.tzy.springbootentity.param.activiti.impl;

import cn.com.tzy.springbootentity.param.activiti.TaskCompleteModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;


@ApiModel("任务完成模型实现类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompleteModelImpl implements TaskCompleteModel {


    @ApiModelProperty("获取任务ID")
    private String taskId;

    @ApiModelProperty("流程变量")
    private Map<String, Object> variables;

    @ApiModelProperty("任务变量")
    private Map<String, Object> taskVariables;

    @ApiModelProperty("获取下一步目标任务的Key，用于任务完成时任务跳转等操作")
    private String targetTaskKey;

    @ApiModelProperty("当前的处理人ID")
    private String userId;


    @ApiModelProperty("获取流程批注信息")
    private CommentEntity comment;

    @ApiModelProperty("1.下一个环节的候选人ID，若是只有一个，则为处理人")
    private List<String> candidateOrAssigned;

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public Map<String, Object> getTaskVariables() {
        return taskVariables;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public List<String> getCandidateOrAssigned() {
        return candidateOrAssigned;
    }

    @Override
    public String getTargetTaskKey() {
        return targetTaskKey;
    }
}
