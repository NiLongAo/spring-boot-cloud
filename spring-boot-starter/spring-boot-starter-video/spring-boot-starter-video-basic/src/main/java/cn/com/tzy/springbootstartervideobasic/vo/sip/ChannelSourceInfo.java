package cn.com.tzy.springbootstartervideobasic.vo.sip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSourceInfo {
    private String name;
    private int count;
}
