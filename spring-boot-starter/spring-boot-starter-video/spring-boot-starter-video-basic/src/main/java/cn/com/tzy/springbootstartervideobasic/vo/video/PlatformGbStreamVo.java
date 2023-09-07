package cn.com.tzy.springbootstartervideobasic.vo.video;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
    * 国标级联关联直播流
    */
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlatformGbStreamVo extends LongIdEntity {
    /**
     * 平台ID
     */
    private String platformId;

    /**
     * 目录ID
     */
    private String catalogId;

    /**
     * 直播流ID
     */
    private Long gbStreamId;
}