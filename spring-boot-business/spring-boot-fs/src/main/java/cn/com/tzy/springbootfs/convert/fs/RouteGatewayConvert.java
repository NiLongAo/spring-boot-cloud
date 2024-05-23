package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.RouteGateway;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGateWayInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RouteGatewayConvert {
    RouteGatewayConvert INSTANCE = Mappers.getMapper(RouteGatewayConvert.class);
    RouteGateway convert(RouteGateWayInfo param);

    List<RouteGateway> convertRouteGatewayList(List<RouteGateWayInfo> param);
    RouteGateWayInfo convert(RouteGateway param);
    List<RouteGateWayInfo> convertRouteGateWayInfoList(List<RouteGateway> param);
}
