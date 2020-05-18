package com.neusoft.bookstore.util;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Liang
 * @date 2020/5/15 9:19
 */
/*
 * 全局异常处理器，用于处理controller层异常， 并且将异常信息返回给前端
 */
@RestControllerAdvice(basePackages = "com.neusoft.bookstore.*.controller")
public class GloableExceptionHandler {

    @ExceptionHandler(Exception.class) //表明处理的异常类型
    public ResponseVo handlerException(Exception e) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL,"系统异常");
        //处理一些我们自定义异常信息返回
        if (e instanceof GoodsInfoException) {
            responseVo.setMsg(e.getMessage());
        }
        //打印异常
        e.printStackTrace();
        return responseVo;
    }
}

