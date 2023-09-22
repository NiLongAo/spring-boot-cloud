package cn.com.tzy.springbootentity.utils;


import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootcomm.interfaces.SFunction;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class TreeUtil {

    public static <T> List<TreeNode<T>> getTree(List<T> originalList, SFunction<T,?> parentKey, SFunction<T,?> idKey, Object parentValue){
       return getTree(originalList, AppUtils.getFieldName(parentKey),AppUtils.getFieldName(idKey),parentValue);
    }

    /**
     * 把列表转换为树结构
     * @param originalList 原始list数据
     * @param parentKey 父级编号
     * @param idKey 父级编号
     * @param parentValue 查询哪个父级开始
     * @return 组装后的集合
     */
    public static <T> List<TreeNode<T>> getTree(List<T> originalList, String parentKey, String idKey, Object parentValue){
        if(parentValue instanceof Collection){
            return getTreeV2(originalList,parentKey,idKey,(Collection<Object>)parentValue);
        }else {
            return getTreeV2(originalList,parentKey,idKey, Collections.singletonList(parentValue));
        }
    }

    /**
     * 树结构优化，降低时间复杂度，
     */
    public static <T> List<TreeNode<T>> getTreeV2(Collection<T> originalList, String parentKey, String idKey, Collection<Object> parentValueList) {
        List<TreeNode<T>> parentNodeMap = new ArrayList<>();
        Map<String, TreeNode<T>> collect = originalList.stream().collect(Collectors.toMap(o -> {
            try {
                return BeanUtils.getProperty(o, idKey);
            } catch (Exception e) {
                throw new RuntimeException("解析值错误：", e);
            }
        }, o -> new TreeNode<>(o, false, new ArrayList<>())));
        try {
            for (T node : originalList) {
                String id = BeanUtils.getProperty(node, idKey);
                String parentId = BeanUtils.getProperty(node, parentKey);
                TreeNode<T> treeNode = collect.get(id);
                if(parentValueList.contains(parentId)){
                    parentNodeMap.add(treeNode);
                    continue;
                }
                TreeNode<T> parentTreeNode = collect.get(parentId);
                if(parentTreeNode == null){
                    continue;
                }
                parentTreeNode.setIsChildren(true);
                parentTreeNode.addChild(treeNode);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析值错误：", e);
        }
        return parentNodeMap;
    }

//    public static void main(String[] args) throws Exception {
//        List<Menu> menuList = new ArrayList<>();
//        String parentId = null;
//        for (int i = 1; i <=100000; i++) {
//            if(i%10==0){
//                parentId = String.format("%s",i/2);
//            }
//            Menu menu = new Menu();
//            menu.setParentId(parentId);
//            menu.setId(String.format("%s",i));
//            menu.setMenuName("菜单："+ i);
//            menuList.add(menu);
//        }
//        Instant now = Instant.now();
//        List<TreeNode<Menu>> tree = getTree(menuList, Menu::getParentId, Menu::getId, null);
//        Instant now1 = Instant.now();
//        System.out.println(now1.toEpochMilli() - now.toEpochMilli());
//    }

}
