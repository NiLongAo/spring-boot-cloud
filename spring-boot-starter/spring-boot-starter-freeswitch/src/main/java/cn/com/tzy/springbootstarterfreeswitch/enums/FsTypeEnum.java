package cn.com.tzy.springbootstarterfreeswitch.enums;

import java.util.HashMap;
import java.util.Map;

public enum FsTypeEnum {
    //通话记录设置
    CDR_PG_CSV(1, "xml/odbc_cdr.conf.xml", "odbcCdr"),
    //对外网关中继设置
    EXTERNAL(2,"xml/external.xml", "external"),
    //网关中继IPV6设置
    EXTERNAL_IPV6(3,"xml/external-ipv6.xml", "externalIpv6"),
    //对内网关
    INTERNAL(4,"xml/internal.xml", "internal"),
    //对内网关IPV6设置
    INTERNAL_IPV6(5,"xml/internal-ipv6.xml", "internalIpv6"),
    //主要设置控制台快捷键、数据库处理句柄、session会话、日志等级等
    SWITCH(6, "xml/switch.xml", "config"),
    //用户目录配置
    USER(7,"xml/user.xml", "user"),
    //服务配置信息
    ;
    private final int value;
    private final String path;
    private final String name;

    FsTypeEnum(int value,String path, String name) {
        this.value = value;
        this.path = path;
        this.name = name;
    }
    public int getValue() {
        return value;
    }
    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
    public static FsTypeEnum getFsTypeEnum(int value){
        return MAP.get(value);
    }

    private final static Map<Integer, FsTypeEnum> MAP = new HashMap<>();
    static {
        for (FsTypeEnum s : FsTypeEnum.values()) {
            MAP.put(s.getValue(), s);
        }
    }
}
