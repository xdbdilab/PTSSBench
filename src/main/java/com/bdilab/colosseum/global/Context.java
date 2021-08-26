package com.bdilab.colosseum.global;

import com.bdilab.colosseum.benchmark.execute.MultiAgExecutor;
import com.bdilab.colosseum.benchmark.workload.base.Workload;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
    private final Map<String, Workload> workloads;
    private final Map<String, MultiAgExecutor> executors;

    private Context() {
        workloads = new ConcurrentHashMap<>();
        executors = new ConcurrentHashMap<>();
    }

    public static Context getInstance() {
        return ContextFactory.instance;
    }

    static class ContextFactory {
        private final static Context instance = new Context();
    }

    public Workload getWorkload(String conversationId) {
        return workloads.get(conversationId);
    }

    public void putWorkload(String conversationId, Workload workload){
        workloads.put(conversationId,workload);
    }

    public void removeWorkload(String conversationId){
        workloads.remove(conversationId);
    }

    public MultiAgExecutor getExecutor(String conversationId){
        return executors.get(conversationId);
    }

    public void putExecutor(String conversationId, MultiAgExecutor executor){
        executors.put(conversationId,executor);
    }

    public void removeExecutor(String conversationId){
        executors.remove(conversationId);
    }
}
