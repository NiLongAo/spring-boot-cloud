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
    * 坐席状态历史表
    */
@ApiModel(description="坐席状态历史表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_agent_state_log")
public class AgentStateLog extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 主技能组id
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="主技能组id")
    private Long groupId;

    /**
     * 坐席id
     */
    @TableField(value = "agent_id")
    @ApiModelProperty(value="坐席id")
    private Long agentId;

    /**
     * 坐席编号
     */
    @TableField(value = "agent_key")
    @ApiModelProperty(value="坐席编号")
    private String agentKey;

    /**
     * 坐席名称
     */
    @TableField(value = "agent_name")
    @ApiModelProperty(value="坐席名称")
    private String agentName;

    /**
     * 通话唯一标识
     */
    @TableField(value = "call_id")
    @ApiModelProperty(value="通话唯一标识")
    private Long callId;

    /**
     * 登录类型
     */
    @TableField(value = "login_type")
    @ApiModelProperty(value="登录类型")
    private Integer loginType;

    /**
     * 工作类型
     */
    @TableField(value = "work_type")
    @ApiModelProperty(value="工作类型")
    private Integer workType;

    /**
     * 服务站点
     */
    @TableField(value = "`host`")
    @ApiModelProperty(value="服务站点")
    private String host;

    /**
     * 远端地址
     */
    @TableField(value = "remote_address")
    @ApiModelProperty(value="远端地址")
    private String remoteAddress;

    /**
     * 变更之前状态
     */
    @TableField(value = "before_state")
    @ApiModelProperty(value="变更之前状态")
    private String beforeState;

    /**
     * 更变之前时间
     */
    @TableField(value = "before_time")
    @ApiModelProperty(value="更变之前时间")
    private Long beforeTime;

    /**
     * 变更之后状态
     */
    @TableField(value = "`state`")
    @ApiModelProperty(value="变更之后状态")
    private String state;

    /**
     * 当前时间(秒)
     */
    @TableField(value = "state_time")
    @ApiModelProperty(value="当前时间(秒)")
    private Long stateTime;

    /**
     * 持续时间(秒)
     */
    @TableField(value = "duration")
    @ApiModelProperty(value="持续时间(秒)")
    private Integer duration;

    /**
     * 忙碌类型
     */
    @TableField(value = "busy_desc")
    @ApiModelProperty(value="忙碌类型")
    private String busyDesc;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}