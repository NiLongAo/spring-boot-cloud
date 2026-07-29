package cn.com.tzy.springbootentity.param.fs.system;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("FreeSWITCH??????")
public class PlatformSaveParam {

    private Long id;

    @NotBlank(message = "??IP????")
    private String localIp;

    private String remoteIp;

    @Min(value = 1, message = "????????1")
    @Max(value = 65535, message = "????????65535")
    private Integer internalPort;

    @Min(value = 1, message = "????????1")
    @Max(value = 65535, message = "????????65535")
    private Integer externalPort;

    @NotNull(message = "RTP????????")
    @Min(value = 1, message = "RTP????????1")
    @Max(value = 65535, message = "RTP????????65535")
    private Integer startRtpPort;

    @NotNull(message = "RTP????????")
    @Min(value = 1, message = "RTP????????1")
    @Max(value = 65535, message = "RTP????????65535")
    private Integer endRtpPort;

    @Min(value = 1, message = "WS??????1")
    @Max(value = 65535, message = "WS??????65535")
    private Integer wsPort;

    @Min(value = 1, message = "WSS??????1")
    @Max(value = 65535, message = "WSS??????65535")
    private Integer wssPort;

    private String audioCode;
    private String videoCode;
    private String frameRate;
    private String bitRate;

    @Min(value = 0, message = "ICE??????0")
    @Max(value = 1, message = "ICE??????1")
    private Integer iceStart;

    private String stunAddress;

    @NotBlank(message = "??????")
    private String name;

    @Min(value = 0, message = "????????0")
    @Max(value = 1, message = "????????1")
    private Integer enable;

    @Min(value = 0, message = "??????????0")
    @Max(value = 1, message = "??????????1")
    private Integer audioRecord;

    @Min(value = 0, message = "??????????0")
    @Max(value = 1, message = "??????????1")
    private Integer videoRecord;

    private String audioRecordPath;
    private String videoRecordPath;
    private String soundRilePath;
    private String freeswitchPath;
    private String freeswitchLogPath;
}
