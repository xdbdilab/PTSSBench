package com.bdilab.colosseum.utils;

import com.alibaba.fastjson.JSONArray;
import com.bdilab.colosseum.vo.ParameterVo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bdilab.colosseum.domain.workflow.Node;

import java.util.*;

/**
 * @Author duyeye
 * @Date 2020/12/28 0028 11:57
 */
public class JsonUtils {

    public static List<ParameterVo> parameters2ParameterVoList(String parameters) {
        List<ParameterVo> parameterVoList = null;
        JSONArray parseArray = JSONArray.parseArray(parameters);
        parameterVoList = parseArray.toJavaList(ParameterVo.class);
        return parameterVoList;
    }
    public static String JSON = "{\"nodes\":[{\"id\":\"1609479113968\",\"componentParams\":{},\"type\":0,\"componentName\":\"random\",\"description\":\"一种在配置参数上进行随机采样的算法\",\"componentId\":2,\"inPorts\":[{\"tableName\":\"germany_credit_data\",\"sequence\":1,\"description\":\"输入1\",\"id\":\"1609479113968_in_1\"}],\"outPorts\":[{\"tableName\":\"germany_credit_data\",\"sequence\":1,\"description\":\"输出表1\",\"id\":\"1609479113968_out_1\"}],\"positionX\":-279,\"positionY\":-276,\"category\":\"source\",\"status\":3,\"groupId\":0},{\"id\":\"1609479116463\",\"componentParams\":{\"algo\":null,\"max_evals\":100},\"type\":3,\"componentName\":\"hyperopt\",\"description\":\"一种通过贝叶斯优化来调整参数的工具\",\"componentId\":1,\"inPorts\":[{\"tableName\":\"germany_credit_data\",\"sequence\":1,\"description\":\"输入1\",\"id\":\"1609479116463_in_1\"}],\"outPorts\":[{\"tableName\":\"germany_credit_data\",\"sequence\":1,\"description\":\"输出表1\",\"id\":\"1609479116463_out_1\"}],\"positionX\":-280,\"positionY\":-160,\"category\":\"source\",\"status\":3,\"groupId\":0},{\"id\":\"1609479118319\",\"componentParams\":{\"Epoch\":50000,\"LR_G\":0.0001,\"LR_D\":0.0001,\"N_IDEAS\":5},\"type\":3,\"componentName\":\"actgan\",\"description\":\"一种基于生成对抗网络（GAN）的软件系统参数优化算法\",\"componentId\":3,\"inPorts\":[{\"tableName\":\"germany_credit_data\",\"sequence\":1,\"description\":\"输入1\",\"id\":\"1609479118319_in_1\"}],\"outPorts\":[{\"tableName\":\"germany_credit_data\",\"sequence\":1,\"description\":\"输出表1\",\"id\":\"1609479118319_out_1\"}],\"positionX\":-280,\"positionY\":-50,\"category\":\"source\",\"status\":3,\"groupId\":0}],\"links\":[{\"source\":\"1609479113968\",\"target\":\"1609479116463\",\"outputPortId\":\"1609479113968_out_1\",\"inputPortId\":\"1609479116463_in_1\"},{\"source\":\"1609479116463\",\"target\":\"1609479118319\",\"outputPortId\":\"1609479116463_out_1\",\"inputPortId\":\"1609479118319_in_1\"}]}";


    public static List<Node> getWorkflowNodes(String workflowJson){
        List<Node> result = new LinkedList<>();
        JSONObject jsonObject = JSONObject.parseObject(workflowJson, Feature.OrderedField);

        //从json串解析出算法的node和参数值
        if (jsonObject.containsKey("nodes")){
            String nodes = jsonObject.get("nodes").toString();
            JSONArray jsonArray = JSONArray.parseArray(nodes);
            System.out.println("nodes="+jsonArray.size());
            for (int i=0; i<jsonArray.size(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Node node = new Node();
                try{
                    node.setId(jsonObject1.get("id").toString());
                    node.setComponentId(jsonObject1.get("componentId").toString());
                    node.setComponentName(jsonObject1.get("componentName").toString());
                    //node.setInput(jsonObject1.get("inPorts").toString());
                    //node.setOutput(jsonObject1.get("outPorts").toString());
                    node.setPriorNode(new LinkedList<>());
                    node.setRearNode(new LinkedList<>());

                    //得到组件参数的值
                    Map<String, Object> params = getParamsValueFromParamsJson(jsonObject1.get("componentParams").toString());
                    node.setComponentParam(params);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                result.add(node);
            }
        }

        //从json串解析出node的前置节点和后置节点
        if (jsonObject.containsKey("links")){
            String edges = jsonObject.get("links").toString();
            JSONArray jsonArray = JSONArray.parseArray(edges);
            System.out.println("links="+jsonArray.size());
            for (int i=0; i<jsonArray.size(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                for (int j=0; j<result.size(); j++){
                    Node node = result.get(j);
                    try{
                        if (jsonObject1.get("source").equals(node.getId())){
                            List<String> rearNode = node.getRearNode();
                            rearNode.add(jsonObject1.get("target").toString());
                            node.setRearNode(rearNode);
                        }
                        if (jsonObject1.get("target").equals(node.getId())){
                            List<String> priorNode = node.getPriorNode();
                            priorNode.add(jsonObject1.get("source").toString());
                            node.setPriorNode(priorNode);
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("result="+JSONObject.toJSONString(result));;
        return result;
    }

    //解析出组件参数的值
    public static Map<String, Object> getParamsValueFromParamsJson(String paramsJson){
        System.out.println("paramsJson=" + paramsJson);
        Map<String, Object> result = new HashMap<>();
        if (paramsJson.equals("{}")){
            return result;
        }else {
            JSONObject object = JSONObject.parseObject(paramsJson);
            for (Map.Entry entry:object.entrySet()){
                result.put(entry.getKey().toString(), entry.getValue());
            }
        }
        return result;
    }
    //从workflowJson中解析出paramValue
    public static Map<String, Object> getParamsValueFromWorkflowJson(String workflowJson){
        Map<String, Object> result = new HashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(workflowJson, Feature.OrderedField);
        if (jsonObject.containsKey("nodes")) {
            String nodes = jsonObject.get("nodes").toString();
            JSONArray jsonArray = JSONArray.parseArray(nodes);
            System.out.println("nodes=" + jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                try {
                    //得到组件参数的值
                    Map<String, Object> param = getParamsValueFromParamsJson(jsonObject1.get("componentParams").toString());
                    for (Map.Entry entry:param.entrySet()){
                        result.put(entry.getKey().toString(),entry.getValue());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        getWorkflowNodes("");
    }
}
