package cn.com.tzy.springbootactiviti.oa;

import cn.com.tzy.springbootactiviti.config.activiti.engine.WorkflowEngineGetterImpl;
import cn.com.tzy.springbootactiviti.config.init.SpringContextConfig;
import cn.com.tzy.springbootactiviti.utils.OAEnum;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.HashMap;

/**
 * 此类可进行处理结束事件等信息
 */
public class OaAbstractListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateTask) {
        WorkflowEngineGetterImpl bean = SpringContextConfig.getBean(WorkflowEngineGetterImpl.class);
        //前端传入的值
        String examineStatus = delegateTask.getParent().getVariableInstance("examineStatus").getTextValue();
        String[] split = delegateTask.getProcessDefinitionId().split(":");

        bean.getProcessEngine().getRuntimeService().setVariables(delegateTask.getId(), new HashMap<String, Object>(){{
            put("status",Integer.valueOf(examineStatus));
            put("statusName", ConstEnum.ReviewStateEnum.getName(Integer.valueOf(examineStatus)));
        }});
        ProcessInstance processInstance = bean.getProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
        Class<? extends OaInterface> clesses = OAEnum.get(split[0]);
        OaInterface updateStatus= (OaInterface) SpringContextConfig.getBean(clesses);
        updateStatus.updateStatus(processInstance.getBusinessKey(),Integer.valueOf(examineStatus));
    }

}
