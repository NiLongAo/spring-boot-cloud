package cn.com.tzy.springbootstartersmscore.client;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmscore.http.SmsHttpClient;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class YtxSmsClient extends SmsHttpClient {
    public  static final YtxSmsClient INSTANCE = new YtxSmsClient();
    private static final String SERVER_IP = "app.cloopen.com";
    private static final String SERVER_PORT = "8883";
    private static final String APP_ID = "f0fca3997842f3e30178826ac54b08a4";

    public YtxSmsClient() {
        this.smsType =  SmsTypeEnum.YTX.getValue();
    }

    @Override
    public Result send(SmsModel account, Param param) {
        try {
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(SERVER_IP, SERVER_PORT);
            sdk.setAccount(account.getAccount(), account.getPassword());
            sdk.setAppId(APP_ID);
            sdk.setBodyType(BodyType.Type_JSON);
            String to = param.mobile;
            String templateId = parseTemplateCode(param.templateCode);
            Map<String, Object> vairables = AppUtils.decodeJson3(param.vairable, LinkedHashMap.class);
            String[] datas = new String[vairables.size()];
            int i = 0;
            for (Object value : vairables.values()) {
                datas[i] = String.valueOf(value);
                i++;
            }
            //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
            String reqId = String.valueOf(param.id);
//            String subAppend = String.valueOf(param.id);
            if(log.isDebugEnabled()) {
                log.debug("send message, mobile: {}, content: {}", param.mobile, param.content);
            }

            Map<String, Object> resultData = sdk.sendTemplateSMS(to, templateId, datas, null, reqId);
            Result result = new Result();
            result.id = account.getId();
            if ("000000".equals(resultData.get("statusCode"))) {
                result.success = true;
                //正常返回输出data包体信息（map）
                //HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
                if(log.isDebugEnabled()) {
                    log.debug("send {} success", param.id);
                }
            } else {
                //异常返回输出错误码和错误信息
                result.success = false;
                result.message = String.format("code: %s, message: %s", resultData.get("statusCode"), resultData.get("statusMsg"));
                log.error("send {} failure, {}", param.id, result.message);
            }

            return result;
        } catch (Exception e) {
            log.error("send {} failure, {}", param.id, e.getMessage());
            return Result.FAIL;
        }
    }

    public static void main(String[] args) {
//         sdk.setAccount("f0fca3997842f3e30178826ac50208a2", "adbb31ec781146008f9c247806aa5a36");
//         sdk.setAppId("f0fca3997842f3e30178826ac54b08a4");
//         SmsConfig smsConfigInfo = new SmsConfig();
//         smsConfigInfo.setId(1);
//         smsConfigInfo.setAccount("f0fca3997842f3e30178826ac50208a2");
//         smsConfigInfo.setPassword("adbb31ec781146008f9c247806aa5a36");
//         Param param = new Param();
//         param.id = 9;
//         param.content = "尊敬的{test}，您当前使用的电池电量过低，请尽快更换！";
//         param.templateCode = "953512";
//         param.mobile = "13675608767";
//         param.vairable = "{\"name\":\"test\"}";
//         YtxSmsClient.INSTANCE.send(smsConfigInfo, param);
    }

}
