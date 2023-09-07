package cn.com.tzy.springbootsms.config.socket.qr.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信小程序登录事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxMiniLoginData {

    private String code;

    private String sessionKey;

    private String encryptedData;

    private String signature;

    private String iv;

    private String rawData;

    private String scene;


}
