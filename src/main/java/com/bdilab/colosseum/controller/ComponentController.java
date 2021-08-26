package com.bdilab.colosseum.controller;

import com.bdilab.colosseum.domain.ComponentParam;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.ComponentService;
import com.bdilab.colosseum.vo.ComponentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @Author duyeye
 * @Date 2020/12/29 0029 14:53
 */
@Controller
@Api(value = "ComponentController")
public class ComponentController {

    @Autowired
    ComponentService componentService;

    @ResponseBody
    @ApiOperation("加载系统预置组件")
    @RequestMapping(value = "/component/loadPubicComponent", method = RequestMethod.POST)
    public ResponseResult loadPubicComponent(HttpSession httpSession){
        Long userId = Long.valueOf(httpSession.getAttribute("user_id").toString());
        List<ComponentVO> data = componentService.loadPublicComponent();
        return new ResponseResult(HttpCode.OK.getCode(), "加载组件成功", true, data);
    }

    @ResponseBody
    @ApiOperation("创建组件")
    @RequestMapping(value = "/component/createComponent", method = RequestMethod.POST)
    public ResponseResult createComponent(@RequestParam @ApiParam(value = "组件名称")String name,
                                          @RequestParam @ApiParam(value = "组件类型")Byte type,
                                          @RequestParam @ApiParam(value = "组件描述")String desc,
                                          @RequestParam @ApiParam(value = "组件镜像名称")String image,
                                          @RequestParam @ApiParam(value = "组件参数列表")List<ComponentParam> param,
                                          @RequestParam @ApiParam(value = "组件文件地址")String componentFile,
                                          @RequestParam(required = false) @ApiParam(value = "组件logo地址")MultipartFile logo,
                                          HttpSession httpSession){
        Long userId = Long.valueOf(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = componentService.createComponent(name,type,desc,image,param,userId,componentFile,logo);
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "成功创建系统组件", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "创建系统组件失败", false);
        }
    }

    @ResponseBody
    @ApiOperation("删除组件")
    @RequestMapping(value = "/component/deleteComponentById", method = RequestMethod.POST)
    public ResponseResult deleteComponentById(@RequestParam @ApiParam(value = "组件Id")Long componentId,
                                              HttpSession httpSession){
        Long userId = Long.valueOf(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = componentService.deleteComponentById(componentId);
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "成功删除组件", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "删除组件失败", false);
        }
    }

    @ResponseBody
    @ApiOperation("分页展示用户自定义组件")
    @RequestMapping(value = "/component/selectUserComponent", method = RequestMethod.POST)
    public ResponseResult selectUserComponent(@RequestParam @ApiParam(value = "页数")int pageNum,
                                              @RequestParam @ApiParam(value = "每页记录数")int pageSize,
                                              HttpSession httpSession){
        Long userId = Long.valueOf(httpSession.getAttribute("user_id").toString());
        Map<String, Object> data = componentService.selectUserComponent(pageNum, pageSize, userId);
        return new ResponseResult(HttpCode.OK.getCode(), "成功获取用户所有自定义组件", true, data);

    }

}
