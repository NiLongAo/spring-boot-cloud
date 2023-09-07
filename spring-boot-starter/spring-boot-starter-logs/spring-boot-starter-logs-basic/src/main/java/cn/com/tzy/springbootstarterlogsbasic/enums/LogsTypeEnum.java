package cn.com.tzy.springbootstarterlogsbasic.enums;

import java.util.HashMap;
import java.util.Map;

public enum LogsTypeEnum{
        OTHER(0, "其他"),
        LOGIN(1, "登录"),
        INSERT(2, "新增"),
        UPDATE(3, "修改"),
        DELETE(4, "删除"),
        ;
        private final int value;
        private final String name;

        private LogsTypeEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (LogsTypeEnum e : LogsTypeEnum.values()) {
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