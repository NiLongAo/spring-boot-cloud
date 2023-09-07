package cn.com.tzy.springbootstartersmscore.client;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootstartersmsbasic.common.SmsTypeEnum;
import cn.com.tzy.springbootstartersmsbasic.demo.SmsModel;
import cn.com.tzy.springbootstartersmscore.http.SmsHttpClient;
import cn.com.tzy.springbootstartersmsbasic.model.NameValuePair;
import cn.com.tzy.springbootstartersmsbasic.model.Param;
import cn.com.tzy.springbootstartersmsbasic.model.Result;
import com.sun.crypto.provider.SunJCE;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class WndHttpClient extends SmsHttpClient {

    public static final WndHttpClient INSTANCE = new WndHttpClient();

    static final String SERVICE_URL = "http://yl.mobsms.net/send/sendAnna.aspx";

    static final String CHARSET = "UTF-8";

    public WndHttpClient() {
        this.smsType =  SmsTypeEnum.WND.getValue();
    }

    public static Map<String, String> RESP_CODE_MAP = new HashMap<String, String>();
    static {
        RESP_CODE_MAP.put("6002", "用户帐号不正确");
        RESP_CODE_MAP.put("6008", "无效的手机号码");
        RESP_CODE_MAP.put("6009", "手机号码是黑名单");
        RESP_CODE_MAP.put("6010", "用户密码不正确");
        RESP_CODE_MAP.put("6011", "短信内容超过了最大长度限制");
        RESP_CODE_MAP.put("6012", "该企业用户设置了 ip 限制");
        RESP_CODE_MAP.put("6013", "该企业用户余额不足");
        RESP_CODE_MAP.put("6014", "发送短信内容不能为空");
        RESP_CODE_MAP.put("6015", "发送内容中含非法字符");
        RESP_CODE_MAP.put("6019", "账户已停机，请联系客服");
        RESP_CODE_MAP.put("6021", "扩展号码未备案");
        RESP_CODE_MAP.put("6023", "发送手机号码超过太长，已超过 300 个号码");
        RESP_CODE_MAP.put("6024", "定制时间不正确");
        RESP_CODE_MAP.put("6025", "扩展号码太长（总长度超过 20 位）");
        RESP_CODE_MAP.put("6080", "提交异常，请联系服务商解决");
        RESP_CODE_MAP.put("6085", "短信内容为空");
    }

    @Override
    public Result send(SmsModel account, Param param) {

        Request request = new Request();
        request.setDst(param.mobile);
        request.setMsg(CodecUtils.urlEncode(handleSign(account, param.content)));
        request.setName(account.getAccount());
        request.setPsw(account.getPassword());
        request.setTime("");
        request.setSender("");
        request.setSequeid(String.format("%d", param.id));

        String m = null;
        try {
            String json = AppUtils.encodeJson(request);
            JimAES jimAES = new JimAES(account.getPassword());
            m = jimAES.encodeDataAes(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<NameValuePair> formParam = new ArrayList<NameValuePair>();
        formParam.add(new NameValuePair("http.protocol.content-charset", "UTF-8"));
        formParam.add(new NameValuePair("u", account.getAccount()));
        formParam.add(new NameValuePair("p", "1"));
        formParam.add(new NameValuePair("c", "3"));
        formParam.add(new NameValuePair("m", m));

        if(log.isDebugEnabled()) {
            log.debug("send message, mobile: {}, content: {}", param.mobile, param.content);
        }

        try {
            Result result = send(SERVICE_URL, formParam, CHARSET, "GBK");
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

            parseResult(param, result);
            return result;
        } catch (IOException e) {
            log.error("发送短信错误", e);
        }
        return Result.FAIL;
    }

    private void parseResult(Param param, Result result) {
        if(result.httpCode == 200 && StringUtils.isNotEmpty(result.httpContent)) {
            try {
                Response response = (Response) AppUtils.decodeJson(result.httpContent, Response.class);
                if(response.getErrid().equals("0")) {
                    if(StringUtils.isNotEmpty(response.getSuccess()) && response.getSuccess().indexOf(param.mobile) >= 0) {
                        result.success = true;
                    } else {
                        result.success = false;
                        result.message = response.getErr();
                    }

                } else {
                    result.success = false;
                    result.message = RESP_CODE_MAP.get(response.getErrid());
                    if(StringUtils.isEmpty(result.message)) {
                        result.message = result.httpContent;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // public static void main(String[] args) {
    //     SmsConfig account = new SmsConfig();
    //     account.setId(2);
    //     account.setAccount("jyzc");
    //     account.setPassword("zc668");
    //     Param param = new Param();
    //     param.mobile = "13675608767";
    //     param.content = "这个是通过新加密接口发送的哦";
    //     Result result = WndHttpClient.INSTANCE.send(account, param);
    //     System.out.println(result.success);
    //     System.out.println(result.message);
    // }

    public static class Request {
        private String name;
        private String psw;
        private String dst;
        private String msg;
        private String time;
        private String sender;
        private String sequeid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPsw() {
            return psw;
        }

        public void setPsw(String psw) {
            this.psw = psw;
        }

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getSequeid() {
            return sequeid;
        }

        public void setSequeid(String sequeid) {
            this.sequeid = sequeid;
        }
    }

    public static class Response {
        private String num;
        private String success;
        private String faile;
        private String err;
        private String errid;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getFaile() {
            return faile;
        }

        public void setFaile(String faile) {
            this.faile = faile;
        }

        public String getErr() {
            return err;
        }

        public void setErr(String err) {
            this.err = err;
        }

        public String getErrid() {
            return errid;
        }

        public void setErrid(String errid) {
            this.errid = errid;
        }
    }

    public class JimAES
    {
        private KeyGenerator keygen;
        private SecretKey deskey;
        private Cipher c;
        private String SC = "123456";

        public JimAES(String key)
        {
            this.SC = key;
            initAes();
        }

        private void initAes()
        {
            Security.addProvider(new SunJCE());
            try
            {
                this.deskey = getKey(this.SC);
                this.c = Cipher.getInstance("AES");
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        private SecretKey getKey(String strKey)
        {
            try
            {
                if (this.keygen == null)
                {
                    this.keygen = KeyGenerator.getInstance("AES");
                    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                    secureRandom.setSeed(strKey.getBytes("utf-8"));

                    this.keygen.init(secureRandom);
                }
                return this.keygen.generateKey();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        private JimAES()
        {
            Security.addProvider(new SunJCE());
            try
            {
                this.deskey = getKey(this.SC);
                this.c = Cipher.getInstance("AES");
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public byte[] Encrytor(String str)
        {
            byte[] cipherByte = (byte[])null;
            try
            {
                this.c.init(1, this.deskey);
                byte[] srcs = str.getBytes("utf-8");
                cipherByte = this.c.doFinal(srcs);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            return cipherByte;
        }


        public String encodeDataAes(String instr)
        {
            return CodecUtils.byte2hex(Encrytor(instr));
        }
    }

    public static class CodecUtils {

        public static String urlEncode(String content) {
            try {
                return URLEncoder.encode(content, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static String byte2hex(byte[] buf)
        {
            if (buf == null) {
                return null;
            }
            int pos = 0;
            int len = buf.length;

            StringBuffer sb = new StringBuffer();
            for (int j = pos; j < len; j++)
            {
                int i = buf[j] & 0xFF;
                if (i < 16) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(i));
            }
            return sb.toString().toUpperCase();
        }
    }
}
