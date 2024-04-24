package cn.com.tzy.springbootstarterfreeswitch.redis.impl;

import cn.com.tzy.springbootstarterfreeswitch.common.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGateWayInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGroupInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnCodeInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class CompanyInfoManager {
    private String FS_COMPANY_INFO = RedisConstant.FS_COMPANY_INFO;


    public void put(CompanyInfo model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKey(model.getId()),model,-1L);
    }


    public CompanyInfo get(String companyId) {
        List<String> scan = RedisUtils.keys(getKey(companyId));
        if (!scan.isEmpty()) {
            return (CompanyInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    public RouteGateWayInfo getRouteGateWayInfo(String companyId, String called){
        CompanyInfo companyInfo = get(companyId);
        if(companyInfo == null){
            return null;
        }
        RouteGroupInfo routeGroup = null;
        //先匹配最长的。
        for (String route : companyInfo.getRouteGroupMap().keySet()) {
            if (called.contains(route) || route.equals("*")) {
                routeGroup = companyInfo.getRouteGroupMap().get(route);
                break;
            }
        }
        if (routeGroup == null || CollectionUtils.isEmpty(routeGroup.getRouteGateWayInfoList())) {
            return null;
        }
        //根据RouteGroup的规则判断
        Integer index = 0;
        return routeGroup.getRouteGateWayInfoList().get(index);
    }

    public VdnCodeInfo getVdnCodeInfo(String companyId, Long vdnId){
        CompanyInfo companyInfo = get(companyId);
        if(companyInfo == null){
            return null;
        }
        return companyInfo.getVdnCodeMap().get(vdnId);
    }

    public void del(String companyId){
        RedisUtils.del(getKey(companyId));
    }

    private String getKey(String companyId){
        return String.format("%s%s",FS_COMPANY_INFO,companyId);
    }
}
