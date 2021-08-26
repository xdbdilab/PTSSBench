package com.bdilab.colosseum.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    //将一个Map中的数据添加到另一个Map中
    public static <K,V> void putParams(Map<K, V> paramsFrom, Map<K, V> paramsTo){
        for (Map.Entry<K, V> entry : paramsFrom.entrySet()) {
            paramsTo.put(entry.getKey(), entry.getValue());
        }
    }

    //java对象转map
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

}
