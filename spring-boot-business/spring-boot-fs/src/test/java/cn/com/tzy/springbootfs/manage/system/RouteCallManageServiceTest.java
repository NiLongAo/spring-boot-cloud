package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGroup;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallSaveParam;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.mapper.fs.RouteCallMapper;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
import cn.com.tzy.springbootfs.service.fs.RouteGroupService;
import cn.com.tzy.springbootfs.service.manage.system.RouteCallManageService;
import cn.com.tzy.springbootfs.service.manage.system.impl.RouteCallManageServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RouteCallManageServiceTest {

    private RouteCallService routeCallService;
    private RouteGroupService routeGroupService;
    private FsRuntimeConfigRefreshPublisher refreshPublisher;
    private RouteCallManageService service;

    @BeforeEach
    void setUp() {
        routeCallService = mock(RouteCallService.class);
        RouteCallMapper routeCallMapper = mock(RouteCallMapper.class);
        routeGroupService = mock(RouteGroupService.class);
        refreshPublisher = mock(FsRuntimeConfigRefreshPublisher.class);
        service = new RouteCallManageServiceImpl(
                routeCallService, routeCallMapper, routeGroupService, refreshPublisher);
    }

    @Test
    void saveRejectsDuplicateCompanyRouteNumber() {
        when(routeGroupService.getById(1L)).thenReturn(new RouteGroup());
        when(routeCallService.count(any(LambdaQueryWrapper.class))).thenReturn(1);

        RestResult<Long> result = service.save(RouteCallSaveParam.builder()
                .companyId(100L).routeGroupId(1L).routeNum("010").numMin(0).numMax(9).status(1).build());

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeCallService, never()).save(any(RouteCall.class));
    }

    @Test
    void saveRejectsMinGreaterThanMax() {
        RestResult<Long> result = service.save(RouteCallSaveParam.builder()
                .companyId(100L).routeGroupId(1L).routeNum("010").numMin(10).numMax(5).status(1).build());

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeCallService, never()).save(any(RouteCall.class));
    }

    @Test
    void saveRejectsUnknownRouteGroup() {
        when(routeGroupService.getById(99L)).thenReturn(null);

        RestResult<Long> result = service.save(RouteCallSaveParam.builder()
                .companyId(100L).routeGroupId(99L).routeNum("010").numMin(0).numMax(9).status(1).build());

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeCallService, never()).save(any(RouteCall.class));
    }

    @Test
    void savePublishesCompanyRefresh() {
        when(routeGroupService.getById(1L)).thenReturn(new RouteGroup());
        when(routeCallService.count(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(routeCallService.save(any(RouteCall.class))).thenReturn(true);

        service.save(RouteCallSaveParam.builder()
                .companyId(100L).routeGroupId(1L).routeNum("010").numMin(0).numMax(9).status(1).build());

        verify(refreshPublisher).publishCompanyChanged(100L);
    }

    @Test
    void statusPublishesCompanyRefresh() {
        RouteCall rc = new RouteCall();
        rc.setId(1L);
        rc.setCompanyId(200L);
        when(routeCallService.getById(1L)).thenReturn(rc);
        when(routeCallService.updateById(any())).thenReturn(true);

        service.status(new FsLongStatusParam(1L, 0));

        verify(refreshPublisher).publishCompanyChanged(200L);
    }

    @Test
    void removePublishesCompanyRefresh() {
        RouteCall rc = new RouteCall();
        rc.setId(1L);
        rc.setCompanyId(300L);
        when(routeCallService.getById(1L)).thenReturn(rc);
        when(routeCallService.removeById(1L)).thenReturn(true);

        service.remove(1L);

        verify(refreshPublisher).publishCompanyChanged(300L);
    }
}
