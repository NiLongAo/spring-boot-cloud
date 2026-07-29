package cn.com.tzy.springbootfs.controller.api.fs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewayPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewaySaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGatewayDetailVo;
import cn.com.tzy.springbootfs.service.manage.system.RouteGatewayManageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ApiFsRouteGatewayManageController")
@RequestMapping("/api/fs/route_gateway")
public class RouteGatewayManageController extends ApiController {

    private final RouteGatewayManageService routeGatewayManageService;

    public RouteGatewayManageController(RouteGatewayManageService routeGatewayManageService) {
        this.routeGatewayManageService = routeGatewayManageService;
    }

    @PostMapping("page")
    public PageResult page(@Validated @RequestBody RouteGatewayPageParam param) {
        return routeGatewayManageService.page(param);
    }

    @GetMapping("detail")
    public RestResult<RouteGatewayDetailVo> detail(@RequestParam Long id) {
        return routeGatewayManageService.detail(id);
    }

    @PostMapping("save")
    public RestResult<Long> save(@Validated @RequestBody RouteGatewaySaveParam param) {
        return routeGatewayManageService.save(param);
    }

    @PostMapping("status")
    public RestResult<?> status(@Validated @RequestBody FsLongStatusParam param) {
        return routeGatewayManageService.status(param);
    }

    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam Long id) {
        return routeGatewayManageService.remove(id);
    }

    @GetMapping("select")
    public RestResult<List<FsOptionVo>> select(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "20") Integer limit) {
        return routeGatewayManageService.select(keyword, limit);
    }
}
