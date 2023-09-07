package cn.com.tzy.springbootwebapi.config.poi.dict;

import cn.com.tzy.springbootcomm.common.model.DictModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.DictionaryItem;
import cn.com.tzy.springbootfeignbean.api.sys.DictionaryItemServiceFeign;
import cn.com.tzy.springbootfeignbean.api.sys.TableServiceFeign;
import cn.com.tzy.springbootstarterautopoi.handler.AbstractExcelDictHandler;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出字典类
 */
@Component
public class ExcelDictHandler extends AbstractExcelDictHandler {

    @Autowired
    private DictionaryItemServiceFeign dictionaryItemServiceFeign;
    @Autowired
    private TableServiceFeign tableServiceFeign;

    /**
     * 服务获取字典项
     * @param clientName 服务code
     * @param dictCode 字典code
     * @return
     */
    @Override
    public List<DictModel> getDictItemName(String clientName, String dictCode) {
        RestResult<?> itemList = dictionaryItemServiceFeign.findItemList(dictCode);
        List<DictionaryItem> dictionaryItems = JSONUtil.toList(JSONUtil.toJsonStr(itemList.getData()), DictionaryItem.class);
        return dictionaryItems.stream().map(o->DictModel.builder().text(o.getName()).value(o.getValue()).build()).collect(Collectors.toList());
    }

    /**
     *
     * @param clientName 服务code
     * @param table 表名
     * @param text 展示名
     * @param code 索引名
     * @return
     */
    @Override
    public List<DictModel> getTable(String clientName, String table, String text, String code) {
        List<DictModel> dictModels= new ArrayList<>();
        RestResult<?> restResult;
        switch (clientName){
            case "bean":
            default:
                restResult = tableServiceFeign.selectDiceData(table, text, Integer.valueOf(code));
                dictModels = JSONUtil.toList(JSONUtil.toJsonStr(restResult.getData()), DictModel.class);
                break;
        }
        return dictModels;
    }
}
