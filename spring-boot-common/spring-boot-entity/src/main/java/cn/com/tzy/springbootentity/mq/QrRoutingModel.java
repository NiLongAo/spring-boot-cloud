package cn.com.tzy.springbootentity.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrRoutingModel implements Serializable {

    private String scene;

    private String openId;
}
