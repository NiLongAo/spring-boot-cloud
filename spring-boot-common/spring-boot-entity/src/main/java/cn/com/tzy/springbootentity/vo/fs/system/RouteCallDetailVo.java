package cn.com.tzy.springbootentity.vo.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCallDetailVo {
    private Long id;
    private Long companyId;
    private Long routeGroupId;
    private String routeNum;
    private Integer numMax;
    private Integer numMin;
    private Integer callerChange;
    private String callerChangeNum;
    private Integer calledChange;
    private String calledChangeNum;
    private Integer status;
}
