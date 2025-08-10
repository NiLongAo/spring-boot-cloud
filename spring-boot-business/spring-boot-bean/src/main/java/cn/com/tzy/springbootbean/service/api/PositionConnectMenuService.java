package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.PositionConnectMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PositionConnectMenuService extends IService<PositionConnectMenu>{

    RestResult<?> findPositionPrivilegeList(Long positionId);

    RestResult<?> save(Long positionId, List<String> privilegeList);
}
