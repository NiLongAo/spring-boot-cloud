package cn.com.tzy.springbootstartersmsbasic.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信类型枚举
 */
public enum SmsTypeEnum {

    DXW(1, "短信网"),
    CLW(2, "创蓝网"),
    WND(3, "维纳多"),
    SWLH(4, "商务领航"),
    ALYDY(5, "阿里云大于"),
    WYYD(6, "网易易盾"),
    YTX(7, "云通讯"),
    TXY(8, "腾讯云"),
    ;

    private final int value;
    private final String name;

    private SmsTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Map<Integer, String> map = new HashMap<Integer, String>();
    static {
        for (SmsTypeEnum e : SmsTypeEnum.values()) {
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
