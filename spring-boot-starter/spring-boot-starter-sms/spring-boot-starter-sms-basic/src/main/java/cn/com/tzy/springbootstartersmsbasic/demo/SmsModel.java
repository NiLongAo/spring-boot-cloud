package cn.com.tzy.springbootstartersmsbasic.demo;


import java.util.Date;

/**
 * 短信配置信息
 */
public interface SmsModel {

    public  Integer getId() ;

    public  void setId(Integer id);

    public  Integer getSmsType();

    public  void setSmsType(Integer smsType);

    public  String getConfigName();

    public  void setConfigName(String configName);

    public  String getAccount();

    public  void setAccount(String account);

    public  String getPassword();

    public  void setPassword(String password);

    public  String getBalance();

    public  void setBalance(String balance);

    public  Integer getIsActive();

    public  void setIsActive(Integer isActive);

    public  String getSign();

    public  void setSign(String sign);

    public  Integer getSignPlace();

    public  void setSignPlace(Integer signPlace);

    public  Date getUpdateTime();

    public  void setUpdateTime(Date updateTime);

}
