package cn.com.tzy.srpingbootstartersecurityoauthbasic.dome;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.Data;

@Data
public class WxMaUserInfoVo extends WxMaUserInfo {
    private String scene;
}
