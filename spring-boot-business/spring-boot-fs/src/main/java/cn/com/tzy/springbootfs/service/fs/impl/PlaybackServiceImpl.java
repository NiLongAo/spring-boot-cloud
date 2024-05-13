package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.PlaybackMapper;
import cn.com.tzy.springbootentity.dome.fs.Playback;
import cn.com.tzy.springbootfs.service.fs.PlaybackService;
@Service
public class PlaybackServiceImpl extends ServiceImpl<PlaybackMapper, Playback> implements PlaybackService{

}
