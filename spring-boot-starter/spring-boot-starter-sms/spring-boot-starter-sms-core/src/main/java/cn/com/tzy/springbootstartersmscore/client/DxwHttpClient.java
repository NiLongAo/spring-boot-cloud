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
public class DxwHttpClient extends SmsHttpClient {


    public static final DxwHttpClient INSTANCE = new DxwHttpClient();

    static final String SERVICE_URL = "http://web.duanxinwang.cc/asmx/smsservice.aspx";

    static final String CHARSET = "UTF-8";

    public DxwHttpClient() {
        this.smsType = SmsTypeEnum.DXW.getValue();
    }

    @Override
    public Result send(SmsModel account, Param param) {

        List<NameValuePair> formParam = new ArrayList<NameValuePair>();
        formParam.add(new NameValuePair("name", account.getAccount()));
        formParam.add(new NameValuePair("pwd", account.getPassword()));
        formParam.add(new NameValuePair("mobile", param.mobile));
        formParam.add(new NameValuePair("content", handleSign(account, param.content)));
        formParam.add(new NameValuePair("type", "pt"));
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
            if(result.httpContent.startsWith("0,")) {
                result.success = true;

            } else if(result.httpContent.startsWith("1,")) {
                result.success = false;
                result.message = "含有敏感词汇";

            } else if(result.httpContent.startsWith("2,")) {
                result.success = false;
                result.message = "余额不足";

            } else if(result.httpContent.startsWith("3,")) {
                result.success = false;
                result.message = "没有号码";

            } else if(result.httpContent.startsWith("4,")) {
                result.success = false;
                result.message = "包含sql语句";

            } else if(result.httpContent.startsWith("10,")) {
                result.success = false;
                result.message = "账号不存在";

            } else if(result.httpContent.startsWith("11,")) {
                result.success = false;
                result.message = "账号注销";

            } else if(result.httpContent.startsWith("12,")) {
                result.success = false;
                result.message = "账号停用";

            } else if(result.httpContent.startsWith("13,")) {
                result.success = false;
                result.message = "IP鉴权失败";

            } else if(result.httpContent.startsWith("14,")) {
                result.success = false;
                result.message = "格式错误";


            } else if(result.httpContent.startsWith("-1,")) {
                result.success = false;
                result.message = "系统异常";

            } else {
                result.success = false;
                result.message = result.httpContent;
            }
        }
    }
}
