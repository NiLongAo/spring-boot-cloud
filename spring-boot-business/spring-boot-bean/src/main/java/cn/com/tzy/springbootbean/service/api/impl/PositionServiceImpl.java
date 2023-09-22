package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.PositionMapper;
import cn.com.tzy.springbootbean.service.api.PositionService;
import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.Position;
import cn.com.tzy.springbootentity.param.bean.PositionParam;
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
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService{

    @SneakyThrows
    @Override
    public PageResult page(PositionParam param) {
        //树级表格全查没有分页
        List<Position> pageResult = baseMapper.selectPositionList(param.positionName);
        Map<Long, Position> map= new HashMap<>();
        pageResult.forEach(obj->{
            map.put(obj.getId(),obj);
        });
        pageResult.forEach(onj->{
            findParent(map,onj);
        });
        pageResult = new ArrayList<>(map.values());
        List<TreeNode<Position>> treeNode = TreeUtil.getTree(pageResult, Position::getParentId, Position::getId, null);
        //转换树结构
        List<Map> maps = AppUtils.transformationTree("children", treeNode);
        return PageResult.result(RespCode.CODE_0.getValue(), pageResult.size(), null, maps);
    }

    public void findParent(Map<Long, Position> map, Position onj){
        if(onj != null && onj.getParentId() != null){
            Position entity = map.get(onj.getParentId());
            if(entity == null){
                Position department = baseMapper.selectById(onj.getParentId());
                map.put(onj.getParentId(),department);
                if(department != null && department.getParentId() != null){
                    findParent(map,onj);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult<?> save(Long parentId, Long id, Integer isEnable, String memo, String positionName) {
        Position position = null;
        if(id != null){
            position = baseMapper.selectOne(new QueryWrapper<Position>().eq("id", id));
            if(position == null){
                return RestResult.result(RespCode.CODE_2.getValue(),"未获取到菜单信息");
            }else if(parentId!= null && parentId.equals(id)){
                return RestResult.result(RespCode.CODE_2.getValue(),"父级编号与本级相同！");
            }
        }else {
            position = new Position();
        }
        if(StringUtils.isEmpty(ConstEnum.Flag.getName(isEnable))){
            return  RestResult.result(RespCode.CODE_2.getValue(),"当前状态错误，请检查");
        }
        position.setParentId(parentId);
        position.setPositionName(positionName);
        position.setIsEnable(isEnable);
        position.setMemo(memo);
        position.setId(id);
        boolean b = super.saveOrUpdate(position);
        if(b){
            return  RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return  RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }



    }

    @SneakyThrows
    @Override
    public RestResult<?> tree(String topName, String positionName) {
        List<Map> positionList = baseMapper.findAvailableTree(positionName);
        List<TreeNode<Map>> treeNode = TreeUtil.getTree(positionList, "parentId", "id", null);
        //顶级树
        Map map = null;
        if(StringUtils.isNotEmpty(topName)){
            map = new HashMap();
            map.put("parentId","");
            map.put("id","");
            map.put("tenantId","");
            map.put("positionName",topName);
        }
        //转换树结构
        List<Map> maps = AppUtils.transformationTree(map,"children", treeNode);
        return RestResult.result(RespCode.CODE_0.getValue(),  null, maps);
    }

    @Override
    public RestResult<?> positionSelect(List<Long> positionIdList, String positionName, Integer limit) {
        List<Position> positionList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(positionIdList)){
            positionList.addAll(baseMapper.selectBatchIds(positionIdList));
        }

        positionList.addAll(baseMapper.selectNameLimit(positionIdList,positionName, limit));
        List<NotNullMap> data = new ArrayList<>();
        positionList.forEach(obj ->{
            NotNullMap map = new NotNullMap();
            map.putLong("id",obj.getId());
            map.putString("name", obj.getPositionName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(), null, data);

    }
}
