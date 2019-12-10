package com.miaoshaproject.service.impl;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.google.protobuf.Enum;
import com.miaoshaproject.dao.OrderDOMapper;
import com.miaoshaproject.dao.SequenceDOMapper;
import com.miaoshaproject.dataobject.OrderDO;
import com.miaoshaproject.dataobject.SequenceDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EnumBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Classname OrderServiceImpl
 * @Description TODO
 * @Date 2019/12/7 19:10
 * @Created by sj
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    UserService userService;

    @Resource
    ItemService itemService;

    @Resource
    OrderDOMapper orderDOMapper;

    @Resource
    SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer itemId, Integer userId, Integer amount) throws BusinessException {
        //入参校验:1、校验商品信息是否存在；2、校验用户信息是否存在；3、校验购买数量是否合法
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }

        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }

        if(amount<0 || amount>99){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量不正确");
        }

        //下单减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result){
            throw new BusinessException(EnumBusinessError.STOCK_NOT_ENOUGH);
        }

        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setAmount(amount);
        orderModel.setItemId(itemModel.getId());
        orderModel.setUserId(userModel.getId());
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));

        //生成订单号
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertOrderDOFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //返回前端
        return orderModel;
    }

    private OrderDO convertOrderDOFromOrderModel(OrderModel orderModel){
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        return orderDO;
    }

    //生成订单号
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //订单提交失败，事务回滚，但是订单号唯一，不会滚，用上面的注解重新开启新事务并提交提交
    String generateOrderNo(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();

        //前8位为日期
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //中间6位为递增序列,思路在数据库中创建一张表格专门用来更新序列
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequence+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);

        //凑足六位
        for(int i=0;i<6-sequenceStr.length();i++){
            stringBuilder.append(0);
        }

        stringBuilder.append(sequenceStr);

        //后两位是分库分表
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
