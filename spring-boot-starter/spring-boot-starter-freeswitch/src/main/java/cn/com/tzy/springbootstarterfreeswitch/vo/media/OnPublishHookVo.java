package cn.com.tzy.springbootstarterfreeswitch.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnPublishHookVo extends HookVo {
    private String id;
    private String app;
    private String stream;
    private String ip;
    private String params;
    private int port;
    private String schema;
    private String vhost;

    @Override
    public String toString() {
        return String.format("%s://%s:%s/%s/%s?%s", schema, ip, port, app, stream, params);
    }
}
