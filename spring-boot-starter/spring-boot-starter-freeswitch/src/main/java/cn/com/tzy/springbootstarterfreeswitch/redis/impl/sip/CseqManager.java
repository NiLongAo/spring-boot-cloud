package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log4j2
@Component
public class CseqManager {

    @Resource
    private VideoProperties videoProperties;

    private final String SIP_CSEQ_PREFIX = SipConstant.SIP_CSEQ_PREFIX;
    /**
     * 生成序列号
     * @return
     */
    public  long getCSEQ(){
        String key = SIP_CSEQ_PREFIX + videoProperties.getServerId();
        long incr = RedisUtils.incr(key, 1L,0);
        if(incr > Integer.MAX_VALUE){
            RedisUtils.set(key,1);
            incr = 1;
        }
        return incr;
    }
}
