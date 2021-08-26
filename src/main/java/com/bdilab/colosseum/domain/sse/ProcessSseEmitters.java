package com.bdilab.colosseum.domain.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * @Description: SseEmitter管理类，用于维护和删除sse会话
 * @Author: Humphrey
 * @Date: 2019/9/26
 */
public class ProcessSseEmitters {
    private static Map<String, SseEmitter> sseEmitters = new Hashtable<>();

    public static void  addSseEmitters(String taskId, SseEmitter sseEmitter) {
        sseEmitters.put(taskId, sseEmitter);
    }

    public static SseEmitter getSseEmitterByKey(String key){
        return sseEmitters.get(key);
    }

    public static void removeSseEmitterByKey(String key){
        try{
            sseEmitters.remove(key);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public static void sendEvent(String conversationId, Object data){
        try{
            ProcessSseEmitters.getSseEmitterByKey(conversationId).send(data);
        }catch (IOException | NullPointerException e){
            e.printStackTrace();
        }
    }
}