package cn.com.tzy.springbootstartersmsbasic.demo;

/**
 * 短信配置模板
 */
public interface MessageTemplate {

    public Integer getId();

    public void setId(Integer id);

    public Integer getConfigId();

    public void setConfigId(Integer configId);

    public Integer getType();

    public void setType(Integer type);

    public String getTitle() ;

    public void setTitle(String title);

    public String getContent();

    public void setContent(String content);

    public String getReceiver();

    public void setReceiver(String receiver);

    public String getVariable();

    public void setVariable(String variable);

    public String getCode();

    public void setCode(String code);

}
