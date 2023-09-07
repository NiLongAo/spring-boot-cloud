package cn.com.tzy.springbootface.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haopeng
 */
@Data
@SuperBuilder(toBuilder = true)
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "face")
public class FaceProperties {

    /**
     * dll基础路径
     * <pre>
     * 1. 下载路径:https://github.com/seetafaceengine/SeetaFace6
     * 2. 拖到下面介绍有一个模块叫<strong>下载地址</strong>
     * 3. 找到windows下载开发包
     * 4. 下载完成后解压 将下面地址指向 %解压的路径%/sf6.0_windows/lib/x64
     * </pre>
     */
    private String dllPath;
    /**
     * CSTA基础路径
     * <pre>
     * 1. 下载路径:https://github.com/seetafaceengine/SeetaFace6
     * 2. 拖到下面介绍有一个模块叫<strong>下载地址</strong>
     * 3. 找到模型文件 下载Part1
     * 4. 下载完成后解压 将下面地址指向 %解压的路径%/sf3.0_models
     * </pre>
     */
    private String cstaPath;
}
