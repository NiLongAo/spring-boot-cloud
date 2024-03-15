package cn.com.tzy.springbootfeignface.api.face;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "face-server",contextId = "face-server",path = "/api/face/detector",configuration = FeignConfiguration.class)
public interface FaceDetectorServiceFeign {

    @RequestMapping(value = "/find_face_info.htm",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    RestResult<?> findFaceInfo(@RequestPart("file") MultipartFile file);

    @RequestMapping(value = "/search.htm",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    PageResult search(@RequestPart("file")MultipartFile file,
                      @RequestParam(value = "size",required = false)Integer size,
                      @RequestParam(value = "calculate",required = false)Integer calculate);


}
