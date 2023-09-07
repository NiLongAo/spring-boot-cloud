package cn.com.tzy.springbootsso.config.init;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignbean.api.bean.PrivilegeServiceFeign;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 容器启动时加载权限信息保存redis缓冲
 */
@Component
@Log4j2
public class PrivilegeInit implements CommandLineRunner {

    @Autowired
    private PrivilegeServiceFeign privilegeServiceFeign;

    @Override
    public void run(String... args) throws Exception {
        while (!allPrivilegeInit()){
            Thread.sleep(10000);//休眠10秒继续执行
        }
    }

    private boolean allPrivilegeInit(){
        boolean flat = false;
        try {
            RestResult<?> restResult = privilegeServiceFeign.init();
            if(restResult.getCode() == RespCode.CODE_0.getValue()){
                flat =true;
            }
        }catch (Exception e){
            flat = false;
        }
        return flat;
    }
}
