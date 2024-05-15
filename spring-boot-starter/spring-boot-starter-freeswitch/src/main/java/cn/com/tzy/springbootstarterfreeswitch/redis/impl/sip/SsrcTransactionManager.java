package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SipTransactionInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SsrcTransaction;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import gov.nist.javax.sip.message.SIPMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频流session管理器，管理视频预览、预览回放的通信句柄
 */
@Component
public class SsrcTransactionManager {

    private String MEDIA_TRANSACTION_USED_PREFIX = SipConstant.MEDIA_TRANSACTION_USED_PREFIX;

    /**
     * 添加一个点播/回放的事务信息
     * 后续可以通过流Id/callID
     * @param agentCode 设备ID
     * @param callId 一次请求的CallID
     * @param stream 流名称
     * @param mediaServerId 所使用的流媒体ID
     * @param response 回复
     */
    public void put(String agentCode, String callId, String app, String stream, String ssrc, String mediaServerId, SIPMessage response, VideoStreamType type){
        put(agentCode,callId,false,false,app,stream,ssrc,mediaServerId,response,type);
    }
    public void put(String agentCode, String callId,boolean onPush,boolean onVideo, String app, String stream, String ssrc, String mediaServerId, SIPMessage response, VideoStreamType type){
        SsrcTransaction ssrcTransaction = new SsrcTransaction();
        ssrcTransaction.setAgentCode(agentCode);
        ssrcTransaction.setStream(app);
        ssrcTransaction.setStream(stream);
        ssrcTransaction.setOnVideo(onVideo);
        ssrcTransaction.setOnPush(onPush);
        ssrcTransaction.setSipTransactionInfo(ObjectUtils.isEmpty(response) ? null : new SipTransactionInfo(response));
        ssrcTransaction.setCallId(callId);
        ssrcTransaction.setSsrc(ssrc);
        ssrcTransaction.setMediaServerId(mediaServerId);
        ssrcTransaction.setType(type);
        String key = String.format("%s:%s:%s:%s:%s",MEDIA_TRANSACTION_USED_PREFIX,agentCode,stream,type.ordinal(),callId);
        RedisUtils.set(key, ssrcTransaction);
    }

    public SsrcTransaction getParamOne(String agentCode,  String callId, String stream,VideoStreamType type){
        if (ObjectUtils.isEmpty(agentCode)) {
            agentCode ="*";
        }

        if (ObjectUtils.isEmpty(callId)) {
            callId ="*";
        }
        if (ObjectUtils.isEmpty(stream)) {
            stream ="*";
        }
        String typeStr = (type != null?String.valueOf(type.ordinal()):"*");
        String key = String.format("%s:%s:%s:%s:%s",MEDIA_TRANSACTION_USED_PREFIX,agentCode,stream,typeStr,callId);
        List<String> scanResult = RedisUtils.keys(key);
        if (scanResult.size() == 0) {
            return null;
        }
        return (SsrcTransaction)RedisUtils.get(scanResult.get(0));
    }

    public List<SsrcTransaction> getParamAll(String agentCode,  String callId, String stream,VideoStreamType type){
        if (ObjectUtils.isEmpty(agentCode)) {
            agentCode ="*";
        }
        if (ObjectUtils.isEmpty(callId)) {
            callId ="*";
        }
        if (ObjectUtils.isEmpty(stream)) {
            stream ="*";
        }
        String typeStr = (type != null?String.valueOf(type.ordinal()):"*");
        String key = String.format("%s:%s:%s:%s:%s",MEDIA_TRANSACTION_USED_PREFIX,agentCode,stream,typeStr,callId);
        List<String> scanResult = RedisUtils.keys(key);
        if (scanResult.size() == 0) {
            return null;
        }
        List<SsrcTransaction> result = new ArrayList<>();
        for (String keyObj : scanResult) {
            result.add((SsrcTransaction)RedisUtils.get( keyObj));
        }
        return result;
    }
    public void remove(String agentCode, String stream) {
        this.remove(agentCode,stream,null,null);
    }

    public void remove(String agentCode, String stream,VideoStreamType type) {
        this.remove(agentCode,stream,null,type);
    }

    public void remove(String agentCode, String stream,String callId,VideoStreamType type) {
        if (ObjectUtils.isEmpty(stream)) {
            stream ="*";
        }
        if (ObjectUtils.isEmpty(callId)) {
            callId ="*";
        }
        String typeStr = (type != null?String.valueOf(type.ordinal()):"*");
        String key = String.format("%s:%s:%s:%s:%s",MEDIA_TRANSACTION_USED_PREFIX,agentCode,stream,typeStr,callId);
        List<String> scan = RedisUtils.keys(key);
        if (scan.size() > 0) {
            for (String keyStr : scan) {
                RedisUtils.del(keyStr);
            }
        }
    }

    public List<SsrcTransaction> getAllSsrc() {
        String keys = String.format("%s:*:*:*:*", MEDIA_TRANSACTION_USED_PREFIX);
        List<String> ssrcTransactionKeys = RedisUtils.keys(keys);
        List<SsrcTransaction> result= new ArrayList<>();
        for (int i = 0; i < ssrcTransactionKeys.size(); i++) {
            String key = ssrcTransactionKeys.get(i);
            SsrcTransaction ssrcTransaction = (SsrcTransaction)RedisUtils.get(key);
            result.add(ssrcTransaction);
        }
        return result;
    }

}
