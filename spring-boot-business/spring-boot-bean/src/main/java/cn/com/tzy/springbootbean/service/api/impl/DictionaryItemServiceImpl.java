package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.DictionaryItemMapper;
import cn.com.tzy.springbootbean.mapper.sql.DictionaryTypeMapper;
import cn.com.tzy.springbootbean.service.api.DictionaryItemService;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.DictionaryItem;
import cn.com.tzy.springbootentity.dome.sys.DictionaryType;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictionaryItemServiceImpl extends ServiceImpl<DictionaryItemMapper, DictionaryItem> implements DictionaryItemService{
    @Autowired
    DictionaryTypeMapper dictionaryTypeMapper;


    @Override
    public PageResult page(DictionaryItemParam param) {
        List<NotNullMap> data = new ArrayList<>();
        if(param.typeId == null){
            return PageResult.result(RespCode.CODE_0.getValue(), 0, null, data);
        }
        int total = baseMapper.findPageCount(param);
        List<DictionaryItem> pageResult = baseMapper.findPageResult(param);
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putString("id", obj.getId());
            map.putInteger("sort", obj.getSort());
            map.putString("name", obj.getName());
            map.putString("typeId", obj.getTypeId());
            map.putString("value", obj.getValue());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestResult<?> save(String id, String name, Integer num, String typeId, String value) {
        DictionaryItem dictionaryItem = null;
        DictionaryType dictionaryType = dictionaryTypeMapper.selectById(typeId);
        if(dictionaryType == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到字典类型信息");
        }
        if(id != null){
            dictionaryItem = baseMapper.selectById(id);
            if(dictionaryItem == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"未获取到字典条目信息");
            }
        }else {
            dictionaryItem = new  DictionaryItem();
        }
        dictionaryItem.setTypeId(typeId);
        dictionaryItem.setName(name);
        dictionaryItem.setSort(num);
        dictionaryItem.setValue(value);
        boolean b = super.saveOrUpdate(dictionaryItem);
        if(b){
            return  RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }
    }

    @Override
    public RestResult<?> findDict() {
       List<DictionaryItem>  dictList= baseMapper.findDict();
       if(dictList.isEmpty()){
           return RestResult.result(RespCode.CODE_0.getValue(),null,new HashMap<>());
       }
       Map<String, List<DictionaryItem>> collect = dictList.stream().collect(Collectors.groupingBy(DictionaryItem::getTypeId));
       Map<String,Map> map= new HashMap<>();
        for (Map.Entry<String, List<DictionaryItem>> stringListEntry : collect.entrySet()) {
            map.put(stringListEntry.getKey(),stringListEntry.getValue().stream().collect(Collectors.toMap(DictionaryItem::getValue, DictionaryItem::getName)));
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,map);
    }
}
