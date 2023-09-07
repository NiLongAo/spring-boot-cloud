package cn.com.tzy.springbootface;

import com.seeta.sdk.*;
import com.seeta.sdk.*;
import com.seeta.sdk.util.LoadNativeCore;
import com.seeta.sdk.util.SeetafaceUtil;

public class RecognizerTest {

    public static FaceDetector detector = null;

    public static FaceLandmarker faceLandmarker = null;
    public static FaceRecognizer faceRecognizer = null;
    public static String CSTA_PATH = "D:\\face\\models";

    static {
        LoadNativeCore.LOAD_NATIVE("",SeetaDevice.SEETA_DEVICE_AUTO);
    }

    public static void main(String[] args) {

        String[] detector_cstas = {CSTA_PATH + "/face_detector.csta"};

        String[] landmarker_cstas = {CSTA_PATH + "/face_landmarker_pts5.csta"};

        String[] recognizer_cstas = {CSTA_PATH + "/face_recognizer_mask.csta"};
        try {
            detector = new FaceDetector(new SeetaModelSetting(0, detector_cstas, SeetaDevice.SEETA_DEVICE_AUTO));
            faceLandmarker = new FaceLandmarker(new SeetaModelSetting(0, landmarker_cstas, SeetaDevice.SEETA_DEVICE_AUTO));
            faceRecognizer = new FaceRecognizer(new SeetaModelSetting(0, recognizer_cstas, SeetaDevice.SEETA_DEVICE_AUTO));
            String fileName = "D:\\face\\image\\me\\00.jpg";
            String fileName2 = "D:\\face\\image\\me\\mask2.jpg";
            float[] features1 = extract(fileName);
            float[] features2 = extract(fileName2);

            if (features1 != null && features2 != null) {
                float calculateSimilarity = faceRecognizer.CalculateSimilarity(features1, features2);
                System.out.printf("相似度:%f\n", calculateSimilarity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取特征数组
     *
     * @author YaoCai Lin
     * @time 2020年7月15日 下午12:10:56
     */
    private static float[] extract(String fileName) {
        SeetaImageData image = SeetafaceUtil.toSeetaImageData(fileName);

        SeetaRect[] detects = detector.Detect(image);
        for (SeetaRect seetaRect : detects) {

            SeetaPointF[] pointFS = new SeetaPointF[5];
            int[] masks = new int[5];
            faceLandmarker.mark(image, seetaRect, pointFS, masks);
            float[] features = new float[faceRecognizer.GetExtractFeatureSize()];

            faceRecognizer.Extract(image, pointFS, features);
            return features;
        }
        return null;
    }
}