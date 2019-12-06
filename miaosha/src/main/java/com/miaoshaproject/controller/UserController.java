package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EnumBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Classname UserController
 * @Description TODO
 * @Date 2019/11/18 15:29
 * @Created by sj
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest httpServletRequest;

    //用户登录
    @RequestMapping(value="/login",method={RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})//method={RequestMethod.POST}表名只有在发生post请求时才会触发
    @ResponseBody
    @CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验：校验从前端拿到的数据
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)|| StringUtils.isEmpty(password)){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务：判断是否存在于数据库中
        UserModel userModel = userService.login(telphone, this.EncodeByMd5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }

    //用户注册接口
    @RequestMapping(value="/register",method={RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})//method={RequestMethod.POST}表名只有在发生post请求时才会触发
    @ResponseBody
    @CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
    public CommonReturnType register(@RequestParam(name="telphone")String telphone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")Integer gender,
                                     @RequestParam(name="age")Integer age,
                                     @RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpcode相符合,用druid是因为有判空
        //ajax中的跨域请求无法共享session数据，所以这里的inSessionOtpCode是null，需要加注解
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
           throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不正确");
        }

        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(Byte.valueOf(String.valueOf(gender)));
        userModel.setRegisterMode("byphone");
        userModel.setTelphone(telphone);
        //加密
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    //密码加密方法
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    //用户获取otp短信接口
    @RequestMapping(value="/getotp",method={RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})//method={RequestMethod.POST}表名只有在发生post请求时才会触发
    @ResponseBody
    @CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        //获取otpCode
        String otpCode = userService.getOtp(telphone);

        //将OTP验证码通对应用户手机号相关联,key value 形式，企业级用redis，这里使用httpsession的方式绑定他的手机号与otpcode
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telphone: "+telphone+"otpcode: "+otpCode);

        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel == null){
            throw new BusinessException(EnumBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO =  convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }


}