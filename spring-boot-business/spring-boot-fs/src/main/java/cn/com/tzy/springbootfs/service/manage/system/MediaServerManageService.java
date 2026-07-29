package cn.com.tzy.springbootfs.service.manage.system;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.fs.common.FsStringStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.MediaServerDetailVo;

import java.util.List;

public interface MediaServerManageService {

    PageResult page(MediaServerPageParam param);

    RestResult<MediaServerDetailVo> detail(String id);

    RestResult<String> save(MediaServerSaveParam param);

    RestResult<?> status(FsStringStatusParam param);

    RestResult<?> setDefault(String id);

    RestResult<?> remove(String id);

    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
