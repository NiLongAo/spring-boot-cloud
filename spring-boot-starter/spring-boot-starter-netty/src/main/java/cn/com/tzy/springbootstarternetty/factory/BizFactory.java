package cn.com.tzy.springbootstarternetty.factory;


import cn.com.tzy.springbootstarternetty.biz.Biz;

public interface BizFactory {
    public Biz create(int msgCode);
}