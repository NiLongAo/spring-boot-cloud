package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootbean.mapper.sql.DictionaryTypeMapper;
import cn.com.tzy.springbootentity.dome.sys.DictionaryType;
import cn.com.tzy.springbootbean.service.api.DictionaryTypeService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DictionaryTypeServiceImpl extends ServiceImpl<DictionaryTypeMapper, DictionaryType> implements DictionaryTypeService{


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(String id, String code, String name, Integer status) {
        DictionaryType dictionaryType = null;
        if(id != null){
            dictionaryType = baseMapper.selectById(id);
            if(dictionaryType == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"未获取到字典类型信息");
            }
        }else {
            dictionaryType = new DictionaryType();
        }
        if(StringUtils.isEmpty(ConstEnum.Flag.getName(status))){
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存状态错误请检查");
        }
        dictionaryType.setCode(code);
        dictionaryType.setName(name);
        dictionaryType.setStatus(status);
        boolean b = super.saveOrUpdate(dictionaryType);
        if(b){
            return  RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }

    }
}
