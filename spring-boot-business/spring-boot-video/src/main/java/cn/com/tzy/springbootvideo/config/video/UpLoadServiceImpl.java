package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootfeignbean.api.staticFile.UpLoadServiceFeign;
import cn.com.tzy.springbootstartervideocore.service.video.UpLoadService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UpLoadServiceImpl implements UpLoadService {

    @Resource
    private UpLoadServiceFeign upLoadServiceFeign;

    @Override
    public void send(String filePath, String path, String fileName) {

        //upLoadServiceFeign.upload("",,,);
    }
}
