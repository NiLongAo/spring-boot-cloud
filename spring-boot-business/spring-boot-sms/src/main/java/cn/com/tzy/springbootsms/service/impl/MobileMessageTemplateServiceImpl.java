package cn.com.tzy.springbootsms.service.impl;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.MobileMessageTemplate;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import cn.com.tzy.springbootsms.mapper.MobileMessageTemplateMapper;
import cn.com.tzy.springbootsms.service.MobileMessageTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MobileMessageTemplateServiceImpl extends ServiceImpl<MobileMessageTemplateMapper, MobileMessageTemplate> implements MobileMessageTemplateService {


    @Override
    public PageResult findPage(MobileMessageTemplateParam param) {
        int total = baseMapper.findPageCount(param);
        List<MobileMessageTemplate> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putInteger("id", obj.getId());
            map.putString("title", obj.getTitle());
            map.putInteger("type", obj.getType());
            map.putString("content", obj.getContent());
            map.putString("receiver", obj.getReceiver());
            map.putString("variable", obj.getVariable());
            map.putString("code", obj.getCode());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> insert(MobileMessageTemplateParam param) {
        MobileMessageTemplate entity = new MobileMessageTemplate();
        entity.setConfigId(param.configId);
        entity.setType(param.type);
        entity.setTitle(param.title);
        entity.setContent(param.content);
        entity.setReceiver(param.receiver);
        entity.setVariable(param.variable);
        entity.setCode(param.code);
        baseMapper.insert(entity);
        return RestResult.result(RespCode.CODE_0.getValue(), "添加成功");
    }

    @Override
    public RestResult<?> update(MobileMessageTemplateParam param) {
        if (param.id == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信模板编号");
        }
        MobileMessageTemplate entity = baseMapper.selectById(param.id);

        if (entity == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信模板信息");
        }
        entity.setConfigId(param.configId);
        entity.setType(param.type);
        entity.setTitle(param.title);
        entity.setContent(param.content);
        entity.setReceiver(param.receiver);
        entity.setVariable(param.variable);
        entity.setCode(param.code);
        baseMapper.updateById(entity);
        return RestResult.result(RespCode.CODE_0.getValue(), "修改成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        MobileMessageTemplate entity = baseMapper.selectById(id);
        if (entity == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取到短信模板信息");
        }
        baseMapper.deleteById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<?> detail(Long id) {
        MobileMessageTemplate entity = baseMapper.selectById(id);
        if (entity == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取短信信息");
        }
        NotNullMap map = new NotNullMap();
        map.putInteger("id", entity.getId());
        map.putInteger("configId", entity.getConfigId());
        map.putString("code", entity.getCode());
        map.putInteger("type", entity.getType());
        map.putString("title", entity.getTitle());
        map.putString("content", entity.getContent());
        map.putString("receiver", entity.getReceiver());
        map.putString("variable", entity.getVariable());
        return RestResult.result(RespCode.CODE_0.getValue(), null, map);
    }

    @Override
    public MobileMessageTemplate findLast(Integer configId, Integer type) {
        return baseMapper.findLast(configId,type);
    }
}


