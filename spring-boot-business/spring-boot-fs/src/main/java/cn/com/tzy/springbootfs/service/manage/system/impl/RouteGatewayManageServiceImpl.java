package cn.com.tzy.springbootfs.service.manage.system.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGateway;
import cn.com.tzy.springbootentity.dome.fs.RouteGatewayGroup;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewayPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewaySaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGatewayDetailVo;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.mapper.fs.RouteGatewayMapper;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayGroupService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayService;
import cn.com.tzy.springbootfs.service.manage.system.RouteGatewayManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteGatewayManageServiceImpl implements RouteGatewayManageService {

    private final RouteGatewayService routeGatewayService;
    private final RouteGatewayMapper routeGatewayMapper;
    private final RouteGatewayGroupService routeGatewayGroupService;
    private final RouteCallService routeCallService;
    private final FsRuntimeConfigRefreshPublisher refreshPublisher;

    public RouteGatewayManageServiceImpl(
            RouteGatewayService routeGatewayService,
            RouteGatewayMapper routeGatewayMapper,
            RouteGatewayGroupService routeGatewayGroupService,
            RouteCallService routeCallService,
            FsRuntimeConfigRefreshPublisher refreshPublisher) {
        this.routeGatewayService = routeGatewayService;
        this.routeGatewayMapper = routeGatewayMapper;
        this.routeGatewayGroupService = routeGatewayGroupService;
        this.routeCallService = routeCallService;
        this.refreshPublisher = refreshPublisher;
    }

    @Override
    public PageResult page(RouteGatewayPageParam param) {
        Page<RouteGateway> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<RouteGateway> wrapper = new LambdaQueryWrapper<RouteGateway>()
                .like(StringUtils.isNotBlank(param.getQuery()), RouteGateway::getName, param.getQuery())
                .eq(param.getStatus() != null, RouteGateway::getStatus, param.getStatus());
        return MyBatisUtils.selectPage(routeGatewayMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResult<RouteGatewayDetailVo> detail(Long id) {
        RouteGateway rg = routeGatewayService.getById(id);
        if (rg == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由网关不存在");
        }
        return RestResult.result(RespCode.CODE_0.getValue(), null, toDetailVo(rg));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Long> save(RouteGatewaySaveParam param) {
        boolean isUpdate = param.getId() != null;
        if (isUpdate && routeGatewayService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由网关不存在");
        }

        long nameCount = routeGatewayService.count(new LambdaQueryWrapper<RouteGateway>()
                .eq(RouteGateway::getName, param.getName())
                .ne(isUpdate, RouteGateway::getId, param.getId()));
        if (nameCount > 0) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由网关名称已存在");
        }

        RouteGateway entity = toEntity(param);
        if (isUpdate) {
            routeGatewayService.updateById(entity);
        } else {
            routeGatewayService.save(entity);
        }

        publishAffectedCompanies(entity.getId());
        return RestResult.result(RespCode.CODE_0.getValue(), null, entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> status(FsLongStatusParam param) {
        if (routeGatewayService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由网关不存在");
        }
        RouteGateway update = new RouteGateway();
        update.setId(param.getId());
        update.setStatus(param.getStatus());
        routeGatewayService.updateById(update);
        publishAffectedCompanies(param.getId());
        return RestResult.result(RespCode.CODE_0.getValue(), "更新成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        if (routeGatewayService.getById(id) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由网关不存在");
        }
        int refCount = routeGatewayGroupService.count(
                new LambdaQueryWrapper<RouteGatewayGroup>().eq(RouteGatewayGroup::getGatewayId, id));
        if (refCount > 0) {
            return RestResult.result(RespCode.CODE_2.getValue(), "该路由网关已被 " + refCount + " 个路由组引用，无法删除");
        }
        routeGatewayService.removeById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<List<FsOptionVo>> select(String keyword, Integer limit) {
        int pageLimit = limit != null ? limit : 20;
        LambdaQueryWrapper<RouteGateway> wrapper = new LambdaQueryWrapper<RouteGateway>()
                .like(StringUtils.isNotBlank(keyword), RouteGateway::getName, keyword)
                .last("LIMIT " + pageLimit);
        List<RouteGateway> list = routeGatewayService.list(wrapper);
        List<FsOptionVo> options = list.stream().map(rg -> FsOptionVo.builder()
                .id(String.valueOf(rg.getId()))
                .name(rg.getName())
                .code(rg.getMediaHost())
                .status(rg.getStatus())
                .build()).collect(Collectors.toList());
        return RestResult.result(RespCode.CODE_0.getValue(), null, options);
    }

    /** 通过 fs_route_gateway_group -> fs_route_call 查找受影响的企业 ID 并发布刷新 */
    private void publishAffectedCompanies(Long gatewayId) {
        if (gatewayId == null) {
            return;
        }
        List<RouteGatewayGroup> groups = routeGatewayGroupService.list(
                new LambdaQueryWrapper<RouteGatewayGroup>().eq(RouteGatewayGroup::getGatewayId, gatewayId));
        if (groups.isEmpty()) {
            return;
        }
        List<Long> groupIds = groups.stream().map(RouteGatewayGroup::getRouteGroupId).distinct().collect(Collectors.toList());
        List<Long> companyIds = routeCallService.list(
                new LambdaQueryWrapper<RouteCall>().in(RouteCall::getRouteGroupId, groupIds))
                .stream().map(RouteCall::getCompanyId).distinct().collect(Collectors.toList());
        refreshPublisher.publishCompaniesChanged(companyIds);
    }

    private RouteGatewayDetailVo toDetailVo(RouteGateway rg) {
        return RouteGatewayDetailVo.builder()
                .id(rg.getId()).name(rg.getName()).mediaHost(rg.getMediaHost())
                .mediaPort(rg.getMediaPort()).callerPrefix(rg.getCallerPrefix())
                .calledPrefix(rg.getCalledPrefix()).profile(rg.getProfile())
                .sipHeader1(rg.getSipHeader1()).sipHeader2(rg.getSipHeader2())
                .sipHeader3(rg.getSipHeader3()).status(rg.getStatus())
                .build();
    }

    private RouteGateway toEntity(RouteGatewaySaveParam p) {
        RouteGateway rg = new RouteGateway();
        rg.setId(p.getId());
        rg.setName(p.getName());
        rg.setMediaHost(p.getMediaHost());
        rg.setMediaPort(p.getMediaPort());
        rg.setCallerPrefix(p.getCallerPrefix());
        rg.setCalledPrefix(p.getCalledPrefix());
        rg.setProfile(p.getProfile());
        rg.setSipHeader1(p.getSipHeader1());
        rg.setSipHeader2(p.getSipHeader2());
        rg.setSipHeader3(p.getSipHeader3());
        rg.setStatus(p.getStatus());
        return rg;
    }
}
