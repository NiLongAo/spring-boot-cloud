package cn.com.tzy.springbootentity.param.activiti.impl;

import cn.com.tzy.springbootentity.param.activiti.StartProcessModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;


@Data
@Builder(toBuilder = true)
@ApiModel("启动流程模型实现类")
public class StartProcessModelImpl implements StartProcessModel {


    @ApiModelProperty("流程程定义Key，用于启动流程或处理流程相关操作")
    private String processDefineKey;

    @ApiModelProperty("业务主键")
    private String businessKey;

    @ApiModelProperty("携带参数")
    private Map<String, Object> variables;

    @ApiModelProperty("获取流程定义名称")
    private String name;

    @ApiModelProperty("获取流程批注信息")
    private CommentEntity comment;

    @ApiModelProperty("当前的处理人ID")
    private String userId;

    @ApiModelProperty("1.下一个环节的候选人ID，若是只有一个，则为处理人")
    private List<String> candidateOrAssigned;

    @Override
    public String getProcessDefineKey() {
        return processDefineKey;
    }

    @Override
    public String getBusinessKey() {
        return businessKey;
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public List<String> getCandidateOrAssigned() {
        return candidateOrAssigned;
    }
}
