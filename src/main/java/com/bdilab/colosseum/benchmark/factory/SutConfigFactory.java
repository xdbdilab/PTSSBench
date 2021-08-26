package com.bdilab.colosseum.benchmark.factory;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;

import java.lang.reflect.InvocationTargetException;

public class SutConfigFactory {
    public static SutConfig getSutConfig(String sutName,String version){
        sutName = sutName.substring(0,1).toUpperCase() + sutName.substring(1);
        version = "_"+version.replaceAll("\\.","_")+"_"+"SutConfig";
        String fullClassName = "com.bdilab.colosseum.benchmark.sutconfig."+sutName + version;
        SutConfig sutConfig = null;
        try {
            sutConfig = (SutConfig) Class.forName(fullClassName).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException ignored) {

        }
        return sutConfig;
    }
}
