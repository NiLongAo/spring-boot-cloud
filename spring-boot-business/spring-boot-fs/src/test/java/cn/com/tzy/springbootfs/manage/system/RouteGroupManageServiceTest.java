package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGateway;
import cn.com.tzy.springbootentity.dome.fs.RouteGatewayGroup;
import cn.com.tzy.springbootentity.dome.fs.RouteGroup;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupGatewaySaveParam;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGroupDetailVo;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.mapper.fs.RouteGroupMapper;
import cn.com.tzy.springbootfs.service.fs.RouteCallService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayGroupService;
import cn.com.tzy.springbootfs.service.fs.RouteGatewayService;
import cn.com.tzy.springbootfs.service.fs.RouteGroupService;
import cn.com.tzy.springbootfs.service.manage.system.RouteGroupManageService;
import cn.com.tzy.springbootfs.service.manage.system.impl.RouteGroupManageServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RouteGroupManageServiceTest {

    private RouteGroupService routeGroupService;
    private RouteGatewayGroupService routeGatewayGroupService;
    private RouteGatewayService routeGatewayService;
    private RouteCallService routeCallService;
    private FsRuntimeConfigRefreshPublisher refreshPublisher;
    private RouteGroupManageService service;

    @BeforeEach
    void setUp() {
        routeGroupService = mock(RouteGroupService.class);
        RouteGroupMapper routeGroupMapper = mock(RouteGroupMapper.class);
        routeGatewayGroupService = mock(RouteGatewayGroupService.class);
        routeGatewayService = mock(RouteGatewayService.class);
        routeCallService = mock(RouteCallService.class);
        refreshPublisher = mock(FsRuntimeConfigRefreshPublisher.class);
        service = new RouteGroupManageServiceImpl(
                routeGroupService, routeGroupMapper,
                routeGatewayGroupService, routeGatewayService,
                routeCallService, refreshPublisher);
    }

    @Test
    void saveGatewaysRejectsUnknownGateway() {
        when(routeGroupService.getById(1L)).thenReturn(new RouteGroup());
        when(routeGatewayService.getById(99L)).thenReturn(null);

        RestResult<?> result = service.saveGateways(
                new RouteGroupGatewaySaveParam(1L, Collections.singletonList(99L)));

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeGatewayGroupService, never()).saveBatch(any());
    }

    @Test
    void saveGatewaysReplacesAllRelationsInOneTransaction() {
        when(routeGroupService.getById(1L)).thenReturn(new RouteGroup());
        when(routeGatewayService.getById(10L)).thenReturn(new RouteGateway());
        when(routeGatewayService.getById(20L)).thenReturn(new RouteGateway());
        when(routeCallService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(routeGatewayGroupService.remove(any(LambdaQueryWrapper.class))).thenReturn(true);
        when(routeGatewayGroupService.saveBatch(any())).thenReturn(true);

        InOrder inOrder = inOrder(routeGatewayGroupService);
        service.saveGateways(new RouteGroupGatewaySaveParam(1L, Arrays.asList(10L, 20L)));

        inOrder.verify(routeGatewayGroupService).remove(any(LambdaQueryWrapper.class));
        inOrder.verify(routeGatewayGroupService).saveBatch(any());
    }

    @Test
    void removeRejectsRouteCallReference() {
        when(routeGroupService.getById(1L)).thenReturn(new RouteGroup());
        when(routeCallService.count(any(LambdaQueryWrapper.class))).thenReturn(3);

        RestResult<?> result = service.remove(1L);

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(routeGroupService, never()).removeById(any());
    }

    @Test
    void detailReturnsAssignedGatewayOptions() {
        RouteGroup rg = new RouteGroup();
        rg.setId(1L);
        rg.setName("rg1");
        when(routeGroupService.getById(1L)).thenReturn(rg);

        RouteGatewayGroup gg = new RouteGatewayGroup();
        gg.setGatewayId(10L);
        when(routeGatewayGroupService.list(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(gg));

        RouteGateway gw = new RouteGateway();
        gw.setId(10L);
        gw.setName("gw1");
        when(routeGatewayService.listByIds(any())).thenReturn(Collections.singletonList(gw));

        RestResult<RouteGroupDetailVo> result = service.detail(1L);

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
        assertFalse(result.getData().getGateways().isEmpty());
        assertEquals("10", result.getData().getGateways().get(0).getId());
    }

    @Test
    void routeGroupChangePublishesAffectedCompanies() {
        when(routeGroupService.getById(1L)).thenReturn(new RouteGroup());
        when(routeGroupService.updateById(any())).thenReturn(true);
        RouteCall rc = new RouteCall();
        rc.setCompanyId(200L);
        when(routeCallService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(rc));

        service.status(new cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam(1L, 1));

        verify(refreshPublisher).publishCompaniesChanged(argThat(ids -> ids.contains(200L)));
    }
}
