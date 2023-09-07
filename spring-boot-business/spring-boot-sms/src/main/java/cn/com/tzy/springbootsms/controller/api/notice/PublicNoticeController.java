package cn.com.tzy.springbootsms.controller.api.notice;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import cn.com.tzy.springbootsms.service.PublicNoticeService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("PublicNoticeController")
@RequestMapping(value = "/api/notice/public_notice")
public class PublicNoticeController extends ApiController {

    @Autowired
    PublicNoticeService publicNoticeService;

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody PublicNoticeParam param){
       return publicNoticeService.findPage(param);
    }

    @PostMapping("user_page")
    @ResponseBody
    public PageResult userPage(@Validated @RequestBody PublicNoticeParam param){
        return publicNoticeService.findUserPage(param);
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return publicNoticeService.detail(id);
    }

    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@Validated(BaseModel.add.class) @RequestBody PublicNoticeParam param){
        return publicNoticeService.insert(param);
    }

    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@Validated(BaseModel.edit.class) @RequestBody PublicNoticeParam param){
        return publicNoticeService.update(param);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        return publicNoticeService.remove(id);
    }

    @GetMapping("user_read_notice_detail")
    @ResponseBody
    public RestResult<?> userReadNoticeDetail(@RequestParam("userId") Long userId,@RequestParam("publicNoticeId") Long publicNoticeId){
        return publicNoticeService.userReadNoticeDetail(userId,publicNoticeId);
    }
}
