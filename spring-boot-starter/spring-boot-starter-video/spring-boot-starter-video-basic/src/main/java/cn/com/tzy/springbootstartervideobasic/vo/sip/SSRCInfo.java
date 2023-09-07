package cn.com.tzy.springbootstartervideobasic.vo.sip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SSRCInfo {

    private int port;
    private String ssrc;
    private String stream;

}
