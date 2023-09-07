package cn.com.tzy.springbootstartervideobasic.vo.sip;


import lombok.Data;

/**
 * 摄像机同步状态
 * @author lin
 */
@Data
public class SyncStatus {
    /**
     * 总数
     */
    private int total;
    /**
     * 当前更新多少
     */
    private int current;
    /**
     * 错误描述
     */
    private String errorMsg;
    /**
     * 是否同步中
     */
    private boolean syncIng;
}
