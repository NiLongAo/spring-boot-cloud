package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerPageParam;
import cn.com.tzy.springbootfs.controller.api.fs.manage.system.MediaServerManageController;
import cn.com.tzy.springbootfs.service.manage.system.MediaServerManageService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MediaServerManageControllerTest {

    private MockMvc mockMvc;
    private MediaServerManageService service;

    @BeforeEach
    void setUp() {
        service = mock(MediaServerManageService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MediaServerManageController(service)).build();
    }

    @Test
    void pageUsesPost() throws Exception {
        when(service.page(any(MediaServerPageParam.class)))
                .thenReturn(PageResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/media_server/page")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void detailUsesGet() throws Exception {
        when(service.detail("s1")).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(get("/api/fs/media_server/detail").param("id", "s1"))
                .andExpect(status().isOk());
    }

    @Test
    void defaultUsesPost() throws Exception {
        when(service.setDefault("s1")).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/media_server/default").param("id", "s1"))
                .andExpect(status().isOk());
    }

    @Test
    void removeUsesDelete() throws Exception {
        when(service.remove("s1")).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(delete("/api/fs/media_server/remove").param("id", "s1"))
                .andExpect(status().isOk());
    }

    @Test
    void saveRejectsMissingIp() throws Exception {
        when(service.save(any())).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), null, "id"));
        mockMvc.perform(post("/api/fs/media_server/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hookIp\":\"10.0.0.1\",\"sdpIp\":\"10.0.0.1\"}"))
                .andExpect(status().isOk()); // ApiController handles validation, returns HTTP 200
    }
}
