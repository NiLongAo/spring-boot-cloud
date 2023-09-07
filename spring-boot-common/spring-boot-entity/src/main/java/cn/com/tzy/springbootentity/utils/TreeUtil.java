package cn.com.tzy.springbootentity.utils;


import cn.com.tzy.springbootcomm.common.bean.Tree;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootcomm.interfaces.SFunction;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;

@Log4j2
public class TreeUtil {

    public static <T> List<Tree<T>> getTree(List<T> originalList, SFunction<T,?> parentKey, SFunction<T,?> idKey, Object parentValue){
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
    public static <T> List<Tree<T>> getTree(List<T> originalList, String parentKey, String idKey, Object parentValue){
        if(parentValue instanceof Collection){
            return getTreeV2(originalList,parentKey,idKey,(Collection<Object>)parentValue);
        }else {
            return getTreeV2(originalList,parentKey,idKey, Collections.singletonList(parentValue));
        }
    }

    /**
     * 树结构优化，降低时间复杂度，
     */
    public static <T> List<Tree<T>> getTreeV2(Collection<T> originalList, String parentKey, String idKey, Collection<Object> parentValueList) {
        Map<String, Tree<T>> parentNodeMap = new LinkedHashMap<>();
        try {
            Map<String, Tree<T>> nodeMap = new LinkedHashMap<>();
            for (T t : originalList) {
                String id = BeanUtils.getProperty(t, idKey);
                String parentId = BeanUtils.getProperty(t, parentKey);
                //兼容 父节点不规范 可能为 null 或者 ''
                if(StringUtils.isEmpty(parentId) && !parentValueList.contains(parentId)){
                    parentId = parentId == null?"":null;
                }
                if(parentValueList.contains(parentId)){
                    parentNodeMap.put(id,new Tree<>(t, false, new ArrayList<>()));
                }else {
                    nodeMap.put(id,new Tree<>(t, false, new ArrayList<>()));
                }
            }
            for (T node : originalList) {
                String id = BeanUtils.getProperty(node, idKey);
                String parentId = BeanUtils.getProperty(node, parentKey);
                if(parentValueList.contains(parentId)){
                    continue;
                }
                Tree<T> parentTree = nodeMap.get(parentId);
                if(parentTree == null){
                    parentTree = parentNodeMap.get(parentId);
                }
                if(parentTree == null){
                    continue;
                }
                Tree<T> nodeTree = nodeMap.get(id);
                if(nodeTree == null){
                    continue;
                }
                parentTree.setIsChildren(true);
                parentTree.addChild(nodeTree);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析值错误：", e);
        }
        return new ArrayList<>(parentNodeMap.values());
    }

//    public static void main(String[] args) throws Exception {
//        List<Menu> menuList = new ArrayList<>();
//        String parentId = null;
//        for (int i = 1; i <=10000; i++) {
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
//        List<Tree<Menu>> tree = getTree(menuList, Menu::getParentId, Menu::getId, null);
//        Instant now1 = Instant.now();
//        System.out.println(now1.toEpochMilli() - now.toEpochMilli());
//        System.out.println(JSONUtil.toJsonStr(tree));
//    }

}
