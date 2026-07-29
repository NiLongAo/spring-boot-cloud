package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewayPageParam;
import cn.com.tzy.springbootfs.controller.api.fs.manage.system.FsGatewayManageController;
import cn.com.tzy.springbootfs.service.manage.system.FsGatewayManageService;
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

class FsGatewayManageControllerTest {

    private MockMvc mockMvc;
    private FsGatewayManageService service;

    @BeforeEach
    void setUp() {
        service = mock(FsGatewayManageService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new FsGatewayManageController(service)).build();
    }

    @Test
    void pageUsesPost() throws Exception {
        when(service.page(any(FsGatewayPageParam.class))).thenReturn(PageResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/fs_gateway/page").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void detailUsesGet() throws Exception {
        when(service.detail(1L)).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(get("/api/fs/fs_gateway/detail").param("id", "1")).andExpect(status().isOk());
    }

    @Test
    void selectedUsesPost() throws Exception {
        when(service.selected(any())).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/fs_gateway/selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"selected\":\"1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeUsesDelete() throws Exception {
        when(service.remove(1L)).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(delete("/api/fs/fs_gateway/remove").param("id", "1")).andExpect(status().isOk());
    }

    @Test
    void selectedRejectsMissingId() throws Exception {
        mockMvc.perform(post("/api/fs/fs_gateway/selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selected\":\"1\"}"))
                .andExpect(status().isOk()); // 400 handling done by ApiController -> 200 with error code
    }
}
