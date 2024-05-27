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

import java.util.Date;
import java.util.List;

/**
    * 座席工号表
    */
@ApiModel(description="座席工号表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_agent")
public class Agent extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 坐席工号
     */
    @TableField(value = "agent_id")
    @ApiModelProperty(value="坐席工号")
    private String agentId;

    /**
     * 坐席账户
     */
    @TableField(value = "agent_key")
    @ApiModelProperty(value="坐席账户")
    private String agentKey;

    /**
     * 坐席名称
     */
    @TableField(value = "agent_name")
    @ApiModelProperty(value="坐席名称")
    private String agentName;

    /**
     * 坐席分机号
     */
    @TableField(value = "agent_code")
    @ApiModelProperty(value="坐席分机号")
    private String agentCode;

    /**
     * 座席类型：1:普通座席；2：班长
     */
    @TableField(value = "agent_type")
    @ApiModelProperty(value="座席类型：1:普通座席；2：班长")
    private Integer agentType;

    /**
     * 座席密码
     */
    @TableField(value = "passwd")
    @ApiModelProperty(value="座席密码")
    private String passwd;

    /**
     * 是否录音 0 no 1 yes
     */
    @TableField(value = "record")
    @ApiModelProperty(value="是否录音 0 no 1 yes")
    private Integer record;

    /**
     * 座席主要技能组  不能为空 必填项
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="座席主要技能组  不能为空 必填项")
    private Long groupId;

    /**
     * 话后自动空闲间隔时长
     */
    @TableField(value = "after_interval")
    @ApiModelProperty(value="话后自动空闲间隔时长")
    private Integer afterInterval;

    /**
     * 振铃时长
     */
    @TableField(value = "ring_time")
    @ApiModelProperty(value="振铃时长")
    private Integer ringTime;

    /**
     * 登录服务器地址
     */
    @TableField(value = "`host`")
    @ApiModelProperty(value="登录服务器地址")
    private String host;

    /**
     * 坐席状态(1:在线,0:不在线)
     */
    @TableField(value = "`state`")
    @ApiModelProperty(value="坐席状态(1:在线,0:不在线)")
    private Integer state;

    /**
     * 状态：1 开通，0关闭
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态：1 开通，0关闭")
    private Integer status;

    /**
     * 注册时间
     */
    @TableField(value = "register_time")
    @ApiModelProperty(value="注册时间")
    private Date registerTime;

    /**
     * 续订时间
     */
    @TableField(value = "renew_time")
    @ApiModelProperty(value="续订时间")
    private Date renewTime;

    /**
     * 心跳时间
     */
    @TableField(value = "keepalive_time")
    @ApiModelProperty(value="心跳时间")
    private Date keepaliveTime;

    /**
     * 心跳间隔 (最低25秒)
     */
    @TableField(value = "keep_timeout")
    @ApiModelProperty(value="心跳间隔 (最低25秒)")
    private Integer keepTimeout;

    /**
     * 注册有效期（单位：秒 默认1天）
     */
    @TableField(value = "expires")
    @ApiModelProperty(value="注册有效期（单位：秒 默认1天）")
    private Integer expires;

    /**
     * 数据流传输模式 0.UDP:udp传输 1.TCP-PASSIVE：tcp被动模式 2.TCP-ACTIVE：tcp主动模式
     */
    @TableField(value = "stream_mode")
    @ApiModelProperty(value="数据流传输模式 0.UDP:udp传输 1.TCP-PASSIVE：tcp被动模式 2.TCP-ACTIVE：tcp主动模式")
    private Integer streamMode;

    /**
     * 传输协议 1.UDP 2.TCP
     */
    @TableField(value = "transport")
    @ApiModelProperty(value="传输协议 1.UDP 2.TCP")
    private Integer transport;

    /**
     * 字符集, 1.UTF-8 2.GB2312
     */
    @TableField(value = "charset")
    @ApiModelProperty(value="字符集, 1.UTF-8 2.GB2312")
    private Integer charset;

    /**
     * 绑定的电话号码
     */
    @TableField(exist = false)
    private List<String> sipPhoneList;
}