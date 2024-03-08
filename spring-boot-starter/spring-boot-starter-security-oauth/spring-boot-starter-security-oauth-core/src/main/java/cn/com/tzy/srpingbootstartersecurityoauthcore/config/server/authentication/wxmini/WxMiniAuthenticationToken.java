package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.authentication.wxmini;

import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.OAuthUserDetails;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.dome.WxLoginParam;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class WxMiniAuthenticationToken extends AbstractAuthenticationToken {

    private OAuthUserDetails user;
    private String code;
    private String sessionKey;
    private String iv;
    private String signature;
    private String encryptedData;
    private String rawData;

    private String scene ;


    public WxMiniAuthenticationToken(WxLoginParam wxLoginParam) {
        super(null);
        this.code=wxLoginParam.getCode();
        this.sessionKey=wxLoginParam.getSessionKey();
        this.signature=wxLoginParam.getSignature();
        this.encryptedData=wxLoginParam.getEncryptedData();
        this.iv=wxLoginParam.getIv();
        this.rawData=wxLoginParam.getRawData();
        this.scene=wxLoginParam.getScene();
        //标记未认证
        super.setAuthenticated(false);//注意这个构造方法是认证时使用的
    }

    public WxMiniAuthenticationToken(OAuthUserDetails user, Collection<? extends GrantedAuthority> authorities) {
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
