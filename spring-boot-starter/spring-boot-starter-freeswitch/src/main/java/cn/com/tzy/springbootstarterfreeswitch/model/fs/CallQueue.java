package cn.com.tzy.springbootstarterfreeswitch.model.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CallQueue implements Comparable<CallQueue>{

    private Long priority;

    private String callId;

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
