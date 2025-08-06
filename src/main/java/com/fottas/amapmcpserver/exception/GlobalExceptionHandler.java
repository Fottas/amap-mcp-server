package com.fottas.amapmcpserver.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常，返回标准化的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 错误响应结构
     */
    @Data
    public static class ErrorResponse {
        // Getters and Setters
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, Object> details;

        public ErrorResponse() {
            this.timestamp = LocalDateTime.now().toString();
        }

        public ErrorResponse(int status, String error, String message, String path) {
            this();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

    }

    /**
     * 处理高德地图API相关异常
     */
    @ExceptionHandler(AmapApiException.class)
    public ResponseEntity<ErrorResponse> handleAmapApiException(AmapApiException ex) {
        logger.error("高德地图API异常: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                "Amap API Error",
                "高德地图API调用失败: " + ex.getMessage(),
                ex.getApiPath()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("apiEndpoint", ex.getApiPath());
        details.put("errorCode", ex.getErrorCode());
        details.put("apiMessage", ex.getApiMessage());
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    /**
     * 处理WebClient HTTP异常
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException ex) {
        logger.error("HTTP请求异常: 状态码={}, 响应体={}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        String message = String.format("外部API调用失败 (状态码: %s)", ex.getStatusCode());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                "External API Error",
                message,
                ex.getRequest() != null ? ex.getRequest().getURI().getPath() : "unknown"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("httpStatus", ex.getStatusCode().value());
        details.put("responseBody", ex.getResponseBodyAsString());
        details.put("headers", ex.getHeaders().toSingleValueMap());
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    /**
     * 处理WebClient一般异常
     */
    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientException ex) {
        logger.error("WebClient异常: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "外部服务暂时不可用: " + ex.getMessage(),
                "external-api"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("exceptionType", ex.getClass().getSimpleName());
        details.put("cause", ex.getCause() != null ? ex.getCause().getMessage() : null);
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.warn("参数验证失败: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "请求参数验证失败",
                "validation"
        );

        Map<String, Object> details = new HashMap<>();
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        details.put("fieldErrors", fieldErrors);
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        logger.warn("数据绑定异常: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Binding Failed",
                "请求数据绑定失败",
                "binding"
        );

        Map<String, Object> details = new HashMap<>();
        Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        details.put("fieldErrors", fieldErrors);
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("约束验证失败: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                "数据约束验证失败",
                "constraint"
        );

        Map<String, Object> details = new HashMap<>();
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));
        details.put("violations", violations);
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("非法参数异常: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                "请求参数无效: " + ex.getMessage(),
                "argument"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        logger.error("空指针异常", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "服务器内部错误，请稍后重试",
                "internal"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("errorType", "NullPointerException");
        details.put("stackTrace", ex.getStackTrace()[0].toString());
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理MCP相关异常
     */
    @ExceptionHandler(McpServerException.class)
    public ResponseEntity<ErrorResponse> handleMcpServerException(McpServerException ex) {
        logger.error("MCP服务器异常: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "MCP Server Error",
                "MCP服务器错误: " + ex.getMessage(),
                "mcp"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("mcpOperation", ex.getOperation());
        details.put("errorCode", ex.getErrorCode());
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("运行时异常: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Runtime Error",
                "服务器运行时错误: " + ex.getMessage(),
                "runtime"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("exceptionType", ex.getClass().getSimpleName());
        details.put("cause", ex.getCause() != null ? ex.getCause().getMessage() : null);
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("未处理的异常: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "服务器内部错误，请联系技术支持",
                "generic"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("exceptionType", ex.getClass().getSimpleName());
        details.put("message", ex.getMessage());
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // ====================== 自定义异常类 ======================

    /**
     * 高德地图API异常
     */
    public static class AmapApiException extends RuntimeException {
        private final String apiPath;
        private final String errorCode;
        private final String apiMessage;

        public AmapApiException(String message, String apiPath, String errorCode, String apiMessage) {
            super(message);
            this.apiPath = apiPath;
            this.errorCode = errorCode;
            this.apiMessage = apiMessage;
        }

        public AmapApiException(String message, String apiPath, String errorCode, String apiMessage, Throwable cause) {
            super(message, cause);
            this.apiPath = apiPath;
            this.errorCode = errorCode;
            this.apiMessage = apiMessage;
        }

        public String getApiPath() { return apiPath; }
        public String getErrorCode() { return errorCode; }
        public String getApiMessage() { return apiMessage; }
    }

    /**
     * MCP服务器异常
     */
    public static class McpServerException extends RuntimeException {
        private final String operation;
        private final String errorCode;

        public McpServerException(String message, String operation, String errorCode) {
            super(message);
            this.operation = operation;
            this.errorCode = errorCode;
        }

        public McpServerException(String message, String operation, String errorCode, Throwable cause) {
            super(message, cause);
            this.operation = operation;
            this.errorCode = errorCode;
        }

        public String getOperation() { return operation; }
        public String getErrorCode() { return errorCode; }
    }

    /**
     * 限流异常
     */
    public static class RateLimitException extends RuntimeException {
        private final String resourceId;
        private final long retryAfter;

        public RateLimitException(String message, String resourceId, long retryAfter) {
            super(message);
            this.resourceId = resourceId;
            this.retryAfter = retryAfter;
        }

        public String getResourceId() { return resourceId; }
        public long getRetryAfter() { return retryAfter; }
    }

    /**
     * 处理限流异常
     */
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(RateLimitException ex) {
        logger.warn("API限流异常: 资源={}, 重试时间={}秒", ex.getResourceId(), ex.getRetryAfter());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Rate Limit Exceeded",
                "API调用频率超限，请稍后重试",
                "rate-limit"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("resourceId", ex.getResourceId());
        details.put("retryAfter", ex.getRetryAfter());
        details.put("suggestion", "请等待 " + ex.getRetryAfter() + " 秒后重试");
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfter()))
                .body(errorResponse);
    }

    /**
     * 配置异常
     */
    public static class ConfigurationException extends RuntimeException {
        private final String configKey;

        public ConfigurationException(String message, String configKey) {
            super(message);
            this.configKey = configKey;
        }

        public ConfigurationException(String message, String configKey, Throwable cause) {
            super(message, cause);
            this.configKey = configKey;
        }

        public String getConfigKey() { return configKey; }
    }

    /**
     * 处理配置异常
     */
    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<ErrorResponse> handleConfigurationException(ConfigurationException ex) {
        logger.error("配置异常: 配置键={}, 错误={}", ex.getConfigKey(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Configuration Error",
                "服务器配置错误，请联系管理员",
                "configuration"
        );

        Map<String, Object> details = new HashMap<>();
        details.put("configKey", ex.getConfigKey());
        details.put("suggestion", "请检查配置文件中的 " + ex.getConfigKey() + " 配置项");
        errorResponse.setDetails(details);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}