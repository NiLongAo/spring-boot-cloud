package cn.com.tzy.springbootactiviti.service.impl;

import cn.com.tzy.springbootactiviti.service.ActivitiService;
import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiServcieImplTest {

  @Autowired
  private ActivitiService activitiServcie;

  @Test
  public void getProcessEngine() {}

  @Test
  public void getTaskCandidateResolver() {}

  @Test
  public void deployProcessParameter() {}

  @Test
  public void deployProcess() {}

  @Test
  public void deleteProcess() {}

  @Test
  public void starProcess() {}

  @Test
  public void complete() {}

  @Test
  public void setCandidateOrAssigned() {}

  @Test
  public void claim() {}

  @Test
  public void jump() {}

  @Test
  public void suspend() {}

  @Test
  public void activate() {}

  @Test
  public void backProcess() {}

  @Test
  public void getFlowImgByInstanceId() {}

  @Test
  public void findUserNeedList() {

  }

  @Test
  public void findUserLaunchList() {}

  @Test
  public void findUserAlreadyList() {}

  @Test
  public void findNeedList() {
    PageModel pageModel = new PageModel();
    PageResult userNeedList = activitiServcie.findNeedList(pageModel);
  }

  @Test
  public void findAlreadyList() {}

  @Test
  public void findHistoricalInstanceIdList() {}

  @Test
  public void findNextTaskUser() {
    activitiServcie.findNextTaskUser("740ce1e8-92e5-11ec-b8f2-5254000c82bc", new HashMap<String, Object>() {
      {
        put("examineStatus",2);
      }
    });


  }
}