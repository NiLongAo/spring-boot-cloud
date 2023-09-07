package cn.com.tzy.springbootface.service.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.excption.BizException;
import cn.com.tzy.springbootentity.dome.face.Person;
import cn.com.tzy.springbootface.config.FaceFeaturesCache;
import cn.com.tzy.springbootface.proxy.*;
import com.seeta.sdk.*;
import com.seeta.sdk.util.SeetafaceUtil;
import cn.com.tzy.springbootface.service.FaceDetectorService;
import cn.com.tzy.springbootface.service.PersonService;
import cn.com.tzy.springbootface.vo.FaceVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Service
public class FaceDetectorServiceImpl implements FaceDetectorService {

    private final FaceDetectorProxy faceDetectorProxy;//人脸检测器
    private final FaceLandmarkerProxy faceLandmarkerProxy;//人脸特征点检测器
    private final FaceRecognizerProxy faceRecognizerProxy;//人脸识别器
    private final FaceAntiSpoofingProxy faceAntiSpoofingProxy;//活体识别
    private final AgePredictorProxy agePredictorProxy;//年龄估计器
    private final EyeStateDetectorProxy eyeStateDetectorProxy;//眼睛状态检测器
    private final GenderPredictorProxy genderPredictorProxy;//性别估计器
    private final MaskDetectorProxy maskDetectorProxy;//口罩检测器
    private final PersonService personService;

    @Override
    public RestResult<?> findFaceInfo(MultipartFile file) throws Exception {

        FaceVo vo = null;
        long startTime = System.currentTimeMillis();
        long endTime = startTime;

        //1.获取图片信息
        SeetaImageData imageData = SeetafaceUtil.toMultipartData(file);
        SeetaRect rect = findSeetaFaceInfo(imageData);

        endTime =System.currentTimeMillis();
        log.info("1.检测获取图片信息耗时：{}",endTime-startTime);
        startTime =endTime;

        //2.裁剪
        BufferedImage bufferedImage = SeetafaceUtil.toBufferedImage(imageData);
        imageData = SeetafaceUtil.toSeetaImageData(SeetafaceUtil.writeCut(bufferedImage, rect));
        rect = findSeetaFaceInfo(imageData);

        endTime =System.currentTimeMillis();
        log.info("2.裁剪图片耗时：{}",endTime-startTime);
        startTime =endTime;

        //3.获取人脸特征点
        LandmarkerMask mask = faceLandmarkerProxy.isMask(imageData, rect);
        for (int maskMask : mask.getMasks()) {
            if(maskMask != 0 ){
                throw new BizException("检测人脸被遮挡，请确保周围环境");
            }
        }
        SeetaPointF[] points = mask.getSeetaPointFS();

        endTime =System.currentTimeMillis();
        log.info("3.获取人脸特征点耗时：{}",endTime-startTime);
        startTime =endTime;

        //4.活体识别,检测是真伪
        FaceAntiSpoofing.Status status = faceAntiSpoofingProxy.predict(imageData, rect, points);
        log.info("人脸状态：{}",status);

        endTime =System.currentTimeMillis();
        log.info("4.活体识别,检测是真伪耗时：{}",endTime-startTime);
        startTime =endTime;

        if(status != FaceAntiSpoofing.Status.REAL){
            return RestResult.result(RespCode.CODE_2.getValue(),"照片活体检测无法通过");
        }
        //5.获取检测到的性别
        GenderPredictorProxy.GenderItem gender = genderPredictorProxy.predictGenderWithCrop(imageData, points);

        endTime =System.currentTimeMillis();
        log.info("5.获取检测到的性别耗时：{}",endTime-startTime);
        startTime =endTime;

        //6.获取检测到的年龄
        int age = agePredictorProxy.predictAgeWithCrop(imageData, points);

        endTime =System.currentTimeMillis();
        log.info("6.获取检测到的年龄耗时：{}",endTime-startTime);
        startTime =endTime;

        //7.获取检测眼睛状态
        EyeStateDetector.EYE_STATE[] detect = eyeStateDetectorProxy.detect(imageData, points);

        endTime =System.currentTimeMillis();
        log.info("7.获取检测眼睛状态耗时：{}",endTime-startTime);
        startTime =endTime;

        //8.获取口罩状态 口罩算法异常在liunx中运行异常
        MaskDetectorProxy.MaskItem maskStatus = maskDetectorProxy.detect(imageData, rect);

        endTime =System.currentTimeMillis();
        log.info("8.获取口罩状态耗时：{}",endTime-startTime);
        startTime =endTime;

        vo = FaceVo.builder()
                .age(age)
                .gender(gender.getGender()==GenderPredictor.GENDER.MALE?1:2)
                .leftEyeState(detect[0]==EyeStateDetector.EYE_STATE.EYE_CLOSE?0:detect[0]==EyeStateDetector.EYE_STATE.EYE_OPEN?1:detect[0]==EyeStateDetector.EYE_STATE.EYE_RANDOM?2:3)
                .rightEyeState(detect[1]==EyeStateDetector.EYE_STATE.EYE_CLOSE?0:detect[0]==EyeStateDetector.EYE_STATE.EYE_OPEN?1:detect[0]==EyeStateDetector.EYE_STATE.EYE_RANDOM?2:3)
                .maskStatus(maskStatus.getMask()?0:1)
                .build();
        return RestResult.result(RespCode.CODE_0.getValue(),null,vo);
    }

    @Override
    public RestResult<?> comparison(MultipartFile file1, MultipartFile file2) throws Exception {
        float calculateSimilarity = 0.0f;
        //1.获取图片1人脸特征数组
        float[] features1 = findFeatures(file1);
        //获取图片2人脸特征数组
        float[] features2 = findFeatures(file2);
        if (features1 == null || features2 == null) {
            return  RestResult.result(RespCode.CODE_2.getValue(),"图片人脸信息无法识别");
        }
        calculateSimilarity = faceRecognizerProxy.calculateSimilarity(features1, features2);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,calculateSimilarity);
    }

    @Override
    public RestResult<?> search(MultipartFile file, Integer size, Integer calculate) throws Exception {
        List<String> imgIdList=new ArrayList<>();
        List<Person> list = new ArrayList<>();
        //获取全局中所有图片向量
        Map<String, float[]> faceFeatures = FaceFeaturesCache.getFaceFeatures();
        //获取查找的图片向量
        float[] features = findFeatures(file);
        if (features == null) {
            return  RestResult.result(RespCode.CODE_2.getValue(),"图片人脸信息无法识别");
        }
        //返回个数
        size =(size==null || size<=0)?1:size;
        //阀值
        calculate=(calculate == null ||calculate <= 0)?70:calculate;
        //循环次数
        int i = 0;
        //图片编号集合
        for (Map.Entry<String, float[]> stringEntry : faceFeatures.entrySet()) {
            float v = faceRecognizerProxy.calculateSimilarity(features, stringEntry.getValue());
            if(calculate <= v *100){
                i++;
                imgIdList.add(stringEntry.getKey());
            }
            if(size <= i ){
                break;
            }
        }
        if(imgIdList.isEmpty()){
            return RestResult.result(RespCode.CODE_0.getValue(),null,imgIdList);
        }
        list = personService.selectImgIdList(imgIdList);
        return RestResult.result(RespCode.CODE_0.getValue(),null,list);
    }

    /**
     * 获取人脸信息数组
     */
    private synchronized SeetaRect findSeetaFaceInfo(SeetaImageData imageData) throws Exception {
        //2.获取人脸质量信息
        SeetaRect[] data = faceDetectorProxy.detect(imageData);
        if(data == null || data.length == 0){
            throw new BizException("未检测到人脸信息");
        }
        //3.保证图片中只有一张人脸
        if(data.length > 1){
            throw new BizException("检测到多张人脸，请保证图片中只有一人");
        }
        return data[0];
    }

    /**
     * 获取人脸特征数组
     */
    private synchronized float[] findFeatures(MultipartFile file) throws Exception {
        //1.获取图片信息
        SeetaImageData imageData = SeetafaceUtil.toMultipartData(file);

        SeetaRect rect = findSeetaFaceInfo(imageData);
        //2.获取人脸特征点
        LandmarkerMask mask = faceLandmarkerProxy.isMask(imageData, rect);
        for (int maskMask : mask.getMasks()) {
            if(maskMask != 0 ){
                throw new BizException("检测人脸被遮挡，请确保周围环境");
            }
        }
        SeetaPointF[] points = mask.getSeetaPointFS();

        //5.返回人脸特征数组
        return faceRecognizerProxy.extract(imageData, points);
    }

}
