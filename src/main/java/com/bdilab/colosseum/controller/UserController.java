package com.bdilab.colosseum.controller;

import com.bdilab.colosseum.domain.User;
import com.bdilab.colosseum.domain.UserRole;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.UserRoleService;
import com.bdilab.colosseum.service.UserService;
import com.bdilab.colosseum.utils.EncryptUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UserController
 * @Author wx
 * @Date 2020/12/10 0010 15:30
 **/
@Api(tags = "用户管理API")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);


    /**
     * 用户登录
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResponseResult login(@ApiParam(value = "用户名", name = "username")
                                @RequestParam("username") String username,
                                @ApiParam(value = "密码", name = "password")
                                @RequestParam("password") String password,
                                HttpSession httpSession) {
        String md5Password = EncryptUtil.getMd5(password);
        User user = userService.userLoginCheck(username,md5Password);
        if(user != null){
            List<UserRole> userRoleList = userRoleService.findByUserId(user.getId());
            if(userRoleList.size() == 0) {
                logger.error("用户[{}]没有对应的角色信息",user.getUsername());
                return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "内部服务器错误", false);
            }
            int type = userRoleList.get(0).getType();
            httpSession.setAttribute("user_id", user.getId());
            httpSession.setAttribute("name", user.getUsername());
            //在session中添加角色类型 0：普通用户 1：管理员
            httpSession.setAttribute("role",type);
            Map<String, Object> data = new HashMap<>(2);
            data.put("uid",user.getId());
            data.put("name",user.getUsername());
            data.put("role",type);
            logger.info("========================");
            logger.info("用户 {} 登录成功", data);
            return new ResponseResult(HttpCode.OK.getCode(), "登录成功", true, data);
        }
        return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "用户名或密码错误", false);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResponseResult register(@ApiParam(value = "用户名", name = "username")
                                   @RequestParam("username") String username,
                                   @ApiParam(value = "密码", name = "password")
                                   @RequestParam("password") String password) {
        if(userService.userRegisterCheck(username)!=null) {
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "注册失败，用户名已存在", false);
        }
        User user = new User();
        UserRole userRole = new UserRole();
        String md5Password = EncryptUtil.getMd5(password);
        user.setUsername(username);
        user.setPassword(md5Password);
        userService.saveUser(user);
        logger.info("========================");
        logger.info("用户 {} 注册成功", username);
        return new ResponseResult(HttpCode.OK.getCode(), "注册成功", true);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/get")
    public ResponseResult getDetail(HttpSession session) {
        Long userId = Long.valueOf(session.getAttribute("user_id").toString());
        String username = session.getAttribute("name").toString();
        logger.info("========================");
        logger.info("用户 {} 获取用户信息成功", username);
        return new ResponseResult(HttpCode.OK, userService.findById(userId));
    }

    /**
     * 用户注销
     * @param httpSession
     * @return
     */
    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public ResponseResult logout(HttpSession httpSession) {
        String username = httpSession.getAttribute("name").toString();
        // 清除session
        httpSession.invalidate();
        logger.info("========================");
        logger.info("用户 {} 注销成功", username);
        return new ResponseResult(HttpCode.OK.getCode(), "注销成功", true);
    }

}
