package cn.com.tzy.springbootstartervideobasic.vo.media;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnStreamChangedResult extends OnStreamChangedHookVo{

    private int code;

    public static OnStreamChangedResult result(int code) {
        return new OnStreamChangedResult(code);
    }
}
