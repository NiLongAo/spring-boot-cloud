package cn.com.tzy.springbootwebapi.service.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.PositionParam;
import cn.com.tzy.springbootfeignbean.api.bean.PositionServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    @Autowired
    PositionServiceFeign positionServiceFeign;

    public PageResult page(PositionParam userPageModel){
        return positionServiceFeign.page(userPageModel);
    }

    public RestResult<?> findAll(){return positionServiceFeign.findAll();}

    public RestResult<?> save(PositionParam param){return positionServiceFeign.save(param);}

    public RestResult<?> remove(Long id){return positionServiceFeign.remove(id);}

    public RestResult<?> detail(Long id){return positionServiceFeign.detail(id);}

    public RestResult<?> tree(PositionParam param){
        return positionServiceFeign.tree(param);
    }

    public RestResult<?> select(List<Long> positionIdList, String positionName, Integer limit){
        return positionServiceFeign.select(positionIdList,positionName,limit);
    }
}
