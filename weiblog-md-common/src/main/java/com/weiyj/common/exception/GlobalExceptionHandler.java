/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 16:11
 * @Description
 **/
package com.weiyj.common.exception;

import com.weiyj.common.response.Result;
import com.weiyj.common.response.ResultCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. 处理具体的业务异常 (优先级高)
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 2. 处理参数校验异常 (优先级高)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        return Result.error(ResultCode.BAD_REQUEST.code, ResultCode.BAD_REQUEST.message);
    }

    // 3. 处理所有其他异常 (兜底，优先级低)
    // 因为 Exception 是上面两个类的父类，所以只有上面都没匹配到，才会进这里
    @ExceptionHandler(Exception.class)
    public Result<?> handleAllException(Exception e) {
        return Result.error(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.message);
    }
}
