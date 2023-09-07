package cn.com.tzy.springbootsso.config.oauth.service.user;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.common.info.SecurityBaseUser;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootfeignbean.api.bean.MiniServiceFeign;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstarterstreamrabbitmq.config.MqClient;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.WxMaUserInfoVo;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service.UserDetailsTypeService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Order(2)
@Log4j2
@Service
public class WxMiniUserServiceImpl implements UserDetailsService, UserDetailsTypeService {
    @Autowired
    private MiniServiceFeign miniServiceFeign;
    @Resource
    private MqClient mqClient;

    @Override
    public TypeEnum getTypeEnum() {
        return TypeEnum.APP_WX_MINI;
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
        //用于web扫码 登陆或绑定web用户
        if(StrUtil.isNotEmpty(wxMaUserInfo.getScene())){
            Map<String,Object> data = new HashMap<>();
            data.put("scene",wxMaUserInfo.getScene());
            data.put("openId",wxMaUserInfo.getOpenId());
            //发送mq消息
            mqClient.send(MqConstant.QR_EXCHANGE,MqConstant.QR_ROUTING_KEY,data);
        }
        build = BeanUtil.toBean(result.getData(),Mini.class);
        SecurityBaseUser user = SecurityBaseUser.builder()
                .id(build.getId())
                .userName(build.getOpenId())//账号  唯一标识
                .imageUrl(build.getAvatarUrl())
                .isAdmin(ConstEnum.Flag.YES.getValue()) //不是系统管理员
                .isEnabled(ConstEnum.Flag.YES.getValue()) //启用
                .tenantId(Constant.TENANT_ID) //默认系统租户
                .tenantStatus(ConstEnum.Flag.YES.getValue()) //默认启用
                .build();
        OAuthUserDetails oauthUserDetails = new OAuthUserDetails(user);
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
