package cn.com.tzy.springbootbean.service.es.impl;

import cn.com.tzy.springbootbean.SpringBootBeanApplicationTests;
import cn.com.tzy.springbootbean.service.es.AreaElasticsearchService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AreaElasticsearchServiceImplTest extends SpringBootBeanApplicationTests {

  @Autowired
  AreaElasticsearchService areaElasticsearchService;

   @Test
   public void findAll() {
     RestResult<?> all = areaElasticsearchService.findAll();
     System.out.println(all);
   }
}