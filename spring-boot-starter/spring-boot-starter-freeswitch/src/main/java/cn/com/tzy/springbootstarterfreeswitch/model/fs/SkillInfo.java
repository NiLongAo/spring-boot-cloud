package cn.com.tzy.springbootstarterfreeswitch.model.fs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


/**
 * 技能
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SkillInfo implements Serializable {
    /**
     * PK
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;
}
