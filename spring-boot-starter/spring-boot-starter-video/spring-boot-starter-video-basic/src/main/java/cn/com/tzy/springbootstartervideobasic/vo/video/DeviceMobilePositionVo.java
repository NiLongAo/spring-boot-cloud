package cn.com.tzy.springbootstartervideobasic.vo.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
    * 移动位置信息
    */
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceMobilePositionVo extends LongIdEntity {
    /**
     * 设备Id
     */
    private String deviceId;

    /**
     * 通道Id
     */
    private String channelId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通知时间
     */
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date time;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 海拔高度
     */
    private Double altitude;

    /**
     * 速度
     */
    private Double speed;

    /**
     * 方向
     */
    private Double direction;

    /**
     * 位置信息上报来源（Mobile Position、GPS Alarm）
     */
    private String reportSource;

    /**
     * 国内坐标系：经度坐标
     */
    private Double longitudeGcj02;

    /**
     * 国内坐标系：纬度坐标
     */
    private Double latitudeGcj02;

    /**
     * 国内坐标系：经度坐标
     */
    private Double longitudeWgs84;

    /**
     * 国内坐标系：纬度坐标
     */
    private Double latitudeWgs84;
}