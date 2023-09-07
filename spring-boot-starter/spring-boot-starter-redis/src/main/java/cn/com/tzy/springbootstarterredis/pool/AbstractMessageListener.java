package cn.com.tzy.springbootstarterredis.pool;

import org.springframework.data.redis.connection.MessageListener;


public abstract class AbstractMessageListener implements MessageListener {

    private final String patternTopicName;

    public String getPatternTopicName() {
        return patternTopicName;
    }

    public AbstractMessageListener(String patternTopicName){
        this.patternTopicName = patternTopicName;
    }
}
