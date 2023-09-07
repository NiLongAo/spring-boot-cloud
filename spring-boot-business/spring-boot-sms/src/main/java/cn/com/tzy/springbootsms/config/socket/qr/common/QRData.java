package cn.com.tzy.springbootsms.config.socket.qr.common;

import cn.com.tzy.springbootstartersocketio.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * 发出消息信息类
 * 发送人可从Header中查询
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QRData extends Message {

    /**
     * 扫码类型
     */
    private Integer type;

    /**
     * 扫描类型
     */
    public enum QRType{
        WX_MINI_QR_LOGIN(1,"微信小程序二维码登录"),
        WX_MINI_QR_BIND(2,"微信小程序二维码绑定web"),
        ;

        private final int value;
        private final String name;

        QRType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Map<Integer, QRType> map = new HashMap<Integer, QRType>();
        static {
            for (QRType e : QRType.values()) {
                map.put(e.getValue(), e);
            }
        }

        public static QRType getName(int value) {
            return map.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


}
