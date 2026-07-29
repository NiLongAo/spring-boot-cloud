package cn.com.tzy.springbootentity.param.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaServerSaveParam {

    private String id;

    @NotBlank(message = "服务IP不能为空")
    private String ip;

    @Min(value = 0, message = "SSL状态只能为0或1")
    @Max(value = 1, message = "SSL状态只能为0或1")
    private Integer sslStatus;

    @NotBlank(message = "Hook地址不能为空")
    private String hookIp;

    @NotBlank(message = "SDP IP不能为空")
    private String sdpIp;

    private String streamIp;

    @Min(value = 0, message = "HTTP端口不能小于0")
    @Max(value = 65535, message = "HTTP端口不能大于65535")
    private Integer httpPort;

    @Min(value = 0, message = "HTTPS端口不能小于0")
    @Max(value = 65535, message = "HTTPS端口不能大于65535")
    private Integer httpSslPort;

    @Min(value = 0, message = "RTMP端口不能小于0")
    @Max(value = 65535, message = "RTMP端口不能大于65535")
    private Integer rtmpPort;

    @Min(value = 0, message = "RTMPS端口不能小于0")
    @Max(value = 65535, message = "RTMPS端口不能大于65535")
    private Integer rtmpSslPort;

    @Min(value = 0, message = "RTP代理端口不能小于0")
    @Max(value = 65535, message = "RTP代理端口不能大于65535")
    private Integer rtpProxyPort;

    @Min(value = 0, message = "RTSP端口不能小于0")
    @Max(value = 65535, message = "RTSP端口不能大于65535")
    private Integer rtspPort;

    @Min(value = 0, message = "RTSPS端口不能小于0")
    @Max(value = 65535, message = "RTSPS端口不能大于65535")
    private Integer rtspSslPort;

    @Min(value = 0, message = "自动配置只能为0或1")
    @Max(value = 1, message = "自动配置只能为0或1")
    private Integer autoConfig;

    /** 写入时设置，详情脱敏，空值保持原值 */
    private String secret;

    @Min(value = 0, message = "RTP代理开关只能为0或1")
    @Max(value = 1, message = "RTP代理开关只能为0或1")
    private Integer rtpEnable;

    @Min(value = 0, message = "启用状态只能为0或1")
    @Max(value = 1, message = "启用状态只能为0或1")
    private Integer enable;

    private String rtpPortRange;

    private Integer recordAssistPort;

    @Min(value = 0, message = "默认节点只能为0或1")
    @Max(value = 1, message = "默认节点只能为0或1")
    private Integer defaultServer;

    private Integer hookAliveInterval;
    private String videoPlayPrefix;
    private String videoHttpPrefix;
}
