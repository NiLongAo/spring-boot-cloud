package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.service.api.MiniService;
import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("ApiBeanMiniController")
@RequestMapping(value = "/api/bean/mini")
public class MiniController extends ApiController {

    @Autowired
    private MiniService miniService;

    /**
     * 保存 修改 微信小程序信息
     * @param param
     * @return
     */
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated Mini param){
        Mini mini = miniService.getOne(new LambdaQueryWrapper<Mini>().eq(Mini::getOpenId, param.getOpenId()));
        if(mini == null){
            miniService.save(param);
        }else {
            param.setId(mini.getId());
            miniService.updateById(param);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,param);
    }

    /**
     * 根据web用户编号获取小程序绑定用户
     * @param userId 微信用户openId
     * @return
     */
    @GetMapping("find_web_user_id")
    @ResponseBody
    public RestResult<?> findWebUserId(@RequestParam("userId") Long userId){
        return miniService.findWebUserId(userId);
    }

    /**
     * 保存 修改 微信小程序信息
     * @param openId 微信用户openId
     * @return
     */
    @GetMapping("find_open_id")
    @ResponseBody
    public RestResult<?> findOpenId(@RequestParam("openId") String openId){
        Mini mini = miniService.getOne(new LambdaQueryWrapper<Mini>().eq(Mini::getOpenId, openId));
        if(mini ==null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取微信用户");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null,mini);
    }

    /**
     * 微信绑定web端用户
     * @param param
     * @return
     */
    @PostMapping("save_mini_user")
    @ResponseBody
    public RestResult<?> saveMiniUser(@RequestBody @Validated(BaseModel.add.class) MiniUserParam param){
        return miniService.saveMiniUser(param);
    }


    /**
     * 微信绑定web端用户
     * @param param
     * @return
     */
    @PostMapping("unbind_mini_web")
    @ResponseBody
    public RestResult<?> unbindMiniWeb(@RequestBody @Validated MiniUserParam param){
        return miniService.unbindMiniWeb(param);
    }

}
