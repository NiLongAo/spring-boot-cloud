package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGateway;
import cn.com.tzy.springbootentity.dome.fs.RouteGatewayGroup;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewaySaveParam;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.mapper.fs.RouteGatewayMapper;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayGroupService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayService;
import cn.com.tzy.springbootfs.service.manage.system.RouteGatewayManageService;
import cn.com.tzy.springbootfs.service.manage.system.impl.RouteGatewayManageServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RouteGatewayManageServiceTest {

    private RouteGatewayService routeGatewayService;
    private RouteGatewayGroupService routeGatewayGroupService;
    private RouteCallService routeCallService;
    private FsRuntimeConfigRefreshPublisher refreshPublisher;
    private RouteGatewayManageService service;

    @BeforeEach
    void setUp() {
        routeGatewayService = mock(RouteGatewayService.class);
        RouteGatewayMapper routeGatewayMapper = mock(RouteGatewayMapper.class);
        routeGatewayGroupService = mock(RouteGatewayGroupService.class);
        routeCallService = mock(RouteCallService.class);
        refreshPublisher = mock(FsRuntimeConfigRefreshPublisher.class);
        service = new RouteGatewayManageServiceImpl(
                routeGatewayService, routeGatewayMapper,
                routeGatewayGroupService, routeCallService, refreshPublisher);
    }

    @Test
    void routeGatewayRejectsDuplicateName() {
        when(routeGatewayService.count(any(LambdaQueryWrapper.class))).thenReturn(1);

        RestResult<Long> result = service.save(RouteGatewaySaveParam.builder().name("dup").build());

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeGatewayService, never()).save(any(RouteGateway.class));
    }

    @Test
    void routeGatewayRemoveRejectsRouteGroupReference() {
        when(routeGatewayService.getById(1L)).thenReturn(new RouteGateway());
        when(routeGatewayGroupService.count(any(LambdaQueryWrapper.class))).thenReturn(2);

        RestResult<?> result = service.remove(1L);

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeGatewayService, never()).removeById(any());
    }

    @Test
    void routeGatewayStatusOnlyAcceptsZeroOrOne() {
        when(routeGatewayService.getById(1L)).thenReturn(new RouteGateway());
        when(routeGatewayService.updateById(any())).thenReturn(true);
        when(routeGatewayGroupService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        RestResult<?> result = service.status(new FsLongStatusParam(1L, 1));

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
    }

    @Test
    void routeGatewayChangePublishesAffectedCompanies() {
        when(routeGatewayService.getById(5L)).thenReturn(new RouteGateway());
        when(routeGatewayService.updateById(any())).thenReturn(true);
        RouteGatewayGroup gg = new RouteGatewayGroup();
        gg.setRouteGroupId(10L);
        when(routeGatewayGroupService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(gg));
        RouteCall rc = new RouteCall();
        rc.setCompanyId(100L);
        when(routeCallService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(rc));

        service.status(new FsLongStatusParam(5L, 0));

        verify(refreshPublisher).publishCompaniesChanged(argThat(ids -> ids.contains(100L)));
    }
}
