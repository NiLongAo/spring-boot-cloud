package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.codec.Base64;
import gov.nist.javax.sip.message.SIPRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AgentInfoManager {

    private String FS_AGENT_INFO = RedisConstant.FS_AGENT_INFO;
    private String FS_SOCKET_AGENT_CODE = RedisConstant.FS_SOCKET_AGENT_CODE;
    private String FS_CALL_PHONE = RedisConstant.FS_CALL_PHONE;

    public void put(AgentVoInfo model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKey(model.getAgentKey()),model,-1L);
    }

    public AgentVoInfo get(String agentKey) {
        List<String> scan = RedisUtils.keys(getKey(agentKey));
        if (!scan.isEmpty()) {
            return (AgentVoInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    public void del(String agentKey){
        RedisUtils.del(getKey(agentKey));
    }

    public void putCallPhone(String callId, SIPRequest model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getCallPhoneKey(callId),SerializationUtils.serialize(model),30L);
    }

    public SIPRequest getCallPhone(String callId) {
        List<String> scan = RedisUtils.keys(getCallPhoneKey(callId));
        if (!scan.isEmpty()) {
            Object o = RedisUtils.get(scan.get(0));
            if(ObjectUtils.isEmpty(o)){
                return null;
            }
            return (SIPRequest)SerializationUtils.deserialize(Base64.decode((String)o));
        }else {
            return null;
        }
    }
    public void delCallPhone(String callId){
        RedisUtils.del(getCallPhoneKey(callId));
    }

    public void putAgentCode(String uuid,String agentCode){
        if(uuid == null || agentCode == null){
            return;
        }
        RedisUtils.set(getAgentCodeKey(uuid),agentCode,-1L);
    }
    public String getAgentCode(String uuid) {
        List<String> scan = RedisUtils.keys(getAgentCodeKey(uuid));
        if (!scan.isEmpty()) {
            return (String) RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    public void delAgentCode(String uuid){
        if(StringUtils.isEmpty(uuid)){
            uuid = "*";
        }
        for (String key : RedisUtils.keys(getAgentCodeKey(uuid))) {
            RedisUtils.del(key);
        }
    }

    private String getKey(String agentKey){
        return String.format("%s%s",FS_AGENT_INFO,agentKey);
    }

    private String getCallPhoneKey(String agentKey){
        return String.format("%s%s",FS_CALL_PHONE,agentKey);
    }

    private String getAgentCodeKey(String uuid){
        return String.format("%s%s",FS_SOCKET_AGENT_CODE,uuid);
    }
}
