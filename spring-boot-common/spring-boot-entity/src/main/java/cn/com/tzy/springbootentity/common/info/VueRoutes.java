package cn.com.tzy.springbootentity.common.info;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Vue路由结构
 */
@Getter
@Setter
public class VueRoutes {
    //前端路径
    private String path;
    //前端名称
    private String name;
    //跳转路径
    private String component;
    //父级默认跳转子集路径
    private String redirect;
    //菜单信息
    private Meta meta;
    //子集信息
    private List<VueRoutes> children;

    @Getter
    @Setter
    public static class  Meta{
        //菜单名称
        private String title;
        //图标
        private String icon;
        //图标
        private Boolean hideMenu = false;
        //内嵌外部页面url
        private String  frameSrc;
        //作为隐藏目录的上级目录用户详情页的父页
        private String currentActiveMenu;

    }

}
