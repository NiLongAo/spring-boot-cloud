import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Encoder {
    public static void main(String[] args) {
        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encode =delegatingPasswordEncoder.encode("123456789");
        System.out.println(encode);
        System.out.println("bcrypt密码对比:" + delegatingPasswordEncoder.matches("123456", "{bcrypt}$2a$10$UuvjgxLiffvn9i4HwisMJeuaRIy5VU3yBvjloLKrwMH6hxXaiewi."));
//        System.out.println(new BCryptPasswordEncoder().encode("tzy18789432816"));

    }
}
