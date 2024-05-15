package cn.com.tzy.springbootstarterfreeswitch.enums.sip;

import java.util.HashMap;
import java.util.Map;

public enum InviteStreamType {

    FreeSWITCH("FreeSWITCH","FreeSWITCH软电话"),
    ;

    private final String value;
    private final String name;

    InviteStreamType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static InviteStreamType getInviteStreamType(String value) {
        return map.get(value);
    }
    private static Map<String , InviteStreamType> map = new HashMap<String , InviteStreamType>();
    static {
        for (InviteStreamType s : InviteStreamType.values()) {
            map.put(s.getValue(), s);
        }
    }

}