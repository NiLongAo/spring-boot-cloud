package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.bean.Tree;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.param.video.DeviceChannelPageParam;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import cn.com.tzy.springbootentity.vo.video.DeviceChannelTreeVo;
import cn.com.tzy.springbootstartervideobasic.enums.GbIdConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SyncStatus;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.com.tzy.springbootvideo.convert.video.DeviceChannelConvert;
import cn.com.tzy.springbootvideo.mapper.DeviceChannelMapper;
import cn.com.tzy.springbootvideo.service.DeviceChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class DeviceChannelServiceImpl extends ServiceImpl<DeviceChannelMapper, DeviceChannel> implements DeviceChannelService{

    @Resource
    private SipServer sipServer;
    @Resource
    private SIPCommander sipCommander;

    @Override
    public DeviceChannel findPlatformIdChannelId(String platformId, String channelId) {
        return baseMapper.findPlatformIdChannelId(platformId,channelId);
    }

    @Override
    public List<DeviceChannel> queryChannelWithCatalog(String serverGbId) {
        return baseMapper.queryChannelWithCatalog(serverGbId);
    }

    @Override
    public List<DeviceChannel> queryGbStreamListInPlatform(String serverGbId, String gbId, boolean usPushingAsStatus) {
        return baseMapper.queryGbStreamListInPlatform(serverGbId,gbId,usPushingAsStatus);
    }


    @Override
    public void updateChannelSubCount(String deviceId, String channelId) {
        baseMapper.updateChannelSubCount(deviceId,channelId);
    }


    @Override
    public RestResult<?> detail(String channelId) {
        DeviceChannel deviceChannel = baseMapper.selectOne(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getChannelId, channelId));
        return RestResult.result(RespCode.CODE_0.getValue(),null,deviceChannel);
    }

    @Override
    public RestResult<?> del(String deviceId, String channelId) {
        int del = VideoService.getDeviceChannelService().del(deviceId, channelId);
        if(del > 0){
            return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"删除失败");
        }
    }
    @Override
    public PageResult findPage(DeviceChannelPageParam param) throws Exception {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(param.deviceId);
        if(deviceVo == null){
            return PageResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        SipConfigProperties sipInfoAll = sipServer.getSipConfigProperties();
        ArrayList<String> strings = new ArrayList<>();
        strings.add(null);
        strings.add(param.deviceId);
        // 海康设备 的 parentId 是 SIP id
        strings.add(sipInfoAll.getId());
        if(GbIdConstant.Type.TYPE_216.getValue() == deviceVo.getTreeType()){
            List<DeviceChannel> deviceChannelList = baseMapper.businessGroupList(param.deviceId,param.online,true);
            List<Tree<DeviceChannel>> tree = TreeUtil.getTree(deviceChannelList, DeviceChannel::getCivilCode, DeviceChannel::getChannelId, strings);
            List<Map> maps = AppUtils.transformationTree("children",tree);
            return PageResult.result(RespCode.CODE_0.getValue(),deviceChannelList.size(),null,maps);
        }else if(GbIdConstant.Type.TYPE_215.getValue() == deviceVo.getTreeType()){
            List<DeviceChannel> deviceChannelList = baseMapper.businessGroupList(param.deviceId,param.online,false);
            List<Tree<DeviceChannel>> tree = TreeUtil.getTree(deviceChannelList, DeviceChannel::getParentId, DeviceChannel::getChannelId, strings);
            List<Map> maps = AppUtils.transformationTree("children",tree);
            return PageResult.result(RespCode.CODE_0.getValue(),deviceChannelList.size(),null,maps);
        }else {
            return PageResult.result(RespCode.CODE_2.getValue(),"设备业务类型错误");
        }
    }

    @Override
    public RestResult<?> saveDeviceChannel(DeviceChannel param) {
        DeviceChannelVo deviceChannelVo = DeviceChannelConvert.INSTANCE.convert(param);
        int save = VideoService.getDeviceChannelService().save(deviceChannelVo);
        if(save > 0){
            return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }
    }

    @Override
    public RestResult<?> sync(String deviceId) {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        boolean sync = VideoService.getDeviceService().sync(sipServer, sipCommander, deviceVo);
        if(sync){
            return RestResult.result(RespCode.CODE_0.getValue(),"设备同步中");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"设备同步失败");
        }
    }

    @Override
    public RestResult<?> syncStatus(String deviceId) {
        SyncStatus syncStatus = VideoService.getDeviceService().getChannelSyncStatus(deviceId);
        if(syncStatus == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"设备同步失败");
        }else {
            return RestResult.result(RespCode.CODE_0.getValue(),null,syncStatus);
        }
    }

    @Override
    public RestResult<?> findTreeDeviceChannel() throws Exception {
        List<DeviceChannelTreeVo> tree = baseMapper.findTreeDeviceChannel();
        List<Tree<DeviceChannelTreeVo>> dcTree = TreeUtil.getTree(tree, DeviceChannelTreeVo::getParentId, DeviceChannelTreeVo::getId, null);
        List<Map> mapList = AppUtils.transformationTree("children", dcTree);
        return RestResult.result(RespCode.CODE_0.getValue(),null,mapList);
    }

}
