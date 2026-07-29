package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupPageParam;
import cn.com.tzy.springbootfs.controller.api.fs.manage.system.RouteGroupManageController;
import cn.com.tzy.springbootfs.service.manage.system.RouteGroupManageService;
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

class RouteGroupManageControllerTest {

    private MockMvc mockMvc;
    private RouteGroupManageService service;

    @BeforeEach
    void setUp() {
        service = mock(RouteGroupManageService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RouteGroupManageController(service)).build();
    }

    @Test
    void pageUsesPost() throws Exception {
        when(service.page(any(RouteGroupPageParam.class))).thenReturn(PageResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/route_group/page").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void detailUsesGet() throws Exception {
        when(service.detail(1L)).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(get("/api/fs/route_group/detail").param("id", "1")).andExpect(status().isOk());
    }

    @Test
    void saveGatewaysUsesPost() throws Exception {
        when(service.saveGateways(any())).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(post("/api/fs/route_group/save_gateways")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routeGroupId\":1,\"gatewayIds\":[10,20]}"))
                .andExpect(status().isOk());
    }

    @Test
    void saveGatewaysMissingRouteGroupIdReturnsOk() throws Exception {
        // @Validated on param - missing routeGroupId triggers validation; ApiController returns HTTP 200
        mockMvc.perform(post("/api/fs/route_group/save_gateways")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gatewayIds\":[10]}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeUsesDelete() throws Exception {
        when(service.remove(1L)).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), "ok"));
        mockMvc.perform(delete("/api/fs/route_group/remove").param("id", "1")).andExpect(status().isOk());
    }

    @Test
    void selectUsesGet() throws Exception {
        when(service.select(any(), any())).thenReturn(RestResult.result(RespCode.CODE_0.getValue(), null, Collections.emptyList()));
        mockMvc.perform(get("/api/fs/route_group/select")).andExpect(status().isOk());
    }
}
