package cn.com.tzy.springbootfs.controller.api.fs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsStringStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.MediaServerDetailVo;
import cn.com.tzy.springbootfs.service.manage.system.MediaServerManageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ApiFsMediaServerManageController")
@RequestMapping("/api/fs/media_server")
public class MediaServerManageController extends ApiController {

    private final MediaServerManageService mediaServerManageService;

    public MediaServerManageController(MediaServerManageService mediaServerManageService) {
        this.mediaServerManageService = mediaServerManageService;
    }

    @PostMapping("page")
    public PageResult page(@Validated @RequestBody MediaServerPageParam param) {
        return mediaServerManageService.page(param);
    }

    @GetMapping("detail")
    public RestResult<MediaServerDetailVo> detail(@RequestParam String id) {
        return mediaServerManageService.detail(id);
    }

    @PostMapping("save")
    public RestResult<String> save(@Validated @RequestBody MediaServerSaveParam param) {
        return mediaServerManageService.save(param);
    }

    @PostMapping("status")
    public RestResult<?> status(@Validated @RequestBody FsStringStatusParam param) {
        return mediaServerManageService.status(param);
    }

    @PostMapping("default")
    public RestResult<?> setDefault(@RequestParam String id) {
        return mediaServerManageService.setDefault(id);
    }

    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam String id) {
        return mediaServerManageService.remove(id);
    }

    @GetMapping("select")
    public RestResult<List<FsOptionVo>> select(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "20") Integer limit) {
        return mediaServerManageService.select(keyword, limit);
    }
}
