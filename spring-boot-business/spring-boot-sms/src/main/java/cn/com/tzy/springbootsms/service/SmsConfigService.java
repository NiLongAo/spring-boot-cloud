package cn.com.tzy.springbootsms.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.SmsConfig;
import cn.com.tzy.springbootentity.param.sms.SmsConfigParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SmsConfigService extends IService<SmsConfig> {

    RestResult<?> findAll();

    PageResult findPage(SmsConfigParam param);

    RestResult<?> insert(SmsConfigParam param);

    RestResult<?> update(SmsConfigParam param);

    RestResult<?> remove(Long id);

    RestResult<?> detail(Long id);

    List<SmsConfig> findList(Integer isActive, Integer type);

}

