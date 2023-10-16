package cn.com.tzy.springbootface.service.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.face.Person;
import cn.com.tzy.springbootface.config.FaceFeaturesCache;
import cn.com.tzy.springbootface.utils.FacePool;
import cn.com.tzy.springbootface.service.FaceDetectorService;
import cn.com.tzy.springbootface.service.PersonService;
import cn.com.tzy.springbootface.vo.FaceVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class FaceDetectorServiceImpl implements FaceDetectorService {

    @Resource
    private PersonService personService;
    @Override
    public RestResult<?> findFaceInfo(MultipartFile file) {
        FaceVo build = FacePool.builder(file,true)
                .logs()
                .predictStatus()
                .ageStatus()
                .detectStatus()
                .genderStatus()
                .maskStatus()
                .build();
        return RestResult.result(RespCode.CODE_0.getValue(),null,build);
    }

    @Override
    public RestResult<?> comparison(MultipartFile file1, MultipartFile file2){
        FaceVo build1 = FacePool.builder(file1,true)
                .extract()
                .build();
        FaceVo build2 = FacePool.builder(file2,true)
                .extract()
                .build();
        if (build1.getExtract() == null || build2.getExtract() == null) {
            return  RestResult.result(RespCode.CODE_2.getValue(),"图片人脸信息无法识别");
        }
        float comparison = FacePool.comparison(build1.getExtract(), build2.getExtract());
        return  RestResult.result(RespCode.CODE_0.getValue(),null,comparison);
    }

    @Override
    public RestResult<?> search(MultipartFile file, Integer size, Integer calculate){
        List<String> imgIdList=new ArrayList<>();
        List<Person> list = new ArrayList<>();
        //获取全局中所有图片向量
        Map<String, float[]> faceFeatures = FaceFeaturesCache.getFaceFeatures();
        FaceVo build = FacePool.builder(file,true)
                .extract()
                .build();
        //获取查找的图片向量
        float[] features = build.getExtract();
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
            float v = FacePool.comparison(features, stringEntry.getValue());
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


}
