package cn.com.tzy.springbootentity.param.bean;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("职位信息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PositionParam extends PageModel {

    @ApiModelProperty("编号")
    public Long id;

    @ApiModelProperty("父级菜单")
    public Long parentId;

    @ApiModelProperty("部门名称")
    public String positionName;

    @ApiModelProperty("是否开启 1.是 0否")
    public Integer isEnable;

    @ApiModelProperty("备注")
    public String memo;

    @ApiModelProperty("顶级名称.用于给树添加顶级树")
    public String topName;
}
