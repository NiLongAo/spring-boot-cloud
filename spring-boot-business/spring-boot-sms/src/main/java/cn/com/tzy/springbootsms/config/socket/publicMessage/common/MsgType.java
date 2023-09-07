package cn.com.tzy.springbootsms.config.socket.publicMessage.common;

import java.util.HashMap;
import java.util.Map;

public enum MsgType{
        ALL(1,"全体消息"),
        ROLE(2,"角色消息"),
        DEPARTMENT(3,"部门消息"),
        POSITION(4,"职位消息"),
        USER(5,"用户消息"),
        ;
        private final Integer value;
        private final String name;

        MsgType(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        private static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (MsgType s : MsgType.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }