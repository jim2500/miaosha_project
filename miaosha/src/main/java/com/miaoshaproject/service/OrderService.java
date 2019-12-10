package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * @Classname OrderService
 * @Description TODO
 * @Date 2019/12/7 19:08
 * @Created by sj
 */
public interface OrderService {
    //创建订单
    OrderModel createOrder(Integer itemId,Integer userId,Integer amount) throws BusinessException;

}
