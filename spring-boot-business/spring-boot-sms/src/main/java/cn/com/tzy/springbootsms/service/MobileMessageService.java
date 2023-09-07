package cn.com.tzy.springbootsms.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.MobileMessage;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MobileMessageService extends IService<MobileMessage> {

    PageResult findPage(MobileMessageParam param);

    RestResult<?> detail(Long id);


}

