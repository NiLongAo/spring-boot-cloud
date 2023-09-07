package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.service.api.PositionConnectPrivilegeService;
import cn.com.tzy.springbootbean.service.api.PositionService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.Position;
import cn.com.tzy.springbootentity.dome.bean.PositionConnectPrivilege;
import cn.com.tzy.springbootentity.param.bean.PositionParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 职位信息
 */
@RestController("ApiBeanPositionController")
@RequestMapping(value = "/api/bean/position")
public class PositionController  extends ApiController {

    @Autowired
    PositionService positionService;
    @Autowired
    PositionConnectPrivilegeService positionConnectPrivilegeService;

    /**
     * 权限信息下拉展示(动态搜索数据源)
     * @return
     */
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> positionSelect(@RequestParam(value = "positionIdList",required = false)List<Long> positionIdList,@RequestParam(value = "positionName",required = false)String positionName,@RequestParam("limit") Integer limit){
        return positionService.positionSelect(positionIdList,positionName,limit);
    }

    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@Validated @RequestBody PositionParam param){
        return positionService.tree(param.topName,param.positionName);
    }

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody PositionParam userPageModel){
        return positionService.page(userPageModel);
    }

    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        List<NotNullMap> data = new ArrayList<>();
        List<Position> list = positionService.list();
        list.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putLong("parentId",obj.getParentId());
            map.putLong("positionId",obj.getId());
            map.putString("positionName",obj.getPositionName());
            map.putInteger("isEnable",obj.getIsEnable());
            map.putString("memo",obj.getMemo());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated PositionParam param){
        return positionService.save(param.parentId,param.id,param.isEnable,param.memo,param.positionName);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        Position position = positionService.getById(id);
        if(position == null){
            return RestResult.result(RespCode.CODE_0.getValue(),"未获取到职位信息");
        }
        Position parent = positionService.getOne(new LambdaQueryWrapper<Position>().eq(Position::getParentId,position.getId()));
        if(parent != null){
            return RestResult.result(RespCode.CODE_0.getValue(),"请先删除子级职位");
        }
        positionConnectPrivilegeService.remove(new LambdaQueryWrapper<PositionConnectPrivilege>().eq(PositionConnectPrivilege::getPositionId,id));
        positionService.removeById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        Position position = positionService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,position);
    }
}
