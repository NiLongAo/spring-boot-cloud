package cn.com.tzy.springbootcomm.common.model;

import cn.com.tzy.springbootcomm.utils.ChangeChar;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * @author TZY
 */
@ApiModel("排序对象")
public class PageSortModel {
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    public static final String ASCEND = "ascend";

    public static final String DESCEND = "descend";
    @ApiModelProperty("排序字段 例子:endTime,id")
    private final String field;
    @ApiModelProperty("正序倒序 例子:desc,ace")
    private final String order;

    public PageSortModel(String field, String order) {
        this.field = field;
        this.order = order;
    }

    public String getField() {
        return ChangeChar.camelToUnderline(field,1);
    }

    public String getOrder() {
        if(StringUtils.isNotEmpty(this.order)){
            if(order.equals(DESCEND)){
                return DESC;
            }else if(order.equals(ASCEND)){
                return ASC;
            }else {
                return order;
            }
        }else {
            return order;
        }
    }
}
