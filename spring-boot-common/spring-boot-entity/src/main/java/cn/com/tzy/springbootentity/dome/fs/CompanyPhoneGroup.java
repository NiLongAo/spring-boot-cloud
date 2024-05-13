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
    * 企业号码与号码池中间表
    */
@ApiModel(description="企业号码与号码池中间表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_company_phone_group")
public class CompanyPhoneGroup extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 号码池id
     */
    @TableField(value = "display_id")
    @ApiModelProperty(value="号码池id")
    private Long displayId;

    /**
     * 号码id
     */
    @TableField(value = "phone_id")
    @ApiModelProperty(value="号码id")
    private Long phoneId;
}