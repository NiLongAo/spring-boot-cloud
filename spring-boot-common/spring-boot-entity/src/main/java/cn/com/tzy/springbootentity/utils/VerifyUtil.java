package cn.com.tzy.springbootentity.utils;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class VerifyUtil {
    public static AbstractCaptcha createLineCaptcha(int width, int height, int codeCount, int interfereCount) {
        return  new AbstractCaptcha(width, height, new RandomGenerator(codeCount){
            @Override
            public String generate() {
                return RandomUtil.randomString(this.baseStr, this.length).toUpperCase();
            }
        }, interfereCount) {
            @Override
            protected Image createImage(String code) {
                // 图像buffer
                final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                final Graphics2D g = GraphicsUtil.createGraphics(image, ObjectUtil.defaultIfNull(this.background, Color.WHITE));
                // 干扰线
                drawInterfere(g);
                // 字符串
                drawString(g, code);
                return image;
            }
            private void drawString(Graphics2D g, String code) {
                // 指定透明度
                if (null != this.textAlpha) {
                    g.setComposite(this.textAlpha);
                }
                GraphicsUtil.drawString(g, code, this.font, Color.BLACK,this.width, this.height);
            }
            private void drawInterfere(Graphics2D g) {
                final ThreadLocalRandom random = RandomUtil.getRandom();
                // 干扰线
                for (int i = 0; i < this.interfereCount; i++) {
                    int xs = random.nextInt(width);
                    int ys = random.nextInt(height);
                    int xe = xs + random.nextInt(width / 8);
                    int ye = ys + random.nextInt(height / 8);
                    g.setColor(ImgUtil.randomColor(random));
                    g.drawLine(xs, ys, xe, ye);
                }
            }
        };
    }
}
