package com.miaoshaproject.error;

/**
 * @Classname CommonError
 * @Description TODO
 * @Date 2019/11/20 14:58
 * @Created by sj
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);

}
