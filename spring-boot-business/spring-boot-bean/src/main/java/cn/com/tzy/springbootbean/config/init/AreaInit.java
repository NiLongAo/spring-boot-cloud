package cn.com.tzy.springbootbean.config.init;

import cn.com.tzy.springbootbean.mapper.sql.AreaMapper;
import cn.com.tzy.springbootentity.common.info.AreaInfo;
import cn.com.tzy.springbootcomm.common.bean.Tree;
import cn.com.tzy.springbootentity.dome.sys.Area;
import cn.com.tzy.springbootentity.utils.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AreaInit {

    @Autowired
    AreaMapper areaMapper;

    public void init(AppConfig appConfig) throws Exception {
        List<Area> allArea = areaMapper.selectList(new QueryWrapper<Area>());
        Map<Integer, String> collect = allArea.stream().collect(Collectors.toMap(Area::getAreaId, Area::getAreaName, (k1, k2) -> k1));
        appConfig.setAllAreaName(collect);
        List<Tree<Area>> tree = TreeUtil.getTree(allArea, Area::getParentId, Area::getAreaId, null);
        appConfig.setAllArea(transformationTree(tree));
        //取出AreaId做K,AreaName做V,并且去重
    }

    private List<AreaInfo> transformationTree(List<Tree<Area>> tree){
        List<AreaInfo> areaInitList = new ArrayList<>();
        tree.forEach(obj->{
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
