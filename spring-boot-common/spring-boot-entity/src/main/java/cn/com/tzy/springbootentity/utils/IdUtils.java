package cn.com.tzy.springbootentity.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class IdUtils {

    private static SecureRandom random = new SecureRandom();
    private static char[] CHAR_ARRAY = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "a");
    }

    public static String randomSixChar() {
        int size = 4;
        char[] result = new char[size];

        for(int i = 0; i < size; i++) {
            int index = Math.abs(random.nextInt() % CHAR_ARRAY.length);
            result[i] = CHAR_ARRAY[index];
        }
        return new String(result);
    }

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return random.nextLong();
    }
}
