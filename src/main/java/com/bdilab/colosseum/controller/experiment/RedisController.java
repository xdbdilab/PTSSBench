package com.bdilab.colosseum.controller.experiment;

import com.bdilab.colosseum.service.experiment.RedisService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jin Lingming
 * @version 1.0
 * @date 2021/03/08 15:05
 */
@Deprecated
@RestController
@RequestMapping("/redis")
public class RedisController {
    @Resource
    RedisService redisService;

    @ApiOperation("执行redisBenchmark测试redis")
    @GetMapping("/exec")
    public String execRedisBenchmark(String softwareLocationBOStr, String paramList, String performance){
        // 参数列表表示形式：参数名=参数值
        return redisService.execRedisBenchmark(softwareLocationBOStr, paramList, performance);
    }

}
