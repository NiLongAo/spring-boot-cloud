package cn.com.tzy.springbootface.service;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import org.springframework.web.multipart.MultipartFile;

public interface FaceDetectorService {

    /**
     * 根据人脸图片返回人脸特征值
     * @param file
     * @return
     */
    RestResult<?> findFaceInfo(MultipartFile file) throws Exception;


    /**
     * 1v1 图片比对 返回相似度
     */
    RestResult<?> comparison(MultipartFile file1,MultipartFile file2) throws Exception;

    /**
     * 图片检索 根据图片，在库中找到相似图片
     * @param file 图片
     * @param size 返回最大数量
     * @param calculate 阀值
     * @return
     */
    RestResult<?> search(MultipartFile file,Integer size,Integer calculate) throws Exception;

}
