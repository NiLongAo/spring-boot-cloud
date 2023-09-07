package cn.com.tzy.springbootentity.param.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@ApiModel("流媒体请求类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaServerSaveParam{
    /**
     * 流媒体主键
     */
    @ApiModelProperty(value="流媒体主键")
    private String id;
    /**
     * IP
     */
    @ApiModelProperty(value="IP")
    private String ip;

    /**
     * hook使用的IP（zlm访问 使用的IP）
     */
    @ApiModelProperty(value="hook使用的IP（zlm访问 使用的IP）")
    private Integer sslStatus;
    /**
     * hook使用的IP（zlm访问 使用的IP）
     */
    @ApiModelProperty(value="hook使用的IP（zlm访问 使用的IP）")
    private String hookIp;
    /**
     * SDP IP
     */
    @ApiModelProperty(value="SDP IP")
    private String sdpIp;

    /**
     * 流IP
     */
    @ApiModelProperty(value="流IP")
    private String streamIp;

    /**
     * HTTP端口
     */
    @ApiModelProperty(value="HTTP端口")
    private Integer httpPort;

    /**
     * HTTPS端口
     */
    @ApiModelProperty(value="HTTPS端口")
    private Integer httpSslPort;

    /**
     * RTMP端口
     */
    @ApiModelProperty(value="RTMP端口")
    private Integer rtmpPort;

    /**
     * RTMPS端口
     */
    @ApiModelProperty(value="RTMPS端口")
    private Integer rtmpSslPort;

    /**
     * RTP收流端口（单端口模式有用）
     */
    @ApiModelProperty(value="RTP收流端口（单端口模式有用）")
    private Integer rtpProxyPort;

    /**
     * RTSP端口
     */
    @ApiModelProperty(value="RTSP端口")
    private Integer rtspPort;

    /**
     * RTSPS端口
     */
    @ApiModelProperty(value="RTSPS端口")
    private Integer rtspSslPort;

    /**
     * 是否开启自动配置ZLM
     */
    @ApiModelProperty(value="是否开启自动配置ZLM")
    private Integer autoConfig;

    /**
     * ZLM鉴权参数
     */
    @ApiModelProperty(value="ZLM鉴权参数")
    private String secret;

    /**
     * 是否使用多端口模式
     */
    @ApiModelProperty(value="是否使用多端口模式")
    private Integer rtpEnable;

    /**
     * 多端口RTP收流端口范围
     */
    @ApiModelProperty(value="多端口RTP收流端口范围")
    private String rtpPortRange;

    /**
     * assist服务端口
     */
    @ApiModelProperty(value="assist服务端口")
    private Integer recordAssistPort;

    /**
     * 是否是默认ZLM
     */
    @ApiModelProperty(value="是否是默认ZLM")
    private Integer defaultServer;

    /**
     * keepalive hook触发间隔,单位秒
     */
    @ApiModelProperty(value="keepalive hook触发间隔,单位秒")
    private Integer hookAliveInterval;
    /**
     * 启用状态
     */
    @ApiModelProperty(value="是否是默认ZLM")
    private Integer enable;

    /**
     * 心跳时间
     */
    @ApiModelProperty(value="keepalive hook触发间隔,单位秒")
    private Date keepaliveTime;

    /**
     * 流媒体播放 代理前缀
     */
    @ApiModelProperty(value="流媒体播放 代理前缀")
    private String  videoPlayPrefix;

    /**
     * video请求时前缀
     */
    @ApiModelProperty(value="video请求时前缀")
    private String  videoHttpPrefix;
}
