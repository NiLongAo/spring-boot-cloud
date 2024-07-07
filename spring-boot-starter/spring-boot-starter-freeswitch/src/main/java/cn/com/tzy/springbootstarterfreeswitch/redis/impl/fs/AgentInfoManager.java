package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.codec.Base64;
import gov.nist.javax.sip.message.SIPRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SerializationUtils;

import java.util.List;

@Component
public class AgentInfoManager {

    private String FS_AGENT_INFO = RedisConstant.FS_AGENT_INFO;
    private String FS_AGENT_SIP_INFO = RedisConstant.FS_AGENT_SIP_INFO;
    private String FS_SOCKET_AGENT_CODE = RedisConstant.FS_SOCKET_AGENT_CODE;
    private String FS_CALL_PHONE = RedisConstant.FS_CALL_PHONE;
    private String FS_AGENT_LOGIN_LOGOUT = RedisConstant.FS_AGENT_LOGIN_LOGOUT;
    public void put(AgentVoInfo model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKey(model.getAgentKey()),model,-1L);
        if(StringUtils.isNotEmpty(model.getSipPhone())){
            RedisUtils.set(getAgentSipKey(model.getSipPhone()),model.getAgentKey(),-1L);
        }
    }
    public AgentVoInfo get(String agentKey) {
        List<String> scan = RedisUtils.keys(getKey(agentKey));
        if (!scan.isEmpty()) {
            return (AgentVoInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }
    public AgentVoInfo getSip(String agentSip) {
        List<String> scan = RedisUtils.keys(getAgentSipKey(agentSip));
        if (scan.isEmpty()) {
            return null;

        }
        String agentKey = (String) RedisUtils.get(scan.get(0));
        return get(agentKey);
    }
    public void delSip(String agentKey){
        List<String> scan = RedisUtils.keys(getAgentSipKey(agentKey));
        for (String key : scan) {
            RedisUtils.del(key);
        }
    }
    public void del(String agentKey){
        List<String> scan = RedisUtils.keys(getKey(agentKey));
        for (String key : scan) {
            RedisUtils.del(key);
            AgentVoInfo agentVoInfo =  (AgentVoInfo)RedisUtils.get(key);
            if(agentVoInfo != null && StringUtils.isNotEmpty(agentVoInfo.getSipPhone())){
                RedisUtils.del(getAgentSipKey(agentVoInfo.getSipPhone()));
            }
        }
    }
    public void delAll(){
        del("*");
        delSip("*");
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

    public void putAgentKey(String uuid, String agentKey){
        if(uuid == null || agentKey == null){
            return;
        }
        RedisUtils.set(getAgentKeyKey(uuid),agentKey,-1L);
    }
    public String getAgentKey(String uuid) {
        List<String> scan = RedisUtils.keys(getAgentKeyKey(uuid));
        if (!scan.isEmpty()) {
            return (String) RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    public void delAgentKey(String uuid){
        if(StringUtils.isEmpty(uuid)){
            uuid = "*";
        }
        for (String key : RedisUtils.keys(getAgentKeyKey(uuid))) {
            RedisUtils.del(key);
        }
    }

    private String getKey(String agentKey){
        return String.format("%s%s",FS_AGENT_INFO,agentKey);
    }

    private String getAgentSipKey(String agentSip){
        return String.format("%s%s",FS_AGENT_SIP_INFO,agentSip);
    }

    private String getCallPhoneKey(String agentKey){
        return String.format("%s%s",FS_CALL_PHONE,agentKey);
    }

    private String getAgentKeyKey(String uuid){
        return String.format("%s%s",FS_SOCKET_AGENT_CODE,uuid);
    }
}
