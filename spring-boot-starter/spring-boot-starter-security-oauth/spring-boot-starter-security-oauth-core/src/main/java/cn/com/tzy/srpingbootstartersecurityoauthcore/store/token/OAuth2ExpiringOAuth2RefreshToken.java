package cn.com.tzy.srpingbootstartersecurityoauthcore.store.token;

import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;

import java.util.Date;

@NoArgsConstructor
public class OAuth2ExpiringOAuth2RefreshToken extends OAuth2ExpiringRefreshToken implements ExpiringOAuth2RefreshToken {
    private Date expiration;

    /**
     * @param value
     */
    public OAuth2ExpiringOAuth2RefreshToken(String value, Date expiration) {
        super(value);
        this.expiration = expiration;
    }

    /**
     * The instant the token expires.
     *
     * @return The instant the token expires.
     */
    public Date getExpiration() {
        return expiration;
    }
}
