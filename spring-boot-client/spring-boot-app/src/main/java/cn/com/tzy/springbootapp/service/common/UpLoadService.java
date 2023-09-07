package cn.com.tzy.springbootapp.service.common;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignbean.api.staticFile.UpLoadServiceFeign;
import cn.com.tzy.springbootfeignface.api.face.FaceDetectorServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UpLoadService {

    @Autowired
    private UpLoadServiceFeign upLoadServiceFeign;
    @Autowired
    private FaceDetectorServiceFeign faceDetectorServiceFeign;


    public RestResult<?> upload(Integer type, MultipartFile file){
        if(ConstEnum.StaticPath.USER_IMAGE_PATH.getType() == type){
            RestResult<?> faceInfo = faceDetectorServiceFeign.findFaceInfo(file);
            if(faceInfo.getCode() != RespCode.CODE_0.getValue()){
                return faceInfo;
            }
        }
        return upLoadServiceFeign.upload(type,file);
    }
}
