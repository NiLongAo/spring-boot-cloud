package cn.com.tzy.springbootentity.param.sys;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@ApiModel("字典类型条目表")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryItemParam extends PageModel {

    @ApiModelProperty("编号")
    public String id;

    @ApiModelProperty("序号")
    public Integer num;

    @ApiModelProperty("字典类型条目名称")
    public String name;

    @ApiModelProperty("值")
    public String value;

    @ApiModelProperty("类型ID")
    public String typeId;
}
