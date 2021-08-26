package com.bdilab.colosseum.benchmark.sutconfig.base;

import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ConfSutConfig extends SutConfig{
    public ConfSutConfig(){
        super();
    }

    public String buildSutConf(String confPath, List<String> needToReplace, List<String> replaceBy) throws IOException {
        // 本地配置文件模板
        File my = new File(confPath);

        //从配置文件模板中读入配置数据
        BufferedReader br = new BufferedReader(new FileReader(my));
        StringBuilder confStringBuilder = new StringBuilder();
        String line =br.readLine();

        while (line!=null){
            confStringBuilder.append(line);
            confStringBuilder.append('\n');
            line = br.readLine();
        }
        String confString = confStringBuilder.toString();
        //将模板中的待确定参数替换为确定参数
        int length = Math.min(needToReplace.size(),replaceBy.size());
        for(int i = 0;i < length;i++){
            confString = confString.replaceAll(needToReplace.get(i),replaceBy.get(i));
        }
        return confString;
    }

    // 根据yaml路径和参数列表，将yaml文件中的配置参数进行修改
    public String buildSutYaml(String yamlPath, String paramList) throws IOException{
        // 根据参数列表对yaml配置进行修改
        // 生成参数列表,修改yaml的配置
        String[] params = paramList.split(",");
        Map<String, String> args = new HashMap<>();
        for (String param : params) {
            String[] paramValue = param.split("=");
            args.put(paramValue[0], paramValue[1]);
        }

        // 本地yaml文件模板
        File yamlFile = new File(yamlPath);

        // 从yaml文件模板中读入配置数据
        BufferedReader br = new BufferedReader(new FileReader(yamlFile));
        StringBuilder yamlStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            String[] yaml = line.split(": ");
            Iterator iter=args.keySet().iterator();
            while(iter.hasNext()){
                String key=(String )iter.next();
                if (yaml[0].equals(key)) {
                    yaml[1] = args.get(key);
                    line = yaml[0] + ": " + yaml[1];
                    iter.remove();
                    args.remove(yaml[0]);
                }
            }
            yamlStringBuilder.append(line);
            yamlStringBuilder.append("\n");
        }
        br.close();
//        System.out.println(yamlStringBuilder.toString());
        return yamlStringBuilder.toString();
    }
}
