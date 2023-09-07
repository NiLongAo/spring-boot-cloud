package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
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
    * 流媒体服务信息
    */
@ApiModel(value="流媒体服务信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_media_server")
public class MediaServer extends StringIdEntity {
    /**
     * IP
     */
    @TableField(value = "ip")
    @ApiModelProperty(value="IP")
    private String ip;

    /**
     * hook使用的IP（zlm访问 使用的IP）
     */
    @TableField(value = "ssl_status")
    @ApiModelProperty(value="hook使用的IP（zlm访问 使用的IP）")
    private Integer sslStatus;

    /**
     * hook使用的IP（zlm访问 使用的IP）
     */
    @TableField(value = "hook_ip")
    @ApiModelProperty(value="hook使用的IP（zlm访问 使用的IP）")
    private String hookIp;

    /**
     * SDP IP
     */
    @TableField(value = "sdp_ip")
    @ApiModelProperty(value="SDP IP")
    private String sdpIp;

    /**
     * 流IP
     */
    @TableField(value = "stream_ip")
    @ApiModelProperty(value="流IP")
    private String streamIp;

    /**
     * HTTP端口
     */
    @TableField(value = "http_port")
    @ApiModelProperty(value="HTTP端口")
    private Integer httpPort;

    /**
     * HTTPS端口
     */
    @TableField(value = "http_ssl_port")
    @ApiModelProperty(value="HTTPS端口")
    private Integer httpSslPort;

    /**
     * RTMP端口
     */
    @TableField(value = "rtmp_port")
    @ApiModelProperty(value="RTMP端口")
    private Integer rtmpPort;

    /**
     * RTMPS端口
     */
    @TableField(value = "rtmp_ssl_port")
    @ApiModelProperty(value="RTMPS端口")
    private Integer rtmpSslPort;

    /**
     * RTP收流端口（单端口模式有用）
     */
    @TableField(value = "rtp_proxy_port")
    @ApiModelProperty(value="RTP收流端口（单端口模式有用）")
    private Integer rtpProxyPort;

    /**
     * RTSP端口
     */
    @TableField(value = "rtsp_port")
    @ApiModelProperty(value="RTSP端口")
    private Integer rtspPort;

    /**
     * RTSPS端口
     */
    @TableField(value = "rtsp_ssl_port")
    @ApiModelProperty(value="RTSPS端口")
    private Integer rtspSslPort;

    /**
     * 是否开启自动配置ZLM
     */
    @TableField(value = "auto_config")
    @ApiModelProperty(value="是否开启自动配置ZLM")
    private Integer autoConfig;

    /**
     * ZLM鉴权参数
     */
    @TableField(value = "secret")
    @ApiModelProperty(value="ZLM鉴权参数")
    private String secret;

    /**
     * 是否使用多端口模式
     */
    @TableField(value = "rtp_enable")
    @ApiModelProperty(value="是否使用多端口模式")
    private Integer rtpEnable;

    /**
     * 启用状态
     */
    @TableField(value = "`enable`")
    @ApiModelProperty(value="启用状态")
    private Integer enable;

    /**
     * 心跳时间
     */
    @TableField(value = "keepalive_time")
    @ApiModelProperty(value = "心跳时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date keepaliveTime;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;

    /**
     * 多端口RTP收流端口范围
     */
    @TableField(value = "rtp_port_range")
    @ApiModelProperty(value="多端口RTP收流端口范围")
    private String rtpPortRange;

    /**
     * assist服务端口
     */
    @TableField(value = "record_assist_port")
    @ApiModelProperty(value="assist服务端口")
    private Integer recordAssistPort;

    /**
     * 是否是默认ZLM
     */
    @TableField(value = "default_server")
    @ApiModelProperty(value="是否是默认ZLM")
    private Integer defaultServer;

    /**
     * keepalive hook触发间隔,单位秒
     */
    @TableField(value = "hook_alive_interval")
    @ApiModelProperty(value="keepalive hook触发间隔,单位秒")
    private Integer hookAliveInterval;

    /**
     * 流媒体播放 代理前缀
     */
    @TableField(value = "video_play_prefix")
    @ApiModelProperty(value="多端口RTP收流端口范围")
    private String  videoPlayPrefix;

    /**
     * video请求时前缀
     */
    @TableField(value = "video_http_prefix")
    @ApiModelProperty(value="多端口RTP收流端口范围")
    private String  videoHttpPrefix;
}