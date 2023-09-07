package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Privilege;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PrivilegeService extends IService<Privilege> {


    RestResult<?> save(String id, String menuId, String privilegeName, String requestUrl, String memo);

    boolean init();
}
