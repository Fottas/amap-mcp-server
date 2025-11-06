package com.fottas.amapmcpserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

/**
 * 高德地图API数据模型定义 - 优化版
 * 基于高德API文档：https://amap.apifox.cn/
 */
public class AmapApiModels {

    // ====================== API结果接口 ======================

    /**
     * API结果接口
     */
    public interface ApiResult<T> {
        boolean isSuccess();
        String getMessage();
        T getData();
    }

    // ====================== 通用响应基类 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BaseResponse<T> implements ApiResult<T> {
        @JsonProperty("status")
        private String status;

        @JsonProperty("info")
        private String info;

        @JsonProperty("infocode")
        private String infocode;

        @JsonProperty("count")
        private String count;

        private T data;

        @Override
        public boolean isSuccess() {
            return Objects.equals(this.status, "1");
        }

        @Override
        public String getMessage() {
            return this.info;
        }

        @Override
        public T getData() {
            return this.data;
        }
    }

    // ====================== 地理编码相关 - 优化版 ======================

    /**
     * 地理编码请求 - 优化版
     */
    @Data
    public static class GeocodingRequest {
        @NotBlank(message = "地址不能为空")
        private String address;

        private String city;
        private String batch = "false";
        private String output = "json";

        public GeocodingRequest() {}

        public GeocodingRequest(String address) {
            this.address = address;
        }

        public GeocodingRequest(String address, String city) {
            this.address = address;
            this.city = city;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class GeocodingResponse extends BaseResponse<List<AmapApiModels.Geocode>> {
        @JsonProperty("geocodes")
        private List<Geocode> geocodes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geocode {
        @JsonProperty("country")
        private String country;

        @JsonProperty("province")
        private String province;

        @JsonProperty("city")
        private String city;

        @JsonProperty("citycode")
        private String citycode;

        @JsonProperty("district")
        private String district;

        @JsonProperty("street")
        private String street;

        @JsonProperty("number")
        private String number;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("location")
        private String location;

        @JsonProperty("level")
        private String level;
    }

    // ====================== 逆地理编码相关 - 优化版 ======================

    /**
     * 逆地理编码请求 - 优化版
     */
    @Data
    public static class ReverseGeocodingRequest {
        @NotBlank(message = "经纬度坐标不能为空")
        @Pattern(regexp = "^\\d+\\.\\d+,\\d+\\.\\d+$", message = "坐标格式错误，应为：经度,纬度")
        private String location;

        private String poitype;
        private String radius = "1000";
        private String extensions = "base";
        private String batch = "false";
        private String roadlevel = "0";
        private String output = "json";

        public ReverseGeocodingRequest() {}

        public ReverseGeocodingRequest(String location) {
            this.location = location;
        }

        public ReverseGeocodingRequest(double longitude, double latitude) {
            this.location = longitude + "," + latitude;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ReverseGeocodingResponse extends BaseResponse<AmapApiModels.Regeocode> {
        @JsonProperty("regeocode")
        private Regeocode regeocode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Regeocode {
        @JsonProperty("addressComponent")
        private AddressComponent addressComponent;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressComponent {
        @JsonProperty("country")
        private String country;

        @JsonProperty("province")
        private String province;

        @JsonProperty("city")
        private String city;

        @JsonProperty("district")
        private String district;
    }

    // ====================== POI和路段相关 ======================

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Poi {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("tel")
        private String tel;

        @JsonProperty("direction")
        private String direction;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("location")
        private String location;

        @JsonProperty("address")
        private String address;

        @JsonProperty("poiweight")
        private String poiweight;

        @JsonProperty("businessarea")
        private String businessarea;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Road {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("direction")
        private String direction;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("location")
        private String location;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadInter {
        @JsonProperty("direction")
        private String direction;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("location")
        private String location;

        @JsonProperty("first_id")
        private String firstId;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("second_id")
        private String secondId;

        @JsonProperty("second_name")
        private String secondName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Aoi {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("location")
        private String location;

        @JsonProperty("area")
        private String area;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("type")
        private String type;
    }
}