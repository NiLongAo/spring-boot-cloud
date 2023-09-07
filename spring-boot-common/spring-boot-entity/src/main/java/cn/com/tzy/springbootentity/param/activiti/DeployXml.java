package cn.com.tzy.springbootentity.param.activiti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流程上传xml")
public class DeployXml {

    @ApiModelProperty("流程id")
    private String id;
    @ApiModelProperty("流程名称")
    private String name;
    @ApiModelProperty("流程xml信息")
    private String xml;

}
