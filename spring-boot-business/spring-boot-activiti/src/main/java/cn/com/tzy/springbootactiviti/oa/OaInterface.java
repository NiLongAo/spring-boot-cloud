package cn.com.tzy.springbootactiviti.oa;

import java.util.Map;

/**
 * 工作流基本信息通用实现
 */
public interface OaInterface {

    Map<String, Object> findObject(String id);

    void updateStatus(String id,Integer status);

    void deleteId(String id);
}
