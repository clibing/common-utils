package cn.linuxcrypt.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Json装换工具
 */
public final class JsonUtil {
    public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DATETIME);
        OBJECT_MAPPER.setConfig(OBJECT_MAPPER.getSerializationConfig().with(dateFormat));
        OBJECT_MAPPER.setConfig(OBJECT_MAPPER.getDeserializationConfig().with(dateFormat));
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        OBJECT_MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        OBJECT_MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
    }

    public static <T> T toBean(Class<T> entryClass, String json) {
        try {
            return OBJECT_MAPPER.readValue(json, entryClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
