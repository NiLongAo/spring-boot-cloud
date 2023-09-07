package cn.com.tzy.springbootentity.dome.sms;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 已读公告客户
 */
@ApiModel(value = "已读公告客户")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_read_notice_user")
public class ReadNoticeUser extends LongIdEntity {
    /**
     * 平台通知公告主键
     */
    @TableField(value = "notice_id")
    @ApiModelProperty(value = "平台通知公告主键")
    private Long noticeId;

    /**
     * 用户主键
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户主键")
    private Long userId;

}