package cn.com.tzy.springbootcomm.common.enumcom;
import lombok.Getter;


/**
 * @author haoxr
 * @description TODO
 * @createTime 2021/5/31 23:55
 */
public enum OAuthClientEnum {

    WEB_API_CLIENT("web-api-client", "webApi客户端"),
    APP_CLIENT("app-client", "app客户端"),
    WEAPP("youlai-weapp", "微信小程序端");


    @Getter
    private String clientId;

    @Getter
    private String  desc;

    OAuthClientEnum(String clientId, String desc){
        this.clientId=clientId;
        this.desc=desc;
    }

    public static OAuthClientEnum getByClientId(String clientId) {
        for (OAuthClientEnum client : OAuthClientEnum.values()) {
            if(client.getClientId().equals(clientId)){
                return client;
            }
        }
        return null;
    }


}
