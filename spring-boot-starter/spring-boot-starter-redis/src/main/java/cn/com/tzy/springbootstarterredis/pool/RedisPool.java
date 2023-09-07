package cn.com.tzy.springbootstarterredis.pool;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.text.SimpleDateFormat;
import java.util.Map;

public class RedisPool {

    public static Jackson2JsonRedisSerializer<Object> serializer(){
        return serializer(null);
    }

    public static Jackson2JsonRedisSerializer<Object> serializer(Map<Class<?>,Class<?>> addMixIn){
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper(addMixIn));
        return jackson2JsonRedisSerializer;
    }

    public static ObjectMapper objectMapper(Map<Class<?>,Class<?>> addMixIn){
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域(field,get,set)，访问修饰符(public,private,protected)
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //序列化时添加类型，否则反序列化时会报错，无法转换
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止使用int代表Enum的order()來反序列化Enum,非常危險
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 序列化时将Long 转成string输出，js的long有精度错误
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        // 所有日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        if(addMixIn!= null && !addMixIn.isEmpty()){
            for (Map.Entry<Class<?>, Class<?>> entry : addMixIn.entrySet()) {
                objectMapper.addMixIn(entry.getKey(),entry.getValue());
            }
        }
        return objectMapper;
    }
}
