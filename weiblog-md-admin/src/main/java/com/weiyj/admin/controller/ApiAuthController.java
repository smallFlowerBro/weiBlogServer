/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-28 15:46
 * @Description
 **/
package com.weiyj.admin.controller;

import com.weiyj.common.controller.BaseController;
import com.weiyj.common.response.Result;
import com.weiyj.common.response.ResultCode;
import com.weiyj.common.util.WebUtil;
import com.weiyj.jwt.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "校验模块", description = "包含用户的登录和其他校验接口接口") // 替代 @Api
public class ApiAuthController extends BaseController {


    @Resource
    private AuthService authService;


    @Operation(summary = "用户登录",description = "根据用户传入的账户密码登录")
    @GetMapping("/login")
    public Map login(HttpServletRequest request,
                      HttpServletResponse response,
                      @RequestParam @NotNull(message = "用户名不得为空") String userName,
                      @RequestParam @NotNull(message = "密码不得为空")String password){
        try {
            Map data = new HashMap<>();

            String authToken = authService.auth(userName, password);
            data.put("token", authToken);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, data);
        }catch (BadCredentialsException e){
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }

}
