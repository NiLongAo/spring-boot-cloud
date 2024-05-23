package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.RouteCall;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteCallInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RouteCallConvert {

    RouteCallConvert INSTANCE = Mappers.getMapper(RouteCallConvert.class);
    RouteCall convert(RouteCallInfo param);

    List<RouteCall> convertRouteCallList(List<RouteCallInfo> param);
    RouteCallInfo convert(RouteCall param);
    List<RouteCallInfo> convertRouteCallInfoList(List<RouteCall> param);
}
