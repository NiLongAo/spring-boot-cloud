package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
    * 平台信息
    */
@ApiModel(value="平台信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_parent_platform")
public class ParentPlatform extends LongIdEntity {
    /**
     * 是否启用
     */
    @TableField(value = "`enable`")
    @ApiModelProperty(value="是否启用")
    private Integer enable;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * SIP服务国标编码
     */
    @TableField(value = "server_gb_id")
    @ApiModelProperty(value="SIP服务国标编码")
    private String serverGbId;

    /**
     * SIP服务国标域
     */
    @TableField(value = "server_gb_domain")
    @ApiModelProperty(value="SIP服务国标域")
    private String serverGbDomain;

    /**
     * SIP服务IP
     */
    @TableField(value = "server_ip")
    @ApiModelProperty(value="SIP服务IP")
    private String serverIp;

    /**
     * SIP服务端口
     */
    @TableField(value = "server_port")
    @ApiModelProperty(value="SIP服务端口")
    @Max(message = "端口错误,最大65535",value = 65535,groups = {BaseModel.add.class})
    @Min(message = "端口错误,最小0",value = 0,groups = {BaseModel.add.class})
    private Integer serverPort;

    /**
     * SIP认证用户名(默认使用设备国标编号)
     */
    @TableField(value = "username")
    @ApiModelProperty(value="SIP认证用户名(默认使用设备国标编号)")
    private String username;

    /**
     * SIP认证密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value="SIP认证密码")
    private String password;

    /**
     * 设备国标编号
     */
    @TableField(value = "device_gb_id")
    @ApiModelProperty(value="设备国标编号")
    private String deviceGbId;

    /**
     * 设备ip
     */
    @TableField(value = "device_ip")
    @ApiModelProperty(value="设备ip")
    private String deviceIp;

    /**
     * 设备端口
     */
    @TableField(value = "device_port")
    @ApiModelProperty(value="设备端口")
    private Integer devicePort;



    /**
     * 注册周期 (秒)
     */
    @TableField(value = "expires")
    @ApiModelProperty(value="注册周期 (秒)")
    private Integer expires;

    /**
     * 心跳周期(秒)
     */
    @TableField(value = "keep_timeout")
    @ApiModelProperty(value="心跳周期(秒)")
    private Integer keepTimeout;

    /**
     * 传输协议 1.UDP 2.TCP
     */
    @TableField(value = "transport")
    @ApiModelProperty(value="传输协议 1.UDP 2.TCP")
    private Integer transport;

    /**
     * 字符集, 1.UTF-8 2.GB2312
     */
    @TableField(value = "character_set")
    @ApiModelProperty(value="字符集, 1.UTF-8 2.GB2312")
    private Integer characterSet;

    /**
     * 默认目录Id,自动添加的通道多放在这个目录下
     */
    @TableField(value = "catalog_id")
    @ApiModelProperty(value="默认目录Id,自动添加的通道多放在这个目录下")
    private String catalogId;

    /**
     * 目录分组-每次向上级发送通道信息时单个包携带的通道数量，取值1,2,4,8
     */
    @TableField(value = "catalog_group")
    @ApiModelProperty(value="目录分组-每次向上级发送通道信息时单个包携带的通道数量，取值1,2,4,8")
    private Integer catalogGroup;

    /**
     * 是否允许云台控制
     */
    @TableField(value = "ptz")
    @ApiModelProperty(value="是否允许云台控制")
    private Integer ptz;

    /**
     * RTCP流保活
     */
    @TableField(value = "rtcp")
    @ApiModelProperty(value="RTCP流保活")
    private Integer rtcp;

    /**
     * 在线状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="在线状态")
    private Integer status;

    /**
     * 点播未推流的设备时是否拉起
     */
    @TableField(value = "start_offline_push")
    @ApiModelProperty(value="点播未推流的设备时是否使用redis通知拉起")
    private Integer startOfflinePush;

    /**
     * 行政区划
     */
    @TableField(value = "administrative_division")
    @ApiModelProperty(value="行政区划")
    private String administrativeDivision;

    /**
     * 树类型 国标规定了两种树的展现方式 216.行政区划：CivilCode 215.业务分组:BusinessGroup
     */
    @TableField(value = "tree_type")
    @ApiModelProperty(value="树类型 国标规定了两种树的展现方式 216.行政区划：CivilCode 215.业务分组:BusinessGroup")
    private Integer treeType;


    /**
     * 是否作为消息通道 1.是 0.否
     */
    @TableField(value = "as_message_channel")
    @ApiModelProperty(value="是否作为消息通道")
    private Integer asMessageChannel;
}