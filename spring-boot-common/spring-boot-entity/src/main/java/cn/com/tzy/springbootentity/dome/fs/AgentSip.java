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
    * sip表
    */
@ApiModel(description="sip表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_agent_sip")
public class AgentSip extends LongIdEntity {
    /**
     * 企业主键
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业主键")
    private Long companyId;

    /**
     * 坐席主键
     */
    @TableField(value = "agent_id")
    @ApiModelProperty(value="坐席主键")
    private Long agentId;

    /**
     * sip编号
     */
    @TableField(value = "sip")
    @ApiModelProperty(value="sip编号")
    private String sip;

    /**
     * sip密码
     */
    @TableField(value = "sip_pwd")
    @ApiModelProperty(value="sip密码")
    private String sipPwd;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}