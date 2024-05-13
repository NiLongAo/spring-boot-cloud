package cn.com.tzy.springbootentity.dome.fs;

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
    * 日程表
    */
@ApiModel(description="日程表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_vdn_schedule")
public class VdnSchedule extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 日程名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="日程名称")
    private String name;

    /**
     * 优先级
     */
    @TableField(value = "level_value")
    @ApiModelProperty(value="优先级")
    private Integer levelValue;

    /**
     * 1:指定时间,2:相对时间
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="1:指定时间,2:相对时间")
    private Integer type;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(value="开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="结束时间")
    private Date endTime;

    /**
     * 周一
     */
    @TableField(value = "mon")
    @ApiModelProperty(value="周一")
    private Integer mon;

    /**
     * 周二
     */
    @TableField(value = "tue")
    @ApiModelProperty(value="周二")
    private Integer tue;

    /**
     * 周三
     */
    @TableField(value = "wed")
    @ApiModelProperty(value="周三")
    private Integer wed;

    /**
     * 周四
     */
    @TableField(value = "thu")
    @ApiModelProperty(value="周四")
    private Integer thu;

    /**
     * 周五
     */
    @TableField(value = "fri")
    @ApiModelProperty(value="周五")
    private Integer fri;

    /**
     * 周六
     */
    @TableField(value = "sat")
    @ApiModelProperty(value="周六")
    private Integer sat;

    /**
     * 周天
     */
    @TableField(value = "sun")
    @ApiModelProperty(value="周天")
    private Integer sun;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}