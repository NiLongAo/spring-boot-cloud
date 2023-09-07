package cn.com.tzy.springbootentity.utils;

import cn.com.tzy.springbootcomm.constant.Constant;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptUtil {
    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_PROVIDER = "AES/CBC/PKCS5Padding"; //算法/模式/补码方式

    /**
     * AES解密
     * @param encryptStr 密文
     * @param decryptKey 秘钥，必须为16个字符组成
     * @return 明文
     */
    public static String aesDecrypt(String encryptStr, String decryptKey, String decryptIv) throws Exception {
        if (StringUtils.isEmpty(encryptStr) || StringUtils.isEmpty(decryptKey) || StringUtils.isEmpty(decryptIv)) {
            return null;
        }
        byte[] encryptByte = Base64.getDecoder().decode(encryptStr);
        Cipher cipher = Cipher.getInstance(ALGORITHM_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), ALGORITHM),new IvParameterSpec(decryptIv.getBytes(StandardCharsets.UTF_8)));
        byte[] decryptBytes = cipher.doFinal(encryptByte);
        return new String(decryptBytes);
    }

    /**
     * AES加密
     * @param content 明文
     * @param encryptKey 秘钥，必须为16个字符组成
     * @return 密文
     */
    public static String aesEncrypt(String content, String encryptKey, String decryptIv) throws Exception {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(encryptKey) || StringUtils.isEmpty(decryptIv)) {
            return null;
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), ALGORITHM),new IvParameterSpec(decryptIv.getBytes(StandardCharsets.UTF_8)));

        byte[] encryptStr = cipher.doFinal(content.getBytes(Constant.CHARSET_UTF_8));
        return Base64.getEncoder().encodeToString(encryptStr);
    }

  public static void main(String[] args) throws Exception {
    System.out.println(EncryptUtil.aesEncrypt("root", Constant.SECRET_KEY,Constant.SECRET_IV));
      System.out.println(EncryptUtil.aesEncrypt("123456", Constant.SECRET_KEY,Constant.SECRET_IV));
  }
}
