package com.bdilab.colosseum.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author duyeye
 * @Date 2021/1/14 0014 13:33
 */
public class TypeCastUtils {

    //Object转换为List
    public static <T> List<T> objectToList(Object object, Class<T> tClass){
        List<T> result = new ArrayList<T>();
        if (object instanceof List<?>){
            for (Object o:(List<?>) object){
                result.add(tClass.cast(o));
            }
            return result;
        }
        return null;
    }
}
