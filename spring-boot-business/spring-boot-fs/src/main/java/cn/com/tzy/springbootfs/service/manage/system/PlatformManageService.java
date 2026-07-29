package cn.com.tzy.springbootfs.service.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformPageParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.PlatformDetailVo;

import java.util.List;

public interface PlatformManageService {

    PageResult page(PlatformPageParam param);

    RestResult<PlatformDetailVo> detail(Long id);

    RestResult<Long> save(PlatformSaveParam param);

    RestResult<?> status(FsLongStatusParam param);

    RestResult<?> remove(Long id);

    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
