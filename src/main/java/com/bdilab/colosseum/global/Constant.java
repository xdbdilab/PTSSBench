package com.bdilab.colosseum.global;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public static final String PERFORMANCE_ERROR = "error";
    public static final int MIN_MAX_ROW_NUMBER = 3;
    public static final int ACTUAL_PERFORMANCE_ROW_NUMBER = 1;

    //生成pipeline相关
    public static final String COMMON_PARAMS = "common_params";
    public static final String COMPONENT_PARAMS = "component_params";
    public static final String CONFIG_PARAMS = "config_params";

    //创建运行所需参数
    public static final String IP_PORT = "120.27.69.55:8020";
    //public static final String IP_PORT = "colosseum.cn1.utools.club";
    public static final String OUTPUT_DIR = "/nfs/colosseum";
    public static final int PERFORMANCE_RETRY_TIMES = 1;
    public static final int REPORT_RETRY_TIMES = 1;
    public static final int REPORT_RETRY_INTERVAL = 2;

    //内置算法-组件顺序：算法名
    public static final Map<String,String> BUILD_IN_ALGORITHM = new HashMap<>();

    static {
        BUILD_IN_ALGORITHM.put("select_all@@@default","default");
        BUILD_IN_ALGORITHM.put("select_all@@@random_sample@@@random_tuning","random");
        BUILD_IN_ALGORITHM.put("select_all@@@random_sample@@@actgan","actgan");
        BUILD_IN_ALGORITHM.put("select_all@@@random_sample@@@random_forest@@@genetic_algorithm","rfhoc");
        BUILD_IN_ALGORITHM.put("select_all@@@random_sample@@@lasso_select@@@gaussian_process@@@random_search","ottertune");
        BUILD_IN_ALGORITHM.put("select_all@@@space_search","bestconfig");
        BUILD_IN_ALGORITHM.put("sensitivity_analysis@@@bo","sa_bo");
    }
}
