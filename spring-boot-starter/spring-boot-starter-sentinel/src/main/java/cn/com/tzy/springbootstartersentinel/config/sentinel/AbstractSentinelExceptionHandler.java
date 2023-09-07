package cn.com.tzy.springbootstartersentinel.config.sentinel;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;

public abstract class AbstractSentinelExceptionHandler {
    public RestResult<?> handle(Throwable e) {
        RestResult<?> restResult = new RestResult();
        if (e instanceof FlowException) {
            restResult.setCode(RespCode.CODE_101.getValue());
            restResult.setMessage(RespCode.CODE_101.getName());
        } else if (e instanceof DegradeException) {
            restResult.setCode(RespCode.CODE_102.getValue());
            restResult.setMessage(RespCode.CODE_102.getName());
        } else if (e instanceof ParamFlowException) {
            restResult.setCode(RespCode.CODE_103.getValue());
            restResult.setMessage(RespCode.CODE_103.getName());
        } else if (e instanceof SystemBlockException) {
            restResult.setCode(RespCode.CODE_104.getValue());
            restResult.setMessage(RespCode.CODE_104.getName());
        } else if (e instanceof AuthorityException) {
            restResult.setCode(RespCode.CODE_105.getValue());
            restResult.setMessage(RespCode.CODE_105.getName());
        }
        return restResult;
    }

}
