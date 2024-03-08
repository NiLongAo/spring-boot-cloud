package cn.com.tzy.springbootentity.common.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserPayload {

    private Long user_id;

    private String user_name;

    private String login_type;
}
