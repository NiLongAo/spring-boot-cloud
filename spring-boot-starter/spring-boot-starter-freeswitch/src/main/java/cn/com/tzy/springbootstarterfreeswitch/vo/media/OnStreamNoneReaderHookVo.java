package cn.com.tzy.springbootstarterfreeswitch.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnStreamNoneReaderHookVo extends HookVo {

    private String schema;

    private String app;

    private String stream;

    private String vhost;

}
