package cn.com.tzy.springbootstarterautopoi.handler;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataHandler;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.com.tzy.springbootcomm.annotation.Desensitized;
import cn.com.tzy.springbootcomm.utils.DesensitizedFormatterUtils;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 敏感字符处理
 * @param <T>
 */
public class ExcelDataHandlerImpl <T> implements IExcelDataHandler<T> {
    private String[] needHandlerFields;

    /**
     * 导出时敏感字段处理
     */
    @Override
    public Object exportHandler(T obj, String name, Object value) {
        Field[] fileds   = PoiPublicUtil.getClassFields(obj.getClass());
        for (Field filed : fileds) {
            Excel excel = filed.getAnnotation(Excel.class);
            if(name.equals(excel.name())){
                Desensitized desensitized = filed.getAnnotation(Desensitized.class);
                if(desensitized == null){
                    continue;
                }
                DesensitizedFormatterUtils desensitizedFormatterUtils = new DesensitizedFormatterUtils(desensitized.type());
                return desensitizedFormatterUtils.parse(String.valueOf(value));
            }

        }


        return value;
    }

    @Override
    public String[] getNeedHandlerFields() {
        return needHandlerFields;
    }

    @Override
    public Object importHandler(T obj, String name, Object value) {
        return value;
    }

    @Override
    public void setNeedHandlerFields(String[] needHandlerFields) {
        this.needHandlerFields = needHandlerFields;
    }

    @Override
    public void setMapValue(Map<String, Object> map, String originKey, Object value) {
        map.put(originKey, value);
    }

    @Override
    public Hyperlink getHyperlink(CreationHelper creationHelper, T obj, String name, Object value) {
        return null;
    }
}
