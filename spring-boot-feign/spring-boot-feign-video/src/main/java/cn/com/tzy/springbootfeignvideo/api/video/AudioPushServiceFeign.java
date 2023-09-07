package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 语音推流等相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/audio/push",configuration = FeignConfiguration.class)
public interface AudioPushServiceFeign {

    /**
     * 获取语音对讲推流地址
     */
    @RequestMapping(value = "/audio_push_path",method = RequestMethod.GET)
    RestResult<?> findAudioPushPath(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId);



    /**
     * 语音广播命令
     * @param deviceId 设备国标编号
     * @return
     */
    @RequestMapping(value = "/broadcast",method = RequestMethod.GET)
    RestResult<?> broadcast(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId);

}
