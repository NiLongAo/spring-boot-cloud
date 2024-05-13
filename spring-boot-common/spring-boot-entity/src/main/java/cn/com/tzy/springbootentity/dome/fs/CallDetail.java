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
    * 通话流程表
    */
@ApiModel(description="通话流程表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_call_detail")
public class CallDetail extends LongIdEntity {
    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(value="开始时间")
    private Long startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="结束时间")
    private Long endTime;

    /**
     * 通话ID
     */
    @TableField(value = "call_id")
    @ApiModelProperty(value="通话ID")
    private Long callId;

    /**
     * 顺序
     */
    @TableField(value = "detail_index")
    @ApiModelProperty(value="顺序")
    private Integer detailIndex;

    /**
     * 1:进vdn,2:进ivr,3:技能组,4:按键收号,5:外线,6:机器人,10:服务评价
     */
    @TableField(value = "transfer_type")
    @ApiModelProperty(value="1:进vdn,2:进ivr,3:技能组,4:按键收号,5:外线,6:机器人,10:服务评价")
    private Integer transferType;

    /**
     * 转接ID
     */
    @TableField(value = "transfer_id")
    @ApiModelProperty(value="转接ID")
    private Long transferId;

    /**
     * 出队列原因:排队挂机或者转坐席
     */
    @TableField(value = "reason")
    @ApiModelProperty(value="出队列原因:排队挂机或者转坐席")
    private String reason;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}