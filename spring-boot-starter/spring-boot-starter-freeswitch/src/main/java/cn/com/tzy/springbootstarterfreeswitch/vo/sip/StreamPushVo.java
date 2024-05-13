package cn.com.tzy.springbootstarterfreeswitch.vo.sip;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedHookVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 推流信息
 * 第三方给自己推流的信息
*/
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class StreamPushVo extends LongIdEntity {
    /**
     * 应用名
     */
    private String app;

    /**
     * 流id
     */
    private String stream;

    /**
     * 观看总人数，包括hls/rtsp/rtmp/http-flv/ws-flv
     */
    private Integer totalReaderCount;

    /**
     * 产生源类型 unknown = 0,rtmp_push=1,rtsp_push=2,rtp_push=3,pull=4,ffmpeg_pull=5,mp4_vod=6,device_chn=7
     */
    private Integer originType;

    /**
     * 产生源类型的字符串描述
     */
    private String originTypeStr;

    /**
     * 存活时间，单位秒
     */
    private Integer aliveSecond;

    /**
     * 使用的流媒体ID
     */
    private String mediaServerId;

    /**
     * 使用的服务ID
     */
    private String serverId;

    /**
     * 推流时间
     */
    private Date pushTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否正在推流
     */
    private Integer pushIng;


    public StreamPushVo transform(OnStreamChangedHookVo vo){
        this.app = vo.getApp();
        this.stream = vo.getStream();
        this.totalReaderCount = vo.getTotalReaderCount();
        this.originType = vo.getOriginType();
        this.originTypeStr = vo.getOriginTypeStr();
        this.aliveSecond = Math.toIntExact(vo.getAliveSecond());
        this.mediaServerId = vo.getMediaServerId();
        this.serverId= vo.getMediaServerId();
        this.pushTime = new Date();
        this.status = ConstEnum.Flag.YES.getValue();
        this.pushIng = vo.isRegist()?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
        return  this;
    }
}