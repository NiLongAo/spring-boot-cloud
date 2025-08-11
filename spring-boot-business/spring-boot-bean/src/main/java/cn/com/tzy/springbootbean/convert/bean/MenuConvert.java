package cn.com.tzy.springbootbean.convert.bean;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.common.info.VueRoutes;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenuConvert {
    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    Menu convert(MenuParam param);

    @Named("menuTypeToVueRouteType")
    default String menuTypeToVueRouteType(Integer type) {
        if (type == null) {
            return null;
        }
        return VueRoutes.MenuType.getType(type).getTitle();
    }
    @Named("componentToFrameSrc")
    default String componentToFrameSrc(Menu menu) {
        // 当类型为4（EMBEDDED）时，将component映射为frameSrc
        if (menu.getType() != null && menu.getType() == VueRoutes.MenuType.EMBEDDED.getType()) {
            return menu.getComponent();
        }
        return null;
    }
    @Named("componentToLink")
    default String componentToLink(Menu menu) {
        // 当类型为4（EMBEDDED）时，将component映射为frameSrc
        if (menu.getType() != null && menu.getType() == VueRoutes.MenuType.LINK.getType()) {
            return menu.getComponent();
        }
        return null;
    }
    @Named("badgeTypeToVueRouteBadgeType")
    default String badgeTypeToVueRouteBadgeType(Integer badgeType) {
        if (badgeType == null) {
            return null;
        }
        return VueRoutes.BadgeType.getType(badgeType).getTitle();
    }
    @Named("badgeVariantsToVueRouteBadgeVariants")
    default String badgeVariantsToVueRouteBadgeVariants(Integer badgeVariants) {
        if (badgeVariants == null) {
            return null;
        }
        return VueRoutes.BadgeVariant.getType(badgeVariants).getTitle();
    }
    @Named("toVueRouteFlag")
    default Boolean toVueRouteFlag(Integer flag) {
        if (flag == null) {
            return null;
        }
        return ConstEnum.Flag.YES.getValue() == flag;
    }
    @Mappings( {
            @Mapping(target = "name",source = "menuName"),
            @Mapping(target = "pid",source = "parentId"),
            @Mapping(target = "type",source = "type",qualifiedByName = "menuTypeToVueRouteType"),
            @Mapping(target = "meta.title",source = "menuName"),
            @Mapping(target = "meta.icon",source = "icon"),
            @Mapping(target = "meta.iframeSrc", source = ".", qualifiedByName = "componentToFrameSrc"),
            @Mapping(target = "meta.activeIcon",source = "activeIcon"),
            @Mapping(target = "meta.activePath",source = "activePath"),
            @Mapping(target = "meta.badge",source = "badgeContext"),
            @Mapping(target = "meta.badgeType",source = "badgeType",qualifiedByName = "badgeTypeToVueRouteBadgeType"),
            @Mapping(target = "meta.badgeVariants",source = "badgeVariants",qualifiedByName = "badgeVariantsToVueRouteBadgeVariants"),
            @Mapping(target = "meta.hideChildrenInMenu",source = "hideChildrenInMenu",qualifiedByName = "toVueRouteFlag"),
            @Mapping(target = "meta.hideInMenu",source = "hideInMenu",qualifiedByName = "toVueRouteFlag"),
            @Mapping(target = "meta.hideInTab",source = "hideInTab",qualifiedByName = "toVueRouteFlag"),
            @Mapping(target = "meta.keepAlive",source = "keepAlive",qualifiedByName = "toVueRouteFlag"),
            @Mapping(target = "meta.link",source = ".",qualifiedByName = "componentToLink"),
            @Mapping(target = "meta.noBasicLayout",source = "noBasicLayout",qualifiedByName = "toVueRouteFlag"),
            @Mapping(target = "meta.openInNewWindow",source = "openInNewWindow",qualifiedByName = "toVueRouteFlag"),
            @Mapping(target = "meta.order",source = "order"),
    })
    VueRoutes convert(Menu menu);

    @Named("vueRouteTypeToType")
    default Integer vueRouteTypeToType(VueRoutes type) {
        if (type == null) {
            return null;
        }
        return VueRoutes.MenuType.getTitle(type.getType()).getType();
    }

    @Named("vueRouteToComponent")
    default String vueRouteToComponent(VueRoutes type) {
        if (type == null) {
            return null;
        }
        VueRoutes.MenuType title = VueRoutes.MenuType.getTitle(type.getType());
        switch ( title){
            case LINK:
                return type.getMeta().getLink();
            case EMBEDDED:
                return type.getMeta().getIframeSrc();
            default:
                return type.getComponent();
        }
    }
    @Named("vueRouteBadgeTypeToBadgeType")
    default Integer vueRouteBadgeTypeToBadgeType(String badgeType) {
        if (badgeType == null) {
            return null;
        }
        return VueRoutes.BadgeType.getTitle(badgeType).getType();
    }
    @Named("vueRouteBadgeVariantsToBadgeVariants")
    default Integer vueRouteBadgeVariantsToBadgeVariants(String badgeVariants) {
        if (badgeVariants == null) {
            return null;
        }
        return VueRoutes.BadgeVariant.getTitle(badgeVariants).getType();
    }

    @Named("toFlagVueRoute")
    default Integer toFlagVueRoute(boolean flag) {
        return flag?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }

    @Mappings( {
            @Mapping(source = "name",target = "menuName"),
            @Mapping(source = "pid",target = "parentId"),
            @Mapping(source = ".",target = "type",qualifiedByName = "vueRouteTypeToType"),
            @Mapping(source = ".", target = "component", qualifiedByName = "vueRouteToComponent"),
            @Mapping(source = "meta.icon",target = "icon"),
            @Mapping(source = "meta.activeIcon",target = "activeIcon"),
            @Mapping(source = "meta.activePath",target = "activePath"),
            @Mapping(source = "meta.badge",target = "badgeContext"),
            @Mapping(source = "meta.badgeType",target = "badgeType",qualifiedByName = "vueRouteBadgeTypeToBadgeType"),
            @Mapping(source = "meta.badgeVariants",target = "badgeVariants",qualifiedByName = "vueRouteBadgeVariantsToBadgeVariants"),
            @Mapping(source = "meta.hideChildrenInMenu",target = "hideChildrenInMenu",qualifiedByName = "toFlagVueRoute"),
            @Mapping(source = "meta.hideInMenu",target = "hideInMenu",qualifiedByName = "toFlagVueRoute"),
            @Mapping(source = "meta.hideInTab",target = "hideInTab",qualifiedByName = "toFlagVueRoute"),
            @Mapping(source = "meta.keepAlive",target = "keepAlive",qualifiedByName = "toFlagVueRoute"),
            @Mapping(source = "meta.noBasicLayout",target = "noBasicLayout",qualifiedByName = "toFlagVueRoute"),
            @Mapping(source = "meta.openInNewWindow",target = "openInNewWindow",qualifiedByName = "toFlagVueRoute"),
            @Mapping(source = "meta.order",target = "order"),
    })
    Menu convert(VueRoutes param);
}
