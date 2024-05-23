package cn.com.tzy.springbootfs.controller.api.fs;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.FsTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.GateWayModel;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import cn.com.tzy.springbootstarterfreeswitch.utils.FreeswitchUtils;
import cn.com.tzy.springbootstarterfreeswitch.vo.fs.FreeswitchXmlVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 人脸图片识别相关接口
 */
@Log4j2
@RestController("ApiFsXmlController")
@RequestMapping(value = "/api/fs/xml")
public class FsXmlController extends ApiController {

    @Resource
    private AgentService agentService;

    /**
     * 获取拨号计划的xml信息
     */

    @PostMapping( "/find_dialplan_xml")
    public String findDialplanXml(HttpServletRequest req){
        Map<String, String[]> parameterMap = req.getParameterMap();
        String xml = null;
        xml = FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                .fsTypeEnum(FsTypeEnum.DIALPLAN)
                .build()
        );
        log.warn("请求Fs拨号计划文件： 是否配置Xml：{}，请求参数：{}", StringUtils.isNotBlank(xml),parameterMap);
        return xml;
    }

    /**
     * 获取呼叫账户的xml信息
     */
    @PostMapping( "/find_directory_xml")
    public String getDirectoryXML(HttpServletRequest req){
        Map<String, String[]> parameterMap = req.getParameterMap();
        String domain = req.getParameter("domain");
        String user = req.getParameter("user");
        String key = req.getParameter("key");
        String xml = null;
        //模拟数据
        UserModel userModel = agentService.findUserModel(user);
        if(userModel == null){
            xml =  FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                    .fsTypeEnum(FsTypeEnum.NOT_FIND)
                    .modelMap(new HashMap<String, Object>(){{
                        put("type","directory" );
                    }})
                    .build()
            );
        }else {
            userModel.setDomain(domain);
            xml =  FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                    .fsTypeEnum(FsTypeEnum.USER)
                    .modelMap(new HashMap<String, Object>(){{
                        put(FsTypeEnum.USER.getName(), userModel);
                    }})
                    .build()
            );
        }
        log.warn("请求Fs呼叫用户文件key：{} user:{} 是否配置Xml：{}，请求参数：{}",key,user, StringUtils.isNotBlank(xml),parameterMap);
        return xml;
    }

    /**
     * 获取配置项的xml信息
     */
    @PostMapping("find_configuration_xml")
    public String findConfigurationXml(HttpServletRequest req) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        String key = req.getParameter("key_value");
        String xml = null;
        switch (key){
            case "sofia.conf":
                if("internal".equals(req.getParameter("profile"))){
                     xml = FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                        .fsTypeEnum(FsTypeEnum.INTERNAL)
                        .modelMap(new HashMap<String, Object>(){{
                            put(FsTypeEnum.SWITCH.getName(), ConfigModel.builder()
                                    .iceStart(ConstEnum.Flag.YES.getValue())
                                    .stunAddress("autonat:192.168.1.26")
                                    .build());
                        }})
                        .build()
                    );
                     break;
                }else if("external".equals(req.getParameter("profile"))){
                    xml = FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                        .fsTypeEnum(FsTypeEnum.EXTERNAL)
                        .modelMap(new HashMap<String, Object>(){{
                            put(FsTypeEnum.SWITCH.getName(), ConfigModel.builder()
                                    .iceStart(ConstEnum.Flag.YES.getValue())
                                    .stunAddress("autonat:192.168.1.26")
                                    .build());
                            put(FsTypeEnum.EXTERNAL.getName(),GateWayModel.builder().build());
                        }})
                        .build()
                    );
                    break;
                }else if("internal-ipv6".equals(req.getParameter("profile"))){
                    xml = FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                        .fsTypeEnum(FsTypeEnum.INTERNAL_IPV6)
                        .modelMap(new HashMap<String, Object>(){{
                            put(FsTypeEnum.SWITCH.getName(), ConfigModel.builder().build());
                        }})
                        .build()
                    );
                    break;
                }else if("external-ipv6".equals(req.getParameter("profile"))){
                    xml = FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                        .fsTypeEnum(FsTypeEnum.INTERNAL_IPV6)
                        .modelMap(new HashMap<String, Object>(){{
                            put(FsTypeEnum.SWITCH.getName(), ConfigModel.builder().build());
                        }})
                        .build()
                    );
                    break;
                }
            case "switch.conf": {
                xml = FreeswitchUtils.getXmlConfig(FreeswitchXmlVo.builder()
                    .fsTypeEnum(FsTypeEnum.SWITCH)
                    .modelMap(new HashMap<String, Object>(){{
                        put(FsTypeEnum.SWITCH.getName(), ConfigModel.builder()
                                .startRtpPort("16384")
                                .endRtpPort("16484")
                                .build());
                    }})
                    .build()
                );
                break;
            }
        }
        log.warn("请求Fs配置文件：{}，是否配置Xml：{}，请求参数：{}",key, StringUtils.isNotBlank(xml),parameterMap);
        return xml;
    }


}
