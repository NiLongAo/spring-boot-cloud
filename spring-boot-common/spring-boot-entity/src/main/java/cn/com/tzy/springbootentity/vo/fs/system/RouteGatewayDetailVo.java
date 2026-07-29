package cn.com.tzy.springbootentity.vo.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteGatewayDetailVo {
    private Long id;
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
