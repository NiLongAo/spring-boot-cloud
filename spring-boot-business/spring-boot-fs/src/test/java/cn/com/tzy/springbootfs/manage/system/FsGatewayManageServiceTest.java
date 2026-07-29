package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.GateWay;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySelectedParam;
import cn.com.tzy.springbootentity.vo.fs.system.FsGatewayDetailVo;
import cn.com.tzy.springbootfs.mapper.fs.GateWayMapper;
import cn.com.tzy.springbootfs.service.fs.GateWayService;
import cn.com.tzy.springbootfs.service.manage.system.FsGatewayManageService;
import cn.com.tzy.springbootfs.service.manage.system.impl.FsGatewayManageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FsGatewayManageServiceTest {

    private GateWayService gateWayService;
    private FsGatewayManageService service;

    @BeforeEach
    void setUp() {
        gateWayService = mock(GateWayService.class);
        GateWayMapper gateWayMapper = mock(GateWayMapper.class);
        service = new FsGatewayManageServiceImpl(gateWayService, gateWayMapper);
    }

    @Test
    void fsGatewaySelectedUsesExistingSelectedField() {
        when(gateWayService.getById(1L)).thenReturn(new GateWay());
        when(gateWayService.updateById(any(GateWay.class))).thenReturn(true);

        RestResult<?> result = service.selected(new FsGatewaySelectedParam(1L, "1"));

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
        ArgumentCaptor<GateWay> captor = ArgumentCaptor.forClass(GateWay.class);
        verify(gateWayService).updateById(captor.capture());
        assertEquals("1", captor.getValue().getSelected());
    }

    @Test
    void fsGatewayPasswordIsNotReturned() {
        GateWay gw = new GateWay();
        gw.setId(1L);
        gw.setPassword("secret");
        when(gateWayService.getById(1L)).thenReturn(gw);

        RestResult<FsGatewayDetailVo> result = service.detail(1L);

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
        assertNull(result.getData().getPassword());
    }

    @Test
    void savePreservesPasswordWhenBlank() {
        GateWay existing = new GateWay();
        existing.setId(2L);
        existing.setPassword("original-password");
        when(gateWayService.getById(2L)).thenReturn(existing);
        when(gateWayService.updateById(any(GateWay.class))).thenReturn(true);

        FsGatewaySaveParam param = FsGatewaySaveParam.builder()
                .id(2L).name("gw").password("").build();
        service.save(param);

        ArgumentCaptor<GateWay> captor = ArgumentCaptor.forClass(GateWay.class);
        verify(gateWayService).updateById(captor.capture());
        assertEquals("original-password", captor.getValue().getPassword());
    }

    @Test
    void selectedRejectsNonexistentGateway() {
        when(gateWayService.getById(99L)).thenReturn(null);

        RestResult<?> result = service.selected(new FsGatewaySelectedParam(99L, "1"));

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(gateWayService, never()).updateById(any());
    }

    @Test
    void removeDeletesExistingGateway() {
        when(gateWayService.getById(3L)).thenReturn(new GateWay());
        when(gateWayService.removeById(3L)).thenReturn(true);

        RestResult<?> result = service.remove(3L);

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
        verify(gateWayService).removeById(3L);
    }
}
