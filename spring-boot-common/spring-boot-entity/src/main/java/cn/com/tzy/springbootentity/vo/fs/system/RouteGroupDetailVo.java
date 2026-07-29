package cn.com.tzy.springbootentity.vo.fs.system;

import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteGroupDetailVo {
    private Long id;
    private String name;
    private Integer status;
    /** 已分配的路由网关列表 */
    private List<FsOptionVo> gateways;
}
