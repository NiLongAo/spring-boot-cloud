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
    * 企业会议室表
    */
@ApiModel(description="企业会议室表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_company_conference")
public class CompanyConference extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 会议室名
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="会议室名")
    private String name;

    /**
     * 会议室号码
     */
    @TableField(value = "code")
    @ApiModelProperty(value="会议室号码")
    private String code;

    /**
     * 会议室号码
     */
    @TableField(value = "password")
    @ApiModelProperty(value="会议室号码")
    private String password;

    /**
     * 使用状态(1.未使用 1.使用中)
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="使用状态(1.未使用 1.使用中)")
    private Integer status;

    /**
     * 启用状态(0.禁用 1.启用)
     */
    @TableField(value = "`enable`")
    @ApiModelProperty(value="启用状态(0.禁用 1.启用)")
    private Integer enable;
}