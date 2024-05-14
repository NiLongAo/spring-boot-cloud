package cn.com.tzy.springbootfs.service.sip;

import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.DeviceVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaServerVoServiceImpl implements MediaServerVoService {
    @Override
    public MediaServerVo findMediaServerId(String mediaServerId) {
        return null;
    }

    @Override
    public MediaServerVo findOnLineMediaServerId(String mediaServerId) {
        return null;
    }

    @Override
    public List<MediaServerVo> findConnectZlmList() {
        return null;
    }

    @Override
    public MediaServerVo findMediaServerForMinimumLoad(DeviceVo deviceVo) {
        return null;
    }

    @Override
    public MediaServerVo findMediaServerForMinimumLoad() {
        return null;
    }

    @Override
    public int updateById(MediaServerVo mediaServerVo) {
        return 0;
    }

    @Override
    public int updateStatus(String mediaServerId, Integer status) {
        return 0;
    }
}
