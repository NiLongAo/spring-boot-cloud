package cn.com.tzy.springbootface.config;

import cn.com.tzy.springbootentity.dome.face.Person;
import cn.com.tzy.springbootface.service.PersonService;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 人脸特征向量初始化加载程序
 */
@Log4j2
@Component
public class FaceFeaturesVectorInit implements CommandLineRunner {

    @Autowired
    private PersonService personService;


    /**
     * 初始化全局图片特征向量
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        List<Person> list = personService.list();
        if(list.isEmpty()){
            return;
        }
        Map<String, float[]> collect = list.stream().collect(Collectors.toMap(Person::getImgId, o -> {
            List<Float> floats = JSONUtil.toList(o.getExtract(), Float.class);
            final float[] arr = new float[floats.size()];
            int index = 0;
            for (Float aFloat : floats) {
                arr[index++]=aFloat;
            }
            return arr;
        }));
        //初始化 全量人脸特征向量
        FaceFeaturesCache.setFaceFeatures(collect);
    }



}
