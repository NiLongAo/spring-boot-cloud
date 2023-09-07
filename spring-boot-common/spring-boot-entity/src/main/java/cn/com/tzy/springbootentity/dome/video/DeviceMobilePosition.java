package cn.com.tzy.springbootentity.dome.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
    * 移动位置信息
    */
@ApiModel(value="移动位置信息")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "video_device_mobile_position")
public class DeviceMobilePosition extends LongIdEntity {
    /**
     * 设备Id
     */
    @TableField(value = "device_id")
    @ApiModelProperty(value="设备Id")
    private String deviceId;

    /**
     * 通道Id
     */
    @TableField(value = "channel_id")
    @ApiModelProperty(value="通道Id")
    private String channelId;

    /**
     * 设备名称
     */
    @TableField(value = "device_name")
    @ApiModelProperty(value="设备名称")
    private String deviceName;

    /**
     * 通知时间
     */
    @TableField(value = "`time`")
    @ApiModelProperty(value="通知时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date time;

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
     * 海拔高度
     */
    @TableField(value = "altitude")
    @ApiModelProperty(value="海拔高度")
    private Double altitude;

    /**
     * 速度
     */
    @TableField(value = "speed")
    @ApiModelProperty(value="速度")
    private Double speed;

    /**
     * 方向
     */
    @TableField(value = "direction")
    @ApiModelProperty(value="方向")
    private Double direction;

    /**
     * 位置信息上报来源（Mobile Position、GPS Alarm）
     */
    @TableField(value = "report_source")
    @ApiModelProperty(value="位置信息上报来源（Mobile Position、GPS Alarm）")
    private String reportSource;

    /**
     * 国内坐标系：经度坐标
     */
    @TableField(value = "longitude_gcj02")
    @ApiModelProperty(value="国内坐标系：经度坐标")
    private Double longitudeGcj02;

    /**
     * 国内坐标系：纬度坐标
     */
    @TableField(value = "latitude_gcj02")
    @ApiModelProperty(value="国内坐标系：纬度坐标")
    private Double latitudeGcj02;

    /**
     * 国内坐标系：经度坐标
     */
    @TableField(value = "longitude_wgs84")
    @ApiModelProperty(value="国内坐标系：经度坐标")
    private Double longitudeWgs84;

    /**
     * 国内坐标系：纬度坐标
     */
    @TableField(value = "latitude_wgs84")
    @ApiModelProperty(value="国内坐标系：纬度坐标")
    private Double latitudeWgs84;
}