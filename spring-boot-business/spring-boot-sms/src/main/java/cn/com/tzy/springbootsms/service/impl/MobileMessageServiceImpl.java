package cn.com.tzy.springbootsms.service.impl;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.MobileMessage;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;
import cn.com.tzy.springbootsms.mapper.MobileMessageMapper;
import cn.com.tzy.springbootsms.config.sms.SmsSendManager;
import cn.com.tzy.springbootsms.service.MobileMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MobileMessageServiceImpl extends ServiceImpl<MobileMessageMapper, MobileMessage> implements MobileMessageService {
    @Autowired
    SmsSendManager smsSendManager;

    @Override
    public PageResult findPage(MobileMessageParam param) {
        int total = baseMapper.findPageCount(param);
        List<MobileMessage> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putString("content", obj.getContent());
            map.putString("mobile", obj.getMobile());
            map.putInteger("status", obj.getStatus());
            map.putInteger("type", obj.getType());
            map.putDateTime("handleTime", obj.getHandleTime());
            map.putDateTime("createTime", obj.getCreateTime());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> detail(Long id) {
        MobileMessage mobileMessage = baseMapper.selectById(id);
        if (mobileMessage == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "未获取短信信息");
        }
        NotNullMap map = new NotNullMap();
        map.putLong("id", mobileMessage.getId());
        map.putString("content", mobileMessage.getContent());
        map.putString("mobile", mobileMessage.getMobile());
        map.putInteger("status", mobileMessage.getStatus());
        map.putInteger("type", mobileMessage.getType());
        map.putDateTime("handleTime", mobileMessage.getHandleTime());
        map.putDateTime("createTime", mobileMessage.getCreateTime());
        return RestResult.result(RespCode.CODE_0.getValue(), null, map);
    }

}

