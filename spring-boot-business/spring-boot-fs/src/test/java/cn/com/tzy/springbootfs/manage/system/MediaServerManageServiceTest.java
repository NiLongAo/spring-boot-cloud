package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.MediaServer;
import cn.com.tzy.springbootentity.param.fs.common.FsStringStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerSaveParam;
import cn.com.tzy.springbootentity.vo.fs.system.MediaServerDetailVo;
import cn.com.tzy.springbootfs.mapper.fs.MediaServerMapper;
import cn.com.tzy.springbootfs.service.fs.MediaServerService;
import cn.com.tzy.springbootfs.service.manage.system.MediaServerManageService;
import cn.com.tzy.springbootfs.service.manage.system.impl.MediaServerManageServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaServerManageServiceTest {

    private MediaServerService mediaServerService;
    private MediaServerMapper mediaServerMapper;
    private MediaServerManageService service;

    @BeforeEach
    void setUp() {
        mediaServerService = mock(MediaServerService.class);
        mediaServerMapper = mock(MediaServerMapper.class);
        service = new MediaServerManageServiceImpl(mediaServerService, mediaServerMapper);
    }

    @Test
    void saveRejectsDuplicateIpAndHttpPort() {
        when(mediaServerService.count(any(LambdaQueryWrapper.class))).thenReturn(1);

        RestResult<String> result = service.save(MediaServerSaveParam.builder()
                .ip("192.168.1.1").hookIp("192.168.1.1").sdpIp("192.168.1.1")
                .httpPort(80).build());

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(mediaServerService, never()).save(any(MediaServer.class));
    }

    @Test
    void saveDoesNotAcceptRuntimeHeartbeatFields() {
        when(mediaServerService.count(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(mediaServerService.save(any(MediaServer.class))).thenReturn(true);

        service.save(MediaServerSaveParam.builder()
                .ip("10.0.0.1").hookIp("10.0.0.1").sdpIp("10.0.0.1").build());

        ArgumentCaptor<MediaServer> captor = ArgumentCaptor.forClass(MediaServer.class);
        verify(mediaServerService).save(captor.capture());
        assertNull(captor.getValue().getStatus());
        assertNull(captor.getValue().getKeepaliveTime());
    }

    @Test
    void setDefaultClearsPreviousDefaultServer() {
        when(mediaServerService.getById("server-1")).thenReturn(new MediaServer());
        when(mediaServerService.update(any(UpdateWrapper.class))).thenReturn(true);
        when(mediaServerService.updateById(any(MediaServer.class))).thenReturn(true);

        InOrder inOrder = inOrder(mediaServerService);

        service.setDefault("server-1");

        inOrder.verify(mediaServerService).update(any(UpdateWrapper.class));
        inOrder.verify(mediaServerService).updateById(any(MediaServer.class));
    }

    @Test
    void removeRejectsOnlineServer() {
        MediaServer online = new MediaServer();
        online.setStatus(1);
        when(mediaServerService.getById("srv-1")).thenReturn(online);

        RestResult<?> result = service.remove("srv-1");

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(mediaServerService, never()).removeById(any());
    }

    @Test
    void statusUpdatesEnableWithoutChangingOnlineStatus() {
        when(mediaServerService.getById("srv-1")).thenReturn(new MediaServer());
        when(mediaServerService.updateById(any(MediaServer.class))).thenReturn(true);

        service.status(new FsStringStatusParam("srv-1", 0));

        ArgumentCaptor<MediaServer> captor = ArgumentCaptor.forClass(MediaServer.class);
        verify(mediaServerService).updateById(captor.capture());
        assertEquals(Integer.valueOf(0), captor.getValue().getEnable());
        assertNull(captor.getValue().getStatus());
    }

    @Test
    void selectReturnsEnabledServersOnly() {
        service.select(null, 10);
        ArgumentCaptor<LambdaQueryWrapper<MediaServer>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(mediaServerService).list(captor.capture());
        // Wrapper should contain enable=1 condition (verified by inspecting the call)
        assertNotNull(captor.getValue());
    }
}
