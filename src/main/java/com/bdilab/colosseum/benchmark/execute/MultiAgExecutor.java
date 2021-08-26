package com.bdilab.colosseum.benchmark.execute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.service.PipelineService;

import java.util.*;

/**
 * @author liran
 */
public class MultiAgExecutor {
    private final ArrayList<ArrayList<String>> parameter;
    private final List<ExecutorAg> executorAgs;
    private final List<String> runIds;
    private int curPoint;

    public MultiAgExecutor(ArrayList<ArrayList<String>> parameter, List<ExecutorAg> executorAgs){
        if(executorAgs == null || executorAgs.size() == 0){
            throw new RuntimeException("executor will start, but algorithm is empty");
        }
        this.parameter = parameter;
        this.executorAgs = executorAgs;
        runIds = new ArrayList<>();
        this.curPoint = -1;
    }

    public String execute(PipelineService pipelineService){
        ExecutorAg executorAg = executorAgs.get(++curPoint);
        modifyAlgorithmName(executorAg.getBuildInAlgorithmName());
        int addNum = addComponentParam(parameter, executorAg.getParamValue());
        //调用实验运行接口并获取runId
        //String runId = UUID.randomUUID().toString();
        String runId = pipelineService.createRun(executorAg.getPipelineId(), "Colosseum-" + executorAg.getAlgorithmName() + UUID.randomUUID(), parameter);
        while (addNum > 0){
            parameter.remove(parameter.size() - 1);
            addNum--;
        }
        runIds.add(runId);
        return runId;
    }

    public boolean hasNextAg(){
        return curPoint + 1 < executorAgs.size();
    }

    public ExecutorAg getCurExecutorAg(){
        return executorAgs.get(curPoint);
    }

    public String getCurRunId(){
        return runIds.get(curPoint);
    }

    public int getCurPoint(){
        return curPoint;
    }

    //向Kubeflow参数中添加组件参数
    private int addComponentParam(ArrayList<ArrayList<String>> parameter, String paramsJsonString){
        JSONArray jsonArray = JSONObject.parseArray(paramsJsonString);
        for (int i = 0;i < jsonArray.size();i++){
            ArrayList<String> list = new ArrayList<>();
            //jsonArray.getJSONObject(i)得到的Map只会有一对kv值
            Set<Map.Entry<String, Object>> entrySet = jsonArray.getJSONObject(i).entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                list.add(entry.getKey()+"_"+ Constant.COMPONENT_PARAMS);
                list.add(entry.getValue().toString());
            }
            parameter.add(list);

        }
        return jsonArray.size();
    }

    //修改config_params中的alogrithm参数
    private void modifyAlgorithmName(String buildInAlgorithmName){
        JSONObject configPrams = JSONObject.parseObject(this.parameter.get(0).get(1));
        configPrams.put("alogrithm",buildInAlgorithmName);
        String configPramsNew = JSONObject.toJSONString(configPrams);
        parameter.get(0).set(1,configPramsNew);
    }

    public static class ExecutorAg{
        private Long id;
        private String algorithmName;
        private String paramValue;
        private String pipelineId;
        private String buildInAlgorithmName;

        public ExecutorAg(){

        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public void setAlgorithmName(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }

        public String getPipelineId() {
            return pipelineId;
        }

        public void setPipelineId(String pipelineId) {
            this.pipelineId = pipelineId;
        }

        public String getBuildInAlgorithmName() {
            return buildInAlgorithmName;
        }

        public void setBuildInAlgorithmName(String buildInAlgorithmName) {
            this.buildInAlgorithmName = buildInAlgorithmName;
        }
    }
}
