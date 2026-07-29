package cn.com.tzy.springbootfs.controller.api.fs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewayPageParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySelectedParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.FsGatewayDetailVo;
import cn.com.tzy.springbootfs.service.manage.system.FsGatewayManageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ApiFsFsGatewayManageController")
@RequestMapping("/api/fs/fs_gateway")
public class FsGatewayManageController extends ApiController {

    private final FsGatewayManageService fsGatewayManageService;

    public FsGatewayManageController(FsGatewayManageService fsGatewayManageService) {
        this.fsGatewayManageService = fsGatewayManageService;
    }

    @PostMapping("page")
    public PageResult page(@Validated @RequestBody FsGatewayPageParam param) {
        return fsGatewayManageService.page(param);
    }

    @GetMapping("detail")
    public RestResult<FsGatewayDetailVo> detail(@RequestParam Long id) {
        return fsGatewayManageService.detail(id);
    }

    @PostMapping("save")
    public RestResult<Long> save(@RequestBody FsGatewaySaveParam param) {
        return fsGatewayManageService.save(param);
    }

    @PostMapping("selected")
    public RestResult<?> selected(@Validated @RequestBody FsGatewaySelectedParam param) {
        return fsGatewayManageService.selected(param);
    }

    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam Long id) {
        return fsGatewayManageService.remove(id);
    }

    @GetMapping("select")
    public RestResult<List<FsOptionVo>> select(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "20") Integer limit) {
        return fsGatewayManageService.select(keyword, limit);
    }
}
