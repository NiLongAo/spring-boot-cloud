package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootentity.dome.fs.MediaServer;
import cn.com.tzy.springbootfs.mapper.fs.MediaServerMapper;
import cn.com.tzy.springbootfs.service.fs.MediaServerService;
@Service
public class MediaServerServiceImpl extends ServiceImpl<MediaServerMapper, MediaServer> implements MediaServerService{

}
