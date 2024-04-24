package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import cn.com.tzy.springbootstarterfreeswitch.enums.AgentStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentPreset implements Serializable {

    /**
     * 1:当前生效 2:永久生效
     */
    @NotNull(message = "预设类型不能为空")
    @Range(min = 1 , max = 2, message = "预设字段错误")
    private Integer type;


    @NotNull(message = "预设状态不能为空")
    private AgentStateEnum agentStateEnum;
}
