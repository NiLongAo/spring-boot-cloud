package cn.com.tzy.springbootactiviti.config.activiti.engine;

import cn.com.tzy.springbootactiviti.config.init.SpringContextConfig;
import org.activiti.engine.ProcessEngine;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 默认的流程引擎获取方式
 *
 * @author yiuman
 * @date 2020/12/17
 */
@Component
public class WorkflowEngineGetterImpl implements WorkflowEngineGetter {

    private ProcessEngine processEngine;

    public WorkflowEngineGetterImpl() {
    }

    @Override
    public ProcessEngine getProcessEngine() {
        return processEngine = Optional.ofNullable(this.processEngine)
                .orElse(SpringContextConfig.getBean(ProcessEngine.class));
    }
}
