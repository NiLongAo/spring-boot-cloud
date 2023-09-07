package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SendRtp;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SendRtpManager {


    @Resource
    private VideoProperties videoProperties;

    private String PLATFORM_SEND_RTP_INFO_PREFIX = VideoConstant.PLATFORM_SEND_RTP_INFO_PREFIX;

    public void put(SendRtp sendRtpItem) {
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                sendRtpItem.getMediaServerId(),
                sendRtpItem.getPlatformId(),
                sendRtpItem.getChannelId(),
                sendRtpItem.getStreamId(),
                sendRtpItem.getCallId()
        );
        RedisUtils.set(key, sendRtpItem);
    }

    public SendRtp querySendRTPServer(String platformGbId, String channelId, String streamId, String callId) {
        if (platformGbId == null) {
            platformGbId = "*";
        }
        if (channelId == null) {
            channelId = "*";
        }
        if (streamId == null) {
            streamId = "*";
        }
        if (callId == null) {
            callId = "*";
        }
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                platformGbId,
                channelId,
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

    public List<SendRtp> querySendRTPServerByChnnelId(String channelId) {
        if (channelId == null) {
            return null;
        }
        String platformGbId = "*";
        String callId = "*";
        String streamId = "*";
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                platformGbId,
                channelId,
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
        String platformGbId = "*";
        String callId = "*";
        String channelId = "*";
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                platformGbId,
                channelId,
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

    public List<SendRtp> querySendRTPServer(String platformGbId) {
        if (platformGbId == null) {
            platformGbId = "*";
        }
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                platformGbId,
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
     * 删除RTP推送信息缓存
     * @param platformGbId
     * @param channelId
     */
    public void deleteSendRTPServer(String platformGbId, String channelId, String streamId ,String callId) {
        if (streamId == null) {
            streamId = "*";
        }
        if (callId == null) {
            callId = "*";
        }
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                platformGbId,
                channelId,
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
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
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
     * @param channelId
     */
    public boolean isChannelSendingRTP(String channelId) {
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                "*",
                channelId,
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


    public int getGbSendCount(String platformGbId) {
        String key = String.format(
                "%s:%s:%s:%s:%s:%s:%s",
                PLATFORM_SEND_RTP_INFO_PREFIX,
                videoProperties.getServerId(),
                "*",
                platformGbId,
                "*",
                "*",
                "*"
        );
        return RedisUtils.keys(key).size();
    }
}
