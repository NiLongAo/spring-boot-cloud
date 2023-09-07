package cn.com.tzy.springbootentity.dome.sms;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@ApiModel(value = "短信表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_mobile_message")
public class MobileMessage extends LongIdEntity {

    @TableField(value = "sender_id")
    @ApiModelProperty(value = "发送人id")
    private Integer senderId;


    @TableField(value = "template_id")
    @ApiModelProperty(value = "模板号")
    private String templateId;


    @TableField(value = "type")
    @ApiModelProperty(value = "类型")
    private Integer type;

    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField(value = "mobile")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @TableField(value = "handle_time")
    @ApiModelProperty(value = "操作时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date handleTime;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date createTime;


    @TableField(value = "status")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @TableField(value = "msg_id")
    @ApiModelProperty(value = "响应编号")
    private String msgId;

    @TableField(value = "callback_status")
    @ApiModelProperty(value = "返回状态")
    private String callbackStatus;

    @TableField(value = "resend_num")
    @ApiModelProperty(value = "重发次数")
    private Integer resendNum;

    @TableField(value = "variable")
    @ApiModelProperty(value = "变量")
    private String variable;

    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}