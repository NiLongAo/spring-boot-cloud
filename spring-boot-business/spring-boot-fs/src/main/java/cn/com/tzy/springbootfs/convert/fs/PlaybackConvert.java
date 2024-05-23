package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.Playback;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.PlaybackInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlaybackConvert {

    PlaybackConvert INSTANCE = Mappers.getMapper(PlaybackConvert.class);
    Playback convert(PlaybackInfo param);

    List<Playback> convertPlaybackList(List<PlaybackInfo> param);
    PlaybackInfo convert(Playback param);
    List<PlaybackInfo> convertPlaybackInfoList(List<Playback> param);
}
