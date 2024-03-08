package cn.com.tzy.springbootfeignsms.api.notice;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sms-server",contextId = "sms-server",path = "/api/notice/public_notice",configuration = FeignConfiguration.class)
public interface PublicNoticeServiceFeign {

    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody PublicNoticeParam param);

    @RequestMapping(value = "/user_page",method = RequestMethod.POST)
    PageResult userPage(@Validated @RequestBody PublicNoticeParam param);

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    RestResult<?> insert(@Validated @RequestBody PublicNoticeParam param);

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    RestResult<?> update(@Validated @RequestBody PublicNoticeParam param);

    @RequestMapping(value = "/remove",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam(value = "id")Long id);

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam(value = "id")Long id);

    @RequestMapping(value = "/user_read_notice_detail",method = RequestMethod.GET)
    RestResult<?> userReadNoticeDetail(@RequestParam("publicNoticeId") Long publicNoticeId);

}
