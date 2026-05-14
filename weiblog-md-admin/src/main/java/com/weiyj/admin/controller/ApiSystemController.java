/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-14 11:30
 * @Description
 **/
package com.weiyj.admin.controller;

import com.weiyj.common.controller.BaseController;
import com.weiyj.common.response.Result;
import com.weiyj.common.response.ResultCode;
import com.weiyj.common.util.SystemMonitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
@Tag(name = "系统模块", description = "系统模块查询状态") // 替代 @Api
public class ApiSystemController extends BaseController {



    @Operation(summary = "获取系统状态",description = "根据用户传入的账户密码登录")
    @PostMapping("/getStatus")
    public Map  getStatus(HttpServletRequest request){

        try{
            Map status = SystemMonitor.getStatus();
            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,status);
        }catch (Exception e){
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }

    }


}
