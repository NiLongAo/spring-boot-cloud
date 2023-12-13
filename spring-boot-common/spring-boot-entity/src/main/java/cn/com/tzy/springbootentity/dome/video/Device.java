package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
    * 人员信息
    */
@ApiModel(value="人员信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_device")
public class Device extends LongIdEntity {
    /**
     * 设备国标编号
     * 国标编码规范
     * 1.2位 省级编码
     * 3.4位 市级编码
     * 5.6位 区级编码
     * 7.8 基层接入单位编号
     * 9.10 行业编码
     * 11.12.13 类型编码
     * 14 网络标识编码
     * 15~20 设备 用户序号
     */
    @TableField(value = "device_id")
    @ApiModelProperty(value="设备国标编号")
    private String deviceId;

    /**
     * 设备名
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="设备名")
    private String name;

    /**
     * 生产厂商
     */
    @TableField(value = "manufacturer")
    @ApiModelProperty(value="生产厂商")
    private String manufacturer;

    /**
     * 型号
     */
    @TableField(value = "model")
    @ApiModelProperty(value="型号")
    private String model;

    /**
     * 风格名词
     */
    @TableField(value = "custom_name")
    @ApiModelProperty(value="风格名词")
    private String customName;

    /**
     * 固件版本
     */
    @TableField(value = "firmware")
    @ApiModelProperty(value="固件版本")
    private String firmware;

    /**
     * 传输协议 1.UDP 2.TCP
     */
    @TableField(value = "transport")
    @ApiModelProperty(value="传输协议 1.UDP 2.TCP")
    private Integer transport;

    /**
     * wan地址_ip
     */
    @TableField(value = "ip")
    @ApiModelProperty(value="wan地址_ip")
    private String ip;

    /**
     * wan地址_port
     */
    @TableField(value = "port")
    @ApiModelProperty(value="wan地址_port")
    private Integer port;

    /**
     * wan地址
     */
    @TableField(value = "host_address")
    @ApiModelProperty(value="wan地址")
    private String hostAddress;

    /**
     * 设备密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value="设备密码")
    private String password;

    /**
     * 收流IP
     */
    @TableField(value = "sdp_ip")
    @ApiModelProperty(value="收流IP")
    private String sdpIp;

    /**
     * SIP交互IP（设备访问平台的IP）
     */
    @TableField(value = "local_ip")
    @ApiModelProperty(value="SIP交互IP（设备访问平台的IP）")
    private String localIp;

    /**
     * 字符集, 1.UTF-8 2.GB2312
     */
    @TableField(value = "charset")
    @ApiModelProperty(value="字符集, 1.UTF-8 2.GB2312")
    private Integer charset;

    /**
     * 树类型 国标规定了两种树的展现方式 216.行政区划：CivilCode 215.业务分组:BusinessGroup
     */
    @TableField(value = "tree_type")
    @ApiModelProperty(value="树类型 国标规定了两种树的展现方式 216.行政区划：CivilCode 215.业务分组:BusinessGroup")
    private Integer treeType;

    /**
     * 地理坐标系， 目前支持 1.WGS84,2.GCJ02
     */
    @TableField(value = "geo_coord_sys")
    @ApiModelProperty(value="地理坐标系， 目前支持 1.WGS84,2.GCJ02")
    private Integer geoCoordSys;

    /**
     * 是否在线，1.为在线，0.为离线
     */
    @TableField(value = "`online`")
    @ApiModelProperty(value="是否在线，1.为在线，0.为离线")
    private Integer online;

    /**
     * 注册时间
     */
    @TableField(value = "register_time")
    @ApiModelProperty(value="注册时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date registerTime;

    /**
     * 续订时间
     */
    @TableField(value = "renew_time")
    @ApiModelProperty(value="注册时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date renewTime;

    /**
     * 心跳时间
     */
    @TableField(value = "keepalive_time")
    @ApiModelProperty(value="心跳时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date keepaliveTime;

    /**
     * 心跳间隔
     */
    @TableField(value = "keepalive_interval_time")
    @ApiModelProperty(value="心跳间隔")
    private Integer keepaliveIntervalTime;

    /**
     * 数据流传输模式 0.UDP:udp传输 1.TCP-PASSIVE：tcp被动模式 2.TCP-ACTIVE：tcp主动模式
     */
    @TableField(value = "stream_mode")
    @ApiModelProperty(value="数据流传输模式 0.UDP:udp传输 1.TCP-PASSIVE：tcp被动模式 2.TCP-ACTIVE：tcp主动模式")
    private Integer streamMode;

    /**
     * 注册有效期
     */
    @TableField(value = "expires")
    @ApiModelProperty(value="注册有效期")
    private Integer expires;

    /**
     * 移动设备位置信息上报时间间隔,单位:秒,默认值5
     */
    @TableField(value = "mobile_position_submission_interval")
    @ApiModelProperty(value="移动设备位置信息上报时间间隔,单位:秒,默认值5")
    private Integer mobilePositionSubmissionInterval;

    /**
     * 目录订阅周期，0为不订阅
     */
    @TableField(value = "subscribe_cycle_for_catalog")
    @ApiModelProperty(value="目录订阅周期，0为不订阅")
    private Integer subscribeCycleForCatalog;

    /**
     * 移动设备位置订阅周期，0为不订阅
     */
    @TableField(value = "subscribe_cycle_for_mobile_position")
    @ApiModelProperty(value="移动设备位置订阅周期，0为不订阅")
    private Integer subscribeCycleForMobilePosition;

    /**
     * 报警心跳时间订阅周期，0为不订阅
     */
    @TableField(value = "subscribe_cycle_for_alarm")
    @ApiModelProperty(value="报警心跳时间订阅周期，0为不订阅")
    private Integer subscribeCycleForAlarm;

    /**
     * 是否开启ssrc校验，默认关闭，开启可以防止串流
     */
    @TableField(value = "ssrc_check")
    @ApiModelProperty(value="是否开启ssrc校验，默认关闭，开启可以防止串流")
    private Integer ssrcCheck;

    /**
     * 流媒体编号 默认为null
     */
    @TableField(value = "media_server_id")
    @ApiModelProperty(value="流媒体编号 默认为null")
    private String mediaServerId;

    /**
     * 是否作为消息通道 1.是 0.否
     */
    @TableField(value = "as_message_channel")
    @ApiModelProperty(value="是否作为消息通道")
    private Integer asMessageChannel;

    /**
     * 是否开启超管权限(只有超级管理员才可查看) 1.是 0.否 默认 ：0
     */
    @TableField(value = "has_administrator")
    @ApiModelProperty(value="是否作为消息通道")
    private Integer hasAdministrator;

    /**
     * 开启主子码流切换的开关
     */
    @TableField(value = "switch_primary_sub_stream")
    @ApiModelProperty(value="开启主子码流切换的开关")
    private Integer switchPrimarySubStream;


    /**
     * 流媒体编号 默认为null
     */
    @TableField(exist = false)
    @ApiModelProperty(value="设备通道")
    private Integer channelCount = 0;
}