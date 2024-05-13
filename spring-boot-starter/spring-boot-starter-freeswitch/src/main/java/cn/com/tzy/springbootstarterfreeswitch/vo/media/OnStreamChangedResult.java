package cn.com.tzy.springbootstarterfreeswitch.vo.media;

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
