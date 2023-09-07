package cn.com.tzy.springbootactiviti.utils;

import cn.com.tzy.springbootactiviti.oa.impl.LeaveOaService;

import java.util.HashMap;
import java.util.Map;

public enum OAEnum {

    IS_REVIEW("Leave", LeaveOaService.class),
    ;

    private final String code;
    private final Class clazz;

    private static Map<String, Class> map = new HashMap<String, Class>();

    static {
        for (OAEnum e : OAEnum.values()) {
            map.put(e.code, e.clazz);
        }
    }

    private OAEnum(String code, Class clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public final String getCode() {
        return code;
    }

    public static Class get(String code) {
        return map.get(code);
    }

}
