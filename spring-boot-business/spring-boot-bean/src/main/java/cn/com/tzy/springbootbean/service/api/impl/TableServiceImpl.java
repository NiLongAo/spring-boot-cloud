package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.TableMapper;
import cn.com.tzy.springbootbean.service.api.TableService;
import cn.com.tzy.springbootcomm.common.model.DictModel;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableMapper tableMapper;

    @Override
    public RestResult<?> selectDiceData(String table, String text, Integer code) {
        List<DictModel> dictModels = tableMapper.selectDiceData(table, text, code);
        return RestResult.result(RespCode.CODE_0.getValue(),null,dictModels);
    }
}
