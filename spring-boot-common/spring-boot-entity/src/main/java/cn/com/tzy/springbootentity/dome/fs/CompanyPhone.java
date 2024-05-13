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
    * 企业号码
    */
@ApiModel(description="企业号码")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_company_phone")
public class CompanyPhone extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 号码
     */
    @TableField(value = "phone")
    @ApiModelProperty(value="号码")
    private String phone;

    /**
     * 1:呼入号码,2:主叫显号,3:被叫显号
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="1:呼入号码,2:主叫显号,3:被叫显号")
    private Integer type;

    /**
     * 1:未启用,2:启用
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="1:未启用,2:启用")
    private Integer status;
}