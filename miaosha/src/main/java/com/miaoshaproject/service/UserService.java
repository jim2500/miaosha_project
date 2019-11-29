package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

/**
 * @Classname UserService
 * @Description TODO
 * @Date 2019/11/18 15:33
 * @Created by sj
 */
public interface UserService {
    //通过用户id获取用户对象的方法
    UserModel getUserById(Integer id);

    //获取短信otp
    String getOtp(String telphone);


    //用戶注冊
    void register(UserModel userModel) throws BusinessException;

    //用户登录
    /*
    telphone:用户注册手机
    password：用户加密后的密码
     */
    UserModel login(String telphone,String encrptPassword) throws BusinessException;
}
