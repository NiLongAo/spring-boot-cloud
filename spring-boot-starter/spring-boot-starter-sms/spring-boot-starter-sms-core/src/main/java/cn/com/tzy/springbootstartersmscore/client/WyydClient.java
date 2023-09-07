package cn.com.tzy.springbootstartersmscore.client;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmscore.http.SmsHttpClient;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import cn.com.tzy.springbootstartersmscore.utils.HttpClientUtils;
import cn.com.tzy.springbootstartersmscore.utils.SignatureUtils;
import lombok.extern.log4j.Log4j2;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Log4j2
public class WyydClient extends SmsHttpClient {

    public static final WyydClient INSTANCE = new WyydClient();

    /** 业务ID，易盾根据产品业务特点分配 */
    private final static String BUSINESSID = "682dadd1aa62494281fe97f3e2aedb00";
    /** 本机认证服务身份证实人认证在线检测接口地址 */
    static final String SERVICE_URL = "https://sms.dun.163.com/v2/sendsms";


    public WyydClient() {
        this.smsType =  SmsTypeEnum.WYYD.getValue();
    }

    @Override
    public Result send(SmsModel account, Param param) {
        if(log.isDebugEnabled()) {
            log.debug("send message, mobile: {}, content: {}", param.mobile, param.content);
        }

        Map<String, String> params = new HashMap<String, String>();
        // 1.设置公共参数
        params.put("secretId", account.getAccount());
        params.put("businessId", BUSINESSID);
        params.put("version", "v2");
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(new Random().nextInt()));

        // 2.设置私有参数d
        params.put("mobile", param.mobile);
        params.put("templateId", parseTemplateCode(param.templateCode));
        params.put("params", param.vairable);
        params.put("paramType", "json");

        // 3.生成签名信息
        String signature = null;
        try {
            signature = SignatureUtils.genSignature(account.getPassword(), params);
        } catch (UnsupportedEncodingException e) {
            log.error("send message fail 签名异常", e);
            return Result.FAIL;
        }
        params.put("signature", signature);

        // 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");
        HttpClientUtils.HttpResp httpResp = null;
        try {
            httpResp = HttpClientUtils.post(SERVICE_URL,params,header);
            if(httpResp != null && httpResp.status == 200) {
                //5.解析报文返回
                Map map = (Map) AppUtils.decodeJson(httpResp.content, Map.class);
                if(map.get("code") == null){
                    log.error("send message fail 返回code异常 {}", map);
                    return Result.FAIL;
                }

                Integer code = Integer.valueOf(map.get("code").toString());
                String msg = map.get("msg").toString();
                Map data = (Map) map.get("data");
                if (code == 200) { //正确码
                    Integer codeResult = Integer.valueOf(data.get("result").toString());
                    String requestId = data.get("requestId").toString();
                    if (codeResult == 200) {
                        Result result = new Result();
                        result.id = account.getId();
                        result.msgId = requestId;
                        result.success = true;

                        log.error("send success requestId {}", requestId);

                        return result;
                    } else {
                        log.error("send message fail data 返回错误result {} message {}", codeResult, msg);
                    }
                } else {
                    log.error("send message fail 返回错误code {} message {}", code, msg);
                }
            } else {
                log.error("send message fail  http请求返回异常 {} ", httpResp);
            }
        } catch (Exception e) {
            log.error("send message fail {}", e);
        }
        return Result.FAIL;
    }

    public static void main(String[] args) {
        // SmsConfig account = new SmsConfig();
        // account.setId(1);
        // account.setAccount("19d88a70c4a473f2706b2d1f6834bd7f");
        // account.setPassword("0fa2f8ec31fb33d6c356b93cb342812e");
        // Param param = new Param();
        // param.mobile = "13777351251";
        // param.content = "可以测试通过吗";
        // param.vairable = "{\"authCode\":\"7781\"}";
        // param.templateCode = "6:SMS_83975030,7:13824";
        // Result result = WyydClient.INSTANCE.send(account, param);
        // System.out.println("result ：" + result.msgId);
    }
}
