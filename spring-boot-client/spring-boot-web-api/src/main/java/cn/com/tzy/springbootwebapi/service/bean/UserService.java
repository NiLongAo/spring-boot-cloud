package cn.com.tzy.springbootwebapi.service.bean;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.ImgConstant;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.common.info.UserPayload;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootentity.export.ExportEntity;
import cn.com.tzy.springbootentity.export.entity.UserExportModel;
import cn.com.tzy.springbootentity.param.bean.*;
import cn.com.tzy.springbootentity.utils.EncryptUtil;
import cn.com.tzy.springbootentity.utils.VerifyUtil;
import cn.com.tzy.springbootentity.vo.bean.UserBindVo;
import cn.com.tzy.springbootfeignbean.api.bean.MiniServiceFeign;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootstarterautopoi.utils.PoiUtils;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserServiceFeign userServiceFeign;
    @Autowired
    private OAuthUserServiceFeign oAuthUserServiceFeign;
    @Autowired
    private MiniServiceFeign miniServiceFeign;



    @Value("${webApiClient.clientId}")
    private  String clientId;
    @Value("${webApiClient.clientSecret}")
    private  String clientSecret;


    @SneakyThrows
    public RestResult<?> login(LoginParam param){
        if(param.grantType == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取登录类型");
        }
        switch (param.grantType) {
            case authorization_code:
                LoginParam.AuthorizationCode authorizationCode = param.authorizationCode;
                if(StringUtils.isEmpty(authorizationCode.code)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"未获取授权码");
                }
                if(StringUtils.isEmpty(authorizationCode.redirectUri)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"未获取跳转地址");
                }
                return oAuthUserServiceFeign.tokenAuthorizationCode(clientId,clientSecret,TypeEnum.WEB_ACCOUNT.getType(), "authorization_code",authorizationCode.code,authorizationCode.redirectUri);
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
            case sms:
                LoginParam.Sms sms = param.sms;
                if(StringUtils.isEmpty(sms.phone)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"请输入手机号");
                }
                if(StringUtils.isEmpty(sms.SmsCodeCode)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"请输入验证码");
                }
                return oAuthUserServiceFeign.tokenSms(clientId,clientSecret,TypeEnum.WEB_MOBILE.getType(),"sms",sms.SmsCodeCode,sms.phone);
            case refresh_token:
                LoginParam.RefreshToken refreshToken = param.refreshToken;
                if(StringUtils.isEmpty(refreshToken.refreshToken)){
                    return RestResult.result(RespCode.CODE_2.getValue(),"未获取到刷新token");
                }
                RestResult<?> result = oAuthUserServiceFeign.refreshToken(clientId, clientSecret, TypeEnum.WEB_ACCOUNT.getType(), "refresh_token", refreshToken.refreshToken);
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
        RestResult<?> result = oAuthUserServiceFeign.logout(TypeEnum.WEB_ACCOUNT.getType());
        if(result.getCode() != RespCode.CODE_0.getValue()){
            result.setCode(RespCode.CODE_315.getValue());
        }
        return result;
    }

    //@GlobalTransactional
    public RestResult<?> findInfo(Long id){
        return userServiceFeign.getInfo(id);
    }

    public PageResult choiceUserPage(UserParam userPageModel){
        return userServiceFeign.choiceUserPage(userPageModel);
    }

    public PageResult page( UserParam userPageModel){
        return userServiceFeign.page(userPageModel);
    }


    public RestResult<?> insert(UserParam userPageModel){
        return userServiceFeign.insert(userPageModel);
    }

    public RestResult<?> update(UserParam userPageModel){
        return userServiceFeign.update(userPageModel);
    }

    public RestResult<?> delete(Long id){
        return userServiceFeign.delete(id);
    }

    /**
     * 获取web用户绑定其他端登陆账户
     */
    public RestResult<?> findUserBind(Long userId){
        List<UserBindVo> userBindVoList= new ArrayList<>();
        //查询绑定微信小程序用户
        RestResult<?> wxMiniUser = miniServiceFeign.findWebUserId(userId);
        Mini mini = BeanUtil.toBean(wxMiniUser.getData(), Mini.class);
        userBindVoList.add(new UserBindVo(mini));
        return RestResult.result(RespCode.CODE_0.getValue(),null,userBindVoList);
    }

    /**
     * 获取用户角色信息
     */
    public RestResult<?> findUserConnectRole(Long userId){
        return userServiceFeign.findUserConnectRole(userId);
    }
    /**
     * 保存用户角色信息
     */
    public RestResult<?> saveUserConnectRole( UserConnectRoleParam param){
        return userServiceFeign.saveUserConnectRole(param);
    }

    /**
     * 获取用户部门信息
     */
    public RestResult<?> findUserConnectDepartment(Long userId){
        return userServiceFeign.findUserConnectDepartment(userId);
    }
    /**
     * 保存用户部门信息
     */
    public RestResult<?> saveUserConnectDepartment(UserConnectDepartmentParam param){
        return userServiceFeign.saveUserConnectDepartment(param);
    }

    /**
     * 获取用户部门信息
     */
    public RestResult<?> findUserConnectPosition(Long userId){
        return userServiceFeign.findUserConnectPosition(userId);
    }
    /**
     * 保存用户部门信息
     */
    public RestResult<?> saveUserConnectPosition(UserConnectPositionParam param){
        return userServiceFeign.saveUserConnectPosition(param);
    }
    /**
     * 保存用户部门信息
     */
    public RestResult<?> openId(String openId){
        return userServiceFeign.openId(openId);
    }

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


    public RestResult<?> select(List<Long> userIdList, String userName, Integer limit){
        return userServiceFeign.select(userIdList,userName,limit);
    }

    public RestResult<?> findExportEntityInfo(){
       return RestResult.result(RespCode.CODE_0.getValue(),null, PoiUtils.exportEntityInfo(UserExportModel.class));
    }

    public void findExportUrl(ExportEntity<UserParam> exportEntity, HttpServletResponse response) throws Exception {
        boolean query = true;
        UserParam request = exportEntity.getRequest();
        List<UserExportModel> dataList = new ArrayList<>();
        int number = 1, size = 2000;
        while (query){
            request.setPageNumber(number);
            request.setPageSize(size);
            PageResult page = userServiceFeign.page(request);
            List<UserExportModel> list = AppUtils.convertValue2(page.getData().data,new TypeReference<List<UserExportModel>>(){});
            if(list.isEmpty()){
                break;
            }else if(list.size() < 2000){
                query = false;
            }
            dataList.addAll(list);
            //下次分页数
            ++number;
        }
        //导出
        PoiUtils.exportExcel("用户信息导出",null,exportEntity.getFieldList(),exportEntity.getDesensitizedList(),UserExportModel.class,dataList,response);
    }
}
