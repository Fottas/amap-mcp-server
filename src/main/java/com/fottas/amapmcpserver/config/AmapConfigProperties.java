package com.fottas.amapmcpserver.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 高德地图API配置属性类
 * 统一管理所有高德地图API的配置信息
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "amap.api")
@Validated
public class AmapConfigProperties {

    // Getters and Setters
    /**
     * 高德地图API基础URL
     */
    @NotBlank(message = "高德地图API基础URL不能为空")
    private String baseUrl = "https://restapi.amap.com";

    /**
     * 高德地图API Key
     */
    @NotBlank(message = "高德地图API Key不能为空")
    private String key;

    /**
     * 请求超时时间
     */
    @NotNull
    private Duration timeout = Duration.ofSeconds(30);

    /**
     * 重试配置
     */
    @Valid
    @NestedConfigurationProperty
    private RetryConfig retry = new RetryConfig();

    /**
     * API端点配置
     */
    @Valid
    @NestedConfigurationProperty
    private EndpointsConfig endpoints = new EndpointsConfig();

    /**
     * HTTP客户端配置
     */
    @Valid
    @NestedConfigurationProperty
    private HttpClientConfig httpClient = new HttpClientConfig();

    /**
     * 限流配置
     */
    @Valid
    @NestedConfigurationProperty
    private RateLimitConfig rateLimit = new RateLimitConfig();

    /**
     * 重试配置
     */
    @Data
    public static class RetryConfig {
        // Getters and Setters
        /**
         * 最大重试次数
         */
        @Min(value = 0, message = "重试次数不能小于0")
        private int maxAttempts = 3;

        /**
         * 重试延迟时间
         */
        @NotNull
        private Duration delay = Duration.ofSeconds(1);

        /**
         * 重试延迟倍数
         */
        @Min(value = 1, message = "重试延迟倍数不能小于1")
        private double multiplier = 2.0;

        /**
         * 最大重试延迟时间
         */
        @NotNull
        private Duration maxDelay = Duration.ofSeconds(30);

    }

    /**
     * API端点配置
     */
    @Data
    public static class EndpointsConfig {
        // Getters and Setters
        /**
         * 地理编码API端点
         */
        private String geocoding = "/v3/geocode/geo";

        /**
         * 逆地理编码API端点
         */
        private String reverseGeocoding = "/v3/geocode/regeo";

        /**
         * 路线规划2.0 API端点配置
         */
        @Valid
        @NestedConfigurationProperty
        private RoutePlanningConfig routePlanning = new RoutePlanningConfig();

        /**
         * POI搜索2.0 API端点配置
         */
        @Valid
        @NestedConfigurationProperty
        private PoiSearchConfig poiSearch = new PoiSearchConfig();

        /**
         * 天气API端点配置
         */
        private String weather = "/v3/weather/weatherInfo";

        /**
         * IP定位API端点
         */
        private String ipLocation = "/v3/ip";

        /**
         * 行政区划API端点
         */
        private String district = "/v3/config/district";

        /**
         * 交通态势API端点
         */
        private String traffic = "/v3/traffic/status/rectangle";

    }

    /**
     * 路线规划2.0 API端点配置
     */
    @Data
    public static class RoutePlanningConfig {
        // Getters and Setters
        /**
         * 驾车路线规划
         */
        private String driving = "/v5/direction/driving";

        /**
         * 步行路线规划
         */
        private String walking = "/v5/direction/walking";

        /**
         * 骑行路线规划
         */
        private String bicycling = "/v5/direction/bicycling";

        /**
         * 公交路线规划
         */
        private String transit = "/v5/direction/transit";

        /**
         * 电动车路线规划
         */
        private String electricBike = "/v5/direction/electrobike";

    }

    /**
     * POI搜索2.0 API端点配置
     */
    @Data
    public static class PoiSearchConfig {
        // Getters and Setters
        /**
         * 关键字搜索
         */
        private String text = "/v5/place/text";

        /**
         * 周边搜索
         */
        private String around = "/v5/place/around";

        /**
         * 多边形区域搜索
         */
        private String polygon = "/v5/place/polygon";

        /**
         * POI详情查询
         */
        private String detail = "/v5/place/detail";

    }

    /**
     * HTTP客户端配置
     */
    @Data
    public static class HttpClientConfig {
        // Getters and Setters
        /**
         * 连接池最大连接数
         */
        @Min(value = 1, message = "连接池最大连接数不能小于1")
        private int maxConnections = 100;

        /**
         * 每个路由的最大连接数
         */
        @Min(value = 1, message = "每个路由的最大连接数不能小于1")
        private int maxConnectionsPerRoute = 20;

        /**
         * 连接超时时间
         */
        @NotNull
        private Duration connectionTimeout = Duration.ofSeconds(10);

        /**
         * 读取超时时间
         */
        @NotNull
        private Duration readTimeout = Duration.ofSeconds(30);

        /**
         * 连接空闲超时时间
         */
        @NotNull
        private Duration idleTimeout = Duration.ofMinutes(5);

        /**
         * 是否启用HTTP/2
         */
        private boolean http2Enabled = false;

        /**
         * 用户代理
         */
        private String userAgent = "AmapMcpServer/1.0";

    }

    /**
     * 限流配置
     */
    @Data
    public static class RateLimitConfig {
        // Getters and Setters
        /**
         * 是否启用限流
         */
        private boolean enabled = true;

        /**
         * QPS限制
         */
        @Min(value = 1, message = "QPS限制不能小于1")
        private int qps = 100;

        /**
         * 突发流量限制
         */
        @Min(value = 1, message = "突发流量限制不能小于1")
        private int burstCapacity = 200;

        /**
         * 限流时间窗口
         */
        @NotNull
        private Duration window = Duration.ofSeconds(1);

        /**
         * 不同API的限流配置
         */
        private Map<String, ApiLimitConfig> apiLimits = new HashMap<>();

    }

    /**
     * API限流配置
     */
    @Data
    public static class ApiLimitConfig {
        // Getters and Setters
        /**
         * QPS限制
         */
        @Min(value = 1, message = "QPS限制不能小于1")
        private int qps = 50;

        /**
         * 突发流量限制
         */
        @Min(value = 1, message = "突发流量限制不能小于1")
        private int burstCapacity = 100;

    }

    /**
     * 获取完整的API URL
     *
     * @param endpoint API端点
     * @return 完整的API URL
     */
    public String getFullUrl(String endpoint) {
        return baseUrl + endpoint;
    }

    /**
     * 获取带有API Key的参数映射
     *
     * @return 包含API Key的参数映射
     */
    public Map<String, String> getBaseParams() {
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        return params;
    }

    /**
     * 检查配置是否有效
     *
     * @return 配置是否有效
     */
    public boolean isValid() {
        return key != null && !key.trim().isEmpty() &&
                baseUrl != null && !baseUrl.trim().isEmpty();
    }
}