package cn.com.tzy.springbootstarterstreamrabbitmq.listenter;

public abstract class AbstractMessageListener<T> implements MessageListener<T>{

    private final String exchangeName;
    private final String routingKey;
    private final String queueName;

    public AbstractMessageListener(String exchangeName,String routingKey,String queueName){
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
    public String getKey(){
        return String.format("%s_%s_%s",exchangeName,routingKey,queueName);
    }


}
