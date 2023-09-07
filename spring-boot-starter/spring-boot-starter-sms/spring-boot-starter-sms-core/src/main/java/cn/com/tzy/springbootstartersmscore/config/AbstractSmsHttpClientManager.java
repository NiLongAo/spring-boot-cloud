package cn.com.tzy.springbootstartersmscore.config;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstartersmsbasic.demo.SendModel;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.MessageTemplate;
import cn.com.tzy.springbootstartersmscore.client.*;
import cn.com.tzy.springbootstartersmsbasic.common.SmsConstant;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractSmsHttpClientManager implements SmsHttpClientManager {

    @Override
    public Result send(SendModel sendModel) {
        List<SmsModel> accountList = findSmsModelList(sendModel.getType());
        if (accountList.isEmpty()) {
            return null;
        }
        Result result;
        int size = accountList.size();
        if (size == 1) {
            SmsModel account = accountList.get(0);
            MessageTemplate messageTemplate = findMobileMessageTemplateLast(account.getId(), sendModel.getType());
            Param param = createParam(sendModel, messageTemplate);
            result = send(account, param);
        } else {
            int index = (int) (Math.random() * size);
            SmsModel account = accountList.get(index);
            MessageTemplate messageTemplate = findMobileMessageTemplateLast(account.getId(), sendModel.getType());
            Param param = createParam(sendModel, messageTemplate);
            result = send(account, param);
        }
        if (result != null && !result.success) {
            for (SmsModel e : accountList) {
                MessageTemplate mobileMessageTemplate = findMobileMessageTemplateLast(e.getId(), sendModel.getType());
                Param param = createParam(sendModel, mobileMessageTemplate);
                result = send(e, param);
                if (result != null && result.success) {
                    break;
                }
            }
        }
        return result;
    }



    private Result send(SmsModel account, Param param) {
        Result result = null;
        if (account.getSmsType() == SmsTypeEnum.DXW.getValue()) {
            result = DxwHttpClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.DXW.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.CLW.getValue()) {
            result = ClwHttpClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.CLW.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.WND.getValue()) {
            result = WndHttpClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.WND.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.SWLH.getValue()) {
            result = SwlhHttpClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.SWLH.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.ALYDY.getValue()) {
            result = AlydyClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.ALYDY.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.WYYD.getValue()) {
            result = WyydClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.WYYD.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.YTX.getValue()) {
            result = YtxSmsClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.YTX.getValue();
        } else if (account.getSmsType() == SmsTypeEnum.TXY.getValue()) {
            result = TxySmsClient.INSTANCE.send(account, param);
            result.type =  SmsTypeEnum.TXY.getValue();
        }
        if(result != null){
            result.content =param.content;
            result.templateType =param.templateType;
            result.templateCode =param.templateCode;
            result.vairable =param.vairable;
            result.verificationCode =param.verificationCode;
            result.redisTime =param.redisTime;
        }
        return result;
    }

    @SneakyThrows
    private Param createParam(SendModel sendModel, MessageTemplate messageTemplate){
        Param param = new Param();
        param.mobile = sendModel.getMobile();
        param.templateType = messageTemplate.getType();
        param.templateCode = messageTemplate.getCode();
        param.content = messageTemplate.getContent();
        if(StringUtils.isNotEmpty(messageTemplate.getVariable())){
            Map<String, Object> vairables = AppUtils.decodeJson3(messageTemplate.getVariable(), LinkedHashMap.class);
            //验证码
            if(messageTemplate.getVariable().contains(SmsConstant.VERIFICATION_CODE)){
                Object verificationCode = vairables.get(SmsConstant.VERIFICATION_CODE);
                if(Objects.isNull(verificationCode) || StringUtils.isEmpty(String.valueOf(verificationCode))){
                    verificationCode = (int) ((Math.random() * 900000) + 100000);
                    vairables.put(SmsConstant.VERIFICATION_CODE,verificationCode);
                }
                param.verificationCode = String.valueOf(verificationCode);
            }
            //缓存时间
            if(messageTemplate.getVariable().contains(SmsConstant.REDIS_CODE)){
                Object redisCode = vairables.get(SmsConstant.REDIS_CODE);
                if(Objects.isNull(redisCode) || StringUtils.isEmpty(String.valueOf(redisCode))){
                    redisCode = SmsConstant.REDIS_TIME;
                    vairables.put(SmsConstant.REDIS_CODE,redisCode);
                }
                param.redisTime = Integer.valueOf(String.valueOf(redisCode));
            }

            String[] k = new String[vairables.size()];
            String[] v = new String[vairables.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : vairables.entrySet()) {
                k[i] = entry.getKey();
                v[i] = String.valueOf(entry.getValue());
                i++;
            }
            param.vairable = AppUtils.encodeJson(vairables);
            param.content = StringUtils.replaceEach(messageTemplate.getContent(),k,v);
        }

        return param;
    }


    /**
     * 获取短信配置
     * @param smsModelType 短信类型
     * @return
     */
    protected abstract List<SmsModel> findSmsModelList(Integer smsModelType);

    /**
     * 获取短信模板
     * @param smsModelId 短信配置主键
     * @param smsModelType 短信类型
     * @return
     */
    protected abstract MessageTemplate findMobileMessageTemplateLast(Integer smsModelId, Integer smsModelType);
}
