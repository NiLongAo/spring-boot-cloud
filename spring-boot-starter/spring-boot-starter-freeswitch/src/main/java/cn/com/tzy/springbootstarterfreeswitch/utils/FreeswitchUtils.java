package cn.com.tzy.springbootstarterfreeswitch.utils;

import cn.com.tzy.springbootstarterfreeswitch.model.BeanModel;
import cn.com.tzy.springbootstarterfreeswitch.vo.FreeswitchXmlVo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 通话中心工具类
 */
public class FreeswitchUtils {

    static {
        Properties p = new Properties();
        try
        {
            // 加载classpath目录下的vm文件
            p.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.name());
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getXmlConfig(FreeswitchXmlVo vo){
        if(vo.getFsTypeEnum() == null){
            return null;
        }
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, List<BeanModel>> entry : vo.getModelMap().entrySet()) {
            context.put(entry.getKey(),entry.getValue());
        }
        // 渲染模板
        StringWriter sw = new StringWriter();
        Template tpl = Velocity.getTemplate(vo.getFsTypeEnum().getPath(), StandardCharsets.UTF_8.name());
        tpl.merge(context, sw);
        return sw.toString();
    }

}
