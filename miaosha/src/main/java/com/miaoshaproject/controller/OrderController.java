package com.miaoshaproject.controller;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EnumBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.impl.OrderServiceImpl;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Classname OrderController
 * @Description TODO
 * @Date 2019/12/8 19:51
 * @Created by sj
 */

@Controller("/order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class OrderController extends BaseController {

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private OrderService orderService;

    //创建service请求用来下单
    @RequestMapping(value="/createorder",method={RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})//method={RequestMethod.POST}表名只有在发生post请求时才会触发
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount) throws BusinessException {

        //获取用户id，从session中获得
        Boolean is_login = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(is_login == null || !is_login){
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(itemId,userModel.getId(),amount);

        return CommonReturnType.create(orderModel);
    }

}
