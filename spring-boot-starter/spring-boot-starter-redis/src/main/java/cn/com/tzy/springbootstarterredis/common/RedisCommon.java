package cn.com.tzy.springbootstarterredis.common;

public class RedisCommon {

    /**
     * 缓存用户个人信息
     */
    public static final String USER_INFO= "REDIS_CACHE:USER:INFO:";
    /**
     * 缓存全部区域信息
     */
    public static final String AREA_ALL_INFO= "REDIS_CACHE:AREA:ALL_INFO";
    /**
     * 缓存全部区域名称信息
     */
    public static final String AREA_ALL_NAME= "REDIS_CACHE:AREA:ALL_NAME";
    /**
     * 缓存根据区域编号查询的区域地址
     */
    public static final String AREA_ADDRESS= "REDIS_CACHE:AREA:ADDRESS:";
    /**
     * redis 发布订阅监听  扫描监听
     */
    public final static String Q_R_EVENT = "qr_event";

    /**
     * 获取平台目录数相关信息
     */
    public final static String PLATFORM_CATALOG_PARENT = "platform_catalog:parent:";

    /**
     * 获取平台目录数相关信息
     */
    public final static String PLATFORM_CATALOG_PARENT_ID = "platform_catalog:parent_id:";

}
