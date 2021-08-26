package com.bdilab.colosseum.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.domain.Component;
import com.bdilab.colosseum.domain.run.ApiParameter;
import com.bdilab.colosseum.domain.run.ApiPipelineSpec;
import com.bdilab.colosseum.domain.run.ApiRun;
import com.bdilab.colosseum.domain.workflow.ComponentParamsInOrder;
import com.bdilab.colosseum.domain.workflow.Node;
import com.bdilab.colosseum.exception.BusinessException;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.mapper.ComponentMapper;
import com.bdilab.colosseum.service.AlgorithmService;
import com.bdilab.colosseum.service.PipelineService;
import com.bdilab.colosseum.utils.CommandUtils;
import com.bdilab.colosseum.utils.JsonUtils;
import com.bdilab.colosseum.utils.MapUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

/**
 * @Author duyeye
 * @Date 2020/12/22 0022 11:24
 */
@Service
public class PipelineServiceImpl implements PipelineService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${pipeline.url}")
    private String pipelineUrl;

    @Value("${run.url}")
    private String runUrl;

    @Value("${pipeline.path}")
    private String pipelinePath;

    @Autowired
    ComponentMapper componentMapper;

    @Override
    public String uploadPipeline(String name, String description, File file) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource;
        fileSystemResource = new FileSystemResource(file);
        map.add("uploadfile",fileSystemResource);

        String url = pipelineUrl + "upload?name=" + name + "&description=" + description;
        HttpEntity<MultiValueMap<String, Object>> params = new HttpEntity<>(map);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, params, String.class);

        if(responseEntity.getStatusCodeValue() != 200){
            return null;
        }
        Gson gson = new Gson();
        Map<String,String> map1 = gson.fromJson(responseEntity.getBody(),Map.class);
        String pipelineId = map1.get("id");
        return pipelineId;
    }

    @Override
    public String getPipelineById(String pipelineId) {
        String url = pipelineUrl + pipelineId;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url,String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        System.out.println("statusCodeValue = " + statusCodeValue);
        if(statusCodeValue == 200){
            return responseEntity.getBody();
        }else {
            return null;
        }
    }

    @Override
    public boolean deletePipelineById(String pipelineId) {
        String url = pipelineUrl + pipelineId;
        restTemplate.delete(url);
        return true;
    }

    @Override
    public String createRun(String pipelineId, String pipelineName, ArrayList<ArrayList<String>> parameter) {
        ApiRun apiRun = new ApiRun();
        ApiPipelineSpec apiPipelineSpec = new ApiPipelineSpec();
        int paramLength = parameter.size();
        Object[] parameters = new Object[paramLength];
        int i = 0;
        for(ArrayList<String> list: parameter){
            ApiParameter apiParameter = new ApiParameter();
            apiParameter.setName(list.get(0));
            apiParameter.setValue(list.get(1));
            parameters[i] = apiParameter;
            i++;
        }
        apiRun.setName("Run of " + pipelineName);
        apiRun.setDescription("desc");
        apiPipelineSpec.setPipelineId(pipelineId);
        apiPipelineSpec.setPipelineName(pipelineName);
        apiPipelineSpec.setParameters(parameters);
        apiRun.setPipeline_spec(apiPipelineSpec);

        Gson gson = new Gson();
        String json = gson.toJson(apiRun);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ApiRun> request = new HttpEntity(apiRun, (MultiValueMap)headers);
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(runUrl, request, String.class, new Object[0]);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue == 200){
            //获取返回结果中的运行id
            Gson gson1 = new Gson();
            Map<String,Map<String,String>> map = gson1.fromJson(responseEntity.getBody(), Map.class);
            Map<String,String> run = map.get("run");
            return run.get("id");
        }
        return null;
        //return "abcdefg";
    }

    /**
     * 根据runId查看运行状态
     *
     * @param runId
     * @return
     */
    @Override
    public boolean checkFailedStatus(String runId) {
        JSONObject resultObject = JSONObject.parseObject(this.restTemplate.getForObject(runUrl, String.class));
        for (Object str : JSONObject.parseArray(resultObject.get("runs").toString())) {
            if ("Failed".equals(JSONObject.parseObject(str.toString()).get("status")) || "Error".equals(JSONObject.parseObject(str.toString()).get("status"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteRunById(String runId) {
        String url = runUrl + "/" + runId;
        restTemplate.delete(url);
        return true;
    }

    @Override
    public boolean terminateRunById(String runId) {
        String url = runUrl + "/" + runId + "/terminate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue != 200){
            return false;
        }
        return true;
    }

    @Override
    public boolean retryRunById(String runId) {
        String url = runUrl + "/" + runId + "/retry";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue != 200){
            return false;
        }
        return true;
    }

    @Deprecated
    @Override
    public String getArtifactData(Long runId, String nodeId, String artifactName) {
        //nodeId现在无法获取到
        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/runs/" + runId +
                "/nodes/" + nodeId + "/artifacts/" + artifactName + ":read";
        String u = "http://120.27.69.55:31380/pipeline/apis/v1beta1/runs/" + runId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue != 200){
            return null;
        }
        return responseEntity.getBody();
    }

    @Override
    public Map generatePipeline(String workflowJson) {
        //等待运行的Node队列
        List<Node> queue = new ArrayList<>();
        //已经运行完成的Node队列
        List<Node> completeQueue = new ArrayList<>();
        Map<String,Object> data = new HashMap<>();
        //记录组件的遍历顺序
        ComponentParamsInOrder componentParamsInOrder = new ComponentParamsInOrder();
        //获取所有组件节点
        List<Node> workflowNodes = JsonUtils.getWorkflowNodes(workflowJson);
        //generateHeadCode表示生成import部分
        StringBuilder pipeline = new StringBuilder(generateHeadCode());

        Set<String> allArguments = new HashSet<>();
        Node firstNode = getFirstNode(workflowNodes);
        queue.add(firstNode);
        Map<String, Object> code = new HashMap<>();
        StringBuilder nodeBodyCode = new StringBuilder();
        StringBuilder runParamsCode = new StringBuilder();
        while (queue.size()!=0){
            if (allPriorCompleted(queue.get(0),workflowNodes,completeQueue)){
                addComponentParams(componentParamsInOrder,queue.get(0));
                //生成每个组件部分的pipeline代码
                code = generateNodeCode(queue.get(0),workflowNodes,queue,completeQueue);
                pipeline.append(code.get("nodeCode"));
                nodeBodyCode.append(code.get("bodyCode"));
                runParamsCode.append(code.get("runParamsCode"));
            }else {
                queue.remove(queue.get(0));
            }
        }

        //bodyCode表示pipeline的每个组件的运行，以及组件之间的数据传递
        StringBuilder bodyCode = new StringBuilder();
        bodyCode.append("@dsl.pipeline(\n")
                .append("    name='colosseum',\n")
                .append("    description='colosseum component'\n")
                .append(")\n")
                .append("def run(")
                .append(runParamsCode)
                .append(Constant.CONFIG_PARAMS+",")
                .append(Constant.COMMON_PARAMS)
                .append("):\n");


        bodyCode.append("\n")
                .append(nodeBodyCode)
                .append("    dsl.get_pipeline_conf().set_image_pull_secrets([k8s_client.V1ObjectReference(name=\"aiflow\")])\n");

        pipeline.append(bodyCode);
        String tailCode = "if __name__ == '__main__':\n" +
                "    compiler.Compiler().compile(run, __file__ + '.yaml')\n";
        pipeline.append(tailCode);
        //将pipeline写入文件中
        System.out.println(pipeline);
        String pyFilePath = pipelinePath + UUID.randomUUID()+".py";
        File pythonFile = new File(pyFilePath);
        try {
            if (!pythonFile.exists()) {
                pythonFile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pythonFile)));
            bw.write(pipeline.toString());
            bw.close();
            CommandUtils.executeCommand("python " + pyFilePath);
            String yamlFilePath = pyFilePath+".yaml";
            File yamlFile = new File(yamlFilePath);
            if (!yamlFile.exists()){
                throw new IOException("编译失败");
            }
            data.put("pyFilePath", pyFilePath);
            data.put("yamlFilePath", yamlFilePath);
            data.put("componentParamsInOrder",componentParamsInOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String getComponentNamesJoinInString(String algorithmJson) {
        List<Node> workflowNodes = JsonUtils.getWorkflowNodes(algorithmJson);
        Node firstNode = getFirstNode(workflowNodes);
        if(firstNode == null && workflowNodes.size() == 0){
            throw new BusinessException(400,"please select component!","用户未选择任何组件");
        }
        if(firstNode == null){
            throw new BusinessException(400,"do not support circulation!","用户的连接包含环");
        }
        Node currentNode = firstNode;
        List<String> componentNamesInOrder = new ArrayList<>();
        int count = 0;
        while (true){
            count = count + 1;
            if(count >workflowNodes.size()){
                throw new BusinessException(400,"do not support circulation!","用户的连接包含环");
            }
            componentNamesInOrder.add(currentNode.getComponentName());
            List<Node> rearNodes = getRearNode(currentNode, workflowNodes);
            if(rearNodes.size() > 1) {
                throw new BusinessException(400,"only support single branch!","用户连接了多分支");
            }
            if (rearNodes.size() == 0) {
                break;
            }
            currentNode = rearNodes.get(0);
        }
        return String.join("@@@", componentNamesInOrder);

    }

    private String generateHeadCode() {
        String headCode = "from kfp import compiler\n" +
                "import kfp.dsl as dsl\n" +
                "from kubernetes import client as k8s_client\n\n";
        return headCode;
    }

    //生成每个组件部分的代码
    private Map<String, Object> generateNodeCode(Node currentNode, List<Node> workflowNodes, List<Node> queue, List<Node> completeQueue){
        Map<String, Object> code = new HashMap<>();
        String curNodeName = currentNode.getComponentName();
        Component curComponent = componentMapper.selectByPrimaryKey(Long.parseLong(currentNode.getComponentId().toString()));
        String nodeCode = "class " + curNodeName + "Op(dsl.ContainerOp):\n\n";
        String bodyCode = "";
        String runParamsCode;

        nodeCode += "    def __init__(self,";
        List<String> arguments = getComponentArguments(currentNode,workflowNodes);
        for (String s:arguments) {
            nodeCode += " " + s + ",";
        }
        nodeCode = nodeCode.substring(0,nodeCode.lastIndexOf(","));
        nodeCode += "):\n";
        nodeCode += "       super(" + curNodeName + "Op, self).__init__(\n" +
                "            name='" + curNodeName + "',\n" +
                "            image='" + curComponent.getImage() + "',\n" +
                "            command=[\n" +
                "                'python3', '" + curNodeName + ".py',\n";
        for (String s:arguments){
            nodeCode += "                '--" + s + "', " + s + ",\n";
        }
        nodeCode += "            " + "],\n" +
                "            file_outputs={\n" +
                "                '" + curNodeName + "_file': '/" + curNodeName + "_file.txt'\n" +
                "            })\n\n";


        runParamsCode = arguments.get(0)+",";


        bodyCode += "    " + curNodeName + " = " +curNodeName + "Op(";
        for (String s:arguments){
            if (!s.endsWith("_file")){
                bodyCode += s + ",";
            }else {
                String priorNodeName = s.substring(0,s.lastIndexOf("_file"));
                bodyCode += priorNodeName + ".outputs['" + s + "'],";
            }
        }
        if (bodyCode.contains(",")){
            bodyCode = bodyCode.substring(0,bodyCode.lastIndexOf(","));
        }
        bodyCode += ").add_volume(k8s_client.V1Volume(name='colosseum',\n" +
                "                 nfs=k8s_client.V1NFSVolumeSource(path='/nfs/colosseum/', server='master'))).add_volume_mount(\n" +
                "                   k8s_client.V1VolumeMount(mount_path='/nfs/colosseum/', name='colosseum'))\n";

        code.put("nodeCode",nodeCode);
        code.put("runParamsCode",runParamsCode);
        code.put("bodyCode",bodyCode);
        //将currentNode的后置Node放入queue中
        completeQueue.add(currentNode);
        queue.remove(currentNode);
        List<Node> rearNodes = getRearNode(currentNode, workflowNodes);
        for (Node node:rearNodes){
            if (!queue.contains(node)){
                queue.add(node);
            }
        }
        return code;

    }

    //获取组件的所有参数
    private List<String> getComponentArguments(Node currentNode, List<Node> workflowNodes){
        List<String> arguments = new LinkedList<>();
        Component curComponent = componentMapper.selectByPrimaryKey(Long.parseLong(currentNode.getComponentId().toString()));

        //所有组件的前两个参数都有component_params和config_params
        arguments.add(currentNode.getComponentName()+"_"+ Constant.COMPONENT_PARAMS);
        arguments.add(Constant.CONFIG_PARAMS);

        //对于属于特征选择的组件，将all_params，black_list，white_list这三个参数，加入参数列表中，由common_params封装
        if (curComponent.getType()==0){
            arguments.add(Constant.COMMON_PARAMS);
        }
        //从数据库中获取到该组件的组件参数，放入参数列表中
//        for (Map.Entry entry:currentNode.getComponentParam().entrySet()){
//            arguments.add(entry.getKey().toString());
//        }

        //将前置节点的输出文件放入参数列表中
        List<Node> priorNodes = getPriorNode(currentNode, workflowNodes);
        for (Node node:priorNodes){
            arguments.add(node.getComponentName() + "_file");
        }

        return arguments;
    }

    //获取第一个node
    private Node getFirstNode(List<Node> workflowNodes){
        for (Node node:workflowNodes){
            if (node.getPriorNode().size()==0){
                return node;
            }
        }
        return null;
    }

    //根据nodeId获取node
    private Node getNodeById(String nodeId, List<Node> workflowNodes){
        for (Node node:workflowNodes){
            if (node.getId().equals(nodeId)){
                return node;
            }
        }
        return null;
    }

    //获取当前node的所有后置node
    private List<Node> getRearNode(Node curNode, List<Node> workflowNodes){
        List<Node> rearNode = new LinkedList<>();
        for (Node node:workflowNodes){
            for (String nodeId:node.getPriorNode()){
                if (nodeId.equals(curNode.getId())){
                    rearNode.add(node);
                }
            }
        }
        return rearNode;
    }

    //获取当前node的所有前置node
    private List<Node> getPriorNode(Node curNode, List<Node> workflowNodes){
        List<Node> priorNode = new LinkedList<>();
        for (Node node:workflowNodes){
            if (node.getRearNode().size()!=0){
                for (String nodeId:node.getRearNode()){
                    if (nodeId.equals(curNode.getId())){
                        priorNode.add(node);
                    }
                }
            }
        }
        return priorNode;
    }

    //判断当前node的所有前置node是否已经完成
    private boolean allPriorCompleted(Node curNode, List<Node> workflowNodes, List<Node> completeQueue){
        List<Node> priorNodes = getPriorNode(curNode, workflowNodes);
        if (priorNodes.size()!=0){
            for (Node node:priorNodes){
                if (!completeQueue.contains(node)){
                    return false;
                }
            }
        }
        return true;
    }

    //将当前节点的参数封装，添加到list中
    private void addComponentParams(ComponentParamsInOrder componentParamsInOrder,Node node){
        HashMap<String, Object> componentParam = new HashMap<>();
        MapUtils.putParams(node.getComponentParam(), componentParam);
        HashMap<String,HashMap<String,Object>> componentParams = new HashMap<>();
        componentParams.put(node.getComponentName(),componentParam);
        componentParamsInOrder.getComponentParamsInOrder().add(componentParams);
    }
}
