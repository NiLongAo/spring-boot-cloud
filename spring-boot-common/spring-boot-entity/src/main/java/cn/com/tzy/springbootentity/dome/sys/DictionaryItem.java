package cn.com.tzy.springbootentity.dome.sys;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "字典类型条目表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_dictionary_item")
public class DictionaryItem extends StringIdEntity {
    /**
     * 序号
     */
    @TableField(value = "sort")
    @ApiModelProperty(value = "序号")
    private Integer sort;

    /**
     * 字典类型条目名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "字典类型条目名称")
    private String name;

    /**
     * 值
     */
    @TableField(value = "value")
    @ApiModelProperty(value = "值")
    private String value;

    /**
     * 类型ID
     */
    @TableField(value = "type_id")
    @ApiModelProperty(value = "类型ID")
    private String typeId;
}
