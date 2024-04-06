import link.thingscloud.freeswitch.esl.InboundClient;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {


    @Resource
    private InboundClient inboundClient;

    @Test
    public void sofia_contact(){
//        inboundClient.sendSyncApiCommand("/1.15.9.228:8021",,)



    }





}
