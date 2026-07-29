package cn.com.tzy.springbootentity.vo.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FsGatewayDetailVo {
    private Long id;
    private String name;
    private String routeId;
    private String realm;
    private Integer register;
    private Integer transport;
    private Integer retrySeconds;
    private Integer username;
    /** 永远不回显 */
    private String password;
    private String selected;
}
