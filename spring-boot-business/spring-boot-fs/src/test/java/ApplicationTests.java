import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.FsTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.utils.FreeswitchUtils;
import cn.com.tzy.springbootstarterfreeswitch.vo.fs.FreeswitchXmlVo;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        vo.setFsTypeEnum(FsTypeEnum.INTERNAL);
        vo.setModelMap(new NotNullMap(){{
            put(FsTypeEnum.SWITCH.getName(), ConfigModel.builder().build());
        }});
        String xmlConfig = FreeswitchUtils.getXmlConfig(vo);
        System.out.println(xmlConfig);
    }

    public static void main(String[] args) {
        List<String> stringList = Arrays.asList("ban",  "bana","date","banana",  "ba");
        String target = "bananaberry";

        Optional<String> longestMatch = findLongestContainingString(stringList, target);
        longestMatch.ifPresent(System.out::println);
    }

    public static Optional<String> findLongestContainingString(List<String> stringList, String target) {
        return stringList.stream()
                .filter(target::contains)
                .max(Comparator.comparingInt(String::length));
    }

}
