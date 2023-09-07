package cn.com.tzy.springbootcomm.common.model;

import cn.com.tzy.springbootcomm.utils.ChangeChar;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

@ApiModel("排序对象")
public class PageSortModel {
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    @ApiModelProperty("排序字段 例子:endTime,id")
    private  String field;
    @ApiModelProperty("正序倒序 例子:desc,ace")
    private  String order;

    public PageSortModel(String field, String order) {
        this.field = field;
        this.order = order;
    }

    public String getField() {
        return ChangeChar.camelToUnderline(field,1);
    }

    public String getOrder() {
        if(StringUtils.isNotEmpty(this.order)){
            if(this.order.equals("ascend")){
                return ASC;
            }else if(order.equals("descend")){
                return DESC;
            }else {
                return order;
            }
        }else {
            return order;
        }
    }
}
