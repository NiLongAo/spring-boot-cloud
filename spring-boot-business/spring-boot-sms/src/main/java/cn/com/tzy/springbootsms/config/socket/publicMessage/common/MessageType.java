package cn.com.tzy.springbootsms.config.socket.publicMessage.common;

import java.util.HashMap;
import java.util.Map;

public enum MessageType{
        PUBLIC_NOTICE(1,"平台通知公告"),
        FORCED_OFFLINE(2,"强制下线"),
        ;
        private final Integer value;
        private final String name;

        MessageType(Integer value, String name) {
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
            for (MessageType s : MessageType.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }