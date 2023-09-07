package cn.com.tzy.springbootstartersmsbasic.demo;


public interface SendModel {

    public Integer getType();

    public void setType(Integer type);

    public String getMobile();

    public void setMobile(String mobile);

    public void setTenantId(Long tenantId);

    public Long getTenantId();
}
