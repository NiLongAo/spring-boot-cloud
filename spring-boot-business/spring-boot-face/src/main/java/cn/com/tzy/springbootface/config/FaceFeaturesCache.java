package cn.com.tzy.springbootface.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FaceFeaturesCache {

    private static Map<String, float[]>  featuresMap= new HashMap<>();

    /**
     * 获取人脸特征数组。
     * @return 人脸特征数组
     */
    public static Map<String, float[]> getFaceFeatures() {
        return featuresMap;
    }

    /**
     * 设置人脸特征数组
     * @param faceFeatureMap 人脸特征数组集合
     */
    public static void setFaceFeatures(Map<String, float[]> faceFeatureMap) {
        featuresMap = faceFeatureMap;
    }

    /**
     * 添加人脸特征数组
     * @param img_id 人脸图片编号Id
     * @param features 人脸图片特征数组
     */
    public static void addFaceFeatures(String img_id, float[] features) {
        featuresMap.put(img_id,features);
    }

    /**
     * 删除人脸特征数组
     * @param img_id 人脸图片编号Id
     */
    public static void deleteFaceFeatures(String img_id) {
        featuresMap.remove(img_id);
    }
    public static void clear() {
        featuresMap = new HashMap<>();
    }
}
