package cn.com.tzy.springbootentity.param.fs.system;

import cn.com.tzy.springbootcomm.common.model.PageModel;
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
public class FsGatewayPageParam extends PageModel {
    private String selected;
}
