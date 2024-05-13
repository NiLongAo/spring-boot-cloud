package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.IvrWorkflowMapper;
import cn.com.tzy.springbootentity.dome.fs.IvrWorkflow;
import cn.com.tzy.springbootfs.service.fs.IvrWorkflowService;
@Service
public class IvrWorkflowServiceImpl extends ServiceImpl<IvrWorkflowMapper, IvrWorkflow> implements IvrWorkflowService{

}
