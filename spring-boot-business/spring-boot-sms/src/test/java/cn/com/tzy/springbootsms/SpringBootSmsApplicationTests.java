package cn.com.tzy.springbootsms;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootSmsApplicationTests {



    @Test
    public void test() {
        QRData build = QRData.builder()
                .code(QRData.Code.SUCCESS.getValue())
                .message(QRData.Code.SUCCESS.getName())
                .build();
        NotNullMap data = new NotNullMap();
        data.put("data",build);
        //登录成功后存储
        String key = String.format("%s%s", WxMiniConstant.WX_MINI_LOGIN_SCENE, "111111111111111");
        RedisUtils.set(key,data,60*3);
        NotNullMap map = BeanUtil.toBean(RedisUtils.get(key), NotNullMap.class);
        String mini_scene = MapUtil.getStr(map, "mini_scene");
        QRData data1 = MapUtil.get(map, "data", QRData.class);
        RedisUtils.del(key);
        System.out.println(mini_scene);
        System.out.println(data1);

    }

}
