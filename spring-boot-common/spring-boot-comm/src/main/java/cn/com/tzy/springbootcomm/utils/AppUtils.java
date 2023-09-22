package cn.com.tzy.springbootcomm.utils;


import cn.com.tzy.springbootcomm.common.bean.TreeNode;
import cn.com.tzy.springbootcomm.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;

@Log4j2
public class AppUtils {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String encodeJson2(Object msg) {
        try {
            return encodeJson(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeJson(Object msg) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止使用int代表Enum的order()來反序列化Enum,非常危險
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 所有日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return objectMapper.defaultPrettyPrintingWriter().writeValueAsString(msg);
    }

    public static <T> T decodeJson3(String json, Class<?> clazz) {
        try {
            return (T) OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T decodeJson2(String json, Class<T> clazz) {
        try {
            return decodeJson(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T decodeJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止使用int代表Enum的order()來反序列化Enum,非常危險
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 所有日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return objectMapper.readValue(json, clazz);
    }

    public static <T> T convertValue2(Object json, Class<T> clazz) {
        try {
            return convertValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertValue(Object json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止使用int代表Enum的order()來反序列化Enum,非常危險
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 所有日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return objectMapper.convertValue(json, clazz);
    }

    public static <T> T convertValue2(Object json, TypeReference toValueTypeRef) {
        try {
            return convertValue(json, toValueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertValue(Object json, TypeReference toValueTypeRef) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁止使用int代表Enum的order()來反序列化Enum,非常危險
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 所有日期格式都统一为以下样式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return objectMapper.convertValue(json, toValueTypeRef);
    }

    public static String getMobileMask(String mobile) {
        if (mobile != null && mobile.length() >= 11) {
            String before = mobile.substring(0, 3);
            String after = mobile.substring(7);
            return before + "****" + after;
        } else {
            return mobile;
        }
    }

    public static String humpToLine(String str){
        Matcher matcher = Constant.HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()){
            matcher.appendReplacement(sb,"_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static Map<String, String> stringToMap(String source, String split) {
        Map<String, String> map = new HashMap<String, String>(10);

        String[] array = StringUtils.split(source, split);
        for (String pair : array) {
            int index = pair.indexOf('=');
            if (index >= 0) {
                String name = pair.substring(0, index).trim();
                String value = pair.substring(index + 1).trim();
                if (!name.isEmpty() && !value.isEmpty()) {
                    map.put(name, value);
                }
            }
        }

        return map;
    }


    public static <T> List<Map> transformationTree(T map,String childrenName,List<TreeNode<T>> treeNode){
        List<TreeNode<T>> treeNodeList = new ArrayList<>();
        if(map != null){
            TreeNode<T> mapTreeNode = new TreeNode<>();
            if(treeNode.size()> 0){
                mapTreeNode.setIsChildren(true);
            }else {
                mapTreeNode.setIsChildren(false);
            }
            mapTreeNode.setT(map);
            mapTreeNode.setChildren(treeNode);
            treeNodeList.add(mapTreeNode);
        }else {
            treeNodeList.addAll(treeNode);
        }
       return transformationTree(childrenName, treeNodeList);
    }

    //树处理
    public static <T> List<Map> transformationTree(String childrenName,List<TreeNode<T>> treeNode){
        List<Map> areaInitList = new ArrayList<>();
        treeNode.forEach(obj->{
            Map map = decodeJson2(encodeJson2(obj.getT()), Map.class);
            if(obj.getIsChildren()){
                List<Map> areaInfoList = transformationTree(childrenName,obj.getChildren());
                map.put(childrenName,areaInfoList);
            }
            map.put("isChildren",obj.getIsChildren());
            areaInitList.add(map);
        });
        return areaInitList;
    }


    public static boolean makeParentDir(File file) {
        if(!file.getParentFile().exists()) {
            return file.getParentFile().mkdirs();
        }
        return false;
    }

    public static String encodeUrl(String text, String encoding) {
        try {
            return URLEncoder.encode(text, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String decodeUrl(String text, String encoding) {
        try {
            return URLDecoder.decode(text, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * (参考mybatis plus 中 AbstractLambdaWrapper 的 getColumnCache 方法)
     * 从 lambda 表达式中推测  反射获取 FieldName 名称
     * @param function
     * @return
     */
    public static <T> String getFieldName(Function<T, ?> function){
        String fieldName = null;
        try {
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda invoke = (SerializedLambda)method.invoke(function);
            fieldName =  methodToProperty(invoke.getImplMethodName());
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            log.error("Tree 解析 错误,",e);
        }
        return fieldName;
    }

    private static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set")) {
                throw new IllegalStateException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }
            name = name.substring(3);
        }
        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }
}
