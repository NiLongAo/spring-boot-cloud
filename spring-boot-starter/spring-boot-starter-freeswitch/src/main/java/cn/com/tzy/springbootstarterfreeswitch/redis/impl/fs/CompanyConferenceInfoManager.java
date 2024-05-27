package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyConferenceInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class CompanyConferenceInfoManager {

    private String FS_COMPANY_CONFERENCE_INFO = RedisConstant.FS_COMPANY_CONFERENCE_INFO;
    public void put(CompanyConferenceInfo model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKey(model.getCompanyId(),model.getCode()),model,-1L);
    }


    public CompanyConferenceInfo get(String companyId,String conferenceCode) {
        List<String> scan = RedisUtils.keys(getKey(companyId,conferenceCode));
        if (!scan.isEmpty()) {
            return (CompanyConferenceInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    public void del(String companyId,String conferenceCode){
        List<String> scan = RedisUtils.keys(getKey(companyId,conferenceCode));
        for (String key : scan) {
            RedisUtils.del(key);
        }
    }

    public void delAll(){
        del("*","*");
    }

    private String getKey(String companyId,String conferenceCode){
        if(companyId == null){
            companyId ="*";
        }
        if(conferenceCode == null){
            conferenceCode ="*";
        }
        return String.format("%s%s:%s",FS_COMPANY_CONFERENCE_INFO,companyId,conferenceCode);
    }


}
