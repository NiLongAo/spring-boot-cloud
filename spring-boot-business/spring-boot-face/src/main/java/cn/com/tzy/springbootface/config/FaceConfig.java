package cn.com.tzy.springbootface.config;

import cn.com.tzy.springbootface.pool.SeetaConfSetting;
import cn.com.tzy.springbootface.properties.FaceProperties;
import cn.com.tzy.springbootface.proxy.*;
import com.seeta.sdk.SeetaDevice;
import com.seeta.sdk.SeetaModelSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 人脸识别相关功能注册到spring容器中
 * https://gitee.com/crazy-of-pig/seeta-sdk-platform.git
 */
@Log4j2
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(FaceProperties.class)
public class FaceConfig {

    private final FaceProperties faceProperties;

    /**
     * 人脸检测器
     */
    @Bean
    public FaceDetectorProxy faceDetectorProxy(){
        //人脸识别检测器对象池配置，可以配置对象的个数哦
        SeetaConfSetting detectorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/face_detector.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //人脸检测器对象池代理 ， spring boot可以用FaceDetectorProxy来配置Bean
        FaceDetectorProxy faceDetectorProxy = new FaceDetectorProxy(detectorPoolSetting);
        log.info("人脸检测器加载完成");
        return faceDetectorProxy;
    }
    /**
     * 人脸特征点检测器
     */
    @Bean
    public FaceLandmarkerProxy faceLandmarkerProxy(){
        //人脸关键点定位器对象池配置
        SeetaConfSetting faceLandmarkerPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/face_landmarker_pts5.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //人脸关键点定位器对象池代理 ， spring boot可以用FaceLandmarkerProxy来配置Bean
        FaceLandmarkerProxy faceLandmarkerProxy = new FaceLandmarkerProxy(faceLandmarkerPoolSetting);
        log.info("人脸特征点检测器加载完成");
        return faceLandmarkerProxy;
    }
    /**
     * 年龄估计器
     * @return
     */
    @Bean
    public AgePredictorProxy agePredictorProxy(){
        //人脸年龄检测器对象池配置
        SeetaConfSetting agePredictorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/age_predictor.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //人脸年龄检测器对象池代理 ， spring boot可以用AgePredictorProxy来配置Bean
        AgePredictorProxy agePredictorProxy = new AgePredictorProxy(agePredictorPoolSetting);
        log.info("年龄估计器加载完成");
        return agePredictorProxy;
    }
    /**
     * 眼睛状态检测器
     */
    @Bean
    public EyeStateDetectorProxy eyeStateDetectorProxy(){
        //眼睛状态检测器 对象池配置
        SeetaConfSetting eyeStateDetectorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/eye_state.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //眼睛状态检测器对象池代理 ， spring boot可以用EyeStateDetectorProxy来配置Bean
        EyeStateDetectorProxy eyeStateDetectorProxy = new EyeStateDetectorProxy(eyeStateDetectorPoolSetting);
        log.info("眼睛状态检测器加载完成");
        return eyeStateDetectorProxy;
    }

    /**
     * 活体识别
     */
    @Bean
    public FaceAntiSpoofingProxy faceAntiSpoofingProxy(){
        SeetaConfSetting faceAntiSpoofingSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/fas_first.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        FaceAntiSpoofingProxy faceAntiSpoofingProxy = new FaceAntiSpoofingProxy(faceAntiSpoofingSetting);
        log.info("活体识别加载完成");
        return faceAntiSpoofingProxy;
    }

    /**
     * 人脸识别器 人脸识别器要求输入原始图像数据和人脸特征点（或者裁剪好的人脸数据），对输入的人脸提取特征值数组，根据提取的特征值数组对人脸进行相似度比较。
     */
    @Bean
    public FaceRecognizerProxy faceRecognizerProxy(){
        SeetaConfSetting faceRecognizerPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/face_recognizer.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //人脸特征提取器，人脸特征相似度计算器
        FaceRecognizerProxy faceRecognizerProxy = new FaceRecognizerProxy(faceRecognizerPoolSetting);
        log.info("人脸识别器加载完成");
        return faceRecognizerProxy;
    }

    /**
     * 性别估计器
     */
    @Bean
    public GenderPredictorProxy genderPredictorProxy(){
        //性别识别器对象池配置
        SeetaConfSetting genderPredictorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/gender_predictor.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //性别识别器对象池代理 ， spring boot可以用GenderPredictorProxy来配置Bean
        GenderPredictorProxy genderPredictorProxy = new GenderPredictorProxy(genderPredictorPoolSetting);
        log.info("性别估计器加载完成");
        return genderPredictorProxy;
    }
    /**
     * 口罩检测器
     */
    @Bean
    public MaskDetectorProxy maskDetectorProxy(){
        //口罩检测器对象池配置，可以配置对象的个数哦
        SeetaConfSetting maskDetectorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/mask_detector.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        //口罩检测器对象池代理 ， spring boot可以用MaskDetectorProxy来配置Bean
        MaskDetectorProxy maskDetectorProxy = new MaskDetectorProxy(maskDetectorPoolSetting);
        log.info("口罩检测器加载完成");
        return maskDetectorProxy;
    }

    /**
     * 深度学习的人脸姿态评估器
     */
    @Bean
    public PoseEstimatorProxy poseEstimatorProxy(){
        SeetaConfSetting poseEstimatorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/pose_estimation.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        PoseEstimatorProxy poseEstimatorProxy = new PoseEstimatorProxy(poseEstimatorPoolSetting);
        log.info("深度学习的人脸姿态评估器加载完成");
        return poseEstimatorProxy;
    }

    /**
     * 非深度的人脸亮度评估器
     */
    @Bean
    public QualityOfBrightnessProxy qualityOfBrightnessProxy(){
        SeetaConfSetting qualityOfBrightnessPoolSetting = new SeetaConfSetting();
        QualityOfBrightnessProxy qualityOfBrightnessProxy = new QualityOfBrightnessProxy(qualityOfBrightnessPoolSetting);
        log.info("非深度的人脸亮度评估器加载完成");
        return qualityOfBrightnessProxy;
    }
    /**
     * 非深度学习的人脸清晰度评估器
     */
    @Bean
    public QualityOfClarityProxy qualityOfClarityProxy(){
        SeetaConfSetting qualityOfClarityProxyS = new SeetaConfSetting();
        QualityOfClarityProxy qualityOfClarityProxy = new QualityOfClarityProxy(qualityOfClarityProxyS);
        log.info("非深度学习的人脸清晰度评估器加载完成");
        return qualityOfClarityProxy;
    }
    /**
     * 非深度学习的人脸完整度评估器，评估人脸靠近图像边缘的程度。
     */
    @Bean
    public QualityOfIntegrityProxy qualityOfIntegrityProxy(){
        SeetaConfSetting setting = new SeetaConfSetting();
        QualityOfIntegrityProxy qualityOfIntegrityProxy = new QualityOfIntegrityProxy(setting);
        log.info("非深度学习的人脸完整度评估器加载完成");
        return qualityOfIntegrityProxy;
    }
    /**
     * 深度学习的人脸清晰度评估器
     */
    @Bean
    public QualityOfLBNProxy qualityOfLBNProxy(){
        SeetaConfSetting setting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/pose_estimation.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        QualityOfLBNProxy qualityOfLBNProxy = new QualityOfLBNProxy(setting);
        log.info("深度学习的人脸清晰度评估器评估器加载完成");
        return qualityOfLBNProxy;
    }
    /**
     * 非深度学习的人脸姿态评估器
     */
    @Bean
    public QualityOfPoseExProxy qualityOfPoseExProxy(){
        SeetaConfSetting setting = new SeetaConfSetting(new SeetaModelSetting(0, new String[]{String.format("%s/pose_estimation.csta", faceProperties.getCstaPath())}, SeetaDevice.SEETA_DEVICE_AUTO));
        QualityOfPoseExProxy qualityOfPoseExProxy = new QualityOfPoseExProxy(setting);
        log.info("非深度学习的人脸姿态评估器ex加载完成");
        return qualityOfPoseExProxy;
    }

    /**
     * 非深度学习的人脸姿态评估器
     */
    @Bean
    public QualityOfPoseProxy qualityOfPoseProxy(){
        SeetaConfSetting setting = new SeetaConfSetting();
        QualityOfPoseProxy qualityOfPoseProxy = new QualityOfPoseProxy(setting);
        log.info("非深度学习的人脸姿态评估器加载完成");
        return qualityOfPoseProxy;
    }

    /**
     * 非深度学习的人脸尺寸评估器
     */
    @Bean
    public QualityOfResolutionProxy qualityOfResolutionProxy(){
        SeetaConfSetting setting = new SeetaConfSetting();
        QualityOfResolutionProxy qualityOfResolutionProxy = new QualityOfResolutionProxy(setting);
        log.info("非深度学习的人脸尺寸评估器加载完成");
        return qualityOfResolutionProxy;
    }



}
