package cn.com.tzy.springbootentity.vo.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaServerDetailVo {
    private String id;
    private String ip;
    private Integer sslStatus;
    private String hookIp;
    private String sdpIp;
    private String streamIp;
    private Integer httpPort;
    private Integer httpSslPort;
    private Integer rtmpPort;
    private Integer rtmpSslPort;
    private Integer rtpProxyPort;
    private Integer rtspPort;
    private Integer rtspSslPort;
    private Integer autoConfig;
    /** 脱敏，不回显原始值 */
    private String secret;
    private Integer rtpEnable;
    private Integer enable;
    private String rtpPortRange;
    private Integer recordAssistPort;
    private Integer defaultServer;
    private Integer hookAliveInterval;
    private String videoPlayPrefix;
    private String videoHttpPrefix;
    /** 只读运行时字段 */
    private Date keepaliveTime;
    private Integer status;
}
