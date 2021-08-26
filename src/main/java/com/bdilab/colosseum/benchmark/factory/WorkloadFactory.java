package com.bdilab.colosseum.benchmark.factory;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workload.base.Workload;

import java.lang.reflect.InvocationTargetException;

public class WorkloadFactory {
    public static Workload getWorkload(String workloadName,String version){
        workloadName = workloadName.substring(0,1).toUpperCase() + workloadName.substring(1);
        version = "_"+version.replaceAll("\\.","_")+"_"+"Workload";
        String fullClassName = "com.bdilab.colosseum.benchmark.workload."+workloadName + version;
        Workload workload = null;
        try {
            workload = (Workload) Class.forName(fullClassName).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException ignored) {

        }
        return workload;
    }
}
