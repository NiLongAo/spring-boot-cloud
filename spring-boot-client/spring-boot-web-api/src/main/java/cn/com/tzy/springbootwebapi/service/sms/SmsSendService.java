package cn.com.tzy.springbootwebapi.service.sms;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootentity.param.sms.SendParam;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootfeignsms.api.sms.SmsSendServiceFegin;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.MobileMessageType;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SmsSendService {

    @Autowired
    UserServiceFeign userServiceFeign;
    @Autowired
    SmsSendServiceFegin smsSendServiceFegin;

    @GlobalTransactional(rollbackFor = Exception.class)
    public RestResult<?> send(SendParam param) throws IOException {
        //默认系统租户发送
        param.setTenantId(Constant.TENANT_ID);
        if(StringUtils.isEmpty(MobileMessageType.getName(param.getType()))){
            return RestResult.result(RespCode.CODE_2.getValue(),"发送类型错误");
        }
        //注册不需要校验用户是否存在
        if(param.getType() != MobileMessageType.REGISTER_VERIFICATION_CODE.getValue()){
            RestResult<?> result = userServiceFeign.findLoginTypeByUserInfo(LoginTypeEnum.WEB_MOBILE,param.getMobile());
            if(result.getCode() != RespCode.CODE_0.getValue()){
                return result;
            }
            SecurityBaseUser securityBaseUser = AppUtils.convertValue(result.getData(), SecurityBaseUser.class);
            param.setTenantId(securityBaseUser.getTenantId());
        }
        return smsSendServiceFegin.send(param);
    }


}
