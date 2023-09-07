package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "菜单表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_menu")
public class Menu extends StringIdEntity {
    /**
     * 父级菜单
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value = "父级菜单")
    private String parentId;

    /**
     * 级别
     */
    @TableField(value = "level")
    @ApiModelProperty(value = "级别")
    private Integer level;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    /**
     * 跳转路径
     */
    @TableField(value = "path")
    @ApiModelProperty(value = "跳转路径")
    private String path;

    /**
     * 页面路径
     */
    @TableField(value = "view_path")
    @ApiModelProperty(value = "页面路径")
    private String viewPath;

    /**
     * 小图标
     */
    @TableField(value = "icon")
    @ApiModelProperty(value = "小图标")
    private String icon;

    /**
     * 是否开启 1.是 0否
     */
    @TableField(value = "is_open")
    @ApiModelProperty(value = "是否开启 1.是 0否")
    private Integer isOpen;

    /**
     * 是否隐藏 1.是 0否
     */
    @TableField(value = "hide_menu")
    @ApiModelProperty(value = "是否隐藏 1.是 0否")
    private Integer hideMenu;

    /**
     * 序号
     */
    @TableField(value = "num")
    @ApiModelProperty(value = "序号")
    private Integer num;

    /**
     * 备注
     */
    @TableField(value = "memo")
    @ApiModelProperty(value = "备注")
    private String memo;
}
