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

/**
    * 呼入路由字码表
    */
@ApiModel(description="呼入路由字码表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_vdn_config")
public class VdnConfig extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 子码日程
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="子码日程")
    private String name;

    /**
     * 呼入路由编号
     */
    @TableField(value = "vdn_id")
    @ApiModelProperty(value="呼入路由编号")
    private Long vdnId;

    /**
     * 日程id
     */
    @TableField(value = "schedule_id")
    @ApiModelProperty(value="日程id")
    private Long scheduleId;

    /**
     * 路由类型(1:技能组,2:放音,3:ivr,4:坐席,5:外呼)
     */
    @TableField(value = "route_type")
    @ApiModelProperty(value="路由类型(1:技能组,2:放音,3:ivr,4:坐席,5:外呼)")
    private Integer routeType;

    /**
     * 路由类型值
     */
    @TableField(value = "route_value")
    @ApiModelProperty(value="路由类型值")
    private String routeValue;

    /**
     * 放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)
     */
    @TableField(value = "play_type")
    @ApiModelProperty(value="放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)")
    private Integer playType;

    /**
     * 放音类型对应值
     */
    @TableField(value = "play_value")
    @ApiModelProperty(value="放音类型对应值")
    private Long playValue;

    /**
     * 结束音
     */
    @TableField(value = "dtmf_end")
    @ApiModelProperty(value="结束音")
    private String dtmfEnd;

    /**
     * 重复播放次数
     */
    @TableField(value = "retry")
    @ApiModelProperty(value="重复播放次数")
    private Integer retry;

    /**
     * 最大收键长度
     */
    @TableField(value = "dtmf_max")
    @ApiModelProperty(value="最大收键长度")
    private Integer dtmfMax;

    /**
     * 最小收键长度
     */
    @TableField(value = "dtmf_min")
    @ApiModelProperty(value="最小收键长度")
    private Integer dtmfMin;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}