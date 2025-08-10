package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.convert.bean.MenuConvert;
import cn.com.tzy.springbootbean.mapper.sql.MenuMapper;
import cn.com.tzy.springbootbean.mapper.sql.UserSetMapper;
import cn.com.tzy.springbootbean.service.api.MenuService;
import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootentity.common.info.VueRoutes;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.com.tzy.springbootentity.dome.bean.UserSet;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private UserSetMapper userSetMapper;

    @SneakyThrows
    @Override
    public RestResult<?> tree(String topName,Integer isShowPrivilege,String menuName){
        // parentId,id,menuName
        List<Map> menuList = baseMapper.findAvailableTree(isShowPrivilege,menuName);

        List<TreeNode<Map>> treeNode = TreeUtil.getTree(menuList, "parentId", "id", null);
        //顶级树
        Map map = null;
        if(StringUtils.isNotEmpty(topName)){
            map = new HashMap();
            map.put("parentId","");
            map.put("id","");
            map.put("menuName",topName);
        }
        //转换树结构
        List<Map> maps = AppUtils.transformationTree(map,"children", treeNode);
        return RestResult.result(RespCode.CODE_0.getValue(), null,maps);
    }

    @SneakyThrows
    @Override
    public PageResult page(MenuParam param) {
        //所有菜单
        List<Menu> menuList = baseMapper.findSelect(param.getMenuName());
        //查询操作开始...
        Map<String, Menu> menuMap = menuList.stream().collect(Collectors.toMap(Menu::getId, Function.identity()));
        for (Menu menu : menuList) {
            findParent(menuMap,menu);
        }
        menuList = new ArrayList<>(menuMap.values());
        menuList = menuList.stream().sorted(Comparator.comparingInt(Menu::getOrder)).collect(Collectors.toList());
        //查询操作结束...
        List<TreeNode<Menu>> treeNode = TreeUtil.getTree(menuList, Menu::getParentId, Menu::getId, Arrays.asList(null,""));
        //树转vue路由
        List<VueRoutes> routes = findRoutes(treeNode);
        return PageResult.result(RespCode.CODE_0.getValue(),null,routes,menuList.size());
    }

    @SneakyThrows
    @Override
    public RestResult<?> menuPrivilegeTree () {
        //所有菜单
        List<Menu> menuList = baseMapper.findMenuPrivilegeTree();
        List<TreeNode<Menu>> treeNode = TreeUtil.getTree(menuList, Menu::getParentId, Menu::getId, Arrays.asList(null,""));
        //转换树结构
        List<Map> maps = AppUtils.transformationTree("children", treeNode);
        return RestResult.result(RespCode.CODE_0.getValue(),null,maps);
    }

    public void findParent(Map<String, Menu> map, Menu onj){
        if(onj != null && StringUtils.isNotEmpty(onj.getParentId())){
            Menu entity= map.get(onj.getParentId());
            if(entity == null){
                Menu menu = baseMapper.selectById(onj.getParentId());
                if(menu != null){
                    map.put(menu.getId(),menu);
                    findParent(map,menu);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(MenuParam param) {
        if(param.getId() == null){
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取菜单编号");
        }
        if(StringUtils.isEmpty(param.getParentId()) && param.getParentId().equals(param.getId())){
            return RestResult.result(RespCode.CODE_2.getValue(),"父级编号与本级相同！");
        }
        if(param.getParentId()!= null){
            Menu parentMenu = baseMapper.selectOne(new QueryWrapper<Menu>().eq("id", param.getParentId()));
            if(parentMenu == null ){
                return RestResult.result(RespCode.CODE_2.getValue(), "未获取到父菜单信息");
            }else if(parentMenu.getType()==Menu.MenuType.BUTTON.getType()){
                return RestResult.result(RespCode.CODE_2.getValue(), "菜单按钮权限无法添加下级");
            }
            Integer parentPrivilegeCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery().eq(Menu::getType, Menu.MenuType.BUTTON.getType()).eq(Menu::getParentId, parentMenu.getId()));
            if(parentPrivilegeCount >0){
                return RestResult.result(RespCode.CODE_2.getValue(), "当前选择父菜单已有权限,无法作为父菜单");
            }
        }
        if (StringUtils.isEmpty(ConstEnum.Flag.getName(param.getStatus()))) {
            return RestResult.result(RespCode.CODE_2.getValue(), "当前状态错误，请检查");
        }
        int menu = baseMapper.selectCount(new QueryWrapper<Menu>().eq("id", param.getId()));
        Menu build = MenuConvert.INSTANCE.convert(param);
        int b = 0;
        if(menu > 0){
            b = baseMapper.updateById(build);
        }else {
            b = baseMapper.insert(build);
        }
        if (b > 0) {
            return RestResult.result(RespCode.CODE_0.getValue(), "保存成功");
        } else {
            return RestResult.result(RespCode.CODE_2.getValue(), "保存失败");
        }
    }

    @Override
    public RestResult<?> findUserTreeMenu(Long userId) {
        List<Menu> userAllMenu = new ArrayList<>();
        List<Menu> userMenu = new ArrayList<>();
        //当前用户所有权限菜单
        //所有菜单
        List<Menu> allMenu = baseMapper.selectList(new LambdaQueryWrapper<Menu>().eq(Menu::getStatus,ConstEnum.Flag.YES.getValue()));
        Set<Menu> userMenuList = new HashSet<>();
        //删除当前用户租户没有菜单
        List<Menu> userTenantMenu = baseMapper.findUserTenantMenu(userId);
        if(CollUtil.isEmpty(userTenantMenu)){
            return RestResult.result(RespCode.CODE_2.getValue(),"租户没有菜单权限");
        }
        for (Menu menu : userTenantMenu) {
            List<Menu> delete = new ArrayList<>();
            findSuperiorMenu(menu, allMenu, userAllMenu, delete);
            allMenu.removeAll(delete);
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
            for (Menu menu : userMenuList) {
                List<Menu> delete = new ArrayList<>();
                findSuperiorMenu(menu, userAllMenu, userMenu, delete);
                userAllMenu.removeAll(delete);
            }
        }else {
            userMenu.addAll(userAllMenu);
        }
        //排序
        userMenu.sort(Comparator.comparing(Menu::getOrder));
        List<TreeNode<Menu>> treeNode = TreeUtil.getTree(new ArrayList<>(userMenu), Menu::getParentId, Menu::getId, Arrays.asList(null,""));
        //树转vue路由
        List<VueRoutes> routes = findRoutes(treeNode);
        return RestResult.result(RespCode.CODE_0.getValue(), null, routes);
    }

    @Override
    public RestResult<?> tenantMenuPrivilegeTree(Long tenantId) {
        //所有菜单
        List<Menu> menuList = baseMapper.findMenuPrivilegeTree();
        Map<String, Menu> mapMap = menuList.stream().collect(Collectors.toMap(Menu::getId, Function.identity(),throwingMerger(),LinkedHashMap::new));
        List<Menu> privileges = baseMapper.findTenantMenuPrivilegeTree(tenantId);
        Set<String> idSet = privileges.stream().map(Menu::getId).collect(Collectors.toSet());
        Set<String> newMenuIdSet = new HashSet<>();
        for (String key : idSet) {
            deleteLastTreeV(key,newMenuIdSet,mapMap);
        }
        List<String> deleteList = CollUtil.subtractToList(mapMap.keySet(), newMenuIdSet);
        for (String deleteId : deleteList) {
            mapMap.remove(deleteId);
        }
        List<Menu> newMenuList = new LinkedList<>(mapMap.values());
        //转换树结构
        List<TreeNode<Menu>> treeNode = TreeUtil.getTree(newMenuList, Menu::getParentId, Menu::getId, null);
        List<Map> maps = AppUtils.transformationTree("children", treeNode);
        return RestResult.result(RespCode.CODE_0.getValue(),null,maps);
    }

    @Override
    public List<Menu> findTypeButtonMenu(Integer bizType, Long bizId) {
        return baseMapper.findTypeButtonMenu(bizType,bizId);
    }

    @Override
    public RestResult<?> initBottom() {
        try {
            List<Menu> typeButtonMenu = baseMapper.findTypeButtonMenu(null, null);
            Map<String,Set<String>> allUrlPrivilege = new HashMap<>();
            for (Menu buttonMenu : typeButtonMenu) {
                if(StringUtils.isEmpty(buttonMenu.getRequestUrl()) || StringUtils.isEmpty(buttonMenu.getAuthCode())){
                    continue;
                }
                String[] split = buttonMenu.getRequestUrl().split(",");//多个页面url组合时
                for (String url : split) {
                    Set<String> object =allUrlPrivilege.computeIfAbsent(url, k -> new HashSet<String>());
                    object.add(buttonMenu.getAuthCode());
                }
            }
            if (RedisUtils.hasKey(Constant.ALL_URL_KEY)) {
                RedisUtils.del(Constant.ALL_URL_KEY);
            }
            RedisUtils.hmset(Constant.ALL_URL_KEY, allUrlPrivilege);
        } catch (Exception e) {
            log.error("初始化权限信息错误 :", e);
        }
        return RestResult.result(RespCode.CODE_0.getValue(), "初始化成功");
    }

    /**
     * 树转vue路由
     *
     * @return
     */
    private List<VueRoutes> findRoutes(List<TreeNode<Menu>> treeNode) {
        List<VueRoutes> routesList = new ArrayList<>();
        for (TreeNode<Menu> menuTreeNode : treeNode) {
            VueRoutes convert = MenuConvert.INSTANCE.convert(menuTreeNode.getT());
            if(Arrays.asList(VueRoutes.MenuType.EMBEDDED.getTitle(),VueRoutes.MenuType.LINK.getTitle()).contains(convert.getType())){
                convert.setComponent("IFrameView");
            }
            if (CollUtil.isNotEmpty(menuTreeNode.getChildren())) {
                convert.setChildren(findRoutes(menuTreeNode.getChildren()));
            } else {
                convert.setChildren(new ArrayList<>());
            }
            routesList.add(convert);
        }
        return routesList;
    }
    //删除没有子集的树
    private void deleteLastTreeV(String key,Set<String> newMenuIdSet,Map<String, Menu> mapMap){
        Menu map = mapMap.get(key);
        if(ObjectUtil.isNotNull(map)){
            newMenuIdSet.add(map.getId());
            if(ObjectUtil.isNotNull(map.getParentId())){
                deleteLastTreeV(map.getParentId(),newMenuIdSet,mapMap);
            }
        }
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


