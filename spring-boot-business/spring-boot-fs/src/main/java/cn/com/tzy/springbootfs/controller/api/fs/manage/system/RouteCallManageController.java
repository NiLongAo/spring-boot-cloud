package cn.com.tzy.springbootfs.controller.api.fs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallSaveParam;
import cn.com.tzy.springbootentity.vo.fs.system.RouteCallDetailVo;
import cn.com.tzy.springbootfs.service.manage.system.RouteCallManageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("ApiFsRouteCallManageController")
@RequestMapping("/api/fs/route_call")
public class RouteCallManageController extends ApiController {

    private final RouteCallManageService routeCallManageService;

    public RouteCallManageController(RouteCallManageService routeCallManageService) {
        this.routeCallManageService = routeCallManageService;
    }

    @PostMapping("page")
    public PageResult page(@Validated @RequestBody RouteCallPageParam param) {
        return routeCallManageService.page(param);
    }

    @GetMapping("detail")
    public RestResult<RouteCallDetailVo> detail(@RequestParam Long id) {
        return routeCallManageService.detail(id);
    }

    @PostMapping("save")
    public RestResult<Long> save(@Validated @RequestBody RouteCallSaveParam param) {
        return routeCallManageService.save(param);
    }

    @PostMapping("status")
    public RestResult<?> status(@Validated @RequestBody FsLongStatusParam param) {
        return routeCallManageService.status(param);
    }

    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam Long id) {
        return routeCallManageService.remove(id);
    }
}
