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
    * 用户座席中间表
    */
@ApiModel(description="用户座席中间表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_user_agent")
public class UserAgent extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 坐席工号
     */
    @TableField(value = "agent_id")
    @ApiModelProperty(value="坐席工号")
    private String agentId;

    /**
     * 用户编号
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户编号")
    private String userId;
}