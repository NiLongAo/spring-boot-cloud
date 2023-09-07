package cn.com.tzy.springbootstartersmsbasic.common;

import java.util.HashMap;
import java.util.Map;

public enum SignPlaceEnum {

    LEFT(1, "左边"),
    RIGHT(2, "右边"),
    ;

    private final int value;
    private final String name;

    private SignPlaceEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, String> map = new HashMap<Integer, String>();
    static {
        for (SignPlaceEnum e : SignPlaceEnum.values()) {
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
