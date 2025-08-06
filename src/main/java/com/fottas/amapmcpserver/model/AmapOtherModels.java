package com.fottas.amapmcpserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yinh
 */
public class AmapOtherModels {
    // ====================== 距离测量API相关 - 新增 ======================

    /**
     * 距离测量请求 - 新增
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistanceRequest {
        @NotBlank(message = "起点坐标不能为空")
        private String origins;  // 起点坐标，支持多个用|分隔

        @NotBlank(message = "终点坐标不能为空")
        private String destination;  // 终点坐标

        private String type = "0";  // 距离测量类型：0-直线距离，1-驾车距离，3-步行距离

        private String output = "json";

        public DistanceRequest(String origins, String destination) {
            this.origins = origins;
            this.destination = destination;
        }

        public DistanceRequest(String origins, String destination, String type) {
            this.origins = origins;
            this.destination = destination;
            this.type = type;
        }
    }
    @EqualsAndHashCode(callSuper = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class DistanceResponse extends AmapApiModels.BaseResponse<List<DistanceInfo>> {
        @JsonProperty("results")
        private List<DistanceInfo> results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class DistanceInfo {
        @JsonProperty("origin_id")
        private String originId;

        @JsonProperty("dest_id")
        private String destId;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("duration")
        private String duration;
    }

    // ====================== 天气API相关 - 优化版 ======================

    /**
     * 天气查询请求 - 优化版
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherRequest {
        @NotBlank(message = "城市编码不能为空")
        private String city;  // 城市编码或城市名称

        private String extensions = "base";  // base-基础；all-详细
        private String output = "json";

        public WeatherRequest(String city) {
            this.city = city;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WeatherResponse extends AmapApiModels.BaseResponse<List<WeatherInfo>> {
        @JsonProperty("lives")
        private List<WeatherInfo> lives;

        @JsonProperty("forecasts")
        private List<WeatherForecast> forecasts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WeatherInfo {
        @JsonProperty("province")
        private String province;

        @JsonProperty("city")
        private String city;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("weather")
        private String weather;  // 天气现象

        @JsonProperty("temperature")
        private String temperature;  // 实时温度

        @JsonProperty("winddirection")
        private String winddirection;  // 风向

        @JsonProperty("windpower")
        private String windpower;  // 风力级别

        @JsonProperty("humidity")
        private String humidity;   // 空气湿度

        @JsonProperty("reporttime")
        private String reporttime;  // 数据发布时间

        @JsonProperty("temperature_float")
        private String temperatureFloat;  // 实时温度（浮点数）

        @JsonProperty("humidity_float")
        private String humidityFloat;     // 空气湿度（浮点数）
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WeatherForecast {
        @JsonProperty("city")
        private String city;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("province")
        private String province;

        @JsonProperty("reporttime")
        private String reporttime;

        @JsonProperty("casts")
        private List<WeatherCast> casts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WeatherCast {
        @JsonProperty("date")
        private String date;

        @JsonProperty("week")
        private String week;

        @JsonProperty("dayweather")
        private String dayweather;

        @JsonProperty("nightweather")
        private String nightweather;

        @JsonProperty("daytemp")
        private String daytemp;

        @JsonProperty("nighttemp")
        private String nighttemp;

        @JsonProperty("daywind")
        private String daywind;

        @JsonProperty("nightwind")
        private String nightwind;

        @JsonProperty("daypower")
        private String daypower;

        @JsonProperty("nightpower")
        private String nightpower;

        @JsonProperty("daytemp_float")
        private String daytempFloat;

        @JsonProperty("nighttemp_float")
        private String nighttempFloat;
    }

    // ====================== IP定位API相关 - 优化版 ======================

    /**
     * IP定位请求 - 优化版
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IpLocationRequest {
        private String ip;  // IP地址，不传则使用请求来源IP
        private String output = "json";

        public IpLocationRequest(String ip) {
            this.ip = ip;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class IpLocationResponse extends AmapApiModels.BaseResponse<IpLocationInfo> {
        @JsonProperty("province")
        private String province;

        @JsonProperty("city")
        private String city;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("rectangle")
        private String rectangle;
    }

    @Data
    public static class IpLocationInfo {
        private String province;
        private String city;
        private String adcode;
        private String rectangle;
    }
}
