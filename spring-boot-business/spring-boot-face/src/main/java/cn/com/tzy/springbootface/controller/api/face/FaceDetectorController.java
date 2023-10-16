package cn.com.tzy.springbootface.controller.api.face;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootface.service.FaceDetectorService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸图片识别相关接口
 */
@RestController("ApiFaceDetectorController")
@RequestMapping(value = "/api/face/detector")
public class FaceDetectorController extends ApiController {

    @Autowired
    private FaceDetectorService faceDetectorService;

    /**
     * 根据人脸图片返回人脸特征值
     * @param file
     * @return
     */
    @PostMapping(value = "/find_face_info.htm")
    public RestResult<?> findFaceInfo(@RequestParam("file")MultipartFile file){
        return faceDetectorService.findFaceInfo(file);
    }

    /**
     * 1v1 图片比对 返回相似度
     */
    @PostMapping(value = "/comparison.htm")
    public RestResult<?> comparison(@RequestParam("file1")MultipartFile file1,@RequestParam("file2")MultipartFile file2){
        return faceDetectorService.comparison(file1,file2);
    }

    /**
     * 图片检索 根据图片，在库中找到相似图片
     * @param file 检索的图片
     * @param size 搜索数量 默认一条
     * @param calculate 阀值 默认70
     * @return
     */
    @PostMapping(value = "/search.htm")
    RestResult<?> search(
            @RequestParam("file")MultipartFile file,
            @RequestParam(value = "size",required = false)Integer size,
            @RequestParam(value = "calculate",required = false)Integer calculate
    ) {
        return faceDetectorService.search(file,size,calculate);
    }

}
