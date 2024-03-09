package cn.com.tzy.springbootentity.mq;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRDataModel {

    private String mini_scene;

    private Object qrData;
}
