package cn.com.tzy.springbootcomm.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author TZY
 */
@Data
@SuperBuilder(toBuilder = true)
@ApiModel("分页数据模型")
@NoArgsConstructor
@AllArgsConstructor
public class PageModel extends BaseModel{

    @ApiModelProperty("查询内容")
    public String query;
    @ApiModelProperty(value = "当前页数",example="1")
    private Integer pageNumber = 1;
    @ApiModelProperty(value = "显示数量",example="10")
    private Integer pageSize = 10;
    @ApiModelProperty("查询其实行")
    private Integer startRow;
    @ApiModelProperty("排序对象")
    private PageSortModel sort;

    //pageNumber有-1的操作，可能出现负数，所以进行整数判断！
    public Integer getStartRow(){
        if(pageNumber != null && pageSize != null && pageNumber >=1){
            return (pageNumber -1 ) * pageSize;
        }else {
            return 0;
        }
    }
}
