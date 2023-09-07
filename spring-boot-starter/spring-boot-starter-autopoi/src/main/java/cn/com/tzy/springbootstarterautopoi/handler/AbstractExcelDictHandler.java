package cn.com.tzy.springbootstarterautopoi.handler;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.com.tzy.springbootcomm.common.model.DictModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实现字典获取方案
 */
public abstract class AbstractExcelDictHandler implements IExcelDictHandler {

    //dict 格式  1. 服务code.字典code  2. 服务code.表名:展示名:索引名

    private final String split = ":";
    private final String split2 = "\\.";

    @Override
    public List<Map> getList(String dict) {
        List<DictModel> dictList = getDictList(dict);
        return dictList.stream().map(o -> new HashMap<String, String>() {{
            put("dictValue", o.getText());
            put("dictName", o.getText());
        }}).collect(Collectors.toList());
    }

    @Override
    public String toName(String dict, Object obj, String name, Object value) {
        List<DictModel> dictList = getDictList(dict);
        Map<String, String> collect = dictList.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        return collect.get(String.valueOf(value));
    }

    @Override
    public String toValue(String dict, Object obj, String name, Object value) {
        List<DictModel> dictList = getDictList(dict);
        Map<String, String> collect = dictList.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
        return collect.get(String.valueOf(value));
    }

    /**
     * 获取字典项
     * @param dict
     * @return
     */
    private List<DictModel> getDictList(String dict){
        String[] split1 = dict.split(split);
        String[] split3 = split1[0].split(split2);
        List<DictModel> models= new ArrayList<>();
        if(split1.length > 3){
            String clientName = split3.length >1 ? split3[0]:"";
            String table = split3.length >1 ? split3[1]:split3[0];
            String text = split1[1];
            String code =split1[2];
            models =  getTable(clientName,table,text,code);
        }else {
            String clientName = split3.length >1 ? split3[0]:"";
            String dictCode  = split3.length >1 ? split3[1]:split3[0];
            models = getDictItemName(clientName,dictCode);
        }
        return models;
    }


    /**
     * 服务获取字典项
     * @param clientName 服务code
     * @param dictCode 字典code
     * @return
     */
    public abstract List<DictModel> getDictItemName(String clientName, String dictCode);

    /**
     *
     * @param clientName 服务code
     * @param table 表名
     * @param text 展示名
     * @param code 索引名
     * @return
     */
    public abstract List<DictModel> getTable(String clientName,String table,String text,String code);
}
