package cn.com.tzy.springbootapp.service.bean;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.constant.ImgConstant;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.common.info.UserPayload;
import cn.com.tzy.springbootentity.param.bean.*;
import cn.com.tzy.springbootentity.utils.EncryptUtil;
import cn.com.tzy.springbootentity.utils.VerifyUtil;
import cn.com.tzy.springbootfeignbean.api.bean.RoleServiceFeign;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootfeignbean.api.staticFile.UpLoadServiceFeign;
import cn.com.tzy.springbootfeignbean.api.sys.ConfigServiceFeign;
import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.lang.UUID;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

@Log4j2
@Service
public class UserService {

    @Autowired
    UserServiceFeign userServiceFeign;
    @Autowired
    RoleServiceFeign roleServiceFeign;
    @Autowired
    ConfigServiceFeign configServiceFeign;
    @Autowired
    OAuthUserServiceFeign oAuthUserServiceFeign;
    @Autowired
    UpLoadServiceFeign upLoadServiceFeign;


    @Value("${appClient.clientId}")
    private  String clientId;
    @Value("${appClient.clientSecret}")
    private  String clientSecret;

    /**
     * 生成图片二维码
     */
    @SneakyThrows
    public RestResult<?> getCode() {
        // 返回的数组第一个参数是生成的验证码，第二个参数是生成的图片
        String key = UUID.fastUUID().toString(true);
        AbstractCaptcha lineCaptcha = VerifyUtil.createLineCaptcha(120, 40, 4, 0);
        // 验证码缓存三分钟
        RedisUtils.set(Constant.VERIFY_CODE_PREFIX+key,lineCaptcha.getCode(),Constant.EXRP_MINUTE * 3);
        NotNullMap map = new NotNullMap();
        map.putString("key",key);
        map.putString("image",lineCaptcha.getImageBase64Data());
        return RestResult.result(RespCode.CODE_0.getValue(),null,map);
    }


    @SneakyThrows
    public RestResult<?> login(LoginParam param){
        if(param.grantType == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取登录类型");
        }
        switch (param.grantType) {
            case code:
                LoginParam.Code code = param.code;
                String VERIFY_CODE = Constant.VERIFY_CODE_PREFIX + code.key;
                if(StringUtils.isEmpty(code.verificationCode)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"请输入验证码");
                }
                if(StringUtils.isEmpty(code.username) || StringUtils.isEmpty(code.password)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"账号密码不能为空");
                }
                String username = EncryptUtil.aesDecrypt(code.username, Constant.SECRET_KEY,Constant.SECRET_IV);
                String password = EncryptUtil.aesDecrypt(code.password, Constant.SECRET_KEY,Constant.SECRET_IV);
                return oAuthUserServiceFeign.tokenCode(clientId,clientSecret,TypeEnum.WEB_ACCOUNT.getType(),"code",VERIFY_CODE,code.verificationCode,username,password);
            case refresh_token:
                LoginParam.RefreshToken refreshToken = param.refreshToken;
                if(StringUtils.isEmpty(refreshToken.refreshToken)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"未获取到刷新token");
                }
                RestResult<?> result = oAuthUserServiceFeign.refreshToken(clientId, clientSecret, TypeEnum.WEB_WX_MINI.getType(), "refresh_token", refreshToken.refreshToken);
                if(result.getCode() != RespCode.CODE_0.getValue()){
                    result.setCode(RespCode.CODE_315.getValue());
                }
                return result;
            default:
                return RestResult.result(RespCode.CODE_2.getValue(),"当前类型暂不支持登录");
        }
    }

    public RestResult<?> findLoginInfo(){
        return userServiceFeign.findLoginInfo();
    }

    public RestResult<?> logout(){
        RestResult<?> result = oAuthUserServiceFeign.logout(TypeEnum.WEB_WX_MINI.getType());
        if(result.getCode() != RespCode.CODE_0.getValue()){
            result.setCode(RespCode.CODE_315.getValue());
        }
        return result;
    }
}
