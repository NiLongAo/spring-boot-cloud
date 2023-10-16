package cn.com.tzy.springbootsms.service.impl;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.excption.ParamException;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.dome.sms.PublicNotice;
import cn.com.tzy.springbootentity.dome.sms.ReadNoticeUser;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import cn.com.tzy.springbootentity.utils.HtmlUtils;
import cn.com.tzy.springbootfeignbean.api.staticFile.UpLoadServiceFeign;
import cn.com.tzy.springbootfeignbean.api.sys.ConfigServiceFeign;
import cn.com.tzy.springbootsms.config.init.AppConfig;
import cn.com.tzy.springbootsms.mapper.PublicNoticeMapper;
import cn.com.tzy.springbootsms.mapper.ReadNoticeUserMapper;
import cn.com.tzy.springbootsms.service.PublicNoticeService;
import cn.com.tzy.springbootstartercloud.utils.MockMultipartFile;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Log4j2
@Service
public class PublicNoticeServiceImpl extends ServiceImpl<PublicNoticeMapper, PublicNotice> implements PublicNoticeService {

    @Autowired
    AppConfig appConfig;
    @Autowired
    ConfigServiceFeign configServiceFeign;
    @Autowired
    ReadNoticeUserMapper readNoticeUserMapper;
    @Autowired
    UpLoadServiceFeign upLoadServiceFeign;

    @Override
    public PageResult findPage(PublicNoticeParam param) {
        int total = baseMapper.findPageCount(param);
        List<PublicNotice> pageResult = baseMapper.findPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putInteger("noticeType", obj.getNoticeType());
            map.putString("title", obj.getTitle());
            map.putDateTime("beginTime", obj.getBeginTime());
            map.putDateTime("endTime", obj.getEndTime());
            map.putInteger("status", obj.getStatus());
            map.putDateTime("createTime", obj.getCreateTime());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }


    @Override
    public PageResult findUserPage(PublicNoticeParam param) {
        int total = baseMapper.findUserPageCount(param);
        List<PublicNotice> pageResult = baseMapper.findUserPageResult(param);
        List<NotNullMap> data = new ArrayList<>();
        pageResult.forEach(obj -> {
            NotNullMap map = new NotNullMap();
            map.putLong("id", obj.getId());
            map.putInteger("noticeType", obj.getNoticeType());
            map.putString("title", obj.getTitle());
            map.putInteger("readNotice", obj.getReadNotice());
            map.putDateTime("beginTime", obj.getBeginTime());
            map.putDateTime("endTime", obj.getEndTime());
            map.putInteger("status", obj.getStatus());
            map.putDateTime("createTime", obj.getCreateTime());
            data.add(map);
        });
        return PageResult.result(RespCode.CODE_0.getValue(), total, null, data);
    }

    @Override
    public RestResult<?> detail(Long id) {
        PublicNotice obj = baseMapper.selectById(id);
        if(obj == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取平台通知公告");
        }
        RestResult<?> restResult = RestResult.result(RespCode.CODE_0.getValue(),null,null);
        try{
            restResult = upLoadServiceFeign.findName(obj.getContent());
        }catch (Exception e){
            log.error("获取文件错误：",e);
        }
        if(restResult.getCode()!=RespCode.CODE_0.getValue()){
            throw new ParamException("获取原文件错误");
        }
        NotNullMap map = new NotNullMap();
        map.putLong("id", obj.getId());
        map.putInteger("noticeType", obj.getNoticeType());
        map.putString("title", obj.getTitle());
        map.putString("content", HtmlUtils.getBody(String.valueOf(restResult.getData())));
        map.putDateTime("beginTime", obj.getBeginTime());
        map.putDateTime("endTime", obj.getEndTime());
        map.putInteger("status", obj.getStatus());
        map.putDateTime("createTime", obj.getCreateTime());
        return RestResult.result(RespCode.CODE_0.getValue(),null,map);
    }

    @Override
    public RestResult<?> insert(PublicNoticeParam param) {
        String content = param.content;
        PublicNotice publicNotice = validatedAndCopy(param);
        int insert = baseMapper.insert(publicNotice);
        if(insert == 0){
            return RestResult.result(RespCode.CODE_2.getValue(),"新增失败",null);
        }
        String url = contextCreateHtml(param.getTitle(), content);
        publicNotice.setContent(url);
        baseMapper.updateById(publicNotice);
        return RestResult.result(RespCode.CODE_0.getValue(),"新增成功",null);
    }

    @Override
    public RestResult<?> update(PublicNoticeParam param) {
        PublicNotice oldPublicNotice = baseMapper.selectById(param.id);
        if (StringUtils.isNotBlank(oldPublicNotice.getContent())){
            RestResult<?> restResult = upLoadServiceFeign.delete(oldPublicNotice.getContent());
            if( restResult.getCode()!=RespCode.CODE_0.getValue()){
                throw new ParamException("删除原文件错误");
            }
        }
        String content = param.content;
        String url = contextCreateHtml(param.getTitle(), content);
        PublicNotice publicNotice = validatedAndCopy(param);
        publicNotice.setContent(url);
        int update = baseMapper.updateById(publicNotice);
        if(update == 0){
            return RestResult.result(RespCode.CODE_2.getValue(),"修改失败",null);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"修改成功",null);
    }

    @Override
    public RestResult<?> remove(Long id) {
        int i = baseMapper.deleteById(id);
        if(i == 0){
            return RestResult.result(RespCode.CODE_2.getValue(),"删除失败",null);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功",null);
    }

    @Override
    public RestResult<?> userReadNoticeDetail(Long userId,Long publicNoticeId) {
        PublicNotice obj = baseMapper.selectById(publicNoticeId);
        if(obj == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取平台通知公告");
        }
        RestResult<?> restResult = RestResult.result(RespCode.CODE_0.getValue(),null,null);
        try{
            restResult = upLoadServiceFeign.findName(obj.getContent());
        }catch (Exception e){
            log.error("获取文件错误：",e);
        }
        if(restResult.getCode()!=RespCode.CODE_0.getValue()){
            throw new ParamException("获取原文件错误");
        }
        int userIdNoticeIdCount = readNoticeUserMapper.findUserIdNoticeIdCount(userId, publicNoticeId);
        if(userIdNoticeIdCount <= 0){
            readNoticeUserMapper.insert(ReadNoticeUser.builder().userId(userId).noticeId(publicNoticeId).build());
        }
        NotNullMap map = new NotNullMap();
        map.putLong("id", obj.getId());
        map.putInteger("noticeType", obj.getNoticeType());
        map.putString("title", obj.getTitle());
        map.putString("content", obj.getContent());
        map.putDateTime("beginTime", obj.getBeginTime());
        map.putDateTime("endTime", obj.getEndTime());
        map.putInteger("status", obj.getStatus());
        map.putDateTime("createTime", obj.getCreateTime());
        return RestResult.result(RespCode.CODE_0.getValue(),null,map);
    }

    @Override
    public List<PublicNotice> findDateRange(Date date) {
        return baseMapper.findDateRange(date);
    }


    private PublicNotice validatedAndCopy(PublicNoticeParam param){
        param.content = "";
        PublicNotice publicNotice = new PublicNotice();
        BeanUtils.copyProperties(param,publicNotice);
        if( publicNotice.getBeginTime().compareTo(publicNotice.getEndTime()) >= 0){
            throw new ParamException("开始时间不能大于结束时间");
        }
        publicNotice.setStatus(PublicNotice.Status.NORMAL.getValue());
        return publicNotice;
    }

    /**
     * 创建HTML静态页面并保存
     * @param name
     * @return
     */
    private String contextCreateHtml(String name,String content){
        //创建html文件
        String html = HtmlUtils.createHtml(content);
        File file = new File(appConfig.tempDir, String.format(ConstEnum.StaticPath.HTML_RICH_TEXT_PATH.getUrl(), DateFormatUtils.format(new Date(), Constant.DATE_FORMAT), UUID.randomUUID().toString(), "html"));
        MockMultipartFile  mockMultipartFile = null;
        try {
            FileUtils.writeStringToFile(file, html, "UTF-8");
            InputStream inputStream = new FileInputStream(file);
            mockMultipartFile=  new MockMultipartFile(name,name+".html",null ,inputStream);
        } catch (IOException e) {
            throw new ParamException("文件创建失败",e);
        }
        RestResult<?> result = upLoadServiceFeign.upload(ConstEnum.StaticPath.HTML_RICH_TEXT_PATH.getType(), mockMultipartFile);
        if(result.getCode() != RespCode.CODE_0.getValue()){
            throw new ParamException("文件上传失败");
        }
        List<Map> list = AppUtils.convertValue2(result.getData(), new TypeReference<List<Map>>(){});
        Map map = list.get(0);
        String path = String.valueOf(map.get("path"));
        String fullPath = String.valueOf(map.get("fullPath"));
        return path;
    }

}
