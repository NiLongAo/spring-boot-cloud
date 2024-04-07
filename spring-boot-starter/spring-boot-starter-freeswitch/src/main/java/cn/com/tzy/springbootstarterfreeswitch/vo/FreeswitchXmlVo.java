package cn.com.tzy.springbootstarterfreeswitch.vo;

import cn.com.tzy.springbootstarterfreeswitch.enums.FsTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.BeanModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用于组装xml时所需要参数
 */
@Data
public class FreeswitchXmlVo {
    /**
     * 获取xml类型
     */
    private FsTypeEnum fsTypeEnum;
    /**
     * xml模板所需要的参数
     */
    Map<String, List<BeanModel>> modelMap;

}
