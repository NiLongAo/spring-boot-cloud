package cn.com.tzy.springbootactiviti.model.impl;

import cn.com.tzy.springbootactiviti.model.CandidateModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 候选人模型的实现
 *
 * @author yiuman
 * @date 2020/12/18
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CandidateModelImpl implements CandidateModel {

    //类型
    private String dimension;
    //类型id
    private List<Long> values;

}
