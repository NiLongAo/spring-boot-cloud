package cn.com.tzy.springbootentity.param.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCallSaveParam {
    private Long id;

    @NotNull(message = "企业ID不能为空")
    private Long companyId;

    @NotNull(message = "路由组ID不能为空")
    private Long routeGroupId;

    @NotBlank(message = "路由号码不能为空")
    private String routeNum;

    @NotNull(message = "号码最大值不能为空")
    @Min(value = 0, message = "号码最大值不能小于0")
    private Integer numMax;

    @NotNull(message = "号码最小值不能为空")
    @Min(value = 0, message = "号码最小值不能小于0")
    private Integer numMin;

    private Integer callerChange;
    private String callerChangeNum;
    private Integer calledChange;
    private String calledChangeNum;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
