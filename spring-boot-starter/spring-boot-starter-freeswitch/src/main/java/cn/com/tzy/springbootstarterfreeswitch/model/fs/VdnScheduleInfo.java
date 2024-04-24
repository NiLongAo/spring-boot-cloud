package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class VdnScheduleInfo implements Serializable,Comparable<VdnScheduleInfo> {
    /**
     * PK
     */
    private Long id;

    /**
     * 创建时间
     */
    private Long cts;

    /**
     * 修改时间
     */
    private Long uts;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 子码日程
     */
    private String name;

    /**
     * vdn_id
     */
    private String vdnId;

    /**
     * 日程id
     */
    private Long scheduleId;

    /**
     * 路由类型(1:技能组,2:放音,3:ivr,4:坐席,5:外呼)
     */
    private Integer routeType;

    /**
     * 路由类型值
     */
    private String routeValue;

    /**
     * 放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)
     */
    private Integer playType;

    /**
     * 放音类型对应值
     */
    private String playValue;

    /**
     * 结束音
     */
    private String dtmfEnd;

    /**
     * 重复播放次数
     */
    private Integer retry;

    /**
     * 最大收键长度
     */
    private Integer dtmfMax;

    /**
     * 最小收键长度
     */
    private Integer dtmfMin;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 每个字码日程有多个按键导航
     */
    private List<VdnDtmf> dtmfList;

    /**
     * 日程
     */
    private VdnSchedule scheduleConfig;

    @Override
    public int compareTo(VdnScheduleInfo o) {
        return this.getScheduleConfig().getLevelValue() - o.getScheduleConfig().getLevelValue();
    }

    public boolean isEffectiveSchedule() {
        if (scheduleConfig == null) {
            return false;
        }
        LocalDate localDate = LocalDate.now();
        int week = localDate.getDayOfMonth();

        switch (week) {
            case 1:
                if (this.scheduleConfig.getMon() == 0) {
                    return false;
                }
                break;
            case 2:
                if (this.scheduleConfig.getTue() == 0) {
                    return false;
                }
                break;
            case 3:
                if (this.scheduleConfig.getWed() == 0) {
                    return false;
                }
                break;
            case 4:
                if (this.scheduleConfig.getThu() == 0) {
                    return false;
                }
                break;
            case 5:
                if (this.scheduleConfig.getFri() == 0) {
                    return false;
                }
                break;
            case 6:
                if (this.scheduleConfig.getSat() == 0) {
                    return false;
                }
                break;
            case 7:
                if (this.scheduleConfig.getSun() == 0) {
                    return false;
                }
                break;

            default:
                break;
        }


        //判断是否在生效日期内
        int dayOfMonth = localDate.getDayOfMonth();
        if (StringUtils.isNotBlank(scheduleConfig.getStartDay())) {
            LocalDate startDate = LocalDate.parse(scheduleConfig.getStartDay(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (scheduleConfig.getType() == 1) {
                //相对时间
                if (startDate.getDayOfMonth() > dayOfMonth) {
                    return false;
                }
            } else if (startDate.isAfter(localDate)) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(scheduleConfig.getEndDay())) {
            final LocalDate endDate = LocalDate.parse(scheduleConfig.getEndDay(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (scheduleConfig.getType() == 1) {
                //相对时间
                if (endDate.getDayOfMonth() < dayOfMonth) {
                    return false;
                }
            } else if (endDate.isBefore(localDate)) {
                return false;
            }
        }

        //判断是否在生效的时间范围内（当天）
        LocalTime nowTime = LocalTime.now();
        if (StringUtils.isNotBlank(scheduleConfig.getStartTime())) {
            LocalTime startTime = LocalTime.parse(scheduleConfig.getStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (startTime.isAfter(nowTime)) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(scheduleConfig.getEndTime())) {
            LocalTime endTime = LocalTime.parse(scheduleConfig.getEndTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (endTime.isBefore(nowTime)) {
                return false;
            }
        }
        return true;
    }
}
