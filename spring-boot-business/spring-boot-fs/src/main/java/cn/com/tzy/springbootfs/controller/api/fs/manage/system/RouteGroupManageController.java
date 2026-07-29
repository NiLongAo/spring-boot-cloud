package cn.com.tzy.springbootfs.controller.api.fs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGroupDetailVo;
import cn.com.tzy.springbootfs.service.manage.system.RouteGroupManageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ApiFsRouteGroupManageController")
@RequestMapping("/api/fs/route_group")
public class RouteGroupManageController extends ApiController {

    private final RouteGroupManageService routeGroupManageService;

    public RouteGroupManageController(RouteGroupManageService routeGroupManageService) {
        this.routeGroupManageService = routeGroupManageService;
    }

    @PostMapping("page")
    public PageResult page(@Validated @RequestBody RouteGroupPageParam param) {
        return routeGroupManageService.page(param);
    }

    @GetMapping("detail")
    public RestResult<RouteGroupDetailVo> detail(@RequestParam Long id) {
        return routeGroupManageService.detail(id);
    }

    @PostMapping("save")
    public RestResult<Long> save(@Validated @RequestBody RouteGroupSaveParam param) {
        return routeGroupManageService.save(param);
    }

    @PostMapping("save_gateways")
    public RestResult<?> saveGateways(@Validated @RequestBody RouteGroupGatewaySaveParam param) {
        return routeGroupManageService.saveGateways(param);
    }

    @PostMapping("status")
    public RestResult<?> status(@Validated @RequestBody FsLongStatusParam param) {
        return routeGroupManageService.status(param);
    }

    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam Long id) {
        return routeGroupManageService.remove(id);
    }

    @GetMapping("select")
    public RestResult<List<FsOptionVo>> select(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "20") Integer limit) {
        return routeGroupManageService.select(keyword, limit);
    }
}
