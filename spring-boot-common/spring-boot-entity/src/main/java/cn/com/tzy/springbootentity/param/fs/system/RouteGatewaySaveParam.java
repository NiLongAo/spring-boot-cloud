package cn.com.tzy.springbootentity.param.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteGatewaySaveParam {
    private Long id;
    @NotBlank(message = "路由网关名称不能为空")
    private String name;
    private String mediaHost;
    private Integer mediaPort;
    private String callerPrefix;
    private String calledPrefix;
    private String profile;
    private String sipHeader1;
    private String sipHeader2;
    private String sipHeader3;
    private Integer status;
}
