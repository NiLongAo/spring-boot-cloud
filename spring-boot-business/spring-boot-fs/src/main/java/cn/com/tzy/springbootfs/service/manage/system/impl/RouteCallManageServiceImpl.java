package cn.com.tzy.springbootfs.service.manage.system.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGroup;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallSaveParam;
import cn.com.tzy.springbootentity.vo.fs.system.RouteCallDetailVo;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.mapper.fs.RouteCallMapper;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
import cn.com.tzy.springbootfs.service.fs.RouteGroupService;
import cn.com.tzy.springbootfs.service.manage.system.RouteCallManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteCallManageServiceImpl implements RouteCallManageService {

    private final RouteCallService routeCallService;
    private final RouteCallMapper routeCallMapper;
    private final RouteGroupService routeGroupService;
    private final FsRuntimeConfigRefreshPublisher refreshPublisher;

    public RouteCallManageServiceImpl(
            RouteCallService routeCallService,
            RouteCallMapper routeCallMapper,
            RouteGroupService routeGroupService,
            FsRuntimeConfigRefreshPublisher refreshPublisher) {
        this.routeCallService = routeCallService;
        this.routeCallMapper = routeCallMapper;
        this.routeGroupService = routeGroupService;
        this.refreshPublisher = refreshPublisher;
    }

    @Override
    public PageResult page(RouteCallPageParam param) {
        Page<RouteCall> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<RouteCall> wrapper = new LambdaQueryWrapper<RouteCall>()
                .eq(param.getCompanyId() != null, RouteCall::getCompanyId, param.getCompanyId())
                .eq(param.getRouteGroupId() != null, RouteCall::getRouteGroupId, param.getRouteGroupId())
                .eq(param.getStatus() != null, RouteCall::getStatus, param.getStatus());
        return MyBatisUtils.selectPage(routeCallMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResult<RouteCallDetailVo> detail(Long id) {
        RouteCall rc = routeCallService.getById(id);
        if (rc == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "外呼路由不存在");
        }
        return RestResult.result(RespCode.CODE_0.getValue(), null, toDetailVo(rc));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<Long> save(RouteCallSaveParam param) {
        // numMin 不能大于 numMax
        if (param.getNumMin() > param.getNumMax()) {
            return RestResult.result(RespCode.CODE_2.getValue(), "号码最小值不能大于最大值");
        }

        boolean isUpdate = param.getId() != null;
        if (isUpdate && routeCallService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "外呼路由不存在");
        }

        // 校验路由组存在
        if (routeGroupService.getById(param.getRouteGroupId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "路由组不存在");
        }

        // 同一企业 routeNum 唯一
        long routeNumCount = routeCallService.count(new LambdaQueryWrapper<RouteCall>()
                .eq(RouteCall::getCompanyId, param.getCompanyId())
                .eq(RouteCall::getRouteNum, param.getRouteNum())
                .ne(isUpdate, RouteCall::getId, param.getId()));
        if (routeNumCount > 0) {
            return RestResult.result(RespCode.CODE_2.getValue(), "该企业下路由号码已存在");
        }

        RouteCall entity = toEntity(param);
        if (isUpdate) {
            routeCallService.updateById(entity);
        } else {
            routeCallService.save(entity);
        }
        refreshPublisher.publishCompanyChanged(entity.getCompanyId());
        return RestResult.result(RespCode.CODE_0.getValue(), null, entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> status(FsLongStatusParam param) {
        RouteCall rc = routeCallService.getById(param.getId());
        if (rc == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "外呼路由不存在");
        }
        RouteCall update = new RouteCall();
        update.setId(param.getId());
        update.setStatus(param.getStatus());
        routeCallService.updateById(update);
        refreshPublisher.publishCompanyChanged(rc.getCompanyId());
        return RestResult.result(RespCode.CODE_0.getValue(), "更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> remove(Long id) {
        RouteCall rc = routeCallService.getById(id);
        if (rc == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "外呼路由不存在");
        }
        routeCallService.removeById(id);
        refreshPublisher.publishCompanyChanged(rc.getCompanyId());
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    private RouteCallDetailVo toDetailVo(RouteCall rc) {
        return RouteCallDetailVo.builder()
                .id(rc.getId()).companyId(rc.getCompanyId()).routeGroupId(rc.getRouteGroupId())
                .routeNum(rc.getRouteNum()).numMax(rc.getNumMax()).numMin(rc.getNumMin())
                .callerChange(rc.getCallerChange()).callerChangeNum(rc.getCallerChangeNum())
                .calledChange(rc.getCalledChange()).calledChangeNum(rc.getCalledChangeNum())
                .status(rc.getStatus())
                .build();
    }

    private RouteCall toEntity(RouteCallSaveParam p) {
        RouteCall rc = new RouteCall();
        rc.setId(p.getId());
        rc.setCompanyId(p.getCompanyId());
        rc.setRouteGroupId(p.getRouteGroupId());
        rc.setRouteNum(p.getRouteNum());
        rc.setNumMax(p.getNumMax());
        rc.setNumMin(p.getNumMin());
        rc.setCallerChange(p.getCallerChange());
        rc.setCallerChangeNum(p.getCallerChangeNum());
        rc.setCalledChange(p.getCalledChange());
        rc.setCalledChangeNum(p.getCalledChangeNum());
        rc.setStatus(p.getStatus());
        return rc;
    }
}
