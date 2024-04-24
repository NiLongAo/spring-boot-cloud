package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlaybackInfo  implements Serializable {

    /**
     * PK
     */
    private String id;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 放音文件
     */
    private String playback;

    /**
     * 1:待审核,2:审核通过
     */
    private Integer status;
}
