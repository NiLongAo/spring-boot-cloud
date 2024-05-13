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
    * 话单明细表
    */
@ApiModel(description="话单明细表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_call_device")
public class CallDevice extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 通话ID
     */
    @TableField(value = "call_id")
    @ApiModelProperty(value="通话ID")
    private Long callId;

    /**
     * 设备id
     */
    @TableField(value = "device_id")
    @ApiModelProperty(value="设备id")
    private String deviceId;

    /**
     * 坐席
     */
    @TableField(value = "agent_key")
    @ApiModelProperty(value="坐席")
    private String agentKey;

    /**
     * 坐席名称
     */
    @TableField(value = "agent_name")
    @ApiModelProperty(value="坐席名称")
    private String agentName;

    /**
     * 1:坐席,2:客户,3:外线
     */
    @TableField(value = "device_type")
    @ApiModelProperty(value="1:坐席,2:客户,3:外线")
    private Integer deviceType;

    /**
     * 1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插
     */
    @TableField(value = "cdr_type")
    @ApiModelProperty(value="1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插")
    private Integer cdrType;

    /**
     * 转接或咨询发起者
     */
    @TableField(value = "from_agent")
    @ApiModelProperty(value="转接或咨询发起者")
    private String fromAgent;

    /**
     * 主叫
     */
    @TableField(value = "caller")
    @ApiModelProperty(value="主叫")
    private String caller;

    /**
     * 被叫
     */
    @TableField(value = "`called`")
    @ApiModelProperty(value="被叫")
    private String called;

    /**
     * 显号
     */
    @TableField(value = "display")
    @ApiModelProperty(value="显号")
    private String display;

    /**
     * 被叫归属地
     */
    @TableField(value = "called_location")
    @ApiModelProperty(value="被叫归属地")
    private String calledLocation;

    /**
     * 被叫归属地
     */
    @TableField(value = "caller_location")
    @ApiModelProperty(value="被叫归属地")
    private String callerLocation;

    /**
     * 呼叫开始时间
     */
    @TableField(value = "call_time")
    @ApiModelProperty(value="呼叫开始时间")
    private Long callTime;

    /**
     * 振铃开始时间
     */
    @TableField(value = "ring_start_time")
    @ApiModelProperty(value="振铃开始时间")
    private Long ringStartTime;

    /**
     * 振铃结束时间
     */
    @TableField(value = "ring_end_time")
    @ApiModelProperty(value="振铃结束时间")
    private Long ringEndTime;

    /**
     * 接通时间
     */
    @TableField(value = "answer_time")
    @ApiModelProperty(value="接通时间")
    private Long answerTime;

    /**
     * 桥接时间
     */
    @TableField(value = "bridge_time")
    @ApiModelProperty(value="桥接时间")
    private Long bridgeTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="结束时间")
    private Long endTime;

    /**
     * 通话时长
     */
    @TableField(value = "talk_time")
    @ApiModelProperty(value="通话时长")
    private Long talkTime;

    /**
     * 录音开始时间
     */
    @TableField(value = "record_start_time")
    @ApiModelProperty(value="录音开始时间")
    private Long recordStartTime;

    /**
     * 录音时长
     */
    @TableField(value = "record_time")
    @ApiModelProperty(value="录音时长")
    private Long recordTime;

    /**
     * 信令协议(tcp/udp)
     */
    @TableField(value = "sip_protocol")
    @ApiModelProperty(value="信令协议(tcp/udp)")
    private String sipProtocol;

    /**
     * 录音地址
     */
    @TableField(value = "record")
    @ApiModelProperty(value="录音地址")
    private String record;

    /**
     * 备用录音地址
     */
    @TableField(value = "record2")
    @ApiModelProperty(value="备用录音地址")
    private String record2;

    /**
     * 备用录音地址
     */
    @TableField(value = "record3")
    @ApiModelProperty(value="备用录音地址")
    private String record3;

    /**
     * 呼叫地址
     */
    @TableField(value = "channel_name")
    @ApiModelProperty(value="呼叫地址")
    private String channelName;

    /**
     * 挂机原因
     */
    @TableField(value = "hangup_cause")
    @ApiModelProperty(value="挂机原因")
    private String hangupCause;

    /**
     * 回铃音识别
     */
    @TableField(value = "ring_cause")
    @ApiModelProperty(value="回铃音识别")
    private String ringCause;

    /**
     * sip状态
     */
    @TableField(value = "sip_status")
    @ApiModelProperty(value="sip状态")
    private String sipStatus;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}