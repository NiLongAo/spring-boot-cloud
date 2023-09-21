package cn.com.tzy.springbootbean.controller.api.staticFile;

import cn.com.tzy.springbootbean.config.init.AppConfig;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstarterminio.utils.MinioUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@Log4j2
@RestController("ApiStaticUpLoadController")
@RequestMapping(value = "/api/static/up_load")
public class UpLoadController extends ApiController {

    @Value("${minio.bucketName}")
    private String BUCKETNAME;
    @Autowired
    private AppConfig appConfig;
    @Resource
    private MinioUtils minioUtils;

    @ResponseBody
    @PostMapping(value = "/upload.htm")
    public RestResult<?> upload(@RequestParam("type") Integer type,@RequestParam (name = "isBuildName",required = false) Boolean isBuildName,@RequestParam("file")MultipartFile file) throws Exception {
        Long userId = JwtUtils.getUserId();
        String url = ConstEnum.StaticPath.getUrl(type);
        if(StringUtils.isEmpty(url)){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到保存路径");
        }
        if(file == null ){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到文件信息");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String fileName =UUID.randomUUID().toString();
        if(isBuildName != null && isBuildName){
            fileName = userId+"/"+split[0];
        }
        String format = minioUtils.upload(BUCKETNAME, String.format(url, DateFormatUtils.format(new Date(), Constant.DATE_FORMAT)), fileName, file);
        Map<String,String> map =new HashMap<>();
        map.put("path",format);
        map.put("fullPath",appConfig.findStaticPath(format));
        return RestResult.result(RespCode.CODE_0.getValue(),"上传成功",map);
    }

    @ResponseBody
    @PostMapping(value = "/bucket_upload.htm")
    public RestResult<?> bucketUpload(@RequestParam(value = "bucketName",required = false) String bucketName, @RequestParam("path") String path,@RequestParam("fileName") String fileName, @RequestParam("file")MultipartFile file
    ) throws Exception {
        String format = minioUtils.upload(bucketName,path, fileName, file);
        Map<String,String> map = new HashMap<>();
        map.put("path",format);
        map.put("fullPath",appConfig.findStaticPath(format));
        return RestResult.result(RespCode.CODE_0.getValue(),"上传成功",map);
    }




    @ResponseBody
    @PostMapping(value = "/delete.htm")
    public RestResult<?> delete(@RequestParam("name") String name) {
        try {
            minioUtils.removeObject(BUCKETNAME,name);
        } catch (Exception e) {
            log.error("删除失败:",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"删除失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @ResponseBody
    @GetMapping(value = "/find_name.htm")
    public RestResult<?> findName(@RequestParam("filename") String filename) {
        InputStream object = null;
        try {
            object = minioUtils.getObject(BUCKETNAME,filename);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = object.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return RestResult.result(RespCode.CODE_0.getValue(),"获取文件信息成功",result.toString("UTF-8"));
        } catch (Exception e) {
            log.error("获取文件信息失败:",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"获取文件信息失败");
        }finally {
            if(object != null) {
                try {
                    object.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @ResponseBody
    @PostMapping(value = "/download.htm")
    public void downloadFiles(@RequestParam("filename") String filename, HttpServletResponse httpResponse) {
        InputStream object = null;
        OutputStream outputStream = null;
        try {
            object =  minioUtils.getObject(BUCKETNAME,filename);
            byte buf[] = new byte[1024];
            int length = 0;
            httpResponse.reset();

            httpResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            httpResponse.setContentType("application/octet-stream");
            httpResponse.setCharacterEncoding("utf-8");
            outputStream = httpResponse.getOutputStream();
            while ((length = object.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
            outputStream.close();
        } catch (Exception ex) {
            log.info("导出失败：{}", ex.getMessage());
        }finally {
            if(object != null) {
                try {
                    object.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
