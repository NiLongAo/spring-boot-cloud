package cn.com.tzy.springbootentity.dome.sys;

import cn.com.tzy.springbootcomm.common.bean.Base;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 系统配置
 */
@ApiModel(value = "系统配置")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_config")
public class Config extends Base {
    /**
     * 配置名称（枚举）
     */
    @TableId(value = "k", type = IdType.INPUT)
    @ApiModelProperty(value = "配置名称（枚举）")
    private String k;

    /**
     * 配置名称
     */
    @TableField(value = "config_name")
    @ApiModelProperty(value = "配置名称")
    private String configName;

    /**
     * 配置值
     */
    @TableField(value = "v")
    @ApiModelProperty(value = "配置值")
    private String v;
}
