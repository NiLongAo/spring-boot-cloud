package cn.com.tzy.springbootentity.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 导出参数
 * 用于动态sql拼接查询查询
 */
@Data
@ApiModel("导出参数")
public class  ExportEntity<T> {

    /**
     * 查询参数
     */
    @ApiModelProperty("查询参数")
    public T request;
    /**
     * 忽略字段
     */
    @ApiModelProperty("忽略字段")
    List<String> fieldList;
    /**
     * 脱敏字段
     */
    @ApiModelProperty("忽略脱敏字段")
    List<String> desensitizedList;

}
