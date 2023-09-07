package cn.com.tzy.springbootface.config;

import cn.com.tzy.springbootface.properties.FaceProperties;
import com.seeta.sdk.SeetaDevice;
import com.seeta.sdk.util.LoadNativeCore;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Log4j2
public class SeetaFaceInit implements InitializingBean {

    @Resource
    private FaceProperties faceProperties;

    @Override
    public void afterPropertiesSet(){
        //初始化人脸识别模块
        try {
            LoadNativeCore.LOAD_NATIVE(faceProperties.getDllPath(),SeetaDevice.SEETA_DEVICE_AUTO);
            log.info("人脸识别模块初始化成功");
        }catch (Exception e){
            log.error("人脸识别模块初始化失败",e);
        }
    }
}
