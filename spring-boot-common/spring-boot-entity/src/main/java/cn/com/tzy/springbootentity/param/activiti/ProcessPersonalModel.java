package cn.com.tzy.springbootentity.param.activiti;

import java.io.Serializable;
import java.util.List;

/**
 * 流程人员模型，用于定义流程环节中的操作的当前处理人与下环节候选人或办理人
 *
 * @author yiuman
 * @date 2020/12/14
 */

public interface ProcessPersonalModel extends Serializable {

    /**
     * 当前的处理人ID
     *
     * @return 处理人主键标识
     */
    String getUserId();

    /**
     * 1.下一个环节的候选人ID，若是只有一个，则为处理人
     * 2.也可能为维度候选人模型的JSON字符串，
     * @return 候选人ID列表
     */
    List<String> getCandidateOrAssigned();
}