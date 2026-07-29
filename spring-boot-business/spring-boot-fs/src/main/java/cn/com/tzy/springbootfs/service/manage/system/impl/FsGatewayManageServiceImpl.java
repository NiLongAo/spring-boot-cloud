package cn.com.tzy.springbootfs.service.manage.system.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.GateWay;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewayPageParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySelectedParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.FsGatewayDetailVo;
import cn.com.tzy.springbootfs.mapper.fs.GateWayMapper;
import cn.com.tzy.springbootfs.service.fs.GateWayService;
import cn.com.tzy.springbootfs.service.manage.system.FsGatewayManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FsGatewayManageServiceImpl implements FsGatewayManageService {

    private final GateWayService gateWayService;
    private final GateWayMapper gateWayMapper;

    public FsGatewayManageServiceImpl(GateWayService gateWayService, GateWayMapper gateWayMapper) {
        this.gateWayService = gateWayService;
        this.gateWayMapper = gateWayMapper;
    }

    @Override
    public PageResult page(FsGatewayPageParam param) {
        Page<GateWay> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<GateWay> wrapper = new LambdaQueryWrapper<GateWay>()
                .like(StringUtils.isNotBlank(param.getQuery()), GateWay::getName, param.getQuery())
                .eq(StringUtils.isNotBlank(param.getSelected()), GateWay::getSelected, param.getSelected());
        return MyBatisUtils.selectPage(gateWayMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResult<FsGatewayDetailVo> detail(Long id) {
        GateWay gateWay = gateWayService.getById(id);
        if (gateWay == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "FS注册网关不存在");
        }
        FsGatewayDetailVo vo = toDetailVo(gateWay);
        // 密码永远不回显
        vo.setPassword(null);
        return RestResult.result(RespCode.CODE_0.getValue(), null, vo);
    }

    @Override
    public RestResult<Long> save(FsGatewaySaveParam param) {
        boolean isUpdate = param.getId() != null;
        GateWay existing = null;
        if (isUpdate) {
            existing = gateWayService.getById(param.getId());
            if (existing == null) {
                return RestResult.result(RespCode.CODE_2.getValue(), "FS注册网关不存在");
            }
        }

        GateWay entity = toEntity(param);
        if (isUpdate) {
            // 密码为空时保持原值
            if (StringUtils.isBlank(param.getPassword())) {
                entity.setPassword(existing.getPassword());
            }
            if (!gateWayService.updateById(entity)) {
                return RestResult.result(RespCode.CODE_2.getValue(), "更新失败");
            }
        } else {
            if (!gateWayService.save(entity)) {
                return RestResult.result(RespCode.CODE_2.getValue(), "保存失败");
            }
        }
        return RestResult.result(RespCode.CODE_0.getValue(), null, entity.getId());
    }

    @Override
    public RestResult<?> selected(FsGatewaySelectedParam param) {
        if (gateWayService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "FS注册网关不存在");
        }
        GateWay update = new GateWay();
        update.setId(param.getId());
        update.setSelected(param.getSelected());
        if (!gateWayService.updateById(update)) {
            return RestResult.result(RespCode.CODE_2.getValue(), "状态更新失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(), "更新成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        if (gateWayService.getById(id) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "FS注册网关不存在");
        }
        gateWayService.removeById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<List<FsOptionVo>> select(String keyword, Integer limit) {
        int pageLimit = limit != null ? limit : 20;
        LambdaQueryWrapper<GateWay> wrapper = new LambdaQueryWrapper<GateWay>()
                .like(StringUtils.isNotBlank(keyword), GateWay::getName, keyword)
                .last("LIMIT " + pageLimit);
        List<GateWay> list = gateWayService.list(wrapper);
        List<FsOptionVo> options = list.stream().map(g -> FsOptionVo.builder()
                .id(String.valueOf(g.getId()))
                .name(g.getName())
                .code(g.getRouteId())
                .build()).collect(Collectors.toList());
        return RestResult.result(RespCode.CODE_0.getValue(), null, options);
    }

    private FsGatewayDetailVo toDetailVo(GateWay g) {
        return FsGatewayDetailVo.builder()
                .id(g.getId()).name(g.getName()).routeId(g.getRouteId())
                .realm(g.getRealm()).register(g.getRegister()).transport(g.getTransport())
                .retrySeconds(g.getRetrySeconds()).username(g.getUsername())
                .password(g.getPassword()).selected(g.getSelected())
                .build();
    }

    private GateWay toEntity(FsGatewaySaveParam p) {
        GateWay g = new GateWay();
        g.setId(p.getId());
        g.setName(p.getName());
        g.setRouteId(p.getRouteId());
        g.setRealm(p.getRealm());
        g.setRegister(p.getRegister());
        g.setTransport(p.getTransport());
        g.setRetrySeconds(p.getRetrySeconds());
        g.setUsername(p.getUsername());
        g.setPassword(p.getPassword());
        g.setSelected(p.getSelected());
        return g;
    }
}
