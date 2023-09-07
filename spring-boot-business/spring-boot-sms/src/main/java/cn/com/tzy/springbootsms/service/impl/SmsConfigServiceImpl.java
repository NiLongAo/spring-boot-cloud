package cn.com.tzy.springbootsms.service.impl;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.SmsConfig;
import cn.com.tzy.springbootentity.param.sms.SmsConfigParam;
import cn.com.tzy.springbootsms.mapper.SmsConfigMapper;
import cn.com.tzy.springbootsms.service.SmsConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SmsConfigServiceImpl extends ServiceImpl<SmsConfigMapper, SmsConfig> implements SmsConfigService {


    @Override
    public RestResult<?> findAll() {
        List<SmsConfig> list = baseMapper.findList(null, null);
        List<NotNullMap> data = new ArrayList<>();
        list.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putInteger("id", obj.getId());
            map.putString("configName", obj.getConfigName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(), null, data);
    }

    @Override
    public PageResult findPage(SmsConfigParam param) {
        int total = baseMapper.findPageCount(param);
        List<SmsConfig> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putInteger("id", obj.getId());
            map.putInteger("smsType", obj.getSmsType());
            map.putString("configName", obj.getConfigName());
            map.putString("account", obj.getAccount());
            map.putInteger("isActive", obj.getIsActive());
            map.putString("sign", obj.getSign());
            map.putDateTime("updateTime", obj.getUpdateTime());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> insert(SmsConfigParam param) {
        SmsConfig entity = new SmsConfig();
        entity.setSmsType(param.smsType);
        entity.setConfigName(param.configName);
        entity.setAccount(param.account);
        entity.setPassword(param.password);
        entity.setBalance(param.balance);
        entity.setIsActive(param.isActive);
        entity.setSign(param.sign);
        entity.setSignPlace(param.signPlace);
        baseMapper.insert(entity);
        return RestResult.result(RespCode.CODE_0.getValue(), "添加成功");
    }

    @Override
    public RestResult<?> update(SmsConfigParam param) {
        if (param.id == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信设置编号");
        }
        SmsConfig entity = baseMapper.selectById(param.id);
        if (entity == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信设置信息");
        }
        entity.setSmsType(param.smsType);
        entity.setConfigName(param.configName);
        entity.setAccount(param.account);
        entity.setPassword(param.password);
        entity.setBalance(param.balance);
        entity.setIsActive(param.isActive);
        entity.setSign(param.sign);
        entity.setSignPlace(param.signPlace);
        baseMapper.updateById(entity);
        return RestResult.result(RespCode.CODE_0.getValue(), "修改成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        SmsConfig entity = baseMapper.selectById(id);
        if (entity == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信设置信息");
        }
        baseMapper.deleteById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<?> detail(Long id) {
        SmsConfig entity = baseMapper.selectById(id);
        if (entity == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信设置信息");
        }
        NotNullMap map = new NotNullMap();
        map.putInteger("id", entity.getId());
        map.putInteger("smsType", entity.getSmsType());
        map.putString("configName", entity.getConfigName());
        map.putString("account", entity.getAccount());
        map.putString("password", entity.getPassword());
        map.putInteger("isActive", entity.getIsActive());
        map.putString("sign", entity.getSign());
        map.putDateTime("updateTime", entity.getUpdateTime());
        return RestResult.result(RespCode.CODE_0.getValue(), null, map);
    }

    @Override
    public List<SmsConfig> findList(Integer isActive, Integer type) {
        return baseMapper.findList(isActive,type);
    }


}

