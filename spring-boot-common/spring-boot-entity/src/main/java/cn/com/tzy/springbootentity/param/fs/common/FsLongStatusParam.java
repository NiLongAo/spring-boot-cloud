package cn.com.tzy.springbootentity.param.fs.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FsLongStatusParam {

    @NotNull(message = "编号不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
