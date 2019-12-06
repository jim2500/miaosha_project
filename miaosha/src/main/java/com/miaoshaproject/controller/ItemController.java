package com.miaoshaproject.controller;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.miaoshaproject.controller.viewobject.ItemVO;
import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname ItemController
 * @Description TODO
 * @Date 2019/11/30 20:55
 * @Created by sj
 */

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class ItemController extends BaseController {

    @Resource
    private ItemService itemService;

    //创建service请求用来创建商品
    @RequestMapping(value="/create",method={RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})//method={RequestMethod.POST}表名只有在发生post请求时才会触发
    @ResponseBody
    public CommonReturnType creatIteam(@RequestParam(name = "title")String title,
                                       @RequestParam(name = "description")String description,
                                       @RequestParam(name = "stock")Integer stock,
                                       @RequestParam(name = "price")BigDecimal price,
                                       @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setStock(stock);
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.creatItem(itemModel);

        ItemVO itemVO = convertItemVOFromItemModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);

    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }

    //商品详情页浏览
    @RequestMapping(value="/get",method={RequestMethod.GET})//method={RequestMethod.GET}表名只有在发生get请求时才会触发
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = convertItemVOFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }


    //商品列表页面浏览
    @RequestMapping(value="/list",method={RequestMethod.GET})//method={RequestMethod.GET}表名只有在发生get请求时才会触发
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList = itemService.listItem();

        //使用streamapi将list内的itemModel转化为itemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }
}
