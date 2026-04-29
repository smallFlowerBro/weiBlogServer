/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 17:04
 * @Description
 **/
package com.weiyj.common.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
public class Result extends HashMap {

    public Result(){}
    public Result(Integer code,String msg,Object data){
        this.put("code",code);
        this.put("msg",msg);
        this.put("data",data);
    }
    public Result(Integer code,String msg){
        this(code,msg,null);
    }

    public void setKeyAndValue(String key,Object value){
        this.put(key,value);
    }



//    // 成功返回
//    public static <T> Result<T> success(T data) {
//        Result<T> r = new Result<>();
//        r.setCode(ResultCode.SUCCESS.code);
//        r.setMsg(ResultCode.SUCCESS.message);
//        r.setData(data);
//        return r;
//    }
//
//    // 失败返回
//    public static <T> Result<T> error(Integer code, String msg) {
//        Result<T> r = new Result<>();
//        r.setCode(code);
//        r.setMsg(msg);
//        return r;
//    }
//
//    // 默认失败
//    public static <T> Result<T> error(String msg) {
//        return error(ResultCode.FAIL.code, msg);
//    }

}
