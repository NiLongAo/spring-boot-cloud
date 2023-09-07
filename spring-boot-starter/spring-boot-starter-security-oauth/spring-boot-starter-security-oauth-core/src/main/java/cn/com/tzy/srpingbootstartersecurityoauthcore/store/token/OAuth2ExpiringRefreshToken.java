package cn.com.tzy.srpingbootstartersecurityoauthcore.store.token;


import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.io.Serializable;

public class OAuth2ExpiringRefreshToken implements Serializable, OAuth2RefreshToken {

    private String value;

    /**
     * Create a new refresh token.
     */
    @JsonCreator
    public OAuth2ExpiringRefreshToken(String value) {
        this.value = value;
    }

    public OAuth2ExpiringRefreshToken() {
        this(null);
    }

    /* (non-Javadoc)
     * @see org.springframework.security.oauth2.common.IFOO#getValue()
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
         this.value = value;
    }
    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExpiringOAuth2RefreshToken)) {
            return false;
        }

        OAuth2ExpiringRefreshToken that = (OAuth2ExpiringRefreshToken) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
