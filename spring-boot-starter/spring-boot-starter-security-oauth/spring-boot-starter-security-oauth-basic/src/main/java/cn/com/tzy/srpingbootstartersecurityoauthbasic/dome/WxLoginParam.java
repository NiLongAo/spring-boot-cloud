package cn.com.tzy.srpingbootstartersecurityoauthbasic.dome;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class WxLoginParam implements Serializable {

    private String code;

    private String sessionKey;

    private String encryptedData;

    private String signature;

    private String iv;

    private String rawData;

    private String scene;

}
