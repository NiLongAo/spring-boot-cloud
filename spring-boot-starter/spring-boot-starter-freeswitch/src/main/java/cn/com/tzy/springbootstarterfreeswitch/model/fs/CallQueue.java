package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import cn.com.tzy.springbootcomm.constant.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CallQueue implements Comparable<CallQueue>{

    private Long priority;

    private String callId;
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date startTime;

    private String groupId;

    private GroupOverFlowInfo groupOverflowInfo;

    private String deviceId;

    private boolean play;

    @Override
    public int compareTo(CallQueue o) {
        return priority.compareTo(this.priority);
    }
}
