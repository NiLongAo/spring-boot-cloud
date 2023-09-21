package cn.com.tzy.springbootcomm.common.enumcom;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举类
 * @author TZY
 */
public class ConstEnum {

    public enum Flag {
        /**
         * 判断枚举
         */
        NO(0, "否"),
        YES(1, "是"),
        ;

        private final int value;
        private final String name;

        Flag(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
        public static String getName(Integer value) {
            return MAP.get(value);
        }
        private static final Map<Integer, String> MAP = new HashMap<>();

        static {
            for (Flag s : Flag.values()) {
                MAP.put(s.getValue(), s.getName());
            }
        }
    }

    public enum Sex {
        /**
         * 姓名
         */
        UNKNOWN(0, "未知"),
        MALE(1, "男"),
        FEMALE(2, "女"),
        ;

        private final int value;
        private final String name;

        Sex(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static String getName(int value){
           return MAP.get(value);
        }
        private static final Map<Integer, String> MAP = new HashMap<>();
        static {
            for (Sex s : Sex.values()) {
                MAP.put(s.getValue(), s.getName());
            }
        }
    }



    public enum ContentType {
        /**
         * 内容类型
         */
        JSON("application/json;charset=UTF-8");

        private final String value;

        ContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    public enum StaticPath {
        /**
         * 静态文件路径
         */
        USER_IMAGE_PATH(1, "用户图像路径","/image/user/%s"),
        EXPERIMENT_TEMPLATE_VIDEO_PATH(2, "模板任务视频路径","/video/experiment_template/%s"),
        TEACHING_TASK_VIDEO_PATH(3, "课程任务视频路径","/video/teaching_task/%s"),
        IMAGE_RICH_TEXT_PATH(4, "富文本图片","/images/rich_text/%s"),
        HTML_RICH_TEXT_PATH(5, "HTML富文本地址","/html/rich_text/%s"),
        FILE_EXPORT_PATH(6, "导出文件","/file/export/%s"),
        ;
        private final int type;
        private final String name;
        private final String url;

        StaticPath(int type,String name, String url) {
            this.type= type;
            this.name= name;
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public static String getUrl(Integer type) {
            return URL_MAP.get(type);
        }
        public static String getName(Integer type) {
            return NAME_MAP.get(type);
        }

        private final static Map<Integer, String> URL_MAP = new HashMap<>();
        private final static Map<Integer, String> NAME_MAP = new HashMap<>();
        static {
            for (StaticPath s : StaticPath.values()) {
                URL_MAP.put(s.getType(), s.getUrl());
                NAME_MAP.put(s.getType(), s.getName());
            }
        }
    }

    public enum PasswordEncoderTypeEnum {
        /**
         * 加密方式
         */
        BCRYPT("{bcrypt}","BCRYPT加密"),
        NOOP("{noop}","无加密明文");

        private final String prefix;
        private final String desc;

        public String getPrefix() {
            return prefix;
        }

        public String getDesc() {
            return desc;
        }

        PasswordEncoderTypeEnum(String prefix, String desc){
            this.prefix=prefix;
            this.desc=desc;
        }

        public static String getName(String value){
            return MAP.get(value);
        }
        private static final Map<String, String> MAP = new HashMap<>();
        static {
            for (PasswordEncoderTypeEnum s : PasswordEncoderTypeEnum.values()) {
                MAP.put(s.getPrefix(), s.getDesc());
            }
        }
    }

    public enum ConfigEnum {
        /**
         * 字典枚举
         */
        STATIC_PATH("minio.path", "静态服务地址"),
        ;

        private final String value;
        private final String name;

        ConfigEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
        public static String getName(String value){
            return MAP.get(value);
        }
        private final static Map<String, String> MAP = new HashMap<>();
        static {
            for (ConfigEnum s : ConfigEnum.values()) {
                MAP.put(s.getValue(), s.getName());
            }
        }
    }

    public enum ReviewStateEnum {
        /**
         * 流媒体状态
         */
        IS_REVIEW(1, "审核中"),
        REVIEW_ADOPT(2, "审核通过"),
        REVIEW_NOT_ADOPT(3, "审核驳回"),
        REVIEW_RETURN(4, "驳回上一节点"),
        ;

        private final int value;
        private final String name;

        ReviewStateEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }
        private final static Map<Integer, String> MAP = new HashMap<>();
        static {
            for (ReviewStateEnum e : ReviewStateEnum.values()) {
                MAP.put(e.getValue(), e.getName());
            }
        }

        public static String getName(int value) {
            return MAP.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    public enum UserTypeEnum {
        /**
         * 用户岗位信息
         */
        ROLE(1, "角色"),
        POSITION(2, "职位"),
        DEPARTMENT(3, "部门"),
        ;

        private final int value;
        private final String name;

        UserTypeEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }
        private final static Map<Integer, String> MAP = new HashMap<>();
        static {
            for (UserTypeEnum e : UserTypeEnum.values()) {
                MAP.put(e.getValue(), e.getName());
            }
        }

        public static String getName(int value) {
            return MAP.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    public enum TriggerType {
        /**
         * 定时类型触发器类型
         */
        CRON(1,"标准CRON支持"),
        INTERVAL_MILLISECOND(2,"固定间隔毫秒"),
        INTERVAL_SECOND(3,"固定间隔秒"),
        INTERVAL_MINUTE(4,"固定间隔分钟"),
        INTERVAL_HOUR(5,"固定间隔小时"),
        WEEKDAYS(6,"工作日，跳过节假日"),
        HOLIDAY(7,"节假日")
        ;

        private final int value;
        private final String name;

        TriggerType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        private static final Map<Integer, String> MAP = new HashMap<>();
        private static final Map<Integer, TriggerType> MAP_TYPE = new HashMap<>();
        static {
            for (TriggerType e : TriggerType.values()) {
                MAP.put(e.getValue(), e.getName());
                MAP_TYPE.put(e.getValue(),e);
            }
        }

        public static String getName(int value) {
            return MAP.get(value);
        }

        public static TriggerType getTriggerType(int value) {
            return MAP_TYPE.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    public enum LoginTypeEnum {
        /**
         * 登陆用户类型
         */
        WX_MINI_USER("wx_mini_user", "微信小程序用户"),
        WEB_USER("web_user", "web登陆用户"),
        ;

        private final String value;
        private final String name;

        LoginTypeEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }
        private static final Map<String, String> MAP = new HashMap<>();
        static {
            for (LoginTypeEnum e : LoginTypeEnum.values()) {
                MAP.put(e.getValue(), e.getName());
            }
        }

        public static String getName(String value) {
            return MAP.get(value);
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

}
