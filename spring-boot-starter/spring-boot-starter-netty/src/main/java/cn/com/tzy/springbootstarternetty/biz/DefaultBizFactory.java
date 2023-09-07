package cn.com.tzy.springbootstarternetty.biz;

import cn.com.tzy.springbootstarternetty.factory.BizFactory;
import cn.hutool.extra.spring.SpringUtil;

public class DefaultBizFactory implements BizFactory {

    @Override
    public Biz create(int msgCode) {
        return (Biz) SpringUtil.getBean(String.format("biz%s",String.format("%09d", msgCode)));
    }
}