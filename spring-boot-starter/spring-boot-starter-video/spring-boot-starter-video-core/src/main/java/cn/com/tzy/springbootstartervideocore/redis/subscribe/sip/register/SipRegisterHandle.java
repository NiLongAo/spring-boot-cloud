package cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.register;

import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;


@Log4j2
public class SipRegisterHandle extends AbstractMessageListener {

    @Autowired
    private SipConfigProperties sipConfigProperties;

    public SipRegisterHandle() {
        super(VideoConstant.VIDEO_SEND_SIP_REGISTER_MESSAGE);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
        ParentPlatformVo vo = (ParentPlatformVo) body;
        if(vo == null || !sipConfigProperties.getId().equals(vo.getDeviceGbId())){
            return;
        }
        //触发服务注册
        VideoService.getParentPlatformService().login(vo);
    }
}
