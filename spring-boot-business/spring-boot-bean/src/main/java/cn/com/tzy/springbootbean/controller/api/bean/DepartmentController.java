package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.service.api.DepartmentConnectPrivilegeService;
import cn.com.tzy.springbootbean.service.api.DepartmentService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.Department;
import cn.com.tzy.springbootentity.dome.bean.DepartmentConnectPrivilege;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门信息
 */
@RestController("ApiBeanDepartmentController")
@RequestMapping(value = "/api/bean/department")
public class DepartmentController  extends ApiController {

    @Autowired
    DepartmentService departmentService;
    @Autowired
    DepartmentConnectPrivilegeService departmentConnectPrivilegeService;

    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@Validated @RequestBody DepartmentParam param){
        return departmentService.tree(param.topName,param.departmentName);
    }

    /**
     * 权限信息下拉展示(动态搜索数据源)
     * @return
     */
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> positionSelect(@RequestParam(value = "departmentIdList",required = false)List<Long> departmentIdList,@RequestParam(value = "departmentName",required = false)String departmentName,@RequestParam("limit") Integer limit){
        return departmentService.positionSelect(departmentIdList,departmentName,limit);
    }

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody DepartmentParam userPageModel){
        return departmentService.page(userPageModel);
    }

    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        List<NotNullMap> data = new ArrayList<>();
        List<Department> list = departmentService.list();
        list.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putLong("parentId",obj.getParentId());
            map.putLong("departmentId",obj.getId());
            map.putInteger("isEnable",obj.getIsEnable());
            map.putString("departmentName",obj.getDepartmentName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated DepartmentParam param){
        return departmentService.save(param.parentId,param.id,param.departmentName,param.isEnable,param.memo);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        Department department = departmentService.getById(id);
        if(department == null){
            return RestResult.result(RespCode.CODE_0.getValue(),"未获取到部门信息");
        }
        Department parent = departmentService.getOne(new LambdaQueryWrapper<Department>().eq(Department::getParentId,department.getId()));
        if(parent != null){
            return RestResult.result(RespCode.CODE_0.getValue(),"请先删除子级部门");
        }
        departmentConnectPrivilegeService.remove(new LambdaQueryWrapper<DepartmentConnectPrivilege>().eq(DepartmentConnectPrivilege::getDepartmentId,id));
        departmentService.removeById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        Department department = departmentService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,department);
    }


}
