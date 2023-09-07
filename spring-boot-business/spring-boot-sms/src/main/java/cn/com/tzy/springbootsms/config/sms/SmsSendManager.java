package cn.com.tzy.springbootsms.config.sms;

import cn.com.tzy.spingbootstartermybatis.core.tenant.utils.TenantUtils;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.param.sms.SendParam;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.MobileMessage;
import cn.com.tzy.springbootentity.dome.sms.SmsConfig;
import cn.com.tzy.springbootsms.service.MobileMessageService;
import cn.com.tzy.springbootsms.service.MobileMessageTemplateService;
import cn.com.tzy.springbootsms.service.SmsConfigService;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersmsbasic.demo.MessageTemplate;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import cn.com.tzy.springbootstartersmscore.config.AbstractSmsHttpClientManager;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.MobileMessageType;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.SmsCodeTokenConstant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//短信发送
@Log4j2
@Component
public class SmsSendManager extends AbstractSmsHttpClientManager {

    @Autowired
    private MobileMessageService mobileMessageService;
    @Autowired
    private SmsConfigService smsConfigService;
    @Autowired
    private MobileMessageTemplateService mobileMessageTemplateService;

    /**
     * @return
     */
    //防止重复发送
    public synchronized RestResult<?> smsSend(SendParam param){
        RestResult<Map> restResult = new RestResult();
        String key = String.format("%s%s_%s", SmsCodeTokenConstant.VERIFICATION_CODE_PREFIX, param.getType(), param.getMobile());
        Map data = new HashMap();
        if(RedisUtils.hasKey(key)){
            long expire = RedisUtils.getExpire(key);
            int s = (int) (expire % 60);
            int m = (int) (expire / 60 % 60);
            data.put("expire",expire);
            return RestResult.result(RespCode.CODE_0.getValue(),"已发送短信，请于" + m + "分" + s + "秒" + "后重试。",data);
        }
        Result result = super.send(param);
        if(result == null) {
            restResult.setCode(RespCode.CODE_2.getValue());
            restResult.setMessage("没有可用的短信接口");
        } else {
            restResult.setCode(result.success ? (short) RespCode.CODE_0.getValue() : RespCode.CODE_2.getValue());
            restResult.setMessage(result.message);
            if(result.success){
                if(result.redisTime > 0){
                    data.put("expire",result.redisTime*60);
                }
                restResult.setData(data);
                handleTemplateType(param,result);
            }
            //TODO 这里如果用 维纳多 是收不到消息回执的
            TenantUtils.execute(param.getTenantId(),()->{
                insertMobileMessage(param.getMobile(), result.content, result.success, result.id, result.msgId, result.type,  result.vairable, result.templateCode);
            });
        }
        return restResult;
    }

    private void handleTemplateType(SendParam param, Result result){
        if(result.templateType != null ){
            if (result.templateType == MobileMessageType.REGISTER_VERIFICATION_CODE.getValue()
                    || result.templateType == MobileMessageType.LOGIN_VERIFICATION_CODE.getValue()
            ) {
                if(result.redisTime >0 && StringUtils.isNotEmpty(result.verificationCode)){
                    RedisUtils.set(String.format("%s%s_%s", SmsCodeTokenConstant.VERIFICATION_CODE_PREFIX,param.getType(),param.getMobile()),result.verificationCode,result.redisTime*60);
                }
            }
        }
    }

    private void insertMobileMessage(String mobile, String content, boolean success, int senderId, String msgId, int type, String variable, String templateId) {
        Date now = new Date();
        MobileMessage mobileMessage = new MobileMessage();
        mobileMessage.setCreateTime(now);
        mobileMessage.setHandleTime(now);
        mobileMessage.setStatus(success ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue());
        if(success) {
            mobileMessage.setMsgId(msgId);
        }
        mobileMessage.setMobile(mobile);
        mobileMessage.setContent(content);
        mobileMessage.setSenderId(senderId);
        mobileMessage.setType(type);
        mobileMessage.setVariable(variable);
        mobileMessage.setTemplateId(templateId);
        log.info("mobile message error: {}", content);
        mobileMessageService.save(mobileMessage);
    }


    @Override
    protected List<SmsModel> findSmsModelList(Integer smsModelType) {
        List<SmsConfig> list = smsConfigService.findList(ConstEnum.Flag.YES.getValue(), smsModelType);
        return new ArrayList<>(list);
    }

    @Override
    protected MessageTemplate findMobileMessageTemplateLast(Integer smsModelId, Integer smsModelType) {
        return mobileMessageTemplateService.findLast(smsModelId,smsModelType);
    }
}
