package cn.com.tzy.springbootentity.common.info;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 缓冲数据格式
 */
@Getter
@Setter
public class AreaInfo implements Serializable {
    private Integer value;
    private String label;
    private List<AreaInfo> children;
}
