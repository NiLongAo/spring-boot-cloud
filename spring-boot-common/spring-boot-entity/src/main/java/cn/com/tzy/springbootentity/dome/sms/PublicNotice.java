package cn.com.tzy.springbootentity.dome.sms;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 平台通知公告
 */
@ApiModel(value = "平台通知公告")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_public_notice")
public class PublicNotice extends LongIdEntity {
    /**
     * 通知类型
     */
    @TableField(value = "notice_type")
    @ApiModelProperty(value = "通知类型")
    private Integer noticeType;

    /**
     * 标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 公告开始时间
     */
    @TableField(value = "begin_time")
    @ApiModelProperty(value = "公告开始时间")
    private Date beginTime;

    /**
     * 公告结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value = "公告结束时间")
    private Date endTime;

    /**
     * 状态 1正常,2已过期
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态 1正常,2已过期")
    private Integer status;

    /**
     * 是否已读
     */
    @ApiModelProperty(value = "是否已读")
    @TableField(exist = false)
    private Integer readNotice;


    public enum NoticeType {
        LOGIN_VERIFICATION_CODE(1, "系统公告"),
        ;

        private final int value;
        private final String name;

        NoticeType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (NoticeType e : NoticeType.values()) {
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

    public enum Status {
        NORMAL(1, "正常"),
        EXPIRED(2, "已过期"),
        ;

        private final int value;
        private final String name;

        Status(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Map<Integer, String> map = new HashMap<Integer, String>();
        static {
            for (Status e : Status.values()) {
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
}