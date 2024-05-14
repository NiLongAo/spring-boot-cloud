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
    * 平台信息
    */
@ApiModel(description="平台信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_platform")
public class Platform extends LongIdEntity {
    /**
     * 本机IP
     */
    @TableField(value = "local_ip")
    @ApiModelProperty(value="本机IP")
    private String localIp;

    /**
     * 外网IP
     */
    @TableField(value = "remote_ip")
    @ApiModelProperty(value="外网IP")
    private String remoteIp;

    /**
     * 注册端口
     */
    @TableField(value = "internal_port")
    @ApiModelProperty(value="注册端口")
    private Integer internalPort;

    /**
     * 中继端口
     */
    @TableField(value = "external_port")
    @ApiModelProperty(value="中继端口")
    private Integer externalPort;

    /**
     * 起始RTP端口
     */
    @TableField(value = "start_rtp_port")
    @ApiModelProperty(value="起始RTP端口")
    private Integer startRtpPort;

    /**
     * 结束RTP端口
     */
    @TableField(value = "end_rtp_port")
    @ApiModelProperty(value="结束RTP端口")
    private Integer endRtpPort;

    /**
     * ws端口
     */
    @TableField(value = "ws_port")
    @ApiModelProperty(value="ws端口")
    private Integer wsPort;

    /**
     * wss端口
     */
    @TableField(value = "wss_port")
    @ApiModelProperty(value="wss端口")
    private Integer wssPort;

    /**
     * 音频编码
     */
    @TableField(value = "audio_code")
    @ApiModelProperty(value="音频编码")
    private String audioCode;

    /**
     * 视频编码
     */
    @TableField(value = "video_code")
    @ApiModelProperty(value="视频编码")
    private String videoCode;

    /**
     * 分辨率
     */
    @TableField(value = "frame_rate")
    @ApiModelProperty(value="分辨率")
    private String frameRate;

    /**
     * 码率
     */
    @TableField(value = "bit_rate")
    @ApiModelProperty(value="码率")
    private String bitRate;

    /**
     * 是否启动ice
     */
    @TableField(value = "ice_start")
    @ApiModelProperty(value="是否启动ice")
    private Integer iceStart;

    /**
     * stun地址
     */
    @TableField(value = "stun_address")
    @ApiModelProperty(value="stun地址")
    private String stunAddress;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 是否启用
     */
    @TableField(value = "`enable`")
    @ApiModelProperty(value="是否启用")
    private Integer enable;

    /**
     * 在线状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="在线状态")
    private Integer status;

    /**
     * 是否开启音频
     */
    @TableField(value = "audio_record")
    @ApiModelProperty(value="是否开启音频")
    private Integer audioRecord;

    /**
     * 是否开启视频
     */
    @TableField(value = "video_record")
    @ApiModelProperty(value="是否开启视频")
    private Integer videoRecord;

    /**
     * 音频存储地址
     */
    @TableField(value = "audio_record_path")
    @ApiModelProperty(value="音频存储地址")
    private String audioRecordPath;

    /**
     * 视频存储地址
     */
    @TableField(value = "video_record_path")
    @ApiModelProperty(value="视频存储地址")
    private String videoRecordPath;

    /**
     * 声音文件地址
     */
    @TableField(value = "sound_rile_path")
    @ApiModelProperty(value="声音文件地址")
    private String soundRilePath;

    /**
     * 交换服务文件路径
     */
    @TableField(value = "freeswitch_path")
    @ApiModelProperty(value="交换服务文件路径")
    private String freeswitchPath;

    /**
     * 交换服务日志路径
     */
    @TableField(value = "freeswitch_log_path")
    @ApiModelProperty(value="交换服务日志路径")
    private String freeswitchLogPath;
}