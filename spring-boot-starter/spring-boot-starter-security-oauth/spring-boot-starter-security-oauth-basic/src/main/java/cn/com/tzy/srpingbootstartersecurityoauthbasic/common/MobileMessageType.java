package cn.com.tzy.srpingbootstartersecurityoauthbasic.common;

import java.util.HashMap;
import java.util.Map;

public enum MobileMessageType {
        LOGIN_VERIFICATION_CODE(1, "登录验证码"),
        REGISTER_VERIFICATION_CODE(2, "注册验证码"),
        RESET_VERIFICATION_CODE(3, "重置密码验证码"),
        ;

        private final int value;
        private final String name;

        MobileMessageType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (MobileMessageType e : MobileMessageType.values()) {
                map.put(e.getValue(), e.getName());
            }
        }

        public static String getName(int value) {
            return map.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }