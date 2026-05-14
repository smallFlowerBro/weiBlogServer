/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-28 15:00
 * @Description
 **/
package com.weiyj.common.controller;

import com.weiyj.common.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public  abstract class BaseController {
    public Map getParameterMap(HttpServletRequest request)  {
        try{
            return WebUtil.getRequestParam(request);
        }catch (Exception e){
            throw new RuntimeException("获取参数出现异常:"+e.getMessage());
        }
    }
}
