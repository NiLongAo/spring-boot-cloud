package cn.com.tzy.springbootsso.config.oauth.service.user;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootfeignbean.api.bean.MiniServiceFeign;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.WxMaUserInfoVo;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.UserDetailsTypeService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Order(2)
@Log4j2
@Service
public class WxMiniUserServiceImpl implements UserDetailsService, UserDetailsTypeService {

    @Autowired
    private MiniServiceFeign miniServiceFeign;
    @Autowired
    private UserServiceFeign userServiceFeign;

    @Override
    public LoginTypeEnum getTypeEnum() {
        return LoginTypeEnum.APP_WX_MINI;
    }
    /**
     * 微信登陆用户操作
     * @param username 微信用户信息json
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WxMaUserInfoVo wxMaUserInfo = JSONUtil.toBean(username, WxMaUserInfoVo.class);
        //更新微信用户信息或没有则创建
        Mini build = Mini.builder()
                .nickName(wxMaUserInfo.getNickName())
                .avatarUrl(wxMaUserInfo.getAvatarUrl())
                .gender(StrUtil.isBlank(wxMaUserInfo.getGender()) ? 0 : Integer.parseInt(wxMaUserInfo.getGender()))
                .openId(wxMaUserInfo.getOpenId())
                .loginLastTime(new Date())
                .build();
        RestResult<?> result = miniServiceFeign.save(build);
        if(result.getCode() != RespCode.CODE_0.getValue()){
            throw new UsernameNotFoundException(result.getMessage());
        }
        RestResult<?> result1 = userServiceFeign.findLoginTypeByUserInfo(getTypeEnum(), build.getOpenId());
        SecurityBaseUser sysUser = null;
        try {
            sysUser = AppUtils.decodeJson2(AppUtils.encodeJson(result1.getData()), SecurityBaseUser.class);
        } catch (IOException e) {
            throw new RuntimeException("用户openId:" + build.getOpenId() + ",Json解析失败");
        }
        if (sysUser==null) {
            log.error("用户不存在");
            throw new UsernameNotFoundException("用户openId:" + build.getOpenId() + ",不存在!");
        }
        OAuthUserDetails oauthUserDetails = new OAuthUserDetails(sysUser);
        oauthUserDetails.setUsername(build.getOpenId());
        oauthUserDetails.setLoginType(getTypeEnum().getType());
        if (oauthUserDetails.getId() == null) {
            throw new UsernameNotFoundException(RespCode.CODE_311.getName());
        } else if (oauthUserDetails.getTenantId() == null) {
            throw new DisabledException("账户租户错误!");
        } else if (oauthUserDetails.getTenantStatus() == null || oauthUserDetails.getTenantStatus() != ConstEnum.Flag.YES.getValue()) {
            throw new DisabledException("账户租户已被禁用!");
        } else if (!oauthUserDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!oauthUserDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!oauthUserDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return oauthUserDetails;
    }
}
