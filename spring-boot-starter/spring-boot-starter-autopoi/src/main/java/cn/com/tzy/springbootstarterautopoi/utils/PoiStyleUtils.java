package cn.com.tzy.springbootstarterautopoi.utils;

import cn.hutool.core.map.MapUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 优化 easypoi 相关工具类
 */
public class PoiStyleUtils {
    /**
     * 更改单元格相关样式
     * @param sheet excel 页
     * @param styleMap 样式值
     * @param region 坐标
     */
    public static void mergedStyle(Sheet sheet,Map<String,Object> styleMap,CellRangeAddress region) {
        BorderStyle borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_BOTTOM, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderBottom(borderStyle, region, sheet); // 下边框
        }
        borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_LEFT, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderLeft(borderStyle, region, sheet); // 左边框
        }
        borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_RIGHT, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderRight(borderStyle, region, sheet); // 右边框
        }
        borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_TOP, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderTop(borderStyle, region, sheet); // 上边框
        }
        styleMap.remove(CellUtil.BORDER_BOTTOM);
        styleMap.remove(CellUtil.BORDER_LEFT);
        styleMap.remove(CellUtil.BORDER_RIGHT);
        styleMap.remove(CellUtil.BORDER_TOP);
        int firstRow = region.getFirstRow();//开始行
        int lastRow = region.getLastRow();//结束行
        int firstColumn = region.getFirstColumn();//开始列
        int lastColumn = region.getLastColumn();//结束列
        for (int i = firstRow; i <= lastRow; i++) {
            for (int j = firstColumn; j < lastColumn; j++) {
                Row row = CellUtil.getRow(i, sheet); //获取excel 的 行
                Cell cell = CellUtil.getCell(row, j);//获取单元格
                CellUtil.setCellStyleProperties(cell,styleMap);//给单元格 赋值样式
            }
        }
    }

    /**
     * 给单元格赋值样式
     * @param sheet excel 页
     * @param style 样式
     * @param region 坐标
     */
    public static void mergedStyle(Sheet sheet, CellStyle style, CellRangeAddress region) {

        Map<String, Object> styleMap = getFormatProperties(style);//获取样式中所有值
        BorderStyle borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_BOTTOM, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderBottom(borderStyle, region, sheet); // 下边框
        }
        borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_LEFT, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderLeft(borderStyle, region, sheet); // 下边框
        }
        borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_RIGHT, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderRight(borderStyle, region, sheet); // 下边框
        }
        borderStyle = MapUtil.get(styleMap, CellUtil.BORDER_TOP, BorderStyle.class);
        if(borderStyle != null){
            RegionUtil.setBorderTop(borderStyle, region, sheet); // 下边框
        }
        styleMap.remove(CellUtil.BORDER_BOTTOM);
        styleMap.remove(CellUtil.BORDER_LEFT);
        styleMap.remove(CellUtil.BORDER_RIGHT);
        styleMap.remove(CellUtil.BORDER_TOP);
        int firstRow = region.getFirstRow();//开始行
        int lastRow = region.getLastRow();//结束行
        int firstColumn = region.getFirstColumn();//开始列
        int lastColumn = region.getLastColumn();//结束列
        for (int i = firstRow; i <= lastRow; i++) {
            for (int j = firstColumn; j < lastColumn; j++) {
                Row row = CellUtil.getRow(i, sheet); //获取excel 的 行
                Cell cell = CellUtil.getCell(row, j);//获取单元格
                CellUtil.setCellStyleProperties(cell,styleMap);//给单元格 赋值样式
            }
        }
    }

    /**
     * 复制 CellUtil 中私有方法 getFormatProperties
     * @param style
     * @return
     */
    private static Map<String, Object> getFormatProperties(CellStyle style) {
        Map<String, Object> properties = new HashMap();
        put(properties, "alignment", style.getAlignment());
        put(properties, "verticalAlignment", style.getVerticalAlignment());
        put(properties, "borderBottom", style.getBorderBottom());
        put(properties, "borderLeft", style.getBorderLeft());
        put(properties, "borderRight", style.getBorderRight());
        put(properties, "borderTop", style.getBorderTop());
        put(properties, "bottomBorderColor", style.getBottomBorderColor());
        put(properties, "dataFormat", style.getDataFormat());
        put(properties, "fillPattern", style.getFillPattern());
        put(properties, "fillForegroundColor", style.getFillForegroundColor());
        put(properties, "fillBackgroundColor", style.getFillBackgroundColor());
        put(properties, "font", style.getFontIndexAsInt());
        put(properties, "hidden", style.getHidden());
        put(properties, "indention", style.getIndention());
        put(properties, "leftBorderColor", style.getLeftBorderColor());
        put(properties, "locked", style.getLocked());
        put(properties, "rightBorderColor", style.getRightBorderColor());
        put(properties, "rotation", style.getRotation());
        put(properties, "topBorderColor", style.getTopBorderColor());
        put(properties, "wrapText", style.getWrapText());
        return properties;
    }

    private static void put(Map<String, Object> properties, String name, Object value) {
        properties.put(name, value);
    }
}
