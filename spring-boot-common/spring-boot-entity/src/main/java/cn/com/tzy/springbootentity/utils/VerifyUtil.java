package cn.com.tzy.springbootentity.utils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class VerifyUtil {
    // 默认验证码字符集
    private static final char[] chars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    // 默认字符数量
    private final Integer SIZE;
    // 默认干扰线数量
    private final int LINES;
    // 默认宽度
    private final int WIDTH;
    // 默认高度
    private final int HEIGHT;
    // 默认字体大小
    private final int FONT_SIZE;
    // 默认字体倾斜
    private final boolean TILT;

    private final Color BACKGROUND_COLOR;

    /**
     * 初始化基础参数
     *
     * @param builder
     */
    private VerifyUtil(Builder builder) {
        SIZE = builder.size;
        LINES = builder.lines;
        WIDTH = builder.width;
        HEIGHT = builder.height;
        FONT_SIZE = builder.fontSize;
        TILT = builder.tilt;
        BACKGROUND_COLOR = builder.backgroundColor;
    }

    /**
     * 实例化构造器对象
     *
     * @return
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * @return 生成随机验证码及图片
     * Object[0]：验证码字符串；
     * Object[1]：验证码图片。
     */
    public Object[] createImage() {
        StringBuffer sb = new StringBuffer();
        // 创建空白图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 获取图片画笔
        Graphics2D graphic = image.createGraphics();
        // 设置抗锯齿
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置画笔颜色
        graphic.setColor(BACKGROUND_COLOR);
        // 绘制矩形背景
        graphic.fillRect(0, 0, WIDTH, HEIGHT);
        // 画随机字符
        Random ran = new Random();

        //graphic.setBackground(Color.WHITE);

        // 计算每个字符占的宽度，这里预留一个字符的位置用于左右边距
        int codeWidth = WIDTH / (SIZE + 1);
        // 字符所处的y轴的坐标
        int y = HEIGHT * 3 / 4;

        for (int i = 0; i < SIZE; i++) {
            // 设置随机颜色
            graphic.setColor(getRandomColor());
            // 初始化字体
            Font font = new Font(null, Font.BOLD + Font.ITALIC, FONT_SIZE);

            if (TILT) {
                // 随机一个倾斜的角度 -45到45度之间
                int theta = ran.nextInt(45);
                // 随机一个倾斜方向 左或者右
                theta = (ran.nextBoolean() == true) ? theta : -theta;
                AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(theta), 0, 0);
                font = font.deriveFont(affineTransform);
            }
            // 设置字体大小
            graphic.setFont(font);

            // 计算当前字符绘制的X轴坐标
            int x = (i * codeWidth) + (codeWidth / 2);

            // 取随机字符索引
            int n = ran.nextInt(chars.length);
            // 得到字符文本
            String code = String.valueOf(chars[n]);
            // 画字符
            graphic.drawString(code, x, y);

            // 记录字符
            sb.append(code);
        }
        // 画干扰线
        for (int i = 0; i < LINES; i++) {
            // 设置随机颜色
            graphic.setColor(getRandomColor());
            // 随机画线
            graphic.drawLine(ran.nextInt(WIDTH), ran.nextInt(HEIGHT), ran.nextInt(WIDTH), ran.nextInt(HEIGHT));
        }
        // 返回验证码和图片
        return new Object[]{sb.toString(), image};
    }

    /**
     * 随机取色
     */
    private Color getRandomColor() {
        Random ran = new Random();
        Color color = new Color(ran.nextInt(256), ran.nextInt(256), ran.nextInt(256));
        return color;
    }

    /**
     * 构造器对象
     */
    public static class Builder {
        // 默认字符数量
        private int size = 4;
        // 默认干扰线数量
        private int lines = 10;
        // 默认宽度
        private int width = 80;
        // 默认高度
        private int height = 35;
        // 默认字体大小
        private int fontSize = 25;
        // 默认字体倾斜
        private boolean tilt = true;
        //背景颜色
        private Color backgroundColor = Color.LIGHT_GRAY;

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setLines(int lines) {
            this.lines = lines;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder setTilt(boolean tilt) {
            this.tilt = tilt;
            return this;
        }

        public Builder setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public VerifyUtil build() {
            return new VerifyUtil(this);
        }
    }

  public static void main(String[] args) throws IOException {
    //测试类
    // 返回的数组第一个参数是生成的验证码，第二个参数是生成的图片
    Object[] objs = VerifyUtil.newBuilder().build().createImage();
    // 将图片输出给浏览器
    BufferedImage image = (BufferedImage) objs[1];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
    ImageIO.write(image, "png", baos);//写入流中
    byte[] bytes = baos.toByteArray();//转换成字节
    Base64.Encoder encoder = Base64.getMimeEncoder();
    String png_base64 = encoder.encodeToString(bytes);
    System.out.println(png_base64);

    // 这个根据自己的需要设置对应的参数来实现个性化
    // 返回的数组第一个参数是生成的验证码，第二个参数是生成的图片
//    Object[] objs1 = VerifyUtil.newBuilder()
//          .setWidth(120)   //设置图片的宽度
//          .setHeight(35)   //设置图片的高度
//          .setSize(6)      //设置字符的个数
//          .setLines(10)    //设置干扰线的条数
//          .setFontSize(25) //设置字体的大小
//          .setTilt(true)   //设置是否需要倾斜
//          .setBackgroundColor(Color.WHITE) //设置验证码的背景颜色
//          .build()         //构建VerifyUtil项目
//          .createImage();  //生成图片
  }



}
