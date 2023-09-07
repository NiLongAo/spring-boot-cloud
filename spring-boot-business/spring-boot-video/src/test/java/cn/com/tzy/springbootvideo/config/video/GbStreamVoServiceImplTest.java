package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideocore.service.video.GbStreamVoService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class GbStreamVoServiceImplTest {

    @Resource
    private GbStreamVoService gbStreamVoService;

    @Test
    void queryGbStreamListInPlatform() {
        List<DeviceChannelVo> deviceChannelVos = gbStreamVoService.queryGbStreamListInPlatform("34020000002000000001", null);
        System.out.println(deviceChannelVos);
    }
}