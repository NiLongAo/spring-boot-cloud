package cn.com.tzy.springbootstarternacos.config.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡 随机选取服务
 */
@Log4j2
public class MyNacosRule extends AbstractLoadBalancerRule {

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

    @Override
    public Server choose(Object key) {
        //自主实现负载均衡
        log.info("测试是否走负载均衡");
        ILoadBalancer lb = getLoadBalancer();
        if(lb == null){
            return null;
        }
        Server server = null;
        while (server==null){
            if(Thread.interrupted()){
                return null;
            }
            List<Server> reachableServers = lb.getReachableServers();
            List<Server> allServers = lb.getAllServers();
            int size = allServers.size();
            if(size== 0){
                return null;
            }
            int index = new Random().nextInt(size);
            server = reachableServers.get(index);
            if(server==null){
                Thread.yield();
                continue;
            }
            if(server.isAlive()){
                return server;
            }
            server= null;
            Thread.yield();
        }
        return server;

    }
}
