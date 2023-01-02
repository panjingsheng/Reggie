package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
   @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;
@PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
    String phone = user.getPhone();
    if (StringUtils.isNotEmpty(phone)) {
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
log.info("code={}",code);
        //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
//session.setAttribute(phone,code);

redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

        return R.success("验证码发送成功");
    }
    return R.error("验证码发送失败");
    }





    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //Object codeInSession = session.getAttribute(phone);
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (codeInSession!=null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

          redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }








}
