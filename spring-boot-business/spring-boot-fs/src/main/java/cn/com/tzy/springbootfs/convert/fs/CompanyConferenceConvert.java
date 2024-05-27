package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.CompanyConference;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyConferenceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyConferenceConvert {

    CompanyConferenceConvert INSTANCE = Mappers.getMapper(CompanyConferenceConvert.class);
    CompanyConference convert(CompanyConferenceInfo param);

    List<CompanyConference> convertCompanyConferenceList(List<CompanyConferenceInfo> param);
    CompanyConferenceInfo convert(CompanyConference param);
    List<CompanyConferenceInfo> convertCompanyConferenceInfoList(List<CompanyConference> param);
}
