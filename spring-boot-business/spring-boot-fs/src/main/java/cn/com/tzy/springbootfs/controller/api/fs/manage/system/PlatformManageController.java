package cn.com.tzy.springbootfs.controller.api.fs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformPageParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.PlatformDetailVo;
import cn.com.tzy.springbootfs.service.manage.system.PlatformManageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ApiFsPlatformManageController")
@RequestMapping("/api/fs/platform")
public class PlatformManageController extends ApiController {

    private final PlatformManageService platformManageService;

    public PlatformManageController(PlatformManageService platformManageService) {
        this.platformManageService = platformManageService;
    }

    @PostMapping("page")
    public PageResult page(@Validated @RequestBody PlatformPageParam param) {
        return platformManageService.page(param);
    }

    @GetMapping("detail")
    public RestResult<PlatformDetailVo> detail(@RequestParam Long id) {
        return platformManageService.detail(id);
    }

    @PostMapping("save")
    public RestResult<Long> save(@Validated @RequestBody PlatformSaveParam param) {
        return platformManageService.save(param);
    }

    @PostMapping("status")
    public RestResult<?> status(@Validated @RequestBody FsLongStatusParam param) {
        return platformManageService.status(param);
    }

    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam Long id) {
        return platformManageService.remove(id);
    }

    @GetMapping("select")
    public RestResult<List<FsOptionVo>> select(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "20") Integer limit) {
        return platformManageService.select(keyword, limit);
    }
}
