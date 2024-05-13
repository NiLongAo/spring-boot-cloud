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
    * 呼入路由表
    */
@ApiModel(description="呼入路由表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_vdn_code")
public class VdnCode extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * vdn名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="vdn名称")
    private String name;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}