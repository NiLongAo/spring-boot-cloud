package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.PositionConnectPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PositionConnectPrivilegeService extends IService<PositionConnectPrivilege>{

    RestResult<?> findPositionPrivilegeList(Long positionId);

    RestResult<?> save(Long positionId, List<String> privilegeList);
}
