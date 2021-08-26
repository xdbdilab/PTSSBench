package com.bdilab.colosseum.service.experiment;

@Deprecated
public interface RedisService {
    public String execRedisBenchmark(String softwareLocationBOStr, String paramList, String performance);
}
