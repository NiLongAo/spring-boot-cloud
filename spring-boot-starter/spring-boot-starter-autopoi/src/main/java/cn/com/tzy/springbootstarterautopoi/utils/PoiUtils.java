package cn.com.tzy.springbootstarterautopoi.utils;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.util.WebFilenameUtils;
import cn.com.tzy.springbootcomm.annotation.Desensitized;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterautopoi.handler.AbstractExcelDictHandler;
import cn.com.tzy.springbootstarterautopoi.handler.ExcelDataHandlerImpl;
import cn.com.tzy.springbootstarterautopoi.style.ExcelExportStylerBorderImpl;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出工具类
 */
public class PoiUtils {

    /**
     *
     * @param title 导出标题
     * @param userName 导出用户
     * @param exclusionsList 忽略字段名称
     * @param desensitizedList 忽略加敏字段
     * @param pojoClass 实体类
     * @param dataList 具体类
     * @return
     * @param <T>
     */
    public static <T> void exportExcel(String title,String userName,
                                           List<String> exclusionsList, List<String> desensitizedList,
                                           Class<T> pojoClass, Collection<T> dataList,
                                           HttpServletResponse response){
        String[] exclusionsArrays =(exclusionsList==null || exclusionsList.isEmpty())?new String[]{}: exclusionsList.toArray(new String[0]);
        String[] desensitizedArrays =(desensitizedList==null || desensitizedList.isEmpty())?new String[]{}: desensitizedList.toArray(new String[0]);
        ExportParams exportParams = new ExportParams(title, title);
        //是否添加序号
        exportParams.setAddIndex(!Arrays.stream(exclusionsArrays).collect(Collectors.toList()).contains("序号"));
        exportParams.setSecondTitle(StringUtils.isNotEmpty(userName)?String.format("创建人：%s",userName):null);
        exportParams.setStyle(ExcelExportStylerBorderImpl.class);
        exportParams.setExclusions(exclusionsArrays);
        AbstractExcelDictHandler bean = SpringUtil.getBean(AbstractExcelDictHandler.class);
        exportParams.setDictHandler(bean);//插入字典处理类
        ExcelDataHandlerImpl<T> excelDataHandler = new ExcelDataHandlerImpl<T>();
        excelDataHandler.setNeedHandlerFields(desensitizedArrays);
        exportParams.setDataHandler(excelDataHandler);//数据加敏处理
        Workbook workbook = ChangeExcelExportUtil.exportExcel(exportParams,pojoClass,dataList);
        out(workbook,title,response);
    }


    /**
     * @param c 导出实体类
     * @return 返回可导出字典 与 可脱敏字段
     */
    public static List<NotNullMap> exportEntityInfo(Class<?> c){
        LinkedList<NotNullMap> list = new LinkedList<>();
        //添加序号字段
        NotNullMap indexMap = new NotNullMap();
        //字段编号
        indexMap.putString("field","indexOrder");
        //字段名词
        indexMap.putString("fieldName","序号");
        //是否可脱敏
        indexMap.put("isDesensitized",false);
        list.add(indexMap);
        for (Field field : PoiPublicUtil.getClassFields(c)) {
            NotNullMap map = new NotNullMap();
            Excel excel = field.getAnnotation(Excel.class);
            if(excel == null){
                continue;
            }
            //字段编号
            map.putString("field",field.getName());
            //字段名词
            map.putString("fieldName",excel.name());
            //是否可脱敏
            map.put("isDesensitized",false);
            Desensitized desensitized = field.getAnnotation(Desensitized.class);
            if(desensitized != null){
                map.put("isDesensitized",true);
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 导出
     */
    private static void out(Workbook workbook, String title, HttpServletResponse response){
        if (workbook instanceof HSSFWorkbook) {
            title = title + ".xls";
        } else {
            title = title + ".xlsx";
        }
        response.setHeader("content-disposition", WebFilenameUtils.disposition(title));
        try (ServletOutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
