package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootentity.dome.fs.RouteGroup;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGroupInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RouteGroupConvert {

    RouteGroupConvert INSTANCE = Mappers.getMapper(RouteGroupConvert.class);
    RouteCall convert(RouteGroupInfo param);

    List<RouteCall> convertRouteCallList(List<RouteGroupInfo> param);
    RouteGroupInfo convert(RouteCall param);
    List<RouteGroupInfo> convertRouteGroupInfoList(List<RouteGroup> param);
}
