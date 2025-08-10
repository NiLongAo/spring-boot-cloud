package cn.com.tzy.springbootentity.param.bean;


import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@ApiModel("菜单信息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MenuParam extends PageModel {

    @ApiModelProperty(value = "菜单编号")
    private String id;

    @ApiModelProperty(value = "父级菜单")
    private String parentId;

    @ApiModelProperty(value = "级别")
    private Integer type;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "跳转路径")
    private String path;

    @ApiModelProperty(value = "页面路径")
    private String component;

    @ApiModelProperty(value = "重定向(默认指向第一个路径)")
    private String redirect;

    @ApiModelProperty(value = "权限标识")
    private String authCode;

    @ApiModelProperty(value = "小图标")
    private String icon;

    @ApiModelProperty(value = "激活时小图标")
    private String activeIcon;

    @ApiModelProperty(value = "作为路由时，需要激活的菜单的Path")
    private String activePath;

    @ApiModelProperty(value = "徽标类型 1.点 2.文字")
    private Integer badgeType;

    @ApiModelProperty(value = "徽标颜色 1.默认 2.主要 3.成功 4.警告 5.错误")
    private Integer badgeVariants;

    @ApiModelProperty(value = "徽标内容(当徽标类型为文字时有效)")
    private String badgeContext;

    @ApiModelProperty(value = "在菜单中隐藏下级 1.是 0否")
    private Integer hideChildrenInMenu;

    @ApiModelProperty(value = "在菜单中隐藏下级 1.是 0否")
    private Integer hideInMenu;

    @ApiModelProperty(value = "在标签栏中隐藏 1.是 0否")
    private Integer hideInTab;

    @ApiModelProperty(value = "是否缓存页面 1.是 0否")
    private Integer keepAlive;

    @ApiModelProperty(value = "无需基础布局 1.是 0否")
    private Integer noBasicLayout;

    @ApiModelProperty(value = "是否在新窗口打开 1.是 0否")
    private Integer openInNewWindow;

    @ApiModelProperty(value = "后端请求路径")
    private String requestUrl;

    @ApiModelProperty(value = "是否开启 1.是 0否")
    private Integer status;

    @ApiModelProperty(value = "序号")
    private Integer order;

    @ApiModelProperty(value = "备注")
    private String memo;
    @ApiModelProperty(value = "顶部名称")
    private String topName;
    @ApiModelProperty(value = "是否保护权限")
    private Integer isShowPrivilege;
}
