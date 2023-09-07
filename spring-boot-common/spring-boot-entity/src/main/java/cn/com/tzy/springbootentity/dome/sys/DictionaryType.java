package cn.com.tzy.springbootentity.dome.sys;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ApiModel(value = "字典类型表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_dictionary_type")
public class DictionaryType extends StringIdEntity {
    /**
     * 编码
     */
    @TableField(value = "code")
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 状态 1.启用 2.禁用
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态 1.启用 2.禁用")
    private Integer status;

    /**
     * 字典类型名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "字典类型名称")
    private String name;
}
