package cn.com.tzy.springbootstartersmscore.client;

import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmscore.http.SmsHttpClient;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 阿里云大于短信接口
 */
@Log4j2
public class AlydyClient extends SmsHttpClient {

    public static final AlydyClient INSTANCE = new AlydyClient();

    public AlydyClient() {
        this.smsType = SmsTypeEnum.ALYDY.getValue();
    }

    @Override
    public Result send(SmsModel account, Param param) {

        Result result = new Result();
        result.id = account.getId();

        if(log.isDebugEnabled()) {
            log.debug("param : {}", ReflectionToStringBuilder.toString(param, ToStringStyle.MULTI_LINE_STYLE));
        }

        try {
            if(StringUtils.isEmpty(account.getSign())) {
                throw new IllegalArgumentException("SmsConfig or content must be contain sign");
            }

            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化ascClient需要的几个参数
            final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
            //替换成你的AK
            final String accessKeyId = account.getAccount();//你的accessKeyId,参考本文档步骤2
            final String accessKeySecret = account.getPassword();//你的accessKeySecret，参考本文档步骤2
            //初始化ascClient,暂时不支持多region
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(param.mobile);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(account.getSign());
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(parseTemplateCode(param.templateCode));
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam(param.vairable);
            //可选-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            request.setOutId(String.format("%d", param.id));

            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            if(sendSmsResponse != null) {
                if(sendSmsResponse.getCode() != null
                        && sendSmsResponse.getCode().equals("OK")) {
                    result.success = true;

                    if(log.isDebugEnabled()) {
                        log.debug("send {} success", param.id);
                    }

                    //请求成功
                } else {
                    result.success = false;
                    result.message = String.format("code: %s, message: %s", sendSmsResponse.getCode(), sendSmsResponse.getMessage());
                    log.error("send {} failure, {}", param.id, result.message);
                }
            } else {
                result.success = false;
                result.message = "response is null";
                log.error("send {} failure, {}", param.id, result.message);
            }

        } catch (Exception e) {
            log.error("send message fail", e);
            return Result.FAIL;
        }
        return result;
    }

    public static void main(String[] args) {
        // SmsConfig config = new SmsConfig();
        // config.setId(1);
        // config.setAccount("LTAILxnxu6O0s6SQ");
        // config.setPassword("YG8qOXvjgnEExgLULEqDNxkwE4Q58I");
        // config.setSign("微电Vee无限畅行");
        //
        // Param param = new Param();
        // param.mobile = "17765161842";
        // param.vairable = "{\"name\":\"潘文轩\"}";
        // param.content = "尊敬的${name}，您当前使用的电池电量过低，请尽快更换！";
        // param.templateCode = "SMS_138077606";
        //
        //
        // AlydyClient client = new AlydyClient();
        // Result result = client.send(config, param);
        // System.out.println(result.success);
        // System.out.println(result.message);
    }
}
