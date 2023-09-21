package cn.com.tzy.springbootfeignbean.api.staticFile;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/static/up_load",configuration = FeignConfiguration.class)
public interface UpLoadServiceFeign {

    @RequestMapping(value = "/upload.htm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    RestResult<?> upload(@RequestParam("type") Integer type, @RequestPart("file") MultipartFile file);

    @RequestMapping(value = "/upload.htm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    RestResult<?> upload(@RequestParam("type") Integer type,@RequestParam("isBuildName") Boolean isBuildName, @RequestPart("file") MultipartFile file);

    @RequestMapping(value = "/bucket_upload.htm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,method = RequestMethod.POST)
    RestResult<?> bucketUpload(@RequestParam(value = "bucketName") String bucketName, @RequestParam("path") String path, @RequestParam("fileName") String fileName, @RequestParam("file")MultipartFile file);

    @RequestMapping(value = "/delete.htm", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> delete(@RequestParam("name") String name);

    @RequestMapping(value = "/find_name.htm", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> findName(@RequestParam("filename") String filename);


}
