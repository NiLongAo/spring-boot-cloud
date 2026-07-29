package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.system.PlatformPageParam;
import cn.com.tzy.springbootfs.controller.api.fs.manage.system.PlatformManageController;
import cn.com.tzy.springbootfs.service.manage.system.PlatformManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PlatformManageControllerTest {

    private MockMvc mockMvc;
    private PlatformManageService platformManageService;

    @BeforeEach
    void setUp() {
        platformManageService = mock(PlatformManageService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PlatformManageController(platformManageService))
                .build();
    }

    @Test
    void pageUsesPostJsonContract() throws Exception {
        when(platformManageService.page(any(PlatformPageParam.class)))
                .thenReturn(PageResult.result(RespCode.CODE_0.getValue(), "success"));

        mockMvc.perform(post("/api/fs/platform/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pageNumber\":1,\"pageSize\":10}"))
                .andExpect(status().isOk());
    }

    @Test
    void detailUsesGetWithIdParam() throws Exception {
        when(platformManageService.detail(1L))
                .thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));

        mockMvc.perform(get("/api/fs/platform/detail").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void saveAcceptsValidJson() throws Exception {
        when(platformManageService.save(any()))
                .thenReturn(RestResult.result(RespCode.CODE_0.getValue(), null, 1L));

        mockMvc.perform(post("/api/fs/platform/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"fs-main\",\"localIp\":\"10.0.0.1\",\"startRtpPort\":10000,\"endRtpPort\":20000}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeUsesDeleteMethod() throws Exception {
        when(platformManageService.remove(1L))
                .thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "删除成功"));

        mockMvc.perform(delete("/api/fs/platform/remove").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void selectUsesGetMethod() throws Exception {
        when(platformManageService.select(any(), any()))
                .thenReturn(RestResult.result(RespCode.CODE_0.getValue(), null, Collections.emptyList()));

        mockMvc.perform(get("/api/fs/platform/select"))
                .andExpect(status().isOk());
    }
}
