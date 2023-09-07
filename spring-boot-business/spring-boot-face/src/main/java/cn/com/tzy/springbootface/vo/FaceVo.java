package cn.com.tzy.springbootface.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 人脸检测信息
 */
@Data
@Builder
public class FaceVo {
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 左眼状态 0闭眼 1睁眼 2非眼部区域 3 未知状态
     */
    private Integer  leftEyeState;
    /**
     * 右眼状态 0闭眼 1睁眼 2非眼部区域 3 未知状态
     */
    private Integer  rightEyeState;
    /**
     * 0.未知 1.男 2.女
     */
    private Integer  gender;
    /**
     * 是否带口罩 0.未带口罩 1.戴了口罩
     */
    private Integer  maskStatus;

}
