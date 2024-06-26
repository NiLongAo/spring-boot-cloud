package cn.com.tzy.springbootstarterfreeswitch.vo.sip;

import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsrcTransaction {

    private String agentCode;
    private String callId;
    private boolean onPush;
    private boolean onVideo;
    private String app;
    private String stream;
    private String mediaServerId;
    private String ssrc;
    private SipTransactionInfo sipTransactionInfo;
    private VideoStreamType type;
}
