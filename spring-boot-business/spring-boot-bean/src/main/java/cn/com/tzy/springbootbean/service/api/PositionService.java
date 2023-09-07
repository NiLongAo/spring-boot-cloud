package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Position;
import cn.com.tzy.springbootentity.param.bean.PositionParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PositionService extends IService<Position>{

    PageResult page(PositionParam param);

    RestResult<?> save(Long parentId, Long id, Integer isEnable, String memo, String positionName);

    RestResult<?> tree(String topName, String positionName);

    RestResult<?> positionSelect(List<Long> positionIdList, String positionName, Integer limit);
}
