package cn.com.tzy.springbootstartersmscore.client;


import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmscore.http.SmsHttpClient;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 腾讯云短信发送
 */
@Log4j2
public class TxySmsClient extends SmsHttpClient {
    public static final TxySmsClient INSTANCE = new TxySmsClient();
    private static final String SDK_APP_ID = "1400580395";


    public TxySmsClient() {
        this.smsType = SmsTypeEnum.TXY.getValue();
    }

    @Override
    public Result send(SmsModel account, Param param) {
        if(log.isDebugEnabled()) {
            log.debug("param : {}", ReflectionToStringBuilder.toString(param, ToStringStyle.MULTI_LINE_STYLE));
        }
        Result result = new Result();
        result.id = account.getId();
        String templateId = parseTemplateCode(param.templateCode);
        try{
             /* 必要步骤：
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
             * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
             * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
             * 以免泄露密钥对危及你的财产安全。
             * CAM密匙查询: https://console.cloud.tencent.com/cam/capi*/
            Credential cred = new Credential(account.getAccount(), account.getPassword());
            /* 实例化要请求产品(以sms为例)的client对象
             * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
            SmsClient client = new SmsClient(cred, "ap-guangzhou");
            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 你可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
            SendSmsRequest req = new SendSmsRequest();

            /* 填充请求参数,这里request对象的成员变量即对应接口的入参
             * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
             * 基本类型的设置:
             * 帮助链接：
             * 短信控制台: https://console.cloud.tencent.com/smsv2
             * sms helper: https://cloud.tencent.com/document/product/382/3773 */
            /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
            req.setSmsSdkAppId(SDK_APP_ID);
            /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
            req.setSignName(account.getSign());
            /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
            req.setTemplateId(templateId);
            /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
            String[] phoneNumberSet = {param.mobile};
            req.setPhoneNumberSet(phoneNumberSet);
            /* 模板参数: 若无模板参数，则设置为空 */
            Map<String, Object> vairables = AppUtils.decodeJson3(param.vairable, LinkedHashMap.class);
            String[] templateParamSet = new String[vairables.size()];
            int i = 0;
            for (Object value : vairables.values()) {
                templateParamSet[i] = String.valueOf(value);
                i++;
            }
            req.setTemplateParamSet(templateParamSet);
            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse sendSmsResponse = client.SendSms(req);
            if(sendSmsResponse != null && sendSmsResponse.getSendStatusSet() != null && sendSmsResponse.getSendStatusSet().length > 0) {
                SendStatus sendStatus = sendSmsResponse.getSendStatusSet()[0];
                if(sendStatus.getCode() != null
                        && sendStatus.getCode().equals("Ok")) {
                    result.success = true;
                    result.msgId = sendSmsResponse.getRequestId();
                    if(log.isDebugEnabled()) {
                        log.debug("send {} success", param.templateCode);
                    }
                    //请求成功
                } else {
                    result.success = false;
                    result.message = String.format("code: %s, message: %s", sendStatus.getCode(), sendStatus.getMessage());

                    log.error("send {} failure, {}", param.templateCode, result.message);
                }
            } else {
                result.success = false;
                result.message = "response is null";
                log.error("send {} failure, {}", param.templateCode, result.message);
            }
        }catch (TencentCloudSDKException e){
            e.printStackTrace();
        }
        return result;
    }

  // public static void main(String[] args) {
  //     SmsConfig con = new SmsConfig();
  //     con.setId(1);
  //     con.setAccount("AKIDVotiHacaIkvqWyAWdU5QUIsELCn9F6k9");
  //     con.setPassword("DBtLYUBcniPq32nJbeS0IK6ctufAPO7b");
  //     con.setSign("仝泽勇我的JAVA学习记");
  //     Param param = new Param();
  //     param.mobile="18789432816";
  //     param.content="{code}为您的登录验证码，请于{num}分钟内填写，如非本人操作，请忽略本短信。";
  //     param.templateCode="1146534";
  //     param.vairable = "{\"code\":\"1516\",\"num\":\"10\"}";
  //     Result send = INSTANCE.send(con, param);
  //     System.out.println(send);
  // }
}
