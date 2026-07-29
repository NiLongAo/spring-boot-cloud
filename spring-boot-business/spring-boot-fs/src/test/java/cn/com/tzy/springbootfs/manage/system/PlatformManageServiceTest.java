package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformSaveParam;
import cn.com.tzy.springbootentity.vo.fs.system.PlatformDetailVo;
import cn.com.tzy.springbootfs.mapper.fs.PlatformMapper;
import cn.com.tzy.springbootfs.service.fs.PlatformService;
import cn.com.tzy.springbootfs.service.manage.system.PlatformManageService;
import cn.com.tzy.springbootfs.service.manage.system.impl.PlatformManageServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlatformManageServiceTest {

    private PlatformService platformService;
    private PlatformMapper platformMapper;
    private PlatformManageService service;

    @BeforeEach
    void setUp() {
        platformService = mock(PlatformService.class);
        platformMapper = mock(PlatformMapper.class);
        service = new PlatformManageServiceImpl(platformService, platformMapper);
    }

    @Test
    void saveRejectsReversedRtpRange() {
        PlatformSaveParam param = PlatformSaveParam.builder()
                .name("fs-main").localIp("10.0.0.10")
                .startRtpPort(17000).endRtpPort(16000)
                .build();

        RestResult<Long> result = service.save(param);

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        assertTrue(result.getMessage().contains("RTP"));
        verify(platformService, never()).save(any(Platform.class));
        verify(platformService, never()).updateById(any(Platform.class));
    }

    @Test
    void saveRejectsDuplicateName() {
        PlatformSaveParam param = PlatformSaveParam.builder()
                .name("duplicate").localIp("10.0.0.1")
                .startRtpPort(10000).endRtpPort(20000)
                .build();
        when(platformService.count(any(LambdaQueryWrapper.class))).thenReturn(1);

        RestResult<Long> result = service.save(param);

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(platformService, never()).save(any(Platform.class));
    }

    @Test
    void updatePreservesRuntimeStatus() {
        Platform existing = Platform.builder().id(1L).name("old-name").status(1).enable(1).build();
        when(platformService.getById(1L)).thenReturn(existing);
        when(platformService.count(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(platformService.updateById(any(Platform.class))).thenReturn(true);

        PlatformSaveParam param = PlatformSaveParam.builder()
                .id(1L).name("new-name").localIp("10.0.0.1")
                .startRtpPort(10000).endRtpPort(20000)
                .build();

        RestResult<Long> result = service.save(param);

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
        ArgumentCaptor<Platform> captor = ArgumentCaptor.forClass(Platform.class);
        verify(platformService).updateById(captor.capture());
        // status must NOT be passed through from the save param
        assertNull(captor.getValue().getStatus());
    }

    @Test
    void statusUpdatesEnableOnly() {
        when(platformService.getById(5L)).thenReturn(Platform.builder().id(5L).build());
        when(platformService.updateById(any(Platform.class))).thenReturn(true);

        RestResult<?> result = service.status(new FsLongStatusParam(5L, 0));

        assertEquals(RespCode.CODE_0.getValue(), result.getCode());
        ArgumentCaptor<Platform> captor = ArgumentCaptor.forClass(Platform.class);
        verify(platformService).updateById(captor.capture());
        assertEquals(Integer.valueOf(0), captor.getValue().getEnable());
        // status and name must not be touched
        assertNull(captor.getValue().getStatus());
        assertNull(captor.getValue().getName());
    }

    @Test
    void removeRejectsEnabledPlatform() {
        when(platformService.getById(3L)).thenReturn(Platform.builder().id(3L).enable(1).build());

        RestResult<?> result = service.remove(3L);

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
        verify(platformService, never()).removeById(any());
    }

    @Test
    void detailReturnsNotFoundForUnknownId() {
        when(platformService.getById(99L)).thenReturn(null);

        RestResult<PlatformDetailVo> result = service.detail(99L);

        assertEquals(RespCode.CODE_2.getValue(), result.getCode());
    }
}
