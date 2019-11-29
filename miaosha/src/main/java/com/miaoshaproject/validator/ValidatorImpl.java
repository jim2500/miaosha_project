package com.miaoshaproject.validator;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.Validation;
import javax.xml.validation.Validator;

/**
 * @Classname ValidatorImpl
 * @Description TODO
 * @Date 2019/11/29 18:03
 * @Created by sj
 */

@Component
public class ValidatorImpl implements InitializingBean{

    private javax.validation.Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator通过工厂的初始化方式使其实例化
        
    }
}
