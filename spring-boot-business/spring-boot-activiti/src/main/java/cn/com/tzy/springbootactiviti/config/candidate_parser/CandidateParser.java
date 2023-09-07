package cn.com.tzy.springbootactiviti.config.candidate_parser;

import cn.com.tzy.springbootactiviti.exception.WorkflowException;
import cn.com.tzy.springbootactiviti.model.CandidateModel;
import cn.com.tzy.springbootactiviti.model.WorkflowContext;

import java.util.List;

/**
 * 候选人解释器
 *
 * @author yiuman
 * @date 2020/12/18
 */
public interface CandidateParser {

    /**
     * 支持哪个维护
     *
     * @param dimension 维度
     * @return true支持、false不支持
     */
    boolean support(String dimension);

    /**
     * 根据当前流程上下文与维度候选人模型获取具体的获选人
     *
     * @param workflowContext 流程上下文
     * @param candidateModel  维度候选人模型
     * @param <T>             维度候选人模型泛型
     * @return 候选人列表，如本框架下就是用户id
     * @throws WorkflowException 工作流程运行时异常
     */
    <T extends CandidateModel> List<String> parse(WorkflowContext workflowContext, T candidateModel) throws WorkflowException;
}