package cn.com.tzy.springbootentity.dome.video;

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

import java.util.Date;

/**
    * 推流信息
    */
@ApiModel(value="推流信息")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_stream_push")
public class StreamPush extends LongIdEntity {
    /**
     * 应用名
     */
    @TableField(value = "app")
    @ApiModelProperty(value="应用名")
    private String app;

    /**
     * 流id
     */
    @TableField(value = "stream")
    @ApiModelProperty(value="流id")
    private String stream;

    /**
     * 观看总人数，包括hls/rtsp/rtmp/http-flv/ws-flv
     */
    @TableField(value = "total_reader_count")
    @ApiModelProperty(value="观看总人数，包括hls/rtsp/rtmp/http-flv/ws-flv")
    private String totalReaderCount;

    /**
     * 产生源类型 unknown = 0,rtmp_push=1,rtsp_push=2,rtp_push=3,pull=4,ffmpeg_pull=5,mp4_vod=6,device_chn=7
     */
    @TableField(value = "origin_type")
    @ApiModelProperty(value="产生源类型 unknown = 0,rtmp_push=1,rtsp_push=2,rtp_push=3,pull=4,ffmpeg_pull=5,mp4_vod=6,device_chn=7")
    private Integer originType;

    /**
     * 产生源类型的字符串描述
     */
    @TableField(value = "origin_type_str")
    @ApiModelProperty(value="产生源类型的字符串描述")
    private String originTypeStr;

    /**
     * 存活时间，单位秒
     */
    @TableField(value = "alive_second")
    @ApiModelProperty(value="存活时间，单位秒")
    private Integer aliveSecond;

    /**
     * 使用的流媒体ID
     */
    @TableField(value = "media_server_id")
    @ApiModelProperty(value="使用的流媒体ID")
    private String mediaServerId;

    /**
     * 使用的流媒体服务ID
     */
    @TableField(value = "server_id")
    @ApiModelProperty(value="使用的服务ID")
    private String serverId;

    /**
     * 推流时间
     */
    @TableField(value = "push_time")
    @ApiModelProperty(value="推流时间")
    private Date pushTime;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;

    /**
     * 是否正在推流
     */
    @TableField(value = "push_ing")
    @ApiModelProperty(value="是否正在推流")
    private Integer pushIng;

    /**
     * 是否自己平台的推流
     */
    @TableField(value = "self")
    @ApiModelProperty(value="是否自己平台的推流")
    private Integer onSelf;
}