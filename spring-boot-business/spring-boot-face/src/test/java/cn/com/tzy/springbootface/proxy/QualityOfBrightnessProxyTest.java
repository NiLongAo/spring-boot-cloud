package cn.com.tzy.springbootface.proxy;

import cn.com.tzy.springbootface.pool.SeetaConfSetting;
import com.seeta.sdk.*;
import com.seeta.sdk.*;
import com.seeta.sdk.util.LoadNativeCore;
import com.seeta.sdk.util.SeetafaceUtil;

public class QualityOfBrightnessProxyTest {

    public static String CSTA_PATH = "D:\\face\\models";

    public static String[] detector_cstas = {CSTA_PATH + "/face_detector.csta"};

    public static String[] landmarker_cstas = {CSTA_PATH + "/face_landmarker_pts5.csta"};


    public static String fileName =  "D:\\face\\image\\me\\88.jpg";

    static {
        LoadNativeCore.LOAD_NATIVE("",SeetaDevice.SEETA_DEVICE_AUTO);
    }

    public static void main(String[] args) {

        //人脸识别检测器对象池配置，可以配置对象的个数哦
        SeetaConfSetting detectorPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, detector_cstas, SeetaDevice.SEETA_DEVICE_AUTO));
        //人脸检测器对象池代理 ， spring boot可以用FaceDetectorProxy来配置Bean
        FaceDetectorProxy faceDetectorProxy = new FaceDetectorProxy(detectorPoolSetting);

        //人脸关键点定位器对象池配置
        SeetaConfSetting faceLandmarkerPoolSetting = new SeetaConfSetting(new SeetaModelSetting(0, landmarker_cstas, SeetaDevice.SEETA_DEVICE_AUTO));
        //人脸关键点定位器对象池代理 ， spring boot可以用FaceLandmarkerProxy来配置Bean
        FaceLandmarkerProxy faceLandmarkerProxy = new FaceLandmarkerProxy(faceLandmarkerPoolSetting);

        SeetaConfSetting qualityOfBrightnessPoolSetting = new SeetaConfSetting();
        QualityOfBrightnessProxy qualityOfBrightnessProxy = new QualityOfBrightnessProxy(qualityOfBrightnessPoolSetting);

        try {
            SeetaImageData image = SeetafaceUtil.toSeetaImageData(fileName);
            SeetaRect[] detects = faceDetectorProxy.detect(image);
            for (SeetaRect seetaRect : detects) {
                SeetaPointF[] pointFS = faceLandmarkerProxy.mark(image, seetaRect);
                QualityOfBrightnessProxy.BrightnessItem check = qualityOfBrightnessProxy.check(image, seetaRect, pointFS);
                System.out.println(check.getCheck());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
