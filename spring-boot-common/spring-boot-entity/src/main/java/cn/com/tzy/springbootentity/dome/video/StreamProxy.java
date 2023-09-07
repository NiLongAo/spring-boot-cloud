package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 拉流代理的信息
    */
@ApiModel(value="拉流代理的信息")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_stream_proxy")
public class StreamProxy extends LongIdEntity {
    /**
     * 类型
     */
    @TableField(value = "type")
    @ApiModelProperty(value="类型")
    private Integer type;

    /**
     * 应用名
     */
    @TableField(value = "app")
    @ApiModelProperty(value="应用名")
    private String app;

    /**
     * 流ID
     */
    @TableField(value = "stream")
    @ApiModelProperty(value="流ID")
    private String stream;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;

    /**
     * 流媒体服务ID
     */
    @TableField(value = "media_server_id")
    @ApiModelProperty(value="流媒体服务ID")
    private String mediaServerId;

    /**
     * 拉流地址
     */
    @TableField(value = "url")
    @ApiModelProperty(value="拉流地址")
    private String url;

    /**
     * 拉流地址
     */
    @TableField(value = "src_url")
    @ApiModelProperty(value="拉流地址")
    private String srcUrl;

    /**
     * 目标地址
     */
    @TableField(value = "dst_url")
    @ApiModelProperty(value="目标地址")
    private String dstUrl;

    /**
     * 超时时间
     */
    @TableField(value = "timeout_ms")
    @ApiModelProperty(value="超时时间")
    private Integer timeoutMs;

    /**
     * ffmpeg模板KEY
     */
    @TableField(value = "ffmpeg_cmd_key")
    @ApiModelProperty(value="ffmpeg模板KEY")
    private String ffmpegCmdKey;

    /**
     * rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播
     */
    @TableField(value = "rtp_type")
    @ApiModelProperty(value="rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播")
    private Integer rtpType;

    /**
     * 是否启用
     */
    @TableField(value = "`enable`")
    @ApiModelProperty(value="是否启用")
    private Integer enable;

    /**
     * 是否启用音频
     */
    @TableField(value = "enable_audio")
    @ApiModelProperty(value="是否启用音频")
    private Integer enableAudio;

    /**
     * 是否启用MP4
     */
    @TableField(value = "enable_mp4")
    @ApiModelProperty(value="是否启用MP4")
    private Integer enableMp4;

    /**
     * 是否 无人观看时删除
     */
    @TableField(value = "enable_remove_none_reader")
    @ApiModelProperty(value="是否 无人观看时删除")
    private Integer enableRemoveNoneReader;

    /**
     * 是否 无人观看时自动停用
     */
    @TableField(value = "enable_disable_none_reader")
    @ApiModelProperty(value="是否 无人观看时自动停用")
    private Integer enableDisableNoneReader;
}