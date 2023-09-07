package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.DepartmentMapper;
import cn.com.tzy.springbootbean.service.api.DepartmentService;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.common.bean.Tree;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.Department;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService{

    @SneakyThrows
    @Override
    public RestResult<?> tree(String topName, String departmentName) {
        List<Map> departmentList = baseMapper.findAvailableTree( departmentName);
        List<Tree<Map>> tree = TreeUtil.getTree(departmentList, "parentId", "id", null);
        //顶级树
        Map map = null;
        if(StringUtils.isNotEmpty(topName)){
            map = new HashMap();
            map.put("parentId","");
            map.put("id","");
            map.put("tenantId","");
            map.put("departmentName",topName);
        }
        //转换树结构
        List<Map> maps = AppUtils.transformationTree(map,"children",tree);
        return RestResult.result(RespCode.CODE_0.getValue(),  null, maps);
    }

    @Override
    public RestResult<?> positionSelect(List<Long> departmentIdList, String departmentName, Integer limit) {
        List<Department> departmentList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(departmentIdList)){
            departmentList.addAll(baseMapper.selectBatchIds(departmentIdList));
        }

        departmentList.addAll(baseMapper.selectNameLimit(departmentIdList,departmentName, limit));
        List<NotNullMap> data = new ArrayList<>();
        departmentList.forEach(obj ->{
            NotNullMap map = new NotNullMap();
            map.putLong("id",obj.getId());
            map.putString("name", obj.getDepartmentName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(), null, data);

    }

    @SneakyThrows
    @Override
    public PageResult page(DepartmentParam param) {
        //树级表格全查没有分页
        List<Department> pageResult = baseMapper.selectDepartmentList(param);
        Map<Long,Department> map= new HashMap<>();
        pageResult.forEach(obj->{
            map.put(obj.getId(),obj);
        });
        pageResult.forEach(onj->{
            findParent(map,onj);
        });
        pageResult = new ArrayList<>(map.values());
        List<Tree<Department>> tree = TreeUtil.getTree(pageResult, Department::getId, Department::getId, null);
        //转换树结构
        List<Map> maps = AppUtils.transformationTree("children",tree);
        return PageResult.result(RespCode.CODE_0.getValue(), pageResult.size(), null, maps);
    }

    public void findParent(Map<Long,Department> map,Department onj){
        if(onj != null && onj.getParentId() != null){
            Department department = map.get(onj.getParentId());
            if(department == null){
                Department department1 = baseMapper.selectById(onj.getParentId());
                map.put(onj.getParentId(),department1);
                if(department1 != null && department1.getParentId() != null){
                    findParent(map,onj);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(Long parentId, Long id, String departmentName, Integer isEnable, String memo) {
        Department department = null;
        if(id != null){
            department = baseMapper.selectOne(new QueryWrapper<Department>().eq("id", id));
            if(department == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"未获取到部门信息");
            }else if(parentId!= null && parentId.equals(id)){
                return RestResult.result(RespCode.CODE_2.getValue(),"父级编号与本级相同！");
            }
        }else {
            department = new Department();
        }
        if(StringUtils.isEmpty(ConstEnum.Flag.getName(isEnable))){
            return  RestResult.result(RespCode.CODE_2.getValue(),"当前状态错误，请检查");
        }
        department.setParentId(parentId);
        department.setId(id);
        department.setIsEnable(isEnable);
        department.setDepartmentName(departmentName);
        department.setMemo(memo);
        boolean b = super.saveOrUpdate(department);
        if(b){
            return  RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }

    }
}
