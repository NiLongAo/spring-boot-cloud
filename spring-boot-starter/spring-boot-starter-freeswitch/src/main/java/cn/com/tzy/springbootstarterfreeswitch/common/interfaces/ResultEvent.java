package cn.com.tzy.springbootstarterfreeswitch.common.interfaces;


import cn.com.tzy.springbootcomm.common.vo.RestResult;

@FunctionalInterface
public interface ResultEvent {
    void result(RestResult result);
}
