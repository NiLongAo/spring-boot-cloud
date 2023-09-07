package cn.com.tzy.springbootentity.param.bean;


import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("菜单信息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MenuParam extends PageModel {

    @ApiModelProperty("编号")
    public String id;

    @ApiModelProperty("父级菜单")
    public String parentId;

    @ApiModelProperty("级别")
    public Integer level;

    @ApiModelProperty("菜单名称")
    public String menuName;

    @ApiModelProperty("跳转路径")
    public String path;

    @ApiModelProperty("页面路径")
    public String viewPath;

    @ApiModelProperty("是否开启 1.是 0否")
    public Integer isOpen;

    @ApiModelProperty("是否隐藏 1.是 0否")
    public Integer hideMenu;

    @ApiModelProperty("序号")
    public Integer num;

    @ApiModelProperty("小图标")
    public String icon;

    @ApiModelProperty("备注")
    public String memo;

    @ApiModelProperty("顶级名称，用于给树添加顶级树")
    public String topName;

    @ApiModelProperty("顶级名称用于给树添加顶级树")
    public Integer isShowPrivilege = ConstEnum.Flag.YES.getValue();
}
