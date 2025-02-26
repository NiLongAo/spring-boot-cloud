package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;

@Log4j2
public class CseqManager {

    @Resource
    private VideoProperties videoProperties;

    private final String SIP_CSEQ_PREFIX = VideoConstant.SIP_CSEQ_PREFIX;
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
