package cn.com.tzy.springbootfs.service.fs.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootfs.mapper.fs.VdnDtmfMapper;
import cn.com.tzy.springbootentity.dome.fs.VdnDtmf;
import cn.com.tzy.springbootfs.service.fs.VdnDtmfService;
@Service
public class VdnDtmfServiceImpl extends ServiceImpl<VdnDtmfMapper, VdnDtmf> implements VdnDtmfService{

}
