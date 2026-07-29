package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootentity.param.fs.system.PlatformPageParam;
import cn.com.tzy.springbootentity.param.fs.system.PlatformSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.PlatformDetailVo;
import cn.com.tzy.springbootfs.convert.manage.system.PlatformManageConvert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformManageConvertTest {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    @AfterAll
    static void closeValidatorFactory() {
        VALIDATOR_FACTORY.close();
    }

    @Test
    void pageParamExtendsPageModelAndCarriesStatuses() {
        PlatformPageParam param = PlatformPageParam.builder().enable(1).status(0).build();
        assertTrue(param instanceof PageModel);
        assertEquals(Integer.valueOf(1), param.getEnable());
        assertEquals(Integer.valueOf(0), param.getStatus());
        // @SuperBuilder does not apply field initializers; defaults come from the no-args constructor
        PlatformPageParam defaults = new PlatformPageParam();
        assertEquals(Integer.valueOf(1), defaults.getPageNumber());
        assertEquals(Integer.valueOf(10), defaults.getPageSize());
    }

    @Test
    void saveParamMapsEditableFieldsWithoutRuntimeStatus() {
        Platform entity = PlatformManageConvert.INSTANCE.convert(completeSaveParam());
        assertNotNull(entity);
        assertEquals(Long.valueOf(11L), entity.getId());
        assertEquals("10.0.0.1", entity.getLocalIp());
        assertEquals("203.0.113.1", entity.getRemoteIp());
        assertEquals(Integer.valueOf(5060), entity.getInternalPort());
        assertEquals(Integer.valueOf(15060), entity.getExternalPort());
        assertEquals(Integer.valueOf(10000), entity.getStartRtpPort());
        assertEquals(Integer.valueOf(20000), entity.getEndRtpPort());
        assertEquals(Integer.valueOf(8080), entity.getWsPort());
        assertEquals(Integer.valueOf(8443), entity.getWssPort());
        assertEquals("PCMU,PCMA", entity.getAudioCode());
        assertEquals("H264", entity.getVideoCode());
        assertEquals("25", entity.getFrameRate());
        assertEquals("2048", entity.getBitRate());
        assertEquals(Integer.valueOf(1), entity.getIceStart());
        assertEquals("stun.example.com:3478", entity.getStunAddress());
        assertEquals("primary", entity.getName());
        assertEquals(Integer.valueOf(1), entity.getEnable());
        assertEquals(Integer.valueOf(1), entity.getAudioRecord());
        assertEquals(Integer.valueOf(0), entity.getVideoRecord());
        assertEquals("/record/audio", entity.getAudioRecordPath());
        assertEquals("/record/video", entity.getVideoRecordPath());
        assertEquals("/sounds", entity.getSoundRilePath());
        assertEquals("/opt/freeswitch", entity.getFreeswitchPath());
        assertEquals("/var/log/freeswitch", entity.getFreeswitchLogPath());
        assertNull(entity.getStatus());
    }

    @Test
    void entityMapsAllBusinessFieldsAndExcludesAuditFields() {
        PlatformDetailVo detail = PlatformManageConvert.INSTANCE.convert(completePlatform());
        assertNotNull(detail);
        assertEquals(Long.valueOf(22L), detail.getId());
        assertEquals("10.0.0.2", detail.getLocalIp());
        assertEquals("203.0.113.2", detail.getRemoteIp());
        assertEquals(Integer.valueOf(5070), detail.getInternalPort());
        assertEquals(Integer.valueOf(15070), detail.getExternalPort());
        assertEquals(Integer.valueOf(21000), detail.getStartRtpPort());
        assertEquals(Integer.valueOf(22000), detail.getEndRtpPort());
        assertEquals(Integer.valueOf(9080), detail.getWsPort());
        assertEquals(Integer.valueOf(9443), detail.getWssPort());
        assertEquals("OPUS", detail.getAudioCode());
        assertEquals("VP8", detail.getVideoCode());
        assertEquals("30", detail.getFrameRate());
        assertEquals("4096", detail.getBitRate());
        assertEquals(Integer.valueOf(0), detail.getIceStart());
        assertEquals("stun2.example.com:3478", detail.getStunAddress());
        assertEquals("secondary", detail.getName());
        assertEquals(Integer.valueOf(0), detail.getEnable());
        assertEquals(Integer.valueOf(1), detail.getStatus());
        assertEquals(Integer.valueOf(0), detail.getAudioRecord());
        assertEquals(Integer.valueOf(1), detail.getVideoRecord());
        assertEquals("/data/audio", detail.getAudioRecordPath());
        assertEquals("/data/video", detail.getVideoRecordPath());
        assertEquals("/data/sounds", detail.getSoundRilePath());
        assertEquals("/srv/freeswitch", detail.getFreeswitchPath());
        assertEquals("/srv/log/freeswitch", detail.getFreeswitchLogPath());
        Set<String> fields = declaredFieldNames(PlatformDetailVo.class);
        assertFalse(fields.contains("createUserId"));
        assertFalse(fields.contains("createTime"));
        assertFalse(fields.contains("updateUserId"));
        assertFalse(fields.contains("updateTime"));
    }

    @Test
    void convertOptionsMapsIdentityDisplayCodeAndStatus() {
        Platform first = Platform.builder().id(101L).name("first").localIp("10.10.0.1").status(1).build();
        Platform second = Platform.builder().id(202L).name("second").localIp("10.10.0.2").status(0).build();
        List<FsOptionVo> options = PlatformManageConvert.INSTANCE.convertOptions(Arrays.asList(first, second));
        assertEquals(2, options.size());
        assertEquals("101", options.get(0).getId());
        assertEquals("first", options.get(0).getName());
        assertEquals("10.10.0.1", options.get(0).getCode());
        assertEquals(Integer.valueOf(1), options.get(0).getStatus());
        assertEquals("202", options.get(1).getId());
        assertEquals("second", options.get(1).getName());
        assertEquals("10.10.0.2", options.get(1).getCode());
        assertEquals(Integer.valueOf(0), options.get(1).getStatus());
    }

    @Test
    void convertOptionsKeepsMapStructNullAndEmptyListBehavior() {
        assertNull(PlatformManageConvert.INSTANCE.convertOptions(null));
        assertTrue(PlatformManageConvert.INSTANCE.convertOptions(Collections.emptyList()).isEmpty());
    }
    @Test
    void saveParamValidationCoversRequiredFieldsPortsAndFlags() {
        PlatformSaveParam param = PlatformSaveParam.builder()
                .localIp(" ").name("")
                .internalPort(0).externalPort(65536)
                .startRtpPort(null).endRtpPort(0)
                .wsPort(0).wssPort(65536)
                .iceStart(-1).enable(2).audioRecord(-1).videoRecord(2)
                .build();

        Set<String> paths = VALIDATOR.validate(param).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                "localIp", "name", "internalPort", "externalPort", "startRtpPort", "endRtpPort",
                "wsPort", "wssPort", "iceStart", "enable", "audioRecord", "videoRecord"
        )), paths);
    }

    @Test
    void optionalPortsAndFlagsMayBeNull() {
        PlatformSaveParam param = PlatformSaveParam.builder()
                .localIp("10.0.0.1").name("primary")
                .startRtpPort(10000).endRtpPort(20000)
                .build();
        assertFalse(VALIDATOR.validate(param).iterator().hasNext());
    }

    private static PlatformSaveParam completeSaveParam() {
        return PlatformSaveParam.builder()
                .id(11L).localIp("10.0.0.1").remoteIp("203.0.113.1")
                .internalPort(5060).externalPort(15060).startRtpPort(10000).endRtpPort(20000)
                .wsPort(8080).wssPort(8443).audioCode("PCMU,PCMA").videoCode("H264")
                .frameRate("25").bitRate("2048").iceStart(1).stunAddress("stun.example.com:3478")
                .name("primary").enable(1).audioRecord(1).videoRecord(0)
                .audioRecordPath("/record/audio").videoRecordPath("/record/video")
                .soundRilePath("/sounds").freeswitchPath("/opt/freeswitch")
                .freeswitchLogPath("/var/log/freeswitch").build();
    }

    private static Platform completePlatform() {
        return Platform.builder()
                .id(22L).localIp("10.0.0.2").remoteIp("203.0.113.2")
                .internalPort(5070).externalPort(15070).startRtpPort(21000).endRtpPort(22000)
                .wsPort(9080).wssPort(9443).audioCode("OPUS").videoCode("VP8")
                .frameRate("30").bitRate("4096").iceStart(0).stunAddress("stun2.example.com:3478")
                .name("secondary").enable(0).status(1).audioRecord(0).videoRecord(1)
                .audioRecordPath("/data/audio").videoRecordPath("/data/video")
                .soundRilePath("/data/sounds").freeswitchPath("/srv/freeswitch")
                .freeswitchLogPath("/srv/log/freeswitch").build();
    }

    private static Set<String> declaredFieldNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
    }

    private static Set<String> expectedDetailFields() {
        return new HashSet<>(Arrays.asList(
                "id", "localIp", "remoteIp", "internalPort", "externalPort", "startRtpPort", "endRtpPort",
                "wsPort", "wssPort", "audioCode", "videoCode", "frameRate", "bitRate", "iceStart",
                "stunAddress", "name", "enable", "status", "audioRecord", "videoRecord", "audioRecordPath",
                "videoRecordPath", "soundRilePath", "freeswitchPath", "freeswitchLogPath"
        ));
    }
}
