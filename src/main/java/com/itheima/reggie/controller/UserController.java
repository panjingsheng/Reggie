package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
@PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
    String phone = user.getPhone();
    if (StringUtils.isNotEmpty(phone)) {
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
log.info("code={}",code);
        //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
session.setAttribute(phone,code);

        return R.success("验证码发送成功");
    }
    return R.error("验证码发送失败");
    }
}
