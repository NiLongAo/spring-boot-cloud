package cn.com.tzy.springbootface.config;

import cn.com.tzy.springbootface.proxy.*;
import cn.hutool.extra.spring.SpringUtil;

public class FaceApplicationContext {

    //人脸检测器
    public static FaceDetectorProxy getFaceDetectorProxy(){return SpringUtil.getBean(FaceDetectorProxy.class);}
    //人脸特征点检测器
    public static FaceLandmarkerProxy getFaceLandmarkerProxy(){return SpringUtil.getBean(FaceLandmarkerProxy.class);}
    //人脸识别器
    public static FaceRecognizerProxy getFaceRecognizerProxy(){return SpringUtil.getBean(FaceRecognizerProxy.class);}
    //活体识别
    public static FaceAntiSpoofingProxy getFaceAntiSpoofingProxy(){return SpringUtil.getBean(FaceAntiSpoofingProxy.class);}
    //年龄估计器
    public static AgePredictorProxy getAgePredictorProxy(){return SpringUtil.getBean(AgePredictorProxy.class);}
    //眼睛状态检测器
    public static EyeStateDetectorProxy getEyeStateDetectorProxy(){return SpringUtil.getBean(EyeStateDetectorProxy.class);}
    //性别估计器
    public static GenderPredictorProxy getGenderPredictorProxy(){return SpringUtil.getBean(GenderPredictorProxy.class);}
    //口罩检测器
    public static MaskDetectorProxy getMaskDetectorProxy(){return SpringUtil.getBean(MaskDetectorProxy.class);}
}
