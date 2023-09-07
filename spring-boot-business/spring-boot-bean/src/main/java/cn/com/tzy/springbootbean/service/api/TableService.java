package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.model.DictModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;

import java.util.List;

public interface TableService {
    RestResult<?> selectDiceData(String table, String text, Integer code);
}
