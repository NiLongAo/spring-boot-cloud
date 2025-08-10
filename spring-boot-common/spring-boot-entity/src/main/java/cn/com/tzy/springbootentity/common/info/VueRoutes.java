package cn.com.tzy.springbootentity.common.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vue路由结构
 */
@Getter
@Setter
public class VueRoutes {
    //前端路径
    private String path;
    //菜单名称
    private String name;
    //跳转路径 组件
    private String component;
    //重定向 如果没有特殊情况，父级路由的 `redirect` 属性，不需要指定，默认会指向第一个子路由。
    private String redirect;
    //菜单信息
    private Meta meta;
    //子集信息
    private List<VueRoutes> children;
    //后端权限标识
    private String authCode;
    //菜单ID
    private String id;
    //父级ID
    private String  pid;
    //菜单类型 'catalog', 'menu', 'embedded', 'link', 'button',
    private String type;

    private Integer status;

    @Getter
    @Setter
    public static class  Meta{
        //菜单标题
        private String title;
        //菜单图标
        private String icon;
        //内嵌Iframe的URL
        private String iframeSrc;
        //激活时显示的图标
        private String activeIcon;
        //作为路由时，需要激活的菜单的Path
        private String activePath;
        //固定在标签栏(默认就行)
        private boolean affixTab;
        //在标签栏固定的顺序(默认就行)
        private Integer affixTabOrder;
        //徽标内容(当徽标类型为normal时有效)
        private String badge;
        //徽标类型 'dot', 'normal'
        private String badgeType;
        //徽标颜色 'default', 'destructive', 'primary', 'success', 'warning',
        private String badgeVariants;
        //在菜单中隐藏下级
        private boolean hideChildrenInMenu;
        //在面包屑中隐藏
        private boolean hideInBreadcrumb;
        //在菜单中隐藏
        private boolean hideInMenu;
        //在标签栏中隐藏
        private boolean hideInTab;
        //是否缓存页面
        private boolean keepAlive;
        //外链页面的URL
        private String link;
        //同一个路由最大打开的标签数(默认就行)
        private Integer maxNumOfOpenTab;
        //无需基础布局
        private boolean noBasicLayout;
        //是否在新窗口打开
        private boolean openInNewWindow;
        //菜单排序
        private Integer order;
        //额外的路由参数
        private Map<String,Object> query;
    }

    @Getter
    public enum MenuType{
        CATALOG(1,"catalog","目录"),
        MENU(2,"menu","菜单"),
        BUTTON(3,"button","按钮"),
        EMBEDDED(4,"embedded","内嵌"),
        LINK(5,"link","外链"),
        ;
        private final int type;
        private final String title;
        private final String name;
        MenuType(int type,String title,String name){
            this.type=type;
            this.title=title;
            this.name=name;
        }
        private static final Map<Integer,MenuType> map = new HashMap<>();

        static {
            for (MenuType s : MenuType.values()) {
                map.put(s.getType(), s);
            }
        }

        public static MenuType getType(int type) {
            return map.get(type);
        }
    }

    @Getter
    public static enum BadgeType{
        DOT(1,"dot","点"),
        NORMAL(2,"normal","文字"),
        ;
        private final int type;
        private final String title;
        private final String name;
        BadgeType(int type,String title,String name){
            this.type=type;
            this.title=title;
            this.name=name;
        }
        private static Map<Integer,BadgeType> map = new HashMap<>();

        static {
            for (BadgeType s : BadgeType.values()) {
                map.put(s.getType(), s);
            }
        }

        public static BadgeType getType(int type) {
            return map.get(type);
        }
    }

    @Getter
    public static enum BadgeVariant{
        //'default', 'destructive', 'primary', 'success', 'warning'
        DEFAULT(1,"default","默认"),
        PRIMARY(2,"primary","主要"),
        SUCCESS(3,"success","成功"),
        WARNING(4,"warning","警告"),
        DESTRUCTIVE(5,"destructive","错误"),

        ;
        private final int type;
        private final String title;
        private final String name;
        BadgeVariant(int type,String title,String name){
            this.type=type;
            this.title=title;
            this.name=name;
        }
        private static Map<Integer,BadgeVariant> map = new HashMap<>();

        static {
            for (BadgeVariant s : BadgeVariant.values()) {
                map.put(s.getType(), s);
            }
        }

        public static BadgeVariant getType(int type) {
            return map.get(type);
        }
    }
}
