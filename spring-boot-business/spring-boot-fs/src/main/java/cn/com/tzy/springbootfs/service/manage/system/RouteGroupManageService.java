package cn.com.tzy.springbootfs.service.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteGroupSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.RouteGroupDetailVo;

import java.util.List;

public interface RouteGroupManageService {

    PageResult page(RouteGroupPageParam param);

    RestResult<RouteGroupDetailVo> detail(Long id);

    RestResult<Long> save(RouteGroupSaveParam param);

    RestResult<?> saveGateways(RouteGroupGatewaySaveParam param);

    RestResult<?> status(FsLongStatusParam param);

    RestResult<?> remove(Long id);

    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
