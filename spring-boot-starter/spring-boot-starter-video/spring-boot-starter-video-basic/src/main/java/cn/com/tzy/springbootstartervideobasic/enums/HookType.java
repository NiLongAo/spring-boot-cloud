package cn.com.tzy.springbootstartervideobasic.enums;

import java.io.Serializable;

/**
 * hook类型
 * @author lin
 */

public enum HookType implements Serializable {

    on_flow_report,
    on_http_access,
    on_play,
    on_publish,
    on_record_mp4,
    on_rtsp_auth,
    on_rtsp_realm,
    on_shell_login,
    on_stream_changed,
    on_stream_none_reader,
    on_stream_not_found,
    on_server_started,

    on_rtp_server_timeout,
    on_server_keepalive
}
