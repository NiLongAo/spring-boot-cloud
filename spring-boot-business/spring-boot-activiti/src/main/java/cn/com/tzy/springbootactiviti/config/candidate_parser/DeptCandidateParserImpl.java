package cn.com.tzy.springbootactiviti.config.candidate_parser;
import cn.com.tzy.springbootactiviti.config.WorkflowDimension;
import cn.com.tzy.springbootactiviti.model.CandidateModel;
import cn.com.tzy.springbootactiviti.model.WorkflowContext;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootcomm.excption.RespException;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门候选人解析器，返回定义部门内的所有用户
 *
 * @author yiuman
 * @date 2020/12/28
 */
@Component
public class DeptCandidateParserImpl implements CandidateParser {

    @Autowired
    UserServiceFeign userServiceFeign;

    @Override
    public boolean support(String dimension) {
        return WorkflowDimension.DEPT.equals(dimension);
    }

    @Override
    public <T extends CandidateModel> List<String> parse(WorkflowContext workflowContext, T candidateModel) {
        List<Long> deptIds = candidateModel.getValues().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        RestResult<?> restResult = userServiceFeign.findDepartmentIdList(deptIds);
        if(restResult.getCode()!= RespCode.CODE_0.getValue()) {
            throw new RespException(restResult.getMessage());
        }
        List<User> userList = AppUtils.convertValue2(restResult.getData(),new TypeReference<List<User>>(){});
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userList.stream().map(User::getId).map(String::valueOf).collect(Collectors.toList());
    }
}
