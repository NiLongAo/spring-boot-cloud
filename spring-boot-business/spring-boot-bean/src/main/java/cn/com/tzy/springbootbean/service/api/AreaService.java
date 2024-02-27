package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.common.info.AreaInfo;
import cn.com.tzy.springbootentity.dome.sys.Area;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AreaService extends IService<Area>{


    RestResult<?> findAreaAll();

    public String findAddress(Integer provinceId,Integer cityId,Integer areaId);

    public Map<Integer, String> findAllAreaName();
}
