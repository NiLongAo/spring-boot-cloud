package cn.com.tzy.springbootentity.param.fs.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FsGatewaySelectedParam {

    @NotNull(message = "编号不能为空")
    private Long id;

    @Pattern(regexp = "[01]", message = "selected 只能为 0 或 1")
    private String selected;
}
