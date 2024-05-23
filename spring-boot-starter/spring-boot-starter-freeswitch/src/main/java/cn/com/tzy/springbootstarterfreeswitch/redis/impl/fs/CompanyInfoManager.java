package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGateWayInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGroupInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnCodeInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
        //先匹配最长的。
        Set<String> routeGroupKey = companyInfo.getRouteGroupMap().keySet();
        if(routeGroupKey == null || routeGroupKey.isEmpty()){
            return null;
        }
        String route = routeGroupKey.stream().filter(called::contains).max(Comparator.comparingInt(String::length)).orElse(null);
        if(StringUtils.isEmpty(route)){
            return null;
        }
        RouteGroupInfo routeGroup = companyInfo.getRouteGroupMap().get(route);
        if(routeGroup == null || CollectionUtils.isEmpty(routeGroup.getRouteGateWayInfoList())){
            return null;
        }
        return routeGroup.getRouteGateWayInfoList().get(0);
    }

    public VdnCodeInfo getVdnCodeInfo(String companyId, Long vdnId){
        CompanyInfo companyInfo = get(companyId);
        if(companyInfo == null){
            return null;
        }
        return companyInfo.getVdnCodeMap().get(vdnId);
    }

    public void del(String companyId){
        List<String> scan = RedisUtils.keys(getKey(companyId));
        for (String key : scan) {
            RedisUtils.del(key);
        }
    }

    public void delAll(){
        del("*");
    }

    private String getKey(String companyId){
        return String.format("%s%s",FS_COMPANY_INFO,companyId);
    }
}
