package cn.com.tzy.springbootcomm.common.enumcom;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


public enum OAuthClientEnum {
    /**
     * 客户端枚举
     */

    WEB_API_CLIENT("web-api-client", "webApi客户端"),
    APP_CLIENT("app-client", "app客户端"),
    MINI_WEB_APP("mini-web-app", "微信小程序端");


    @Getter
    private final String clientId;

    @Getter
    private final String  desc;

    OAuthClientEnum(String clientId, String desc){
        this.clientId=clientId;
        this.desc=desc;
    }

    private final static Map<String, OAuthClientEnum> MAP = new HashMap<>();
    static {
        for (OAuthClientEnum e : OAuthClientEnum.values()) {
            MAP.put(e.getClientId(), e);
        }
    }

    public static OAuthClientEnum getByClientId(String clientId) {
        return MAP.get(clientId);
    }

}
