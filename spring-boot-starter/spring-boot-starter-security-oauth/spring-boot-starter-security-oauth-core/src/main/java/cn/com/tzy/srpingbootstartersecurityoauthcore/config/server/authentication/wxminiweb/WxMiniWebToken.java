package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxminiweb;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class WxMiniWebToken extends AbstractAuthenticationToken {

    private OAuthUserDetails user;
    private String openId;


    public WxMiniWebToken(String openId) {
        super(null);
        this.openId=openId;
        //标记未认证
        super.setAuthenticated(false);//注意这个构造方法是认证时使用的
    }

    public WxMiniWebToken(OAuthUserDetails user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user=user;
        //标记已认证
        super.setAuthenticated(true);//注意这个构造方法是认证成功后使用的
    }

    @Override
    @SneakyThrows
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }


    @Override
    public Object getCredentials() {
       return user.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
