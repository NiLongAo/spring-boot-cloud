package cn.com.tzy.springbootwebapi.service.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import cn.com.tzy.springbootfeignbean.api.bean.DepartmentServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    DepartmentServiceFeign departmentServiceFeign;


    public PageResult page(@Validated @RequestBody DepartmentParam userPageModel){
        return departmentServiceFeign.page(userPageModel);
    }

    public RestResult<?> findAll(){
        return departmentServiceFeign.findAll();
    }


    public RestResult<?> save(@RequestBody @Validated DepartmentParam param){
        return departmentServiceFeign.save(param);
    }


    public RestResult<?> remove(@RequestParam("id")Long id){
        return  departmentServiceFeign.remove(id);
    }

    public RestResult<?> detail(Long id){return departmentServiceFeign.detail(id);}

    public RestResult<?> tree(DepartmentParam param){return departmentServiceFeign.tree(param);}

    public RestResult<?> select(List<Long> departmentIdList, String departmentName, Integer limit){
        return departmentServiceFeign.select(departmentIdList,departmentName,limit);
    }

}
