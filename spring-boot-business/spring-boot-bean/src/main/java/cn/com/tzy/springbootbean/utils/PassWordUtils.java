package cn.com.tzy.springbootbean.utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;
public class PassWordUtils {

    public static String getSalt(){
        return UUID.randomUUID().toString();
    }

    public static String getEncryptionPassWord(String passWord,String sale){
        return new BCryptPasswordEncoder().encode(passWord + sale);
    }


}
