package cn.com.tzy.springbootcomm.common.enumcom;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举类
 */
public class ConstEnum {

    public enum Flag {
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
            return map.get(value);
        }
        private static Map<Integer, String> map = new HashMap<Integer, String>();

        static {
            for (Flag s : Flag.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }

    public enum Sex {
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
        private static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (Sex s : Sex.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }



    public enum ContentType {
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
        USER_IMAGE_PATH(1, "用户图像路径","/image/user/%s/%s.%s"),
        EXPERIMENT_TEMPLATE_VIDEO_PATH(2, "模板任务视频路径","/video/experiment_template/%s/%s.%s"),
        TEACHING_TASK_VIDEO_PATH(3, "课程任务视频路径","/video/teaching_task/%s/%s.%s"),
        IMAGE_RICH_TEXT_PATH(4, "富文本图片","/images/rich_text/%s/%s.%s"),
        HTML_RICH_TEXT_PATH(5, "HTML富文本地址","/html/rich_text/%s/%s.%s"),
        FILE_EXPORT_PATH(6, "导出文件","/file/export/%s/%s.%s"),
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
            return urlMap.get(type);
        }
        public static String getName(Integer type) {
            return nameMap.get(type);
        }

        private static Map<Integer, String> urlMap = new HashMap<Integer, String>();
        private static Map<Integer, String> nameMap = new HashMap<Integer, String>();
        static {
            for (StaticPath s : StaticPath.values()) {
                urlMap.put(s.getType(), s.getUrl());
                nameMap.put(s.getType(), s.getName());
            }
        }
    }


    //加密方式
    public enum PasswordEncoderTypeEnum {
        BCRYPT("{bcrypt}","BCRYPT加密"),
        NOOP("{noop}","无加密明文");
        @Getter
        private String prefix;
        PasswordEncoderTypeEnum(String prefix,String desc){
            this.prefix=prefix;
        }

    }

    public enum ConfigEnum {
        STATSE_PATH("minio.path", "静态服务地址"),
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
        private static Map<String, String> map = new HashMap<String, String>();
        static {
            for (ConfigEnum s : ConfigEnum.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }

    /**
     * 字典枚举
     */
    public enum DictionaryTypeEnum {
        OA_ACTIVITI("OA_ACTIVITI", "工作流字典"),
        ;
        private final String value;
        private final String name;

        DictionaryTypeEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
        private static Map<String, String> map = new HashMap<String, String>();
        static {
            for (DictionaryTypeEnum s : DictionaryTypeEnum.values()) {
                map.put(s.getValue(), s.getName());
            }
        }
    }

    public enum ReviewStateEnum {
        IS_REVIEW(1, "审核中"),
        REVIEW_ADOPT(2, "审核通过"),
        REVIEW_NOT_ADOPT(3, "审核驳回"),
        REVIEW_RETURN(4, "驳回上一节点"),
        ;

        private final int value;
        private final String name;

        private ReviewStateEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }
        private static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (ReviewStateEnum e : ReviewStateEnum.values()) {
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


    public enum UserTypeEnum {
        ROLE(1, "角色"),
        POSITION(2, "职位"),
        DEPARTMENT(3, "部门"),
        ;

        private final int value;
        private final String name;

        private UserTypeEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }
        private static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (UserTypeEnum e : UserTypeEnum.values()) {
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

    /**
     * @author Xiaohan.Yuan
     * @version 1.0.0
     * @ClassName TimingTriggerType.java
     * @Description 定时类型触发器类型
     * @createTime 2021年12月16日
     */
    public enum TriggerType {
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

        private static Map<Integer, String> map = new HashMap<Integer, String>();
        private static Map<Integer, TriggerType> mapType = new HashMap<Integer, TriggerType>();
        static {
            for (TriggerType e : TriggerType.values()) {
                map.put(e.getValue(), e.getName());
                mapType.put(e.getValue(),e);
            }
        }

        public static String getName(int value) {
            return map.get(value);
        }

        public static TriggerType getTriggerType(int value) {
            return mapType.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 登陆用户类型
     */
    public enum LoginTypeEnum {
        WX_MINI_USER("wx_mini_user", "微信小程序用户"),
        WEB_USER("web_user", "web登陆用户"),
        ;

        private final String value;
        private final String name;

        private LoginTypeEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }
        private static Map<String, String> map = new HashMap<String, String>();
        static {
            for (LoginTypeEnum e : LoginTypeEnum.values()) {
                map.put(e.getValue(), e.getName());
            }
        }

        public static String getName(String value) {
            return map.get(value);
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

}
