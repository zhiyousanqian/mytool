package org.gradle;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * json javabean 之间的转换工具;
 * servlet 输出工具
 *
 * @author yangyang.cong
 */
public abstract class JSONUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonFactory JSONFACTORY = new JsonFactory();


    /**
     * 转换Java Bean 为 json
     */
    public static String beanToJson(Object o) {
        StringWriter sw = new StringWriter(300);
        JsonGenerator gen = null;
        try {
            gen = JSONFACTORY.createJsonGenerator(sw);
            MAPPER.writeValue(gen, o);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("JSON转换失败", e);
        } finally {
            if (gen != null) try {
                gen.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 转换Java Bean 为 HashMap
     */
    public static Map<String, Object> beanToMap(Object o) {
        try {
            return (Map) MAPPER.readValue(beanToJson(o), HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException("转换失败", e);
        }
    }


    /**
     * 转换Json String 为 HashMap
     */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return (Map) MAPPER.readValue(json, HashMap.class);
        } catch (IOException e) {
            System.out.println( "转换失败:"+json);
            throw new RuntimeException("转换失败", e);
        }
    }


    /**
     * 转换Json String 为 HashMap
     */
    public static List<Object> jsonToList(String json) {
        try {
            return (List) MAPPER.readValue(json, ArrayList.class);
        } catch (IOException e) {
            throw new RuntimeException("转换失败", e);
        }
    }

    /**
     * 转换Json String 为 JavaBean
     */
    public static <T> T jsonToBean(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
            //return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换Json String 为 JavaBean
     */
    public static <T> T jsonToBean(File json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
            //return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        MAPPER.getSerializationConfig().withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }



}