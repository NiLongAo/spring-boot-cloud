package cn.com.tzy.springbootfs.service.manage.system.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.fs.MediaServer;
import cn.com.tzy.springbootentity.param.fs.common.FsStringStatusParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.fs.system.MediaServerSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.MediaServerDetailVo;
import cn.com.tzy.springbootfs.mapper.fs.MediaServerMapper;
import cn.com.tzy.springbootfs.service.fs.MediaServerService;
import cn.com.tzy.springbootfs.service.manage.system.MediaServerManageService;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaServerManageServiceImpl implements MediaServerManageService {

    private final MediaServerService mediaServerService;
    private final MediaServerMapper mediaServerMapper;

    public MediaServerManageServiceImpl(MediaServerService mediaServerService, MediaServerMapper mediaServerMapper) {
        this.mediaServerService = mediaServerService;
        this.mediaServerMapper = mediaServerMapper;
    }

    @Override
    public PageResult page(MediaServerPageParam param) {
        Page<MediaServer> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<MediaServer> wrapper = new LambdaQueryWrapper<MediaServer>()
                .like(StringUtils.isNotBlank(param.getQuery()), MediaServer::getIp, param.getQuery())
                .eq(param.getEnable() != null, MediaServer::getEnable, param.getEnable())
                .eq(param.getStatus() != null, MediaServer::getStatus, param.getStatus())
                .eq(param.getDefaultServer() != null, MediaServer::getDefaultServer, param.getDefaultServer());
        return MyBatisUtils.selectPage(mediaServerMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResult<MediaServerDetailVo> detail(String id) {
        MediaServer server = mediaServerService.getById(id);
        if (server == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "流媒体服务不存在");
        }
        MediaServerDetailVo vo = toDetailVo(server);
        // 脱敏：secret 不回显
        vo.setSecret(null);
        return RestResult.result(RespCode.CODE_0.getValue(), null, vo);
    }

    @Override
    public RestResult<String> save(MediaServerSaveParam param) {
        boolean isUpdate = StringUtils.isNotBlank(param.getId());
        MediaServer existing = null;
        if (isUpdate) {
            existing = mediaServerService.getById(param.getId());
            if (existing == null) {
                return RestResult.result(RespCode.CODE_2.getValue(), "流媒体服务不存在");
            }
        }

        // IP + httpPort 唯一性校验
        long ipPortCount = mediaServerService.count(new LambdaQueryWrapper<MediaServer>()
                .eq(MediaServer::getIp, param.getIp())
                .eq(param.getHttpPort() != null, MediaServer::getHttpPort, param.getHttpPort())
                .ne(isUpdate, MediaServer::getId, param.getId()));
        if (ipPortCount > 0) {
            return RestResult.result(RespCode.CODE_2.getValue(), "相同IP和HTTP端口的流媒体服务已存在");
        }

        MediaServer entity = toEntity(param);
        // 不接受运行时字段：keepaliveTime, status
        entity.setStatus(null);
        entity.setKeepaliveTime(null);

        if (isUpdate) {
            // secret 为空时保持原值
            if (StringUtils.isBlank(param.getSecret())) {
                entity.setSecret(existing.getSecret());
            }
            if (!mediaServerService.updateById(entity)) {
                return RestResult.result(RespCode.CODE_2.getValue(), "更新失败");
            }
        } else {
            entity.setId(RandomUtil.randomNumbers(19));
            if (!mediaServerService.save(entity)) {
                return RestResult.result(RespCode.CODE_2.getValue(), "保存失败");
            }
        }
        return RestResult.result(RespCode.CODE_0.getValue(), null, entity.getId());
    }

    @Override
    public RestResult<?> status(FsStringStatusParam param) {
        if (mediaServerService.getById(param.getId()) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "流媒体服务不存在");
        }
        // 只更新 enable，不动 status（在线状态）
        MediaServer update = new MediaServer();
        update.setId(param.getId());
        update.setEnable(param.getStatus());
        if (!mediaServerService.updateById(update)) {
            return RestResult.result(RespCode.CODE_2.getValue(), "状态更新失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(), "更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> setDefault(String id) {
        if (mediaServerService.getById(id) == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "流媒体服务不存在");
        }
        // 先清空所有默认节点，再设置目标为默认
        mediaServerService.update(new UpdateWrapper<MediaServer>().set("default_server", 0));
        MediaServer update = new MediaServer();
        update.setId(id);
        update.setDefaultServer(1);
        mediaServerService.updateById(update);
        return RestResult.result(RespCode.CODE_0.getValue(), "设置默认节点成功");
    }

    @Override
    public RestResult<?> remove(String id) {
        MediaServer server = mediaServerService.getById(id);
        if (server == null) {
            return RestResult.result(RespCode.CODE_2.getValue(), "流媒体服务不存在");
        }
        // 在线（status=1）时禁止删除
        if (Integer.valueOf(1).equals(server.getStatus())) {
            return RestResult.result(RespCode.CODE_2.getValue(), "流媒体服务在线，无法删除");
        }
        mediaServerService.removeById(id);
        return RestResult.result(RespCode.CODE_0.getValue(), "删除成功");
    }

    @Override
    public RestResult<List<FsOptionVo>> select(String keyword, Integer limit) {
        int pageLimit = limit != null ? limit : 20;
        LambdaQueryWrapper<MediaServer> wrapper = new LambdaQueryWrapper<MediaServer>()
                .eq(MediaServer::getEnable, 1)
                .like(StringUtils.isNotBlank(keyword), MediaServer::getIp, keyword)
                .last("LIMIT " + pageLimit);
        List<MediaServer> list = mediaServerService.list(wrapper);
        List<FsOptionVo> options = list.stream().map(s -> FsOptionVo.builder()
                .id(s.getId())
                .name(s.getIp())
                .code(s.getIp())
                .status(s.getStatus())
                .build()).collect(Collectors.toList());
        return RestResult.result(RespCode.CODE_0.getValue(), null, options);
    }

    private MediaServerDetailVo toDetailVo(MediaServer s) {
        return MediaServerDetailVo.builder()
                .id(s.getId()).ip(s.getIp()).sslStatus(s.getSslStatus())
                .hookIp(s.getHookIp()).sdpIp(s.getSdpIp()).streamIp(s.getStreamIp())
                .httpPort(s.getHttpPort()).httpSslPort(s.getHttpSslPort())
                .rtmpPort(s.getRtmpPort()).rtmpSslPort(s.getRtmpSslPort())
                .rtpProxyPort(s.getRtpProxyPort()).rtspPort(s.getRtspPort()).rtspSslPort(s.getRtspSslPort())
                .autoConfig(s.getAutoConfig()).secret(s.getSecret())
                .rtpEnable(s.getRtpEnable()).enable(s.getEnable())
                .keepaliveTime(s.getKeepaliveTime()).status(s.getStatus())
                .rtpPortRange(s.getRtpPortRange()).recordAssistPort(s.getRecordAssistPort())
                .defaultServer(s.getDefaultServer()).hookAliveInterval(s.getHookAliveInterval())
                .videoPlayPrefix(s.getVideoPlayPrefix()).videoHttpPrefix(s.getVideoHttpPrefix())
                .build();
    }

    private MediaServer toEntity(MediaServerSaveParam p) {
        MediaServer s = new MediaServer();
        s.setId(p.getId());
        s.setIp(p.getIp());
        s.setSslStatus(p.getSslStatus());
        s.setHookIp(p.getHookIp());
        s.setSdpIp(p.getSdpIp());
        s.setStreamIp(p.getStreamIp());
        s.setHttpPort(p.getHttpPort());
        s.setHttpSslPort(p.getHttpSslPort());
        s.setRtmpPort(p.getRtmpPort());
        s.setRtmpSslPort(p.getRtmpSslPort());
        s.setRtpProxyPort(p.getRtpProxyPort());
        s.setRtspPort(p.getRtspPort());
        s.setRtspSslPort(p.getRtspSslPort());
        s.setAutoConfig(p.getAutoConfig());
        s.setSecret(p.getSecret());
        s.setRtpEnable(p.getRtpEnable());
        s.setEnable(p.getEnable());
        s.setRtpPortRange(p.getRtpPortRange());
        s.setRecordAssistPort(p.getRecordAssistPort());
        s.setDefaultServer(p.getDefaultServer());
        s.setHookAliveInterval(p.getHookAliveInterval());
        s.setVideoPlayPrefix(p.getVideoPlayPrefix());
        s.setVideoHttpPrefix(p.getVideoHttpPrefix());
        return s;
    }
}
