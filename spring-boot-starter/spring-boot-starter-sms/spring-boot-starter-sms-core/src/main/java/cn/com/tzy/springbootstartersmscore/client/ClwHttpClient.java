package cn.com.tzy.springbootstartersmscore.client;

import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmscore.http.SmsHttpClient;
import cn.com.tzy.springbootstartersmsbasic.model.NameValuePair;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ClwHttpClient extends SmsHttpClient {


    public static final ClwHttpClient INSTANCE = new ClwHttpClient();

    static final String SERVICE_URL = "http://222.73.117.156:80/msg/HttpBatchSendSM";

    static final String CHARSET = "UTF-8";

    public ClwHttpClient() {
        this.smsType = SmsTypeEnum.CLW.getValue();
    }

    @Override
    public Result send(SmsModel account, Param param) {

        List<NameValuePair> formParam = new ArrayList<NameValuePair>();
        formParam.add(new NameValuePair("account", account.getAccount()));
        formParam.add(new NameValuePair("pswd", account.getPassword()));
        formParam.add(new NameValuePair("mobile", param.mobile));
        formParam.add(new NameValuePair("msg", handleSign(account, param.content)));
        formParam.add(new NameValuePair("needstatus", "true"));
        formParam.add(new NameValuePair("extno", ""));

        if(log.isDebugEnabled()) {
            log.debug("send message, mobile: {}, content: {}", param.mobile, param.content);
        }

        try {
            Result result = send(SERVICE_URL, formParam, CHARSET, CHARSET);
            if(result != null) {
                result.id = account.getId();
            }

            if(log.isDebugEnabled()) {
                if(result.httpCode / 100 == 2) {
                    log.debug("status: {}, response text: {}", result.httpCode, result.httpContent);
                } else {
                    log.error("status: {}", result.httpCode);
                }
            }

            parseResult(result);
            return result;
        } catch (IOException e) {
            log.error("发送短信错误", e);
        }
        return Result.FAIL;
    }

    private void parseResult(Result result) {
        if(result.httpCode == 200 && StringUtils.isNotEmpty(result.httpContent)) {
            String [] array = StringUtils.split(result.httpContent, "\n");
            String [] resultStatus = StringUtils.split(array[0], ",");

            if(resultStatus[1].equals("0")) {
                result.success = true;
                if(array.length > 1) {
                    result.msgId = array[1];
                }
            } else if(resultStatus[1].equals("101")) {
                result.success = false;
                result.message = "无此用户";

            } else if(resultStatus[1].equals("102")) {
                result.success = false;
                result.message = "密码错误";

            } else if(resultStatus[1].equals("103")) {
                result.success = false;
                result.message = "提交过快（提交速度超过流速限制）";

            } else if(resultStatus[1].equals("104")) {
                result.success = false;
                result.message = "系统忙（因平台侧原因，暂时无法处理提交的短信）";

            } else if(resultStatus[1].equals("105")) {
                result.success = false;
                result.message = "敏感短信（短信内容包含敏感词）";

            } else if(resultStatus[1].equals("106")) {
                result.success = false;
                result.message = "消息长度错（>536或<=0)";

            } else if(resultStatus[1].equals("107")) {
                result.success = false;
                result.message = "包含错误的手机号码";

            } else if(resultStatus[1].equals("108")) {
                result.success = false;
                result.message = "手机号码个数错（群发>50000或<=0;单发>200或<=0）";

            } else if(resultStatus[1].equals("109")) {
                result.success = false;
                result.message = "无发送额度（该用户可用短信数已使用完）";

            } else if(resultStatus[1].equals("110")) {
                result.success = false;
                result.message = "不在发送时间内";

            } else if(resultStatus[1].equals("111")) {
                result.success = false;
                result.message = "超出该账户当月发送额度限制";

            } else if(resultStatus[1].equals("112")) {
                result.success = false;
                result.message = "无此产品，用户没有订购该产品";

            } else if(resultStatus[1].equals("113")) {
                result.success = false;
                result.message = "extno格式错（非数字或者长度不对）";

            } else if(resultStatus[1].equals("115")) {
                result.success = false;
                result.message = "自动审核驳回";

            }  else if(resultStatus[1].equals("116")) {
                result.success = false;
                result.message = "签名不合法，未带签名（用户必须带签名的前提下）";

            }  else if(resultStatus[1].equals("117")) {
                result.success = false;
                result.message = "IP地址认证错,请求调用的IP地址不是系统登记的IP地址";

            }  else if(resultStatus[1].equals("118")) {
                result.success = false;
                result.message = "用户没有相应的发送权限";

            }   else if(resultStatus[1].equals("119")) {
                result.success = false;
                result.message = "用户已过期";

            }   else if(resultStatus[1].equals("120")) {
                result.success = false;
                result.message = "测试内容不是白名单";

            }  else {
                result.success = false;
                result.message = result.httpContent;
            }
        }
    }

    public static void main(String[] args) {
        // SmsConfig account = new SmsConfig();
        // account.setId(2);
        // account.setAccount("yskj666 ");
        // account.setPassword("Tch818181");
        // Param param = new Param();
        // param.mobile = "18457148548";
        // param.content = "可以测试通过吗";
        // Result result = ClwHttpClient.INSTANCE.send(account, param);
        // System.out.println("a ：" + result);
    }
}
