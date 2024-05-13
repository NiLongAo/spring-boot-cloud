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
    * 呼叫按键表
    */
@ApiModel(description="呼叫按键表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_call_dtmf")
public class CallDtmf extends LongIdEntity {
    /**
     * 按键号码
     */
    @TableField(value = "dtmf_key")
    @ApiModelProperty(value="按键号码")
    private String dtmfKey;

    /**
     * 业务流程id
     */
    @TableField(value = "process_id")
    @ApiModelProperty(value="业务流程id")
    private Long processId;

    /**
     * 通话标识id
     */
    @TableField(value = "call_id")
    @ApiModelProperty(value="通话标识id")
    private Long callId;

    /**
     * 按键时间
     */
    @TableField(value = "dtmf_time")
    @ApiModelProperty(value="按键时间")
    private Long dtmfTime;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}