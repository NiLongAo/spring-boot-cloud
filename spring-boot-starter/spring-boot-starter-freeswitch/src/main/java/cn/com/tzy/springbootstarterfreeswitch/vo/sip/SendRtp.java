package cn.com.tzy.springbootstarterfreeswitch.vo.sip;

import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteStreamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sip.SipException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendRtp {
    /**
     * 对应设备id
     */
    private String agentKey;
    /**
     *  invite 的 callId
     */
    private String callId;
    /**
     * 使用的流媒体
     */
    private String mediaServerId;
    /**
     * 推送上来的流
     */
    private String pushStreamId;
    /**
     * 音频推流信息
     */
    private SendRtpInfo videoInfo;
    /**
     * 视频推流信息
     */
    private SendRtpInfo audioInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendRtpInfo {
        /**
         * 推流ip
         */
        private String ip;
        /**
         * 推流端口
         */
        private Integer port;
        /**
         * 推流标识
         */
        private String ssrc;

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
        @Builder.Default
        private int status = 0;
        /**
         * 是否为tcp
         */
        @Builder.Default
        private Boolean tcp =false;
        /**
         * 是否为tcp主动模式
         */
        private Boolean tcpActive =false;
        /**
         * 自己推流使用的端口
         */
        private Integer localPort;

        /**
         * 使用的服务的ID
         */
        private String serverId;
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
        @Builder.Default
        private int pt = 96;
        /**
         * 发送时，rtp的负载类型。为true时，负载为ps；为false时，为es；
         */
        @Builder.Default
        private boolean usePs = true;
        /**
         * 当usePs 为false时，有效。为1时，发送音频；为0时，发送视频；不传时默认为0
         */
        @Builder.Default
        private boolean onlyAudio = false;
        /**
         * 是否开启rtcp保活
         */
        @Builder.Default
        private boolean rtcp = false;
        /**
         * 播放类型
         */
        private String sessionName ;

        /**
         * 播放类型
         */
        private InviteStreamType playType;
        /**
         * 发送rtp同时接收，一般用于双向语言对讲, 如果不为空，说明开启接收，值为接收流的id
         */
        private String recvStreamId;

        public void initKeepPort(MediaServerVo mediaServerVo) throws SipException {
            if(localPort == 0){
                localPort = MediaClient.keepPort(mediaServerVo,localPort,tcpActive?2:tcp?1:0, streamId);
            }
            if (localPort == 0) {
                throw new SipException("流推送端口开启错误");
            }
        }
    }

    /**
     *
     * @param ip 推流ip(对方的IP)
     * @param pushPort 推流端口(对方的端口)
     * @param sendPort 自己推流使用的端口
     * @param ssrc 推流唯一标识
     * @param app appId
     * @param streamId streamId
     * @param tcp 是否为tcp
     * @param tcpActive 是否为tcp主动模式
     * @param serverId 服务id
     * @param rtcp 是否为RTCP流保活
     * @param type 发送rtp类型
     */
    public static SendRtpInfo createSendRtpInfo(
            String sessionName,
            String ip,
            Integer pushPort,
            Integer sendPort,
            String ssrc,
            String app,
            String streamId,
            String recvStreamId,
            Boolean tcp,
            Boolean tcpActive,
            String serverId,
            Boolean rtcp,
            InviteStreamType type){
        return SendRtpInfo.builder()
                .sessionName(sessionName)
                .ip(ip)
                .port(pushPort)
                .ssrc(ssrc)
                .app(app)
                .streamId(streamId)
                .recvStreamId(recvStreamId)
                .status(0)
                .tcp(tcp)
                .tcpActive(tcpActive)
                .localPort(sendPort)
                .serverId(serverId)
                .fromTag(null)
                .toTag(null)
                .pt(96)
                .usePs(true)
                .onlyAudio(false)
                .rtcp(rtcp)
                .playType(type)
                .build();
    }


}
