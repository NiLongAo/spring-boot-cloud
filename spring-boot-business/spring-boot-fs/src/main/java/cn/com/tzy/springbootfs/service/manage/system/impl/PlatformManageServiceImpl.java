package cn.com.tzy.springbootfs.service.manage.system.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformPageParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.PlatformDetailVo;
import cn.com.tzy.springbootfs.convert.manage.system.PlatformManageConvert;
import cn.com.tzy.springbootfs.mapper.fs.PlatformMapper;
import cn.com.tzy.springbootfs.service.fs.PlatformService;
import cn.com.tzy.springbootfs.service.manage.system.PlatformManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformManageServiceImpl implements PlatformManageService {

    private final PlatformService platformService;
    private final PlatformMapper platformMapper;

    public PlatformManageServiceImpl(PlatformService platformService, PlatformMapper platformMapper) {
        this.platformService = platformService;
        this.platformMapper = platformMapper;
    }

    @Override
    public PageResult page(PlatformPageParam param) {
        Page<Platform> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<Platform>()
                .and(StringUtils.isNotBlank(param.getQuery()), w -> w
                        .like(Platform::getName, param.getQuery())
                        .or().like(Platform::getLocalIp, param.getQuery())
                        .or().like(Platform::getRemoteIp, param.getQuery()))
                .eq(param.getEnable() != null, Platform::getEnable, param.getEnable())
                .eq(param.getStatus() != null, Platform::getStatus, param.getStatus());
        return MyBatisUtils.selectPage(platformMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResult<PlatformDetailVo> detail(Long id) {
        Platform platform = platformService.getById(id);
        if (platform == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "平台不存在");
        }
        return RestResult.result(RespCode.CODE_0.getValue(), null, PlatformManageConvert.INSTANCE.convert(platform));
    }

    @Override
    public RestResult<Long> save(PlatformSaveParam param) {
        // RTP 起止端口校验
        if (param.getStartRtpPort() != null && param.getEndRtpPort() != null
                && param.getStartRtpPort() > param.getEndRtpPort()) {
            return RestResult.result(RespCode.CODE_2.getValue(), "RTP起始端口不能大于结束端口");
        }

        boolean isUpdate = param.getId() != null;
        if (isUpdate && platformService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "平台不存在");
        }

        // 名称唯一性校验
        long nameCount = platformService.count(new LambdaQueryWrapper<Platform>()
                .eq(Platform::getName, param.getName())
                .ne(isUpdate, Platform::getId, param.getId()));
        if (nameCount > 0) {
            return RestResult.result(RespCode.CODE_2.getValue(), "平台名称已存在");
        }

        Platform entity = PlatformManageConvert.INSTANCE.convert(param);

        if (isUpdate) {
            // 保留运行时 status，不允许通过 save 覆盖
            entity.setStatus(null);
            if (!platformService.updateById(entity)) {
                return RestResult.result(RespCode.CODE_2.getValue(), "更新失败");
            }
        } else {
            if (!platformService.save(entity)) {
                return RestResult.result(RespCode.CODE_2.getValue(), "保存失败");
            }
        }
        return RestResult.result(RespCode.CODE_0.getValue(), null, entity.getId());
    }

    @Override
    public RestResult<?> status(FsLongStatusParam param) {
        if (platformService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "平台不存在");
        }
        // 只更新 enable 字段
        Platform update = new Platform();
        update.setId(param.getId());
        update.setEnable(param.getStatus());
        if (!platformService.updateById(update)) {
            return RestResult.result(RespCode.CODE_2.getValue(), "状态更新失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(), "更新成功");
    }

    @Override
    public RestResult<?> remove(Long id) {
        Platform platform = platformService.getById(id);
        if (platform == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "平台不存在");
        }
        if (Integer.valueOf(1).equals(platform.getEnable())) {
            return RestResult.result(RespCode.CODE_2.getValue(), "请先禁用平台后再删除");
        }
        platformService.removeById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<List<FsOptionVo>> select(String keyword, Integer limit) {
        int pageLimit = limit != null ? limit : 20;
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<Platform>()
                .like(StringUtils.isNotBlank(keyword), Platform::getName, keyword)
                .last("LIMIT " + pageLimit);
        List<Platform> list = platformService.list(wrapper);
        return RestResult.result(RespCode.CODE_0.getValue(), null,
                PlatformManageConvert.INSTANCE.convertOptions(list));
    }
}
