package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootfeignbean.api.staticFile.UpLoadServiceFeign;
import cn.com.tzy.springbootstartervideocore.service.video.UpLoadVideoService;
import cn.com.tzy.springbootvideo.utils.FileSystemResourceMultipartFile;
import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class UpLoadVideoServiceImpl implements UpLoadVideoService {

    @Resource
    private UpLoadServiceFeign upLoadServiceFeign;

    @Override
    public void send(String filePath, String path, String fileName) {
        File file = new File(filePath);
        if(!FileUtil.exist(file)){
            return;
        }
        upLoadServiceFeign.bucketUpload("video",path,fileName,new FileSystemResourceMultipartFile(filePath));
    }
}
