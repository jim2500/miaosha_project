package com.miaoshaproject.response;

/**
 * @Classname CommonReturnType
 * @Description TODO
 * @Date 2019/11/19 15:31
 * @Created by sj
 */
public class CommonReturnType {
    //表明对应请求的返回处理结果“success”或“fail”,success则data内返回前端需要的json数据，fail则data内使用通用的错误码格式
    private String status;

    private Object data;

    //定义一个通用的创建方法
    public static CommonReturnType creat(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
