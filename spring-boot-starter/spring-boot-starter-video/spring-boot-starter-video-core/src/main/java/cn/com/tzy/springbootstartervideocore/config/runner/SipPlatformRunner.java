package cn.com.tzy.springbootstartervideocore.config.runner;

import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * 系统启动时控制上级平台重新注册
 */
@Log4j2
@Order(50)
public class SipPlatformRunner implements CommandLineRunner {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Autowired
    private SipConfigProperties sipConfigProperties;

    @Override
    public void run(String... args) throws Exception {
        List<ParentPlatformVo> parentPlatformVoList = VideoService.getParentPlatformService().getParentPlatformByDeviceGbId(sipConfigProperties.getId());
        if(parentPlatformVoList == null || parentPlatformVoList.isEmpty()){
            return;
        }
        for (ParentPlatformVo parentPlatformVo : parentPlatformVoList) {
            // 设置所有平台离线
            VideoService.getParentPlatformService().offline(parentPlatformVo);
            // 先注销然后注册
            RedisService.getRegisterServerManager().putPlatform(parentPlatformVo.getServerGbId(),parentPlatformVo.getKeepTimeout()+ VideoConstant.DELAY_TIME, Address.builder().gbId(parentPlatformVo.getServerGbId()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
            VideoService.getParentPlatformService().unregister(parentPlatformVo, (eventResult)->{
                VideoService.getParentPlatformService().login(parentPlatformVo);
            } ,null);
        }
    }
}
