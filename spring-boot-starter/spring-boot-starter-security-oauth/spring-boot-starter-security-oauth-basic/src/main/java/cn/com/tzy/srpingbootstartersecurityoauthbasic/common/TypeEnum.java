package cn.com.tzy.srpingbootstartersecurityoauthbasic.common;

import java.util.HashMap;
import java.util.Map;

public enum TypeEnum{

        WEB_ACCOUNT("web_account","webapi"),
        WEB_MOBILE("web_mobile","webapi"),
        WEB_WX_MINI("web_wx_mini","webapi"),
        APP_WX_MINI("app_wx_mini","app"),
        ;

        private final String type;//登录方式
        private final String clientType;//登录客户端类型  标记 gateway 路由参数

        TypeEnum(String type,String clientType) {
                this.type = type;
                this.clientType = clientType;
        }

        private static Map<String, String> map = new HashMap<String, String>();
        static {
                for (TypeEnum e : TypeEnum.values()) {
                        map.put(e.getType(), e.getClientType());
                }
        }
        public static String getClientType(String type) {
                return map.get(type);
        }
        public String getType() {
                return type;
        }
        public String getClientType() {
                return clientType;
        }



}