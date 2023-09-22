package cn.com.tzy.springbootcomm.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 树级泛型结构
 * @author TZY
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode<T> {

    /**
     * 本级
     */
    private T t;
    /**
     * 判断有没有下一级节点，有的话返回true，否则返回false
     */
    private Boolean isChildren;
    /**
     * 子级
     */
    private List<TreeNode<T>> children = new ArrayList<>();

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }
}
