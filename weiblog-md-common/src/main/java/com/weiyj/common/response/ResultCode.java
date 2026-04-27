/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-04-27 17:10
 * @Description
 **/
package com.weiyj.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
public enum ResultCode {
    // --- 通用状态 ---
    SUCCESS(0, "操作成功"),
    FAIL(-1, "操作失败"),

    // --- 客户端错误 (4xx) ---
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或授权已过期"),
    FORBIDDEN(403, "拒绝访问，权限不足"),
    NOT_FOUND(404, "请求的资源不存在"),

    // --- 服务端错误 (5xx) ---
    INTERNAL_ERROR(500, "服务器内部错误"),
    SYSTEM_BUSY(503, "系统繁忙，请稍后再试"),

    // --- 业务自定义错误 (10000+) ---
    // 建议业务错误码从 10000 开始，避免与 HTTP 标准状态码混淆
    USER_NOT_FOUND(10001, "用户不存在"),
    PASSWORD_ERROR(10002, "用户名或密码错误"),
    TOKEN_INVALID(10003, "Token 无效"),
    PARAM_MISSING(10004, "缺少必要参数");

    /**
     * 状态码
     */
    public final Integer code;

    /**
     * 提示信息
     */
    public final String message;

}
