package cn.com.tzy.springbootfs.service.manage.system.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGateway;
import cn.com.tzy.springbootentity.dome.fs.RouteGatewayGroup;
import cn.com.tzy.springbootentity.dome.fs.RouteGroup;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGroupDetailVo;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.mapper.fs.RouteGroupMapper;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayGroupService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayService;
import cn.com.tzy.springbootfs.service.fs.RouteGroupService;
import cn.com.tzy.springbootfs.service.manage.system.RouteGroupManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteGroupManageServiceImpl implements RouteGroupManageService {

    private final RouteGroupService routeGroupService;
    private final RouteGroupMapper routeGroupMapper;
    private final RouteGatewayGroupService routeGatewayGroupService;
    private final RouteGatewayService routeGatewayService;
    private final RouteCallService routeCallService;
    private final FsRuntimeConfigRefreshPublisher refreshPublisher;

    public RouteGroupManageServiceImpl(
            RouteGroupService routeGroupService,
            RouteGroupMapper routeGroupMapper,
            RouteGatewayGroupService routeGatewayGroupService,
            RouteGatewayService routeGatewayService,
            RouteCallService routeCallService,
            FsRuntimeConfigRefreshPublisher refreshPublisher) {
        this.routeGroupService = routeGroupService;
        this.routeGroupMapper = routeGroupMapper;
        this.routeGatewayGroupService = routeGatewayGroupService;
        this.routeGatewayService = routeGatewayService;
        this.routeCallService = routeCallService;
        this.refreshPublisher = refreshPublisher;
    }

    @Override
    public PageResult page(RouteGroupPageParam param) {
        Page<RouteGroup> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<RouteGroup> wrapper = new LambdaQueryWrapper<RouteGroup>()
                .like(StringUtils.isNotBlank(param.getQuery()), RouteGroup::getName, param.getQuery())
                .eq(param.getStatus() != null, RouteGroup::getStatus, param.getStatus());
        return MyBatisUtils.selectPage(routeGroupMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResult<RouteGroupDetailVo> detail(Long id) {
        RouteGroup rg = routeGroupService.getById(id);
        if (rg == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由组不存在");
        }
        // 查询已分配网关
        List<RouteGatewayGroup> assigned = routeGatewayGroupService.list(
                new LambdaQueryWrapper<RouteGatewayGroup>().eq(RouteGatewayGroup::getRouteGroupId, id));
        List<Long> gatewayIds = assigned.stream().map(RouteGatewayGroup::getGatewayId).collect(Collectors.toList());
        List<FsOptionVo> gateways = gatewayIds.isEmpty() ? java.util.Collections.emptyList() :
                routeGatewayService.listByIds(gatewayIds).stream()
                        .map(g -> FsOptionVo.builder()
                                .id(String.valueOf(g.getId()))
                                .name(g.getName())
                                .code(g.getMediaHost())
                                .status(g.getStatus())
                                .build())
                        .collect(Collectors.toList());
        RouteGroupDetailVo vo = RouteGroupDetailVo.builder()
                .id(rg.getId()).name(rg.getName()).status(rg.getStatus())
                .gateways(gateways)
                .build();
        return RestResult.result(RespCode.CODE_0.getValue(), null, vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Long> save(RouteGroupSaveParam param) {
        boolean isUpdate = param.getId() != null;
        if (isUpdate && routeGroupService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由组不存在");
        }
        RouteGroup entity = new RouteGroup();
        entity.setId(param.getId());
        entity.setName(param.getName());
        entity.setStatus(param.getStatus());
        if (isUpdate) {
            routeGroupService.updateById(entity);
        } else {
            routeGroupService.save(entity);
        }
        publishAffectedCompanies(entity.getId());
        return RestResult.result(RespCode.CODE_0.getValue(), null, entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> saveGateways(RouteGroupGatewaySaveParam param) {
        Long routeGroupId = param.getRouteGroupId();
        if (routeGroupService.getById(routeGroupId) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由组不存在");
        }

        // 校验所有网关存在且状态可用
        List<Long> gatewayIds = param.getGatewayIds().stream().distinct().collect(Collectors.toList());
        for (Long gatewayId : gatewayIds) {
            RouteGateway gw = routeGatewayService.getById(gatewayId);
            if (gw == null) {
                return RestResult.result(RespCode.CODE_2.getValue(), "路由网关 " + gatewayId + " 不存在");
            }
        }

        // 完整替换：先删除旧关系，再批量插入新关系
        routeGatewayGroupService.remove(
                new LambdaQueryWrapper<RouteGatewayGroup>().eq(RouteGatewayGroup::getRouteGroupId, routeGroupId));
        if (!gatewayIds.isEmpty()) {
            List<RouteGatewayGroup> newRelations = gatewayIds.stream().map(gid -> {
                RouteGatewayGroup rel = new RouteGatewayGroup();
                rel.setRouteGroupId(routeGroupId);
                rel.setGatewayId(gid);
                return rel;
            }).collect(Collectors.toList());
            routeGatewayGroupService.saveBatch(newRelations);
        }

        publishAffectedCompanies(routeGroupId);
        return RestResult.result(RespCode.CODE_0.getValue(), "网关关系已更新");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> status(FsLongStatusParam param) {
        if (routeGroupService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由组不存在");
        }
        RouteGroup update = new RouteGroup();
        update.setId(param.getId());
        update.setStatus(param.getStatus());
        routeGroupService.updateById(update);
        publishAffectedCompanies(param.getId());
        return RestResult.result(RespCode.CODE_0.getValue(), "更新成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        if (routeGroupService.getById(id) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由组不存在");
        }
        int refCount = routeCallService.count(
                new LambdaQueryWrapper<RouteCall>().eq(RouteCall::getRouteGroupId, id));
        if (refCount > 0) {
            return RestResult.result(RespCode.CODE_2.getValue(), "该路由组已被 " + refCount + " 条外呼路由引用，无法删除");
        }
        // 删除关联网关关系
        routeGatewayGroupService.remove(
                new LambdaQueryWrapper<RouteGatewayGroup>().eq(RouteGatewayGroup::getRouteGroupId, id));
        routeGroupService.removeById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<List<FsOptionVo>> select(String keyword, Integer limit) {
        int pageLimit = limit != null ? limit : 20;
        LambdaQueryWrapper<RouteGroup> wrapper = new LambdaQueryWrapper<RouteGroup>()
                .like(StringUtils.isNotBlank(keyword), RouteGroup::getName, keyword)
                .last("LIMIT " + pageLimit);
        List<RouteGroup> list = routeGroupService.list(wrapper);
        List<FsOptionVo> options = list.stream().map(rg -> FsOptionVo.builder()
                .id(String.valueOf(rg.getId()))
                .name(rg.getName())
                .status(rg.getStatus())
                .build()).collect(Collectors.toList());
        return RestResult.result(RespCode.CODE_0.getValue(), null, options);
    }

    private void publishAffectedCompanies(Long routeGroupId) {
        if (routeGroupId == null) {
            return;
        }
        List<Long> companyIds = routeCallService.list(
                new LambdaQueryWrapper<RouteCall>().eq(RouteCall::getRouteGroupId, routeGroupId))
                .stream().map(RouteCall::getCompanyId).distinct().collect(Collectors.toList());
        refreshPublisher.publishCompaniesChanged(companyIds);
    }
}
