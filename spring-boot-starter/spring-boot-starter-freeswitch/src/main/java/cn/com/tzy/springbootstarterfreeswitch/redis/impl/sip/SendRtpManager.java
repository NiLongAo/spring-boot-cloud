package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
        RedisUtils.set(getKey(sendRtpItem.getMediaServerId(),sendRtpItem.getAgentKey(),sendRtpItem.getPushStreamId(),sendRtpItem.getCallId()), sendRtpItem);
    }

    public SendRtp querySendRTPServer(String agentKey, String streamId, String callId) {
        List<String> scan = RedisUtils.keys(getKey(null,agentKey,streamId,callId));
        if (scan.size() > 0) {
            return (SendRtp)RedisUtils.get((String)scan.get(0));
        }else {
            return null;
        }
    }

    public List<SendRtp> querySendRTPServerByChnnelId(String agentKey) {
        List<String> scan = RedisUtils.keys(getKey(null,agentKey,null,null));
        List<SendRtp> result = new ArrayList<>();
        for (Object o : scan) {
            result.add((SendRtp) RedisUtils.get((String) o));
        }
        return result;
    }

    public List<SendRtp> querySendRTPServerByStream(String stream) {
        List<String> scan = RedisUtils.keys(getKey(null,null,stream,null));
        List<SendRtp> result = new ArrayList<>();
        for (Object o : scan) {
            result.add((SendRtp) RedisUtils.get((String) o));
        }
        return result;
    }

    /**
     * 删除RTP推送信息缓存
     */
    public void deleteSendRTPServer( String agentKey, String streamId ,String callId) {
        List<String> scan = RedisUtils.keys(getKey(null,agentKey,streamId,callId));
        if (scan.size() > 0) {
            for (Object keyStr : scan) {
                RedisUtils.del((String)keyStr);
            }
        }
    }

    public List<SendRtp> queryAllSendRTPServer() {
        List<String> queryResult = RedisUtils.keys(getKey(null,null,null,null));
        List<SendRtp> result= new ArrayList<>();

        for (String o : queryResult) {
            result.add((SendRtp) RedisUtils.get(o));
        }

        return result;
    }

    /**
     * 查询某个通道是否存在上级点播（RTP推送）
     */
    public boolean isChannelSendingRTP(String agentKey) {
        List<String> RtpStreams = RedisUtils.keys(getKey(null,agentKey,null,null));
        if (RtpStreams.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private String getKey(String mediaServerId,String agentKey, String streamId ,String callId){
        if (StringUtils.isEmpty(mediaServerId)) {
            mediaServerId = "*";
        }
        if (StringUtils.isEmpty(agentKey)) {
            agentKey = "*";
        }
        if (StringUtils.isEmpty(streamId)) {
            streamId = "*";
        }
        if (StringUtils.isEmpty(callId)) {
            callId = "*";
        }
        return String.format(
                "%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                mediaServerId,
                agentKey,
                streamId,
                callId
        );
    }
}
