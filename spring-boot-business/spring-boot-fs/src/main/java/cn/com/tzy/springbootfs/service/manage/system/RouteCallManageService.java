package cn.com.tzy.springbootfs.service.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallPageParam;
import cn.com.tzy.springbootentity.param.fs.system.RouteCallSaveParam;
import cn.com.tzy.springbootentity.vo.fs.system.RouteCallDetailVo;

public interface RouteCallManageService {

    PageResult page(RouteCallPageParam param);

    RestResult<RouteCallDetailVo> detail(Long id);

    RestResult<Long> save(RouteCallSaveParam param);

    RestResult<?> status(FsLongStatusParam param);

    RestResult<?> remove(Long id);
}
