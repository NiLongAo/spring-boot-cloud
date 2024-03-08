package cn.com.tzy.srpingbootstartersecurityoauthbasic.common;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum LoginTypeEnum {
        //WEB端相关
        WEB_ACCOUNT("WEB_ACCOUNT","/webapi", ConstEnum.LoginTypeEnum.WEB_USER.getValue()),
        WEB_MOBILE("WEB_MOBILE","/webapi",ConstEnum.LoginTypeEnum.WEB_USER.getValue()),
        WEB_WX_MINI("WEB_WX_MINI","/webapi",ConstEnum.LoginTypeEnum.WEB_USER.getValue()),
        //Mini相关
		APP_ACCOUNT("APP_ACCOUNT","/app",ConstEnum.LoginTypeEnum.WX_MINI_USER.getValue()),
        APP_WX_MINI("APP_WX_MINI","/app",ConstEnum.LoginTypeEnum.WX_MINI_USER.getValue()),
        ;

        private final String type;//登录方式
        private final String clientType;//登录客户端类型  标记 gateway 路由参数
        private final String userType;//用于标记用户类型

        LoginTypeEnum(String type, String clientType, String userType) {
                this.type = type;
                this.clientType = clientType;
                this.userType = userType;
        }
        private static Map<String, LoginTypeEnum> map = new HashMap<String, LoginTypeEnum>();
        static {
                for (LoginTypeEnum e : LoginTypeEnum.values()) {
                        map.put(e.getType(), e);
                }
        }
        public static LoginTypeEnum getClientType(String type) {
                return map.get(type);
        }



}