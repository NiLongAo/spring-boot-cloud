package cn.com.tzy.springbootentity.param.fs.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteGroupGatewaySaveParam {
    @NotNull(message = "路由组ID不能为空")
    private Long routeGroupId;
    @NotNull(message = "网关列表不能为空")
    private List<Long> gatewayIds;
}
