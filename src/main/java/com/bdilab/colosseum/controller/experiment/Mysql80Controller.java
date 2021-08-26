package com.bdilab.colosseum.controller.experiment;

import com.bdilab.colosseum.service.experiment.Mysql80Service;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/21 15:07
 */
@Deprecated
@RestController
@RequestMapping("/mysql80")
public class Mysql80Controller {
    @Resource
    Mysql80Service mysql80Service;

    @ApiOperation("执行sysbench测试mysql80")
    @GetMapping("/exec")
    public String execSysbenchMysql80(String softwareLocationBOStr,
                                      String paramList,
                                      String performance){
        // 参数列表表示形式：参数名=参数值
        return mysql80Service.execSysbenchMysql80(softwareLocationBOStr, paramList, performance);
    }

}
