package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class SendRtpManager {


    @Resource
    private VideoProperties videoProperties;

    private String PLATFORM_SEND_RTP_INFO_PREFIX = SipConstant.PLATFORM_SEND_RTP_INFO_PREFIX;

    public void put(SendRtp sendRtpItem) {
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                sendRtpItem.getMediaServerId(),
                sendRtpItem.getAgentCode(),
                sendRtpItem.getStreamId(),
                sendRtpItem.getCallId()
        );
        RedisUtils.set(key, sendRtpItem);
    }

    public SendRtp querySendRTPServer(String agentCode, String streamId, String callId) {
        if (agentCode == null) {
            agentCode = "*";
        }
        if (streamId == null) {
            streamId = "*";
        }
        if (callId == null) {
            callId = "*";
        }
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                agentCode,
                streamId,
                callId
        );
        List<String> scan = RedisUtils.keys(key);
        if (scan.size() > 0) {
            return (SendRtp)RedisUtils.get((String)scan.get(0));
        }else {
            return null;
        }
    }

    public List<SendRtp> querySendRTPServerByChnnelId(String agentCode) {
        if (agentCode == null) {
            return null;
        }
        String callId = "*";
        String streamId = "*";
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                agentCode,
                streamId,
                callId
        );
        List<String> scan = RedisUtils.keys(key);
        List<SendRtp> result = new ArrayList<>();
        for (Object o : scan) {
            result.add((SendRtp) RedisUtils.get((String) o));
        }
        return result;
    }

    public List<SendRtp> querySendRTPServerByStream(String stream) {
        if (stream == null) {
            return null;
        }
        String callId = "*";
        String agentCode = "*";
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                agentCode,
                stream,
                callId
        );
        List<String> scan = RedisUtils.keys(key);
        List<SendRtp> result = new ArrayList<>();
        for (Object o : scan) {
            result.add((SendRtp) RedisUtils.get((String) o));
        }
        return result;
    }

    /**
     * 删除RTP推送信息缓存
     * @param agentCode
     */
    public void deleteSendRTPServer( String agentCode, String streamId ,String callId) {
        if (streamId == null) {
            streamId = "*";
        }
        if (callId == null) {
            callId = "*";
        }
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                agentCode,
                streamId,
                callId
        );
        List<String> scan = RedisUtils.keys(key);
        if (scan.size() > 0) {
            for (Object keyStr : scan) {
                RedisUtils.del((String)keyStr);
            }
        }
    }

    public List<SendRtp> queryAllSendRTPServer() {
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                "*",
                "*",
                "*"
        );
        List<String> queryResult = RedisUtils.keys(key);
        List<SendRtp> result= new ArrayList<>();

        for (String o : queryResult) {
            result.add((SendRtp) RedisUtils.get(o));
        }

        return result;
    }

    /**
     * 查询某个通道是否存在上级点播（RTP推送）
     * @param agentCode
     */
    public boolean isChannelSendingRTP(String agentCode) {
        String key = String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                agentCode,
                "*",
                "*"
        );
        List<String> RtpStreams = RedisUtils.keys(key);
        if (RtpStreams.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
