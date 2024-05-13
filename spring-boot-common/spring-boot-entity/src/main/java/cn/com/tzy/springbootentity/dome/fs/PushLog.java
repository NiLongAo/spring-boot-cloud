package cn.com.tzy.springbootentity.dome.fs;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 话单推送记录表
    */
@ApiModel(description="话单推送记录表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_push_log")
public class PushLog extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * callid
     */
    @TableField(value = "call_id")
    @ApiModelProperty(value="callid")
    private Long callId;

    /**
     * 发送次数
     */
    @TableField(value = "cdr_notify_url")
    @ApiModelProperty(value="发送次数")
    private String cdrNotifyUrl;

    /**
     * 推送内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value="推送内容")
    private String content;

    /**
     * 推送次数
     */
    @TableField(value = "push_times")
    @ApiModelProperty(value="推送次数")
    private Integer pushTimes;

    /**
     * 推送返回值
     */
    @TableField(value = "push_response")
    @ApiModelProperty(value="推送返回值")
    private String pushResponse;

    /**
     * 状态(1:推送，0:不推送)
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态(1:推送，0:不推送)")
    private Integer status;
}