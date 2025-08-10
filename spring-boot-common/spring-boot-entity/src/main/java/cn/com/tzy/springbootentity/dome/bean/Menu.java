package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;


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
     * 级别 1.目录 2.菜单 3.按钮 4.内嵌 5.外链
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "级别")
    private Integer type;

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
    @TableField(value = "component")
    @ApiModelProperty(value = "页面路径")
    private String component;

    /**
     * 重定向(默认指向第一个路径)
     */
    @TableField(value = "redirect")
    @ApiModelProperty(value = "重定向(默认指向第一个路径)")
    private String redirect;

    /**
     * 权限标识
     */
    @TableField(value = "auth_code")
    @ApiModelProperty(value = "权限标识")
    private String authCode;

    /**
     * 小图标
     */
    @TableField(value = "icon")
    @ApiModelProperty(value = "小图标")
    private String icon;

    /**
     * 激活时小图标
     */
    @TableField(value = "active_icon")
    @ApiModelProperty(value = "激活时小图标")
    private String activeIcon;

    /**
     * 作为路由时，需要激活的菜单的Path
     */
    @TableField(value = "active_path")
    @ApiModelProperty(value = "作为路由时，需要激活的菜单的Path")
    private String activePath;

    /**
     * 徽标类型 1.点 2.文字
     */
    @TableField(value = "badge_type")
    @ApiModelProperty(value = "徽标类型 1.点 2.文字")
    private Integer badgeType;

    /**
     * 徽标颜色 1.默认 2.主要 3.成功 4.警告 5.错误
     */
    @TableField(value = "badge_variants")
    @ApiModelProperty(value = "徽标颜色 1.默认 2.主要 3.成功 4.警告 5.错误")
    private Integer badgeVariants;

    /**
     * 徽标内容(当徽标类型为文字时有效)
     */
    @TableField(value = "badge_context")
    @ApiModelProperty(value = "徽标内容(当徽标类型为文字时有效)")
    private String badgeContext;

    /**
     * 在菜单中隐藏下级 1.是 0否
     */
    @TableField(value = "hide_children_in_menu")
    @ApiModelProperty(value = "在菜单中隐藏下级 1.是 0否")
    private Integer hideChildrenInMenu;

    /**
     * 在菜单中隐藏 1.是 0否
     */
    @TableField(value = "hide_in_menu")
    @ApiModelProperty(value = "在菜单中隐藏下级 1.是 0否")
    private Integer hideInMenu;

    /**
     * 在标签栏中隐藏 1.是 0否
     */
    @TableField(value = "hide_in_tab")
    @ApiModelProperty(value = "在标签栏中隐藏 1.是 0否")
    private Integer hideInTab;

    /**
     * 是否缓存页面 1.是 0否
     */
    @TableField(value = "keep_alive")
    @ApiModelProperty(value = "是否缓存页面 1.是 0否")
    private Integer keepAlive;

    /**
     * 无需基础布局  1.是 0否
     */
    @TableField(value = "no_basic_layout")
    @ApiModelProperty(value = "无需基础布局 1.是 0否")
    private Integer noBasicLayout;

    /**
     * 是否在新窗口打开  1.是 0否
     */
    @TableField(value = "open_in_new_window")
    @ApiModelProperty(value = "是否在新窗口打开 1.是 0否")
    private Integer openInNewWindow;

    /**
     * 是否开启 1.是 0否
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "是否开启 1.是 0否")
    private Integer status;

    /**
     * 序号
     */
    @TableField(value = "`order`")
    @ApiModelProperty(value = "序号")
    private Integer order;

    /**
     * 后端请求路径
     */
    @TableField(value = "request_url")
    @ApiModelProperty(value = "后端请求路径")
    private String requestUrl;

    /**
     * 备注
     */
    @TableField(value = "memo")
    @ApiModelProperty(value = "备注")
    private String memo;

    @Getter
    public enum MenuType {
        CATALOG(1,"catalog","目录"),
        MENU(2,"menu","菜单"),
        BUTTON(3,"button","按钮"),
        EMBEDDED(4,"embedded","内嵌"),
        LINK(5,"link","外链")
        ;
        private final int type;
        private final String typeName;
        private final String name;
        private static Map<Integer,MenuType> map = new HashMap<>();
        static {
            for (MenuType value : MenuType.values()) {
                map.put(value.type,value);
            }
        }
        public static MenuType getType(int type) {
            return map.get(type);
        }
        MenuType(int type, String typeName, String name) {
            this.type = type;
            this.typeName = typeName;
            this.name = name;
        }
    }
}
