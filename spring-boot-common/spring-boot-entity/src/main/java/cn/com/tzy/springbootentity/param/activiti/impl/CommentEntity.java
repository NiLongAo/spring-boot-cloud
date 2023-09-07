package cn.com.tzy.springbootentity.param.activiti.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 用于组装批准 基本内容
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流程批注信息")
public class CommentEntity implements Serializable {

    @ApiModelProperty("审批人编号")
    private Long userId;

    @ApiModelProperty("审批人姓名")
    private String userName;

    @ApiModelProperty("审批人部门编号")
    private Long departmentId;

    @ApiModelProperty("审批人部门名称")
    private String departmentName;

    @ApiModelProperty("审核状态")
    private Integer status;

    @ApiModelProperty("审核状态名称")
    private String statusName;

    @ApiModelProperty("审批备注")
    private String memo;


    public void setStatus(Integer status){
        this.status = status;
        if(status != null){
            this.statusName = ConstEnum.ReviewStateEnum.getName(status);
        }
    }

}
