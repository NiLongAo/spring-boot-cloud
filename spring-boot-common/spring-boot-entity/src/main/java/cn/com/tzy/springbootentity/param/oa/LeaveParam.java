package cn.com.tzy.springbootentity.param.oa;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@SuperBuilder(toBuilder = true)
@ApiModel("请假信息参数")
@NoArgsConstructor
@AllArgsConstructor
public class LeaveParam extends PageModel {

    @NotNull(message = "id不能为空", groups = {edit.class,delete.class,editState.class})
    @ApiModelProperty("编号")
    private Long id;

    @NotBlank(message = "开始时间不能为空", groups = {add.class})
    @ApiModelProperty("开始时间")
    private String startTime;

    @NotBlank(message = "结束时间不能为空", groups = {add.class})
    @ApiModelProperty("结束时间")
    private String endTime;

    @NotNull(message = "状态错误", groups = {editState.class})
    @ApiModelProperty("状态 1.审核中 2.审核成功 3.审核不通过")
    private Integer state;

    @NotNull(message = "天数不能为空", groups = {add.class})
    @ApiModelProperty("天数")
    private Integer day;

    @NotNull(message = "流程实例主键不能为空", groups = {editProcessInstanceId.class})
    @ApiModelProperty("流程实例主键")
    private String processInstanceId;

    @ApiModelProperty("备注")
    private String memo;

    /**
     * 参数校验分组：增加
     */
    public @interface editState {}

    /**
     * 参数校验分组：增加
     */
    public @interface editProcessInstanceId {}
}