package com.uzero.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uzero.reggie.common.R;
import com.uzero.reggie.entity.User;
import com.uzero.reggie.service.UserService;
import com.uzero.reggie.utils.SMSUtils;
import com.uzero.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-04  18:31:04
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     * @param user 用户
     * @param session session
     * @return 返回发送信息
     * @throws Exception 异常
     */
    @PostMapping("/sendMsg")
    public R<String> sendMag(@RequestBody User user, HttpSession session) throws Exception {
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4为验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码：{}", code);

            //调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("Uzero", "SMS_275535202", phone, code);

            //需要将生成的验证码保存到Session
//            session.setAttribute(phone, code);

            //将生成的验证码缓存到redis中，有效时间为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);


            return R.success("发送成功");
        }


        return R.error("发送失败");
    }


    /**
     * 登录
     * @param map  集合
     * @param session  session
     * @return 返回登录结果
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info("登录：{}", map.toString());
        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从Redis中获取缓存的验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对（页面提交的验证码和Session中的验证码比对）
        if (codeInRedis != null && codeInRedis.equals(code)){
            //登录成功
            //判断是否是新用户，如果不是，创建新用户账号
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getPhone, phone);
            User user = userService.getOne(qw);
            if (user == null){
                //创建新用户账户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName("userR" + user.getPhone());
                userService.save(user);
            }

            session.setAttribute("user", user.getId());

            //如果用户登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        //登录失败
        return R.error("登录失败");
    }


    /**
     * 退出登录
     * @return 结果信息
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
