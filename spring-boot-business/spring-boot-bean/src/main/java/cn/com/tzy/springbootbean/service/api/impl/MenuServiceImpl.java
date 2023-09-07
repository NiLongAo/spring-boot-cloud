package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.MenuMapper;
import cn.com.tzy.springbootbean.mapper.sql.PrivilegeMapper;
import cn.com.tzy.springbootbean.mapper.sql.UserSetMapper;
import cn.com.tzy.springbootbean.service.api.MenuService;
import cn.com.tzy.springbootcomm.common.bean.Tree;
import cn.com.tzy.springbootentity.common.info.VueRoutes;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.com.tzy.springbootentity.dome.bean.Privilege;
import cn.com.tzy.springbootentity.dome.bean.UserSet;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private PrivilegeMapper privilegeMapper;
    @Autowired
    private UserSetMapper userSetMapper;

    @SneakyThrows
    @Override
    public RestResult<?> tree(String topName,Integer isShowPrivilege,String menuName){
        // parentId,id,menuName
        List<Map> menuList = baseMapper.findAvailableTree(isShowPrivilege,menuName);

        List<Tree<Map>> tree = TreeUtil.getTree(menuList, "parentId", "id", null);
        //顶级树
        Map map = null;
        if(StringUtils.isNotEmpty(topName)){
            map = new HashMap();
            map.put("parentId","");
            map.put("id","");
            map.put("menuName",topName);
        }
        //转换树结构
        List<Map> maps = AppUtils.transformationTree(map,"children",tree);
        return RestResult.result(RespCode.CODE_0.getValue(), null,maps);
    }

    @SneakyThrows
    @Override
    public PageResult page(MenuParam param) {
        //所有菜单
        List<Map> menuList = baseMapper.findSelect(param.menuName);
        //查询操作开始...
        Map<String, Map> menuMap= new HashMap<>();
        menuList.forEach(obj->{
            menuMap.put(obj.get("id").toString(),obj);
        });
        menuList.forEach(onj->{
            findParent(menuMap,onj);
        });
        menuList = new ArrayList<>(menuMap.values());
        menuList = menuList.stream().sorted((k,v)-> Integer.parseInt(k.get("num").toString()) - Integer.parseInt(v.get("num").toString())).collect(Collectors.toList());
        //查询操作结束...
        List<Tree<Map>> tree = TreeUtil.getTree(menuList, "parentId", "id", null);
        List<String> stringList = new ArrayList<>();
        //所有子集菜单编号，以及转换树
        selectLastId(stringList,tree);
        List<Map> privileges = privilegeMapper.findMenuList(stringList);
        Map<String,List<Tree<Map>>> map = new HashMap<>();
        privileges.forEach(obj ->{
            List<Tree<Map>> parentMap = map.get(obj.get("parentId"));
            if(parentMap == null){
                parentMap = new ArrayList<>();
                map.put(obj.get("parentId").toString(),parentMap);
            }
            Tree<Map> mapTree = new Tree<>();
            mapTree.setT(obj);
            mapTree.setIsChildren(false);
            parentMap.add(mapTree);
        });
        addLastTree(tree,map);
        //转换树结构
        List<Map> maps = AppUtils.transformationTree("children",tree);
        return PageResult.result(RespCode.CODE_0.getValue(),menuList.size(),null,maps);
    }

    @SneakyThrows
    @Override
    public RestResult<?> menuPrivilegeTree () {
        //所有菜单
        List<Map> menuList = baseMapper.findMenuPrivilegeTree();
        List<Tree<Map>> tree = TreeUtil.getTree(menuList, "parentId", "v", null);
        List<String> stringList = new ArrayList<>();
        //所有子集菜单编号，以及转换树
        selectLastV(stringList,tree);
        List<Map> privileges = privilegeMapper.findMenuPrivilegeTree(stringList);
        Map<String,List<Tree<Map>>> map = new HashMap<>();
        privileges.forEach(obj ->{
            List<Tree<Map>> parentMap = map.get(obj.get("parentId"));
            if(parentMap == null){
                parentMap = new ArrayList<>();
                map.put(obj.get("parentId").toString(),parentMap);
            }
            Tree<Map> mapTree = new Tree<>();
            mapTree.setT(obj);
            mapTree.setIsChildren(false);
            parentMap.add(mapTree);
        });
        addLastTreeV(tree,map);

        //转换树结构
        List<Map> maps = AppUtils.transformationTree("children",tree);
        return RestResult.result(RespCode.CODE_0.getValue(),null,maps);
    }

    public void findParent(Map<String, Map> map, Map onj){
        if(onj != null && onj.get("parentId") != null){
            Map entity= map.get(onj.get("parentId").toString());
            if(entity == null){
                Map menu = baseMapper.find(onj.get("parentId").toString());
                map.put(onj.get("parentId").toString(),menu);
                if(menu != null && menu.get("parentId") != null){
                    findParent(map,onj);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(MenuParam param) {
        if(param.id == null){
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取菜单编号");
        }
        if(param.parentId!= null && param.parentId.equals(param.id)){
            return RestResult.result(RespCode.CODE_2.getValue(),"父级编号与本级相同！");
        }
        if(param.parentId!= null){
            Menu parentMenu = baseMapper.selectOne(new QueryWrapper<Menu>().eq("id", param.parentId));
            if(parentMenu == null ){
                return RestResult.result(RespCode.CODE_2.getValue(), "未获取到父菜单信息");
            }
            Integer parentPrivilegeCount = privilegeMapper.selectCount(new QueryWrapper<Privilege>().eq("menu_id", parentMenu.getId()));
            if(parentPrivilegeCount >0){
                return RestResult.result(RespCode.CODE_2.getValue(), "当前选择父菜单已有权限,无法作为父菜单");
            }
        }
        if (StringUtils.isEmpty(ConstEnum.Flag.getName(param.isOpen))) {
            return RestResult.result(RespCode.CODE_2.getValue(), "当前状态错误，请检查");
        }
        Menu  menu = baseMapper.selectOne(new QueryWrapper<Menu>().eq("id", param.id));
        Menu build = Menu.builder()
                .id(param.id)
                .parentId(param.parentId)
                .icon(param.icon)
                .level(param.level)
                .menuName(param.menuName)
                .viewPath(param.viewPath)
                .hideMenu(param.hideMenu)
                .path(param.path)
                .isOpen(param.isOpen)
                .num(param.num)
                .memo(param.memo)
                .build();
        int b = 0;
        if(menu == null){
            b = baseMapper.insert(build);
        }else {
            b = baseMapper.updateById(build);
        }
        if (b > 0) {
            return RestResult.result(RespCode.CODE_0.getValue(), "保存成功");
        } else {
            return RestResult.result(RespCode.CODE_2.getValue(), "保存失败");
        }
    }

    @Override
    public RestResult<?> findUserTreeMenu(Long userId) throws Exception {
        List<Menu> userAllMenu = new ArrayList<>();
        List<Menu> userMenu = new ArrayList<>();
        //当前用户所有权限菜单
        //所有菜单
        List<Menu> allMenu = baseMapper.selectList(new LambdaQueryWrapper<Menu>().eq(Menu::getIsOpen,ConstEnum.Flag.YES.getValue()));
        Set<Menu> userMenuList = new HashSet<>();
        //删除当前用户租户没有菜单
        List<Menu> userTenantMenu = baseMapper.findUserTenantMenu(userId);
        if(!userTenantMenu.isEmpty()){
            userTenantMenu.forEach(menu -> {
                List<Menu> delete = new ArrayList<>();
                findSuperiorMenu(menu, allMenu, userAllMenu, delete);
                allMenu.removeAll(delete);
            });
        }
        UserSet userSet = userSetMapper.selectById(userId);
        if(userSet == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"用户信息错误");
        }
        if (userSet.getIsAdmin()==ConstEnum.Flag.NO.getValue()) {
            //获取角色权限信息
            userMenuList.addAll(baseMapper.findUserRoleMenu(userId));
            //获取部门权限信息
            userMenuList.addAll(baseMapper.findUserDepartmentMenu(userId));
            //获取职位权限信息
            userMenuList.addAll(baseMapper.findUserPositionMenu(userId));
            userMenuList.forEach(menu -> {
                List<Menu> delete = new ArrayList<>();
                findSuperiorMenu(menu, userAllMenu, userMenu, delete);
                userAllMenu.removeAll(delete);
            });
        }else {
            userMenu.addAll(userAllMenu);
        }
        //排序
        userMenu.sort(Comparator.comparing(Menu::getNum));
        List<Tree<Menu>> tree = TreeUtil.getTree(new ArrayList<>(userMenu), Menu::getParentId, Menu::getId, null);
        //树转vue路由
        List<VueRoutes> routes = findRoutes(null,tree);
        return RestResult.result(RespCode.CODE_0.getValue(), null, routes);
    }

    @Override
    public RestResult<?> tenantMenuPrivilegeTree(Long tenantId) throws Exception {
        //所有菜单
        List<Map> menuList = baseMapper.findMenuPrivilegeTree();
        Map<String, Map> mapMap = menuList.stream().collect(Collectors.toMap(s -> String.valueOf(s.get("v")), s -> s,throwingMerger(),LinkedHashMap::new));
        List<Tree<Map>> tree = TreeUtil.getTree(menuList, "parentId", "v", null);
        List<String> stringList = new ArrayList<>();
        //所有子集菜单编号，以及转换树
        selectLastV(stringList,tree);
        List<Map> privileges = privilegeMapper.findTenantMenuPrivilegeTree(tenantId,stringList);
        Set<String> parentIdSet = privileges.stream().map(v -> String.valueOf(v.get("parentId"))).collect(Collectors.toSet());
        Set<String> newMenuIdSet = new HashSet<>();
        List<Map> newMenuList = new LinkedList<>();
        for (String key : parentIdSet) {
            deleteLastTreeV(key,newMenuIdSet,mapMap);
        }
        List<String> deleteList = CollUtil.subtractToList(mapMap.keySet(), newMenuIdSet);
        for (String deleteId : deleteList) {
            mapMap.remove(deleteId);
        }
        newMenuList.addAll(mapMap.values());
        newMenuList.addAll(privileges);
        //转换树结构
        tree = TreeUtil.getTree(newMenuList, "parentId", "v", null);
        List<Map> maps = AppUtils.transformationTree("children",tree);
        return RestResult.result(RespCode.CODE_0.getValue(),null,maps);
    }

    /**
     * //树转vue路由
     *
     * @return
     */
    private List<VueRoutes> findRoutes(VueRoutes routes,List<Tree<Menu>> tree) {
        List<VueRoutes> routesList = new ArrayList<>();
        tree.forEach(obj -> {
            VueRoutes vueRoutes = new VueRoutes();
            if (obj.getChildren().size() > 0) {
                List<VueRoutes> routes1 = findRoutes(routes,obj.getChildren());
                if (routes1.size() > 0) {
                    vueRoutes.setRedirect(routes1.get(0).getPath());
                }
                vueRoutes.setChildren(routes1);
                vueRoutes.setComponent("Layout");//表示父级菜单
            } else {
                vueRoutes.setChildren(new ArrayList<>());
                vueRoutes.setComponent(obj.getT().getViewPath());
            }
            vueRoutes.setName(obj.getT().getId());
            vueRoutes.setPath(obj.getT().getPath());
            VueRoutes.Meta meta = new VueRoutes.Meta();
            meta.setIcon(obj.getT().getIcon());
            meta.setTitle(obj.getT().getMenuName());
            meta.setHideMenu(obj.getT().getHideMenu() != ConstEnum.Flag.NO.getValue());
            //已http开头路由都为内联路由
            if(obj.getT().getViewPath().startsWith("http")){
                meta.setFrameSrc(obj.getT().getViewPath());
                vueRoutes.setComponent("IFrame");
            }
            if(routes!= null && vueRoutes.getPath().contains(":")){
                meta.setCurrentActiveMenu(routes.getPath());
            }
            vueRoutes.setMeta(meta);
            routesList.add(vueRoutes);
        });
        return routesList;
    }

    /**
     * 获取最后子集编号
     * @param tree
     * @return
     */
    private void selectLastId(List<String> stringList,List<Tree<Map>> tree){
        tree.forEach(obj->{
            if(obj.getIsChildren()){
                selectLastId(stringList,obj.getChildren());
            }else {
                obj.getT().put("type",2);
                stringList.add(obj.getT().get("id").toString());
            }
        });
    }

    /**
     * 获取最后子集编号
     * @param tree
     * @return
     */
    private void selectLastV(List<String> stringList,List<Tree<Map>> tree){
        tree.forEach(obj->{
            if(obj.getIsChildren()){
                selectLastV(stringList,obj.getChildren());
            }else {
                obj.getT().put("type",2);
                stringList.add(obj.getT().get("v").toString());
            }
        });
    }
    //删除没有子集的树
    private void deleteLastTreeV(String key,Set<String> newMenuIdSet,Map<String, Map> mapMap){
        Map map = mapMap.get(key);
        if(ObjectUtil.isNotNull(map)){
            newMenuIdSet.add(String.valueOf(map.get("v")));
            Object v = map.get("parentId");
            if(ObjectUtil.isNotNull(v)){
                deleteLastTreeV(String.valueOf(v),newMenuIdSet,mapMap);
            }
        }
    }

    /**
     * 给树添加子元素
     * @param tree
     * @return
     */
    private void addLastTree(List<Tree<Map>> tree, Map<String,List<Tree<Map>>> map){
        tree.forEach(obj->{
            if(obj.getIsChildren()){
                addLastTree(obj.getChildren(),map);
            }
            List<Tree<Map>> treeList = map.get(obj.getT().get("id"));
            if(treeList != null && treeList.size() > 0){
                obj.setIsChildren(true);
                obj.setChildren(treeList);
            }
        });
    }

    /**
     * 给树添加子元素
     * @param tree
     * @return
     */
    private void addLastTreeV(List<Tree<Map>> tree, Map<String,List<Tree<Map>>> map){
        tree.forEach(obj->{
            if(obj.getIsChildren()){
                addLastTreeV(obj.getChildren(),map);
            }
            List<Tree<Map>> treeList = map.get(obj.getT().get("v"));
            if(treeList != null && treeList.size() > 0){
                obj.setIsChildren(true);
                obj.setChildren(treeList);
            }
        });
    }

    private void findSuperiorMenu(Menu useMenu, List<Menu> allMenuList, List<Menu> userMenuList, List<Menu> delete) {
        userMenuList.add(useMenu);
        delete.add(useMenu);
        if (useMenu.getParentId() != null) {
            for (Menu menuExhibitionInfo : allMenuList) {
                if (menuExhibitionInfo.getId().equals(useMenu.getParentId())) {
                    findSuperiorMenu(menuExhibitionInfo, allMenuList, userMenuList, delete);
                    break;
                }
            }
        }
    }

    /*使用流中方法*/
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }
}


