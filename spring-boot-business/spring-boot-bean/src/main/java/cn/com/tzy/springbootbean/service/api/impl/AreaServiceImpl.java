package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.common.info.AreaInfo;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootbean.mapper.sql.AreaMapper;
import cn.com.tzy.springbootentity.dome.sys.Area;
import cn.com.tzy.springbootbean.service.api.AreaService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService{


    @Override
    @Cacheable(value = RedisCommon.AREA_ALL_INFO)
    public RestResult<?> findAreaAll() {
        List<Area> allArea = baseMapper.selectList(new QueryWrapper<Area>());
        List<TreeNode<Area>> tree = TreeUtil.getTree(allArea, Area::getParentId, Area::getAreaId, null);
        return RestResult.result(RespCode.CODE_0.getValue(),null,transformationTree(tree));
    }

    @Override
    @Cacheable(value = RedisCommon.AREA_ALL_NAME)
    public Map<Integer, String> findAllAreaName() {
        List<Area> allArea = baseMapper.selectList(new QueryWrapper<Area>());
        return allArea.stream().collect(Collectors.toMap(Area::getAreaId, Area::getAreaName, (k1, k2) -> k1));
    }

    @Override
    @Cacheable(value = RedisCommon.AREA_ADDRESS,key = " #provinceId +'_'+ #cityId +'_'+ #areaId")
    public String findAddress(Integer provinceId,Integer cityId,Integer areaId){
        Map<Integer, String> allAreaName = this.findAllAreaName();
        StringBuilder str = new StringBuilder();
        if(provinceId != null){
            str.append(allAreaName.get(provinceId));
        }
        if(cityId != null){
            str.append(allAreaName.get(cityId));
        }
        if(areaId != null){
            str.append(allAreaName.get(areaId));
        }
        return str.toString();
    }

    private List<AreaInfo> transformationTree(List<TreeNode<Area>> treeNode){
        List<AreaInfo> areaInitList = new ArrayList<>();
        treeNode.forEach(obj->{
            AreaInfo areaInfo = new AreaInfo();
            areaInfo.setValue(obj.getT().getAreaId());
            areaInfo.setLabel(obj.getT().getAreaName());
            if(obj.getIsChildren()){
                List<AreaInfo> areaInfoList = transformationTree(obj.getChildren());
                areaInfo.setChildren(areaInfoList);
            }
            areaInitList.add(areaInfo);
        });
        return areaInitList;
    }
}
