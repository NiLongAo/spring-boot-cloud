package cn.com.tzy.springbootwebapi.controller.notice;
import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.notice.PublicNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "平台通知公告相关接口",position = 1)
@RestController("WebApiPublicNoticeController")
@RequestMapping(value = "/webapi/notice/public_notice")
public class PublicNoticeController extends ApiController {

    @Autowired
    PublicNoticeService publicNoticeService;

    @ApiOperation(value = "获取分页信息", notes = "获取分页信息")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody PublicNoticeParam param){
       return publicNoticeService.page(param);
    }

    @ApiOperation(value = "根据当前用户获取平台通知公告", notes = "根据当前用户获取平台通知公告")
    @PostMapping("user_page")
    @ResponseBody
    public PageResult userPage(@Validated @RequestBody PublicNoticeParam param){
        return publicNoticeService.userPage(param);
    }

    @ApiOperation(value = "根据编号查询平台通知公告信息", notes = "根据编号查询平台通知公告信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return publicNoticeService.detail(id);
    }


    @ApiOperation(value = "新增平台通知公告", notes = "新增平台通知公告")
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@Validated(BaseModel.add.class) @RequestBody PublicNoticeParam param){
        return publicNoticeService.insert(param);
    }
    @ApiOperation(value = "修改平台通知公告", notes = "修改平台通知公告")
    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@Validated(BaseModel.edit.class) @RequestBody PublicNoticeParam param){
        return publicNoticeService.update(param);
    }

    @ApiOperation(value = "删除平台通知公告", notes = "删除平台通知公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        return publicNoticeService.remove(id);
    }

    @ApiOperation(value = "用户查看平台通知公告并标记已读", notes = "用户查看平台通知公告并标记已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("user_read_notice_detail")
    @ResponseBody
    public RestResult<?> userReadNoticeDetail(@RequestParam("id") Long id){
        return publicNoticeService.userReadNoticeDetail(id);
    }
}
