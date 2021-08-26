package com.bdilab.colosseum.utils;

import java.io.IOException;

/**
 * @Author duyeye
 * @Date 2021/1/5 0005 17:05
 */
public class CommandUtils {
    public static void executeCommand(String command){
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
