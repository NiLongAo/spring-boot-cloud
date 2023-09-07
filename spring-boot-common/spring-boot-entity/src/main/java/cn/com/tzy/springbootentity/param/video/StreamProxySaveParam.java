package cn.com.tzy.springbootentity.param.video;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@ApiModel("拉流请求类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamProxySaveParam {

    /**
     * 类型
     */
    @ApiModelProperty(value="类型")
    private Integer type;

    /**
     * 应用名
     */
    @ApiModelProperty(value="应用名")
    private String app;

    /**
     * 流id
     */
    @ApiModelProperty(value="流id")
    private String stream;

    /**
     * 国标ID
     */
    @ApiModelProperty(value="国标ID")
    private String gbId;

    /**
     * 名称
     */
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 经度
     */
    @ApiModelProperty(value="经度")
    private Double longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value="纬度")
    private Double latitude;

    /**
     * 流媒体服务ID
     */
    @TableField(value = "media_server_id")
    private String mediaServerId;

    /**
     * 拉流地址
     */
    @ApiModelProperty(value="拉流地址")
    private String url;

    /**
     * 拉流地址
     */
    @ApiModelProperty(value="拉流地址")
    private String srcUrl;

    /**
     * 超时时间
     */
    @ApiModelProperty(value="超时时间")
    private Integer timeoutMs;

    /**
     * ffmpeg模板KEY
     */
    @ApiModelProperty(value="ffmpeg模板KEY")
    private String ffmpegCmdKey;

    /**
     * rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播
     */
    @ApiModelProperty(value="rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播")
    private Integer rtpType;

    /**
     * 是否启用
     */
    @ApiModelProperty(value="是否启用")
    private Integer enable;

    /**
     * 是否启用音频
     */
    @ApiModelProperty(value="是否启用音频")
    private Integer enableAudio;

    /**
     * 是否启用MP4
     */
    @ApiModelProperty(value="是否启用MP4")
    private Integer enableMp4;

    /**
     * 是否 无人观看时删除
     */
    @ApiModelProperty(value="是否 无人观看时删除")
    private Integer enableRemoveNoneReader;

    /**
     * 是否 无人观看时自动停用
     */
    @ApiModelProperty(value="是否 无人观看时自动停用")
    private Integer enableDisableNoneReader;


    public String getSchemaFromFFmpegCmd() {
        if(StringUtils.isEmpty(ffmpegCmdKey)){
            return null;
        }
        ffmpegCmdKey = ffmpegCmdKey.replaceAll(" + ", " ");
        String[] paramArray = ffmpegCmdKey.split(" ");
        if (paramArray.length == 0) {
            return null;
        }
        for (int i = 0; i < paramArray.length; i++) {
            if (paramArray[i].equalsIgnoreCase("-f")) {
                if (i + 1 < paramArray.length - 1) {
                    return paramArray[i+1];
                }else {
                    return null;
                }

            }
        }
        return null;
    }
    
}
