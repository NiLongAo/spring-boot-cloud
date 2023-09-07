package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Area;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AreaService extends IService<Area>{


    RestResult<?> findAreaAll();
}
