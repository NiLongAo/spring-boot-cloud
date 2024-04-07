package cn.com.tzy.springbootstarterfreeswitch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 呼叫账户管理对象
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserModel extends BeanModel{
    /**  */
    private String id;

    /**  */
    private String name;

    /**  */
    private String number;

    /**  */
    private String domain;

    /**  */
    private String password;

    /**  */
    private Integer type;

    /** 录音（0正常 1停用） */
    private String audio;

    /** 录像（0正常 1停用） */
    private String video;

    /**  */
    private Integer level;
}
