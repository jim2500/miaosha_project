package com.miaoshaproject.error;

/**
 * @Classname EnumBusinessError
 * @Description TODO
 * @Date 2019/11/20 15:00
 * @Created by sj
 */
public enum EnumBusinessError implements CommonError {
    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKOWN_ERROR(10002,"未知错误"),
    //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002,"账号或密码不正确")
    ;

    private int errCode;
    private String errMsg;

    private EnumBusinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    //该方法主要用于修改通用错误的错误信息，即同一个错误码不同的错误信息
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
