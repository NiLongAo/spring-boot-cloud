package cn.com.tzy.springbootentity.vo.fs.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDetailVo {

    private Long id;
    private String localIp;
    private String remoteIp;
    private Integer internalPort;
    private Integer externalPort;
    private Integer startRtpPort;
    private Integer endRtpPort;
    private Integer wsPort;
    private Integer wssPort;
    private String audioCode;
    private String videoCode;
    private String frameRate;
    private String bitRate;
    private Integer iceStart;
    private String stunAddress;
    private String name;
    private Integer enable;
    private Integer status;
    private Integer audioRecord;
    private Integer videoRecord;
    private String audioRecordPath;
    private String videoRecordPath;
    private String soundRilePath;
    private String freeswitchPath;
    private String freeswitchLogPath;
}
