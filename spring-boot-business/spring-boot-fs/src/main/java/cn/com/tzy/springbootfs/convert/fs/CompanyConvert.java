package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.Company;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CompanyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyConvert {

    CompanyConvert INSTANCE = Mappers.getMapper(CompanyConvert.class);
    Company convert(CompanyInfo param);

    List<Company> convertCompanyList(List<CompanyInfo> param);
    CompanyInfo convert(Company param);
    List<CompanyInfo> convertCompanyInfoList(List<Company> param);
}
