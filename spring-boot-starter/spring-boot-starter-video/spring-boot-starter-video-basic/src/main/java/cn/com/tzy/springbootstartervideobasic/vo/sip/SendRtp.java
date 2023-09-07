package cn.com.tzy.springbootstartervideobasic.vo.sip;

import cn.com.tzy.springbootstartervideobasic.enums.InviteStreamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendRtp {
    /**
     * 推流ip
     */
    private String ip;
    /**
     * 推流端口
     */
    private int port;
    /**
     * 推流标识
     */
    private String ssrc;
    /**
     * 平台id
     */
    private String platformId;
     /**
     * 对应设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 直播流的应用名
     */
    private String app;
    /**
     * 设备推流的streamId
     */
    private String streamId;
    /**
     * 推流状态
     * 0 等待设备推流上来
     * 1 等待上级平台回复ack
     * 2 推流中
     * 3.推流失败
     */
    private int status = 0;
    /**
     * 是否为tcp
     */
    private boolean tcp;
    /**
     * 是否为tcp主动模式
     */
    private boolean tcpActive;
    /**
     * 自己推流使用的端口
     */
    private Integer localPort;
    /**
     * 使用的流媒体
     */
    private String mediaServerId;
    /**
     * 使用的服务的ID
     */
    private String serverId;
    /**
     *  invite 的 callId
     */
    private String callId;
    /**
     *  invite 的 fromTag
     */
    private String fromTag;
    /**
     *  invite 的 toTag
     */
    private String toTag;
    /**
     * 发送时，rtp的pt（uint8_t）,不传时默认为96
     */
    private int pt = 96;
    /**
     * 发送时，rtp的负载类型。为true时，负载为ps；为false时，为es；
     */
    private boolean usePs = true;
    /**
     * 当usePs 为false时，有效。为1时，发送音频；为0时，发送视频；不传时默认为0
     */
    private boolean onlyAudio = false;
    /**
     * 是否开启rtcp保活
     */
    private boolean rtcp = false;
    /**
     * 播放类型
     */
    private String sessionName ;
    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long stopTime;

    /**
     * 播放类型
     */
    private InviteStreamType playType;

}
