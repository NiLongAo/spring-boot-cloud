package cn.com.tzy.springbootbean;

import cn.com.tzy.springbootstarterminio.utils.MinioUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest
public class MinioTest {

    @Resource
    private MinioUtils minioUtils;

    @Test
    public void ImageToBean64() throws UnsupportedEncodingException {
        String s = streamToBase64("/images/avatar2.png");
        System.out.println(s);
    }

    private InputStream getObject(String netImagePath){
        return minioUtils.getObject("uni", netImagePath);
    }
    /**
     * 网络文件流转换base64
     *
     * @return
     */
    private String streamToBase64(String objectName) throws UnsupportedEncodingException {

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            byte[] by = new byte[1024];
            //创建链接
            InputStream is = this.getObject(objectName);
            if(is == null){
                throw new RuntimeException(String.format("streamToBase64 minio is null pash : %s",objectName));
            }
            //ImgUtil.write(ImgUtil.read(is), ImgUtil.IMAGE_TYPE_JPG, destImageStream, 1);
            //将内容读取到内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            //关闭流
            is.close();
        } catch (IOException e) {
            log.error("minio path:{} is null,",objectName,e);
        }
        //byte[] encode = Base64.getEncoder().encode(data.toByteArray());
        //对字节数组Base64编码
        // DatatypeConverter.printBase64Binary(data.toByteArray());
        return DatatypeConverter.printBase64Binary(data.toByteArray());
    }
}
