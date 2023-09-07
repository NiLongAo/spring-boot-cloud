package cn.com.tzy.springbootface;


import cn.hutool.core.img.ImgUtil;
import com.seeta.sdk.*;
import com.seeta.sdk.util.LoadNativeCore;
import com.seeta.sdk.util.SeetafaceUtil;
import com.seeta.sdk.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 人脸检测器 测试
 */
public class DetectorTest {


    public static String DLL_PATH = "E:\\work\\java\\spring-boot-cloud\\spring-boot-business\\spring-boot-face\\conf\\seetaface6";
    public static String CSTA_PATH = "E:\\work\\java\\spring-boot-cloud\\spring-boot-business\\spring-boot-face\\conf\\sf3.0_models";
    public static String TEST_PICT = "D:\\face\\微信图片_20220325135813.jpg";

    /**
     * 初始化加载dll
     */
    static {
        LoadNativeCore.LOAD_NATIVE(DLL_PATH,SeetaDevice.SEETA_DEVICE_AUTO);
    }

    public static void main(String[] args) {

        String[] detector_cstas = {CSTA_PATH + "/face_detector.csta"};

        try {
            /**
             * 人脸检测器
             */
            FaceDetector detector = new FaceDetector(new SeetaModelSetting(-1, detector_cstas, SeetaDevice.SEETA_DEVICE_AUTO));
            BufferedImage image = SeetafaceUtil.toBufferedImage(TEST_PICT);
            //image = SeetafaceUtil.resize(image, 480, 320);
            //照片数据
            SeetaImageData imageData = SeetafaceUtil.toSeetaImageData(image);

            //检测到的人脸坐标
            SeetaRect[] detects = detector.Detect(imageData);
            int i = 0;
            for (SeetaRect rect : detects) {
                //face_landmarker_pts5 根据这个来的
                SeetaPointF[] pointFS = new SeetaPointF[68];

                System.out.printf("第%s张人脸 x: %s, y: %s, width: %s, height: %s\n", i++, rect.x, rect.y, rect.width, rect.height);
                image = SeetafaceUtil.writeRect(image, rect);

            }
            SeetafaceUtil.show("人脸检测", image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
