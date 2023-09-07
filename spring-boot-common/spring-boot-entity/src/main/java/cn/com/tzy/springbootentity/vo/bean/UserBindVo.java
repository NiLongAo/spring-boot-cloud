package cn.com.tzy.springbootentity.vo.bean;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserBindVo implements Serializable {

    /**
     * 登陆用户类型
     */
    public String loginType;

    /**
     * 用户图片
     */
    public String image;

    /**
     * 用户昵称
     */
    public String nickName;

    /**
     * 是否绑定
     */
    private Integer isBind;


    public UserBindVo(Mini mini){
        this.loginType =ConstEnum.LoginTypeEnum.WX_MINI_USER.getValue();
        if(BeanUtil.isEmpty(mini)){
            this.isBind = ConstEnum.Flag.NO.getValue();
        }else {
            this.isBind = ConstEnum.Flag.YES.getValue();
            this.image = mini.getAvatarUrl();
            this.nickName = mini.getNickName();
        }
    }
}
