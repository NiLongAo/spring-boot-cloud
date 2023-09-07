package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.Base;
import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 直播流关联国标上级平台
    */
@ApiModel(value="直播流关联国标上级平台")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_gb_stream")
public class GbStream extends Base {
    /**
     * 主键id
     */
    @TableId(value = "gb_stream_id", type = IdType.AUTO)
    @ApiModelProperty(value="主键id")
    private Long gbStreamId;

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
     * 国标ID
     */
    @TableField(value = "gb_id")
    @ApiModelProperty(value="国标ID")
    private String gbId;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    @ApiModelProperty(value="经度")
    private Double longitude;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    @ApiModelProperty(value="纬度")
    private Double latitude;

    /**
     * 流类型（1.拉流/2.推流）
     */
    @TableField(value = "stream_type")
    @ApiModelProperty(value="流类型（1.拉流/2.推流）")
    private Integer streamType;

    /**
     * 流媒体ID
     */
    @TableField(value = "media_server_id")
    @ApiModelProperty(value="流媒体ID")
    private String mediaServerId;


    /**
     * 平台国标ID
     */
    @TableField(exist = false)
    private String platformId;

    /**
     * 目录ID
     */
    @TableField(exist = false)
    private String catalogId;
}