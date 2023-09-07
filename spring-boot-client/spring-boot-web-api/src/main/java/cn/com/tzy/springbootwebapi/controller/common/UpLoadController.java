package cn.com.tzy.springbootwebapi.controller.common;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.common.UpLoadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件操作相关接口")
@Log4j2
@RestController("WebApiCommonUpLoadController")
@RequestMapping("/webapi/common/up_load")
public class UpLoadController extends ApiController {

    @Autowired
    private UpLoadService upLoadService;

    @ApiOperation(value = "上传文件", notes = "上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="上传类型", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="file", value="上传类型", required=true, paramType="query", dataType="__file", allowMultiple = true)
    })
    @PostMapping("upload")
    @ResponseBody
    public RestResult<?> upload(@RequestParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        return upLoadService.upload(type,file);
    }
}
