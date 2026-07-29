package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallPageParam;
import cn.com.tzy.springbootfs.controller.api.fs.manage.system.RouteCallManageController;
import cn.com.tzy.springbootfs.service.manage.system.RouteCallManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RouteCallManageControllerTest {

    private MockMvc mockMvc;
    private RouteCallManageService service;

    @BeforeEach
    void setUp() {
        service = mock(RouteCallManageService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RouteCallManageController(service)).build();
    }

    @Test
    void pageUsesPost() throws Exception {
        when(service.page(any(RouteCallPageParam.class))).thenReturn(PageResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/route_call/page").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void detailUsesGet() throws Exception {
        when(service.detail(1L)).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(get("/api/fs/route_call/detail").param("id", "1")).andExpect(status().isOk());
    }

    @Test
    void statusUsesPost() throws Exception {
        when(service.status(any())).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/route_call/status")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"id\":1,\"status\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeUsesDelete() throws Exception {
        when(service.remove(1L)).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(delete("/api/fs/route_call/remove").param("id", "1")).andExpect(status().isOk());
    }

    @Test
    void saveMissingRequiredFieldsReturnsOk() throws Exception {
        mockMvc.perform(post("/api/fs/route_call/save")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }
}
