package cn.com.tzy.springbootentity.vo.fs.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FsOptionVo {

    private String id;

    private String name;

    private String code;

    private Integer status;
}
