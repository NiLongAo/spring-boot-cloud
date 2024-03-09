package cn.com.tzy.springbootsso.controller.oauth;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.ImgConstant;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.srpingbootstartersecurityoauthcore.properties.WxMaProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.UUID;

@Api(tags = "微信web认证回调")
@RestController("SSoMiniController")
@RequestMapping(value = "/sso/mini")
public class MiniController extends ApiController {

    @Resource
    private WxMaService wxMaService;
    @Resource
    private WxMaProperties wxMaProperties;

    @ApiOperation(value = "获取小程序码", notes = "获取小程序码")
    @GetMapping("/get_qr_code")
    public RestResult<?> getQRCode( @RequestParam(value = "uuid",required = false) String uuid ) throws Exception {
        uuid = StringUtils.isNotBlank(uuid) ?uuid:UUID.randomUUID().toString().replaceAll("-", "");
        byte[] release = wxMaService.getQrcodeService().createWxaCodeUnlimitBytes(uuid, wxMaProperties.getPage(), true, wxMaProperties.getEnvVersion(), 430, true, (WxMaCodeLineColor) null, false);
        String s = Base64.encodeBase64String(release);
        HashMap<String, Object> map = new HashMap<>();
        map.put("scene", uuid);
        map.put("img", ImgConstant.IMAGE_PNG + s);
        return RestResult.result(RespCode.CODE_0.getValue(),null,map);
    }

}
