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
    * 技能组表
    */
@ApiModel(description="技能组表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_group")
public class Group extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 技能组名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="技能组名称")
    private String name;

    /**
     * 话后自动空闲时长
     */
    @TableField(value = "after_interval")
    @ApiModelProperty(value="话后自动空闲时长")
    private Integer afterInterval;

    /**
     * 主叫显号号码池
     */
    @TableField(value = "caller_display_id")
    @ApiModelProperty(value="主叫显号号码池")
    private Long callerDisplayId;

    /**
     * 被叫显号号码池
     */
    @TableField(value = "called_display_id")
    @ApiModelProperty(value="被叫显号号码池")
    private Long calledDisplayId;

    /**
     * 1:振铃录音,2:接通录音
     */
    @TableField(value = "record_type")
    @ApiModelProperty(value="1:振铃录音,2:接通录音")
    private Integer recordType;

    /**
     * 技能组优先级
     */
    @TableField(value = "level_value")
    @ApiModelProperty(value="技能组优先级")
    private Integer levelValue;

    /**
     * tts引擎id
     */
    @TableField(value = "tts_engine")
    @ApiModelProperty(value="tts引擎id")
    private Long ttsEngine;

    /**
     * 转坐席时播放内容
     */
    @TableField(value = "play_content")
    @ApiModelProperty(value="转坐席时播放内容")
    private String playContent;

    /**
     * 转服务评价(0:否,1:是)
     */
    @TableField(value = "evaluate")
    @ApiModelProperty(value="转服务评价(0:否,1:是)")
    private Long evaluate;

    /**
     * 排队音
     */
    @TableField(value = "queue_play")
    @ApiModelProperty(value="排队音")
    private Long queuePlay;

    /**
     * 转接提示音
     */
    @TableField(value = "transfer_play")
    @ApiModelProperty(value="转接提示音")
    private Long transferPlay;

    /**
     * 外呼呼叫超时时间
     */
    @TableField(value = "call_time_out")
    @ApiModelProperty(value="外呼呼叫超时时间")
    private Integer callTimeOut;

    /**
     * 技能组类型
     */
    @TableField(value = "group_type")
    @ApiModelProperty(value="技能组类型")
    private Integer groupType;

    /**
     * 0:不播放排队位置,1:播放排队位置
     */
    @TableField(value = "notify_position")
    @ApiModelProperty(value="0:不播放排队位置,1:播放排队位置")
    private Integer notifyPosition;

    /**
     * 频次
     */
    @TableField(value = "notify_rate")
    @ApiModelProperty(value="频次")
    private Integer notifyRate;

    /**
     * 您前面还有$位用户在等待
     */
    @TableField(value = "notify_content")
    @ApiModelProperty(value="您前面还有$位用户在等待")
    private String notifyContent;

    /**
     * 主叫记忆(1:开启,0:不开启)
     */
    @TableField(value = "call_memory")
    @ApiModelProperty(value="主叫记忆(1:开启,0:不开启)")
    private Integer callMemory;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}