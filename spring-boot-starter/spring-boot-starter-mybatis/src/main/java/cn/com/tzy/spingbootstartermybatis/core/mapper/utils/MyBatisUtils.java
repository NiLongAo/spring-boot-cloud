package cn.com.tzy.spingbootstartermybatis.core.mapper.utils;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.model.PageSortModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MyBatis 工具类
 */
public class MyBatisUtils {

    private static final String MYSQL_ESCAPE_CHARACTER = "`";


    public static <T> Page<T> buildPage(PageModel pageParam) {
        return buildPage(pageParam, pageParam.getSort()== null?null: Collections.singletonList(pageParam.getSort()));
    }

    public static <T> Page<T> buildPage(PageModel pageParam, Collection<PageSortModel> sortingFields) {
        // 页码 + 数量
        Page<T> page = new Page<>(pageParam.getPageNumber(), pageParam.getPageSize());
        // 排序字段
        if (!CollectionUtil.isEmpty(sortingFields)) {
            page.addOrder(sortingFields.stream().map(sortingField -> PageSortModel.ASC.equals(sortingField.getOrder()) ?
                    OrderItem.asc(sortingField.getField()) : OrderItem.desc(sortingField.getField()))
                    .collect(Collectors.toList()));
        }
        return page;
    }
    public static <T> PageResult selectPage(BaseMapper<T> mapper,Page<T> page, Wrapper<T> queryWrapper) {
        mapper.selectPage(page,queryWrapper);
        return PageResult.result(RespCode.CODE_0.getValue(), Math.toIntExact(page.getTotal()),null,page.getRecords());
    }

    public static <T> PageResult selectPage(IPage<T> page) {
        return PageResult.result(RespCode.CODE_0.getValue(), Math.toIntExact(page.getTotal()),null,page.getRecords());
    }


    /**
     * 将拦截器添加到链中
     * 由于 MybatisPlusInterceptor 不支持添加拦截器，所以只能全量设置
     *
     * @param interceptor 链
     * @param inner 拦截器
     * @param index 位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    /**
     * 获得 Table 对应的表名
     *
     * 兼容 MySQL 转义表名 `t_xxx`
     *
     * @param table 表
     * @return 去除转移字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构建 Column 对象
     *
     * @param tableName 表名
     * @param tableAlias 别名
     * @param column 字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        return new Column(tableAlias != null ? tableAlias.getName() + "." + column : column);
    }

}
