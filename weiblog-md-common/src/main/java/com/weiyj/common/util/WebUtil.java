/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-29 10:05
 * @Description
 **/
package com.weiyj.common.util;

import com.weiyj.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class WebUtil {


    public static Map createResponse(HttpServletRequest request,Integer code,String msg,Object data){
        //TODO 其他操作

        return new Result(code,msg,data);
    }

}
