package cn.com.tzy.srpingbootstartersecurityoauthbasic.common;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum TypeEnum{
        WEB_ID("web_id","id"),//Id查询
        WEB_ACCOUNT("web_account","webapi"),
        WEB_MOBILE("web_mobile","webapi"),
        WEB_WX_MINI("web_wx_mini","webapi"),
        APP_WX_MINI("app_wx_mini","app"),
        ;

        @Getter
        private final String type;//登录方式
        @Getter
        private final String clientType;//登录客户端类型  标记 gateway 路由参数
        TypeEnum(String type,String clientType) {
                this.type = type;
                this.clientType = clientType;
        }
        private static Map<String, TypeEnum> map = new HashMap<String, TypeEnum>();
        static {
                for (TypeEnum e : TypeEnum.values()) {
                        map.put(e.getType(), e);
                }
        }
        public static TypeEnum getClientType(String type) {
                return map.get(type);
        }



}