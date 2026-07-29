package cn.com.tzy.springbootfs.service.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewayPageParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySaveParam;
import cn.com.tzy.springbootentity.param.fs.system.FsGatewaySelectedParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.FsGatewayDetailVo;

import java.util.List;

public interface FsGatewayManageService {

    PageResult page(FsGatewayPageParam param);

    RestResult<FsGatewayDetailVo> detail(Long id);

    RestResult<Long> save(FsGatewaySaveParam param);

    RestResult<?> selected(FsGatewaySelectedParam param);

    RestResult<?> remove(Long id);

    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
