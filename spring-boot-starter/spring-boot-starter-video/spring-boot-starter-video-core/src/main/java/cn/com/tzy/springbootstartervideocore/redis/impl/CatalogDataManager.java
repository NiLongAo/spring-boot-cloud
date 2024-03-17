package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.CatalogData;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SyncStatus;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceChannelVoService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 通过订阅同步设备通道信息
 */
@Log4j2
public class CatalogDataManager {

    private final String VIDEO_CATALOG_DATA_MANAGER= VideoConstant.VIDEO_CATALOG_DATA_MANAGER;
    private final Integer millis = 30;
    private final DeviceChannelVoService deviceChannelVoService;


    public CatalogDataManager( DeviceChannelVoService deviceChannelVoService){
        this.deviceChannelVoService = deviceChannelVoService;
    }

    public void addReady(DeviceVo deviceVo, int sn ) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceVo.getDeviceId());
        CatalogData catalogData = null;
        if(RedisUtils.hasKey(key)){
            catalogData = (CatalogData)RedisUtils.get(key);
            if(catalogData.getTotal() != null && catalogData.getChannelList().size() >= catalogData.getTotal()){
                catalogData.setDeviceVo(deviceVo);
                catalogData.setSn(sn);
                RedisUtils.set(key,catalogData,millis);
            }
        }else {
            catalogData = new CatalogData();
            catalogData.setDeviceVo(deviceVo);
            catalogData.setSn(sn);
            RedisUtils.set(key,catalogData,millis);//存在时间30秒
        }
    }

    public void put(String deviceId, int sn, int total, DeviceVo deviceVo, List<DeviceChannelVo> deviceChannelVoList) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceId);
        CatalogData catalogData = null;
        if(RedisUtils.hasKey(key)){
            catalogData = (CatalogData)RedisUtils.get(key);
            long expire = RedisUtils.getExpire(key);
            // 同一个设备的通道同步请求只考虑一个，其他的直接忽略
            if (catalogData.getSn() != sn) {
                return;
            }
            catalogData.setTotal(total);
            catalogData.setDeviceVo(deviceVo);
            catalogData.setChannelList(deviceChannelVoList);
            RedisUtils.set(key,catalogData,expire);//存在时间30秒
        }else {
            catalogData = new CatalogData();
            catalogData.setSn(sn);
            catalogData.setTotal(total);
            catalogData.setDeviceVo(deviceVo);
            catalogData.setChannelList(deviceChannelVoList);
            RedisUtils.set(key,catalogData,millis);//存在时间30秒
        }
    }

    public List<DeviceChannelVo> get(String deviceId) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceId);
        if (!RedisUtils.hasKey(key)) {
            return null;
        }
        CatalogData catalogData = (CatalogData)RedisUtils.get(key);
        return catalogData.getChannelList();
    }

    public int getTotal(String deviceId) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceId);
        if (!RedisUtils.hasKey(key)) {
            return 0;
        }
        CatalogData catalogData = (CatalogData)RedisUtils.get(key);
        return catalogData.getTotal() == null?0:catalogData.getTotal();
    }

    public SyncStatus getSyncStatus(String deviceId) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceId);
        if (!RedisUtils.hasKey(key)) {
            return null;
        }
        CatalogData catalogData = (CatalogData)RedisUtils.get(key);
        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setCurrent(catalogData.getChannelList().size());
        syncStatus.setTotal(catalogData.getTotal()==null?0:catalogData.getTotal());
        syncStatus.setErrorMsg(catalogData.getErrorMsg());
        syncStatus.setSyncIng(false);
        if(StringUtils.isNotEmpty(catalogData.getErrorMsg()) || ( catalogData.getTotal() != null && catalogData.getChannelList().size() >= catalogData.getTotal())){
            syncStatus.setSyncIng(true);
        }
        return syncStatus;
    }

    public boolean isSyncRunning(String deviceId) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceId);
        if (!RedisUtils.hasKey(key)) {
            return false;
        }
        CatalogData catalogData = (CatalogData)RedisUtils.get(key);
        return catalogData.getTotal() == null || catalogData.getChannelList().size() <= catalogData.getTotal();
    }

    public void setChannelSyncEnd(String deviceId, String errorMsg) {
        String key = String.format("%s%s", VIDEO_CATALOG_DATA_MANAGER,deviceId);
        if (!RedisUtils.hasKey(key)) {
            return;
        }
        long expire = RedisUtils.getExpire(key);
        CatalogData catalogData = (CatalogData)RedisUtils.get(key);
        catalogData.setErrorMsg(errorMsg);
        RedisUtils.set(key,catalogData,expire);
    }

}
