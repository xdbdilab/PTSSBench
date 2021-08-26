package com.bdilab.colosseum.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/15 16:19
 */
public class FileUtils {

    /**
     * 读取文件
     * @param fileName
     * @return
     */
    public static String readFileContentLineFeed(String fileName) {
        File file = new File(fileName);
        if (file.exists()){
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer();
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    sbf.append(tempStr+"\n");
                }
                reader.close();
                return sbf.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return sbf.toString();
        }else {
            return null;
        }
    }

    public static void creatDirectory(String directoryPath){
        File dir =new File(directoryPath);
        if (!dir.getParentFile().exists()){
            creatDirectory(dir.getParentFile().getPath());
        }
        if(!dir.exists()){
            dir.mkdir();
        }
    }
    public static boolean uploadFile(MultipartFile file, String targetDir){
        String fileName = file.getOriginalFilename();
        try {
            Path dest = Paths.get(targetDir,fileName);
            //判断上传文件目录是否存在，如果不存在就创建
            if (!dest.getParent().toFile().exists()) {
                creatDirectory(dest.getParent().toString());
            }
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean uploadFileRename(MultipartFile file, String targetDir,String fileName){
        try {
            Path dest = Paths.get(targetDir,fileName);
            //判断上传文件目录是否存在，如果不存在就创建
            if (!dest.getParent().toFile().exists()) {
                creatDirectory(dest.getParent().toString());
            }
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void cleanFile(File file){      //清空文件内容
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<String> readNum(File file) {   //读取测试数据
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                arrayList.add(str);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static  String readResult(File file,int lineNum)throws IOException{  //将每次测试运行结果读出 由于不同测试的应得结果所在行数不同，添加行数变量（int）
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
        String line = null;
        for (int lineCounter = 1;;lineCounter++){
            line = input.readLine();
            if (lineCounter==lineNum && line != null) break;
        }
        return line;
    }

    public static void readAndWriteResult(Process process,File file,int lines){
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"));
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            String line = null;
//            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
//            BufferedWriter bufferedWriter = new BufferedWriter(writerStream); //将结果写入到结果文件 完成一轮
            for (int i = 0; i < lines; i++){
                line = br.readLine();
                System.out.println("line: " + line);
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
