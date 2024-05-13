package cn.com.tzy.springbootstarterfreeswitch.vo.sip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String agentCode;

    private String ip;

    private int port;

}
