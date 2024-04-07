import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterfreeswitch.enums.FsTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.utils.FreeswitchUtils;
import cn.com.tzy.springbootstarterfreeswitch.vo.FreeswitchXmlVo;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@Log4j2
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FreeswitchUtils.class})
@SpringBootTest
public class ApplicationTests {


    /**
     * xml模板生成测试
     */
    @Test
    public void sofia_contact(){
        FreeswitchXmlVo vo = new FreeswitchXmlVo();
        vo.setFsTypeEnum(FsTypeEnum.SWITCH);
        vo.setModelMap(new NotNullMap(){{
            put(FsTypeEnum.SWITCH.getName(), Collections.singletonList(ConfigModel.builder().mysqlInTo("ip:3341").startRtpPort("1280").endRtpPort("2280").build()));
        }});
        String xmlConfig = FreeswitchUtils.getXmlConfig(vo);
        System.out.println(xmlConfig);
    }





}
