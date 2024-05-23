package cn.com.tzy.springbootstartervideobasic.vo.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
    * 报警信息
    */
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAlarmVo extends LongIdEntity {
    /**
     * 设备的国标编号
     */
    private String deviceId;

    /**
     * 通道的国标编号
     */
    private String channelId;

    /**
     * 报警级别, 1为一级警情, 2为二级警情, 3为三级警情, 4为四级警情
     */
    private Integer alarmPriority;

    /**
     * 报警方式 , 1为电话报警, 2为设备报警, 3为短信报警, 4为 GPS报警, 5为视频报警, 6为设备故障报警,7其他报警;可以为直接组合如12为电话报警或 设备报警-
     */
    private Integer alarmMethod;

    /**
     * 报警时间
     */
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date alarmTime;

    /**
     * 报警内容描述
     */
    private String alarmDescription;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 报警类型 报警方式为2时， 1-视频丢失报警 2-设备防拆报警 3-存储设备磁盘满报警 4-设备高温报警 5-设备低温报警， 报警方式为5时,取值如下，1-人工视频报警 2-运动目标检测报警 3-遗留物检测报警 4-物体移除检测报警 5-绊线检测报警  6-入侵检测报警 7-逆行检测报警 8-徘徊检测报警 9-流量统计报警 10-密度检测报警 11-视频异常检测报警 12-快速移动报警 报警方式为6时,取值下， 1-存储设备磁盘故障报警 2-存储设备风扇故障报警
     */
    private Integer alarmType;
}