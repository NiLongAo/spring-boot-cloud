package cn.com.tzy.springbootactiviti.config.activiti.resolver;

import cn.com.tzy.springbootactiviti.config.candidate_parser.CandidateParser;
import cn.com.tzy.springbootactiviti.model.WorkflowContext;
import cn.com.tzy.springbootactiviti.model.impl.CandidateModelImpl;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务候选人解析器实现<br/>
 * 1.将候选人的字符串JSON转成候选人实体模型CandidateModel
 * 2.找到支持的维度翻译器，CandidateModel.getDimension()
 * 3.翻译
 *
 * @author yiuman
 * @date 2020/12/18
 */
@Component
@Log4j2
public class TaskCandidateResolverImpl implements TaskCandidateResolver {

    private final ObjectMapper objectMapper;

    /**
     * 候选人解析器实现集合
     */
    private final List<CandidateParser> candidateParsers;

    @Autowired(required = false)
    public TaskCandidateResolverImpl(@NonNull ObjectMapper objectMapper, @Nullable List<CandidateParser> candidateParsers) {
        this.objectMapper = objectMapper;
        this.candidateParsers = candidateParsers;
    }

    @Override
    public List<String> resolve(WorkflowContext workflowContext, List<Object> taskCandidateDefine) {
        List<String> realUserIds = new ArrayList<>();
        //获取当前处理用户
        String currentUserId = workflowContext.getCurrentUserId();
        taskCandidateDefine.parallelStream()
                .forEach(taskCandidate -> {
                    try {
                        //转化成候选人模型
                        CandidateModelImpl candidateModel = new CandidateModelImpl();
                        if(taskCandidate instanceof LinkedHashMap){
                            candidateModel = AppUtils.convertValue2(taskCandidate,CandidateModelImpl.class);
                        }else if(taskCandidate instanceof String){
                            candidateModel = objectMapper
                                    .readValue((String) taskCandidate, CandidateModelImpl.class);
                        }
                        //找到支持的解析器
                        CandidateModelImpl finalCandidateModel = candidateModel;
                        Optional<CandidateParser> candidateParser = candidateParsers.parallelStream()
                                .filter(parser -> parser.support(finalCandidateModel.getDimension()))
                                .findFirst();

                        //找到就进行解释,没找到直接加入
                        realUserIds.addAll(candidateParser.isPresent()
                                ? candidateParser.get().parse(workflowContext, candidateModel)
                                : (candidateModel
                                .getValues()
                                .stream()
                                .map(dimensionValue -> String.format("%s#%s", finalCandidateModel.getDimension(), dimensionValue))
                                .collect(Collectors.toList())));

                    } catch (JsonProcessingException exception) {
                        log.info(String.format("%s resolver exception:", getClass().getName()), exception);
                        realUserIds.add(String.valueOf(taskCandidate));
                    }
                });
        return realUserIds;
    }
}
