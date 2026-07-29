package cn.com.tzy.springbootentity.param.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FsGatewaySaveParam {
    private Long id;
    private String name;
    private String routeId;
    private String realm;
    private Integer register;
    private Integer transport;
    private Integer retrySeconds;
    /** 保持原有 Integer 类型，不修改数据库结构 */
    private Integer username;
    /** 写入时设置，详情固定返回 null，修改时空值保持原值 */
    private String password;
    private String selected;
}
