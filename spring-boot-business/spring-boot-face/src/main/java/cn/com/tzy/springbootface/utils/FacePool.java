package cn.com.tzy.springbootface.utils;

import cn.com.tzy.springbootcomm.excption.BizException;
import cn.com.tzy.springbootface.config.FaceApplicationContext;
import cn.com.tzy.springbootface.proxy.GenderPredictorProxy;
import cn.com.tzy.springbootface.proxy.MaskDetectorProxy;
import cn.com.tzy.springbootface.vo.FaceVo;
import com.seeta.sdk.*;
import com.seeta.sdk.util.SeetafaceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Log4j2
public class FacePool {

    public static Builder builder(MultipartFile file) {
        return new Builder(file);
    }

    public static Builder builder(MultipartFile file,boolean isWriteCut) {
        return new Builder(file,isWriteCut);
    }

    //1v1 图片比对 返回相似度
    public static float comparison(float[] features1,float[] features2){
        return FaceApplicationContext.getFaceRecognizerProxy().calculateSimilarity(features1, features2);
    }

    public static class Builder {
        //传输的人脸图片
        private final MultipartFile file;
        //转换后人脸照片
        private SeetaImageData imageData;
        //是否打印日志
        private boolean isLogs;
        //是否裁剪
        private boolean isWriteCut;
        //是否人脸特征数组
        private  boolean isExtract;
        //是否活体识别
        private boolean isPredict;
        //是否检测性别
        private boolean isGender;
        //是否检测年龄
        private boolean isAge;
        //是否检测眼睛状态
        private boolean isDetect;
        //是否口罩状态
        private boolean isMask;
        private SeetaRect rect;
        private SeetaPointF[] seetaPointFS;

        public Builder(MultipartFile file){
            this.file = file;
            imageData = SeetafaceUtil.toMultipartData(file);
        }
        public Builder(MultipartFile file,boolean isWriteCut){
            this.file = file;
            this.isWriteCut = isWriteCut;
            imageData = SeetafaceUtil.toMultipartData(file);
        }
        //开启日志
        public Builder logs(){
            isLogs= true;
            return this;
        }
        //开启活体识别
        public Builder predictStatus(){
            isPredict= true;
            return this;
        }
        //开启检测性别
        public Builder genderStatus(){
            isGender = true;
            return this;
        }
        //开启检测年龄
        public Builder ageStatus(){
            isAge = true;
            return this;
        }
        //开启检测眼睛状态
        public Builder detectStatus(){
            isDetect = true;
            return this;
        }
        //开启口罩状态
        public Builder maskStatus(){
            isMask = true;
            return this;
        }
        //开启人脸特征数组
        public Builder extract(){
            isExtract = true;
            return this;
        }


        //获取人脸特征点
        private void mask(){
            LandmarkerMask mask = FaceApplicationContext.getFaceLandmarkerProxy().isMask(imageData, rect);
            if(mask == null || mask.getMasks()== null ||mask.getMasks().length == 0){
                log.error("未获取人脸特质点");
                throw new BizException("未获取人脸特质点");
            }
            seetaPointFS = mask.getSeetaPointFS();
        }
        //裁剪
        private void writeCut(){
            BufferedImage bufferedImage = SeetafaceUtil.toBufferedImage(imageData);
            imageData = SeetafaceUtil.toSeetaImageData(SeetafaceUtil.writeCut(bufferedImage, rect));
        }

        /**
         * 获取人脸信息数组
         */
        private SeetaRect findSeetaFaceInfo(SeetaImageData imageData){
            //2.获取人脸质量信息
            SeetaRect[] data;
            try {
                data = FaceApplicationContext.getFaceDetectorProxy().detect(imageData);
            } catch (Exception e) {
                log.error("获取人脸质量信息错误:",e);
                throw new BizException("获取人脸质量信息错误");
            }
            if(data == null || data.length == 0){
                throw new BizException("未检测到人脸信息");
            }
            //3.保证图片中只有一张人脸
            if(data.length > 1){
                throw new BizException("检测到多张人脸，请保证图片中只有一人");
            }
            return data[0];
        }

        public FaceVo build(){
            long startTime = System.currentTimeMillis();
            long endTime = startTime;
            FaceVo faceVo = new FaceVo();
            //获取人脸图片信息数组
            rect = findSeetaFaceInfo(imageData);
            //是否需要裁剪
            if(isWriteCut){
                writeCut();
                rect = findSeetaFaceInfo(imageData);
            }
            //获取人脸特征
            mask();
            if(isLogs){
                endTime =System.currentTimeMillis();
                log.info("人脸特征耗时：{}",endTime-startTime);
                startTime =endTime;
            }
            //是否活体检测真伪
            if(isPredict){
                FaceAntiSpoofing.Status predict = FaceApplicationContext.getFaceAntiSpoofingProxy().predict(imageData, rect, seetaPointFS);
                faceVo.setPredict(predict.ordinal());
                if(isLogs){
                    endTime =System.currentTimeMillis();
                    log.info("活体检测真伪耗时：{}",endTime-startTime);
                    startTime =endTime;
                }
            }
            //是否性别检测
            if(isGender){
                GenderPredictorProxy.GenderItem genderItem = FaceApplicationContext.getGenderPredictorProxy().predictGenderWithCrop(imageData, seetaPointFS);
                faceVo.setGender(genderItem.getGender()==GenderPredictor.GENDER.MALE?1:2);
                if(isLogs){
                    endTime =System.currentTimeMillis();
                    log.info("性别检测耗时：{}",endTime-startTime);
                    startTime =endTime;
                }
            }
            //是否年龄检测
            if(isAge){
                int age = FaceApplicationContext.getAgePredictorProxy().predictAgeWithCrop(imageData, seetaPointFS);
                faceVo.setAge(age);
                if(isLogs){
                    endTime =System.currentTimeMillis();
                    log.info("年龄检测耗时：{}",endTime-startTime);
                    startTime =endTime;
                }
            }
            //是否眼睛检测
            if(isDetect){
                EyeStateDetector.EYE_STATE[] detect = FaceApplicationContext.getEyeStateDetectorProxy().detect(imageData, seetaPointFS);
                faceVo.setLeftEyeState(detect[0]==EyeStateDetector.EYE_STATE.EYE_CLOSE?0:detect[0]==EyeStateDetector.EYE_STATE.EYE_OPEN?1:detect[0]==EyeStateDetector.EYE_STATE.EYE_RANDOM?2:3);
                faceVo.setRightEyeState(detect[1]==EyeStateDetector.EYE_STATE.EYE_CLOSE?0:detect[0]==EyeStateDetector.EYE_STATE.EYE_OPEN?1:detect[0]==EyeStateDetector.EYE_STATE.EYE_RANDOM?2:3);
                if(isLogs){
                    endTime =System.currentTimeMillis();
                    log.info("眼睛检测耗时：{}",endTime-startTime);
                    startTime =endTime;
                }
            }
            //是否口罩检测
            if(isMask){
                MaskDetectorProxy.MaskItem maskStatus = FaceApplicationContext.getMaskDetectorProxy().detect(imageData, rect);
                faceVo.setMaskStatus(maskStatus.getMask()?0:1);
                if(isLogs){
                    endTime =System.currentTimeMillis();
                    log.info("口罩检测耗时：{}",endTime-startTime);
                    startTime =endTime;
                }
            }
            //是否获取人脸特征值
            if(isExtract){
                float[] extract = FaceApplicationContext.getFaceRecognizerProxy().extract(imageData, seetaPointFS);
                faceVo.setExtract(extract);
                if(isLogs){
                    endTime =System.currentTimeMillis();
                    log.info("人脸特征值耗时：{}",endTime-startTime);
                    startTime =endTime;
                }
            }
            return faceVo;
        }
    }




}
