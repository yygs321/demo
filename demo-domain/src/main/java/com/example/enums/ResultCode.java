package com.example.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCode {

    // Success
    SUCCESS("200", "OK", "성공"),

    // Client Errors
    BAD_REQUEST("400", "Bad Request", "잘못된 요청입니다."),
    UNAUTHORIZED("401", "Unauthorized", "인증되지 않은 요청입니다."),
    FORBIDDEN("403", "Forbidden", "접근 권한이 없습니다."),
    NOT_FOUND("404", "Not Found", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed", "허용되지 않는 HTTP 메소드입니다."),
    NOT_ACCEPTABLE("406", "Not Acceptable", "요청한 미디어 타입을 처리할 수 없습니다."),
    CONFLICT("409", "Conflict", "요청이 현재 리소스 상태와 충돌합니다."),
    UNPROCESSABLE_ENTITY("422", "Unprocessable Entity", "요청을 처리할 수 없습니다. 요청 본문을 확인해주세요."),

    // Server Errors
    INTERNAL_SERVER_ERROR("500", "Internal Server Error", "서버 내부 오류가 발생했습니다."),
    NOT_IMPLEMENTED("501", "Not Implemented", "구현되지 않은 기능입니다."),
    BAD_GATEWAY("502", "Bad Gateway", "잘못된 게이트웨이 응답입니다."),
    SERVICE_UNAVAILABLE("503", "Service Unavailable", "서비스를 사용할 수 없습니다."),
    GATEWAY_TIMEOUT("504", "Gateway Timeout", "게이트웨이 시간 초과입니다.");

    private final String code;
    private final String systemMessage;
    private final String userMessage;

    public String getMessage() {
        return systemMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
