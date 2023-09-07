package cn.com.tzy.springbootstartervideocore.demo;

import cn.com.tzy.springbootstartervideobasic.enums.VideoStreamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsrcTransaction {

    private String deviceId;
    private String channelId;
    private String callId;
    private String app;
    private String stream;
    private String mediaServerId;
    private String ssrc;
    private SipTransactionInfo sipTransactionInfo;
    private VideoStreamType type;
}
