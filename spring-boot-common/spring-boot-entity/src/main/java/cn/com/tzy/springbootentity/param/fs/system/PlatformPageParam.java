package cn.com.tzy.springbootentity.param.fs.system;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("FreeSWITCH??????")
public class PlatformPageParam extends PageModel {

    @ApiModelProperty("????")
    private Integer enable;

    @ApiModelProperty("????")
    private Integer status;
}
