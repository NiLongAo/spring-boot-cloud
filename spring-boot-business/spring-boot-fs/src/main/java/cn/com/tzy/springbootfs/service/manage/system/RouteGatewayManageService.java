package cn.com.tzy.springbootfs.service.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewayPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGatewaySaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGatewayDetailVo;

import java.util.List;

public interface RouteGatewayManageService {

    PageResult page(RouteGatewayPageParam param);

    RestResult<RouteGatewayDetailVo> detail(Long id);

    RestResult<Long> save(RouteGatewaySaveParam param);

    RestResult<?> status(FsLongStatusParam param);

    RestResult<?> remove(Long id);

    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
