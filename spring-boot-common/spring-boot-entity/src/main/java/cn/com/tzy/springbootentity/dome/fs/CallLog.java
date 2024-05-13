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
    * 话单表
    */
@ApiModel(description="话单表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_call_log")
public class CallLog extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 话单id
     */
    @TableField(value = "call_id")
    @ApiModelProperty(value="话单id")
    private Long callId;

    /**
     * 主叫显号
     */
    @TableField(value = "caller_display")
    @ApiModelProperty(value="主叫显号")
    private String callerDisplay;

    /**
     * 主叫
     */
    @TableField(value = "caller")
    @ApiModelProperty(value="主叫")
    private String caller;

    /**
     * 被叫显号
     */
    @TableField(value = "called_display")
    @ApiModelProperty(value="被叫显号")
    private String calledDisplay;

    /**
     * 被叫
     */
    @TableField(value = "`called`")
    @ApiModelProperty(value="被叫")
    private String called;

    /**
     * 客户号码归属地
     */
    @TableField(value = "number_location")
    @ApiModelProperty(value="客户号码归属地")
    private String numberLocation;

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
     * 技能组
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="技能组")
    private Long groupId;

    /**
     * 1:sip号,2:webrtc,3:手机
     */
    @TableField(value = "login_type")
    @ApiModelProperty(value="1:sip号,2:webrtc,3:手机")
    private Integer loginType;

    /**
     * 任务ID
     */
    @TableField(value = "task_id")
    @ApiModelProperty(value="任务ID")
    private Long taskId;

    /**
     * ivr
     */
    @TableField(value = "ivr_id")
    @ApiModelProperty(value="ivr")
    private Long ivrId;

    /**
     * 机器人id
     */
    @TableField(value = "bot_id")
    @ApiModelProperty(value="机器人id")
    private Long botId;

    /**
     * 呼叫开始时间
     */
    @TableField(value = "call_time")
    @ApiModelProperty(value="呼叫开始时间")
    private Long callTime;

    /**
     * 接听时间
     */
    @TableField(value = "answer_time")
    @ApiModelProperty(value="接听时间")
    private Long answerTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="结束时间")
    private Long endTime;

    /**
     * 呼叫类型
     */
    @TableField(value = "call_type")
    @ApiModelProperty(value="呼叫类型")
    private String callType;

    /**
     * 呼叫方向
     */
    @TableField(value = "direction")
    @ApiModelProperty(value="呼叫方向")
    private String direction;

    /**
     * 通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)
     */
    @TableField(value = "answer_flag")
    @ApiModelProperty(value="通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)")
    private Integer answerFlag;

    /**
     * 累计等待时长
     */
    @TableField(value = "wait_time")
    @ApiModelProperty(value="累计等待时长")
    private Long waitTime;

    /**
     * 应答设备数
     */
    @TableField(value = "answer_count")
    @ApiModelProperty(value="应答设备数")
    private Integer answerCount;

    /**
     * 挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)
     */
    @TableField(value = "hangup_dir")
    @ApiModelProperty(value="挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)")
    private Integer hangupDir;

    /**
     * 是否sdk挂机(1:sdk挂机)
     */
    @TableField(value = "sdk_hangup")
    @ApiModelProperty(value="是否sdk挂机(1:sdk挂机)")
    private Integer sdkHangup;

    /**
     * 挂机原因
     */
    @TableField(value = "hangup_code")
    @ApiModelProperty(value="挂机原因")
    private Integer hangupCode;

    /**
     * 媒体服务器
     */
    @TableField(value = "media_host")
    @ApiModelProperty(value="媒体服务器")
    private String mediaHost;

    /**
     * cti地址
     */
    @TableField(value = "cti_host")
    @ApiModelProperty(value="cti地址")
    private String ctiHost;

    /**
     * 客户端地址
     */
    @TableField(value = "client_host")
    @ApiModelProperty(value="客户端地址")
    private String clientHost;

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
     * 录音状态
     */
    @TableField(value = "record_type")
    @ApiModelProperty(value="录音状态")
    private Integer recordType;

    /**
     * 录音开始时间
     */
    @TableField(value = "record_start_time")
    @ApiModelProperty(value="录音开始时间")
    private Long recordStartTime;

    /**
     * 录音时间
     */
    @TableField(value = "record_time")
    @ApiModelProperty(value="录音时间")
    private Long recordTime;

    /**
     * 通话时长
     */
    @TableField(value = "talk_time")
    @ApiModelProperty(value="通话时长")
    private Long talkTime;

    /**
     * 第一次进队列时间
     */
    @TableField(value = "frist_queue_time")
    @ApiModelProperty(value="第一次进队列时间")
    private Long fristQueueTime;

    /**
     * 进队列时间
     */
    @TableField(value = "queue_start_time")
    @ApiModelProperty(value="进队列时间")
    private Long queueStartTime;

    /**
     * 出队列时间
     */
    @TableField(value = "queue_end_time")
    @ApiModelProperty(value="出队列时间")
    private Long queueEndTime;

    /**
     * 月份
     */
    @TableField(value = "month_time")
    @ApiModelProperty(value="月份")
    private String monthTime;

    /**
     * 通话随路数据(2048)
     */
    @TableField(value = "follow_data")
    @ApiModelProperty(value="通话随路数据(2048)")
    private String followData;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}