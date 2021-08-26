package com.bdilab.colosseum.utils;

/**
 * 用于将异常的StackTrace转化为字符串
 * @author liran
 */
public class ExceptionStackTrace {
    public static String getStackTraceString(Throwable e){
        StackTraceElement[] elements = e.getStackTrace();
        StringBuilder builder = new StringBuilder();
        builder.append("【stacktrace】\n");
        for(StackTraceElement element : elements){
            builder.append(element.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
