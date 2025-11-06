package com.fottas.amapmcpserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * MCP工具返回的精简数据模型
 * 根据文档要求精简返回内容，保证代码正确性
 */
public class McpResponseModels {

    // ====================== 地理编码返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeocodingResult {
        @JsonProperty("results")
        private List<GeocodingItem> results;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeocodingItem {
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

    // ====================== 逆地理编码返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReverseGeocodingResult {
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("province")
        private String province;
        
        @JsonProperty("city")
        private List<String> city;
        
        @JsonProperty("district")
        private String district;
    }

    // ====================== POI搜索返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoiSearchResult {
        @JsonProperty("suggestion")
        private PoiSuggestion suggestion;
        
        @JsonProperty("pois")
        private List<PoiItem> pois;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoiAroundResult {
        @JsonProperty("pois")
        private List<PoiItem> pois;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoiSuggestion {
        @JsonProperty("keywords")
        private String keywords;
        
        @JsonProperty("cities")
        private CitySuggestion cities;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CitySuggestion {
        @JsonProperty("suggestion")
        private List<String> suggestion;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoiItem {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("address")
        private String address;
        
        @JsonProperty("typecode")
        private String typecode;
        
        @JsonProperty("photo")
        private String photo;
    }

    // ====================== POI详情返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoiDetailResult {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("location")
        private String location;
        
        @JsonProperty("address")
        private String address;
        
        @JsonProperty("business_area")
        private String businessArea;
        
        @JsonProperty("city")
        private String city;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("alias")
        private String alias;
        
        @JsonProperty("photo")
        private String photo;
        
        @JsonProperty("cost")
        private String cost;
        
        @JsonProperty("opentime2")
        private String opentime2;
        
        @JsonProperty("rating")
        private String rating;
        
        @JsonProperty("open_time")
        private String openTime;
    }

    // ====================== 路径规划返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouteResult {
        @JsonProperty("origin")
        private String origin;
        
        @JsonProperty("destination")
        private String destination;
        
        @JsonProperty("paths")
        private List<RoutePath> paths;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoutePath {
        @JsonProperty("path")
        private String path;
        
        @JsonProperty("distance")
        private String distance;
        
        @JsonProperty("duration")
        private String duration;
        
        @JsonProperty("steps")
        private List<RouteStep> steps;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouteStep {
        @JsonProperty("instruction")
        private String instruction;
        
        @JsonProperty("road")
        private String road;
        
        @JsonProperty("distance")
        private String distance;
        
        @JsonProperty("orientation")
        private String orientation;
        
        @JsonProperty("duration")
        private String duration;
    }

    // ====================== 步行路径规划返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingRouteResult {
        @JsonProperty("route")
        private WalkingRoute route;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingRoute {
        @JsonProperty("origin")
        private String origin;
        
        @JsonProperty("destination")
        private String destination;
        
        @JsonProperty("paths")
        private List<WalkingPath> paths;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingPath {
        @JsonProperty("distance")
        private Integer distance;
        
        @JsonProperty("duration")
        private Integer duration;
        
        @JsonProperty("steps")
        private List<WalkingStep> steps;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingStep {
        @JsonProperty("instruction")
        private String instruction;
        
        @JsonProperty("road")
        private String road;
        
        @JsonProperty("distance")
        private Integer distance;
        
        @JsonProperty("orientation")
        private String orientation;
        
        @JsonProperty("duration")
        private Integer duration;
    }

    // ====================== 公交路径规划返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitRouteResult {
        @JsonProperty("origin")
        private String origin;
        
        @JsonProperty("destination")
        private String destination;
        
        @JsonProperty("distance")
        private String distance;
        
        @JsonProperty("transits")
        private List<TransitRoute> transits;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitRoute {
        @JsonProperty("duration")
        private String duration;
        
        @JsonProperty("walking_distance")
        private String walkingDistance;
        
        @JsonProperty("segments")
        private List<TransitSegment> segments;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitSegment {
        @JsonProperty("walking")
        private TransitWalking walking;
        
        @JsonProperty("bus")
        private TransitBus bus;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitWalking {
        @JsonProperty("origin")
        private String origin;
        
        @JsonProperty("destination")
        private String destination;
        
        @JsonProperty("distance")
        private String distance;
        
        @JsonProperty("duration")
        private String duration;
        
        @JsonProperty("steps")
        private List<Object> steps;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitBus {
        @JsonProperty("buslines")
        private List<BusLine> buslines;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusLine {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("distance")
        private String distance;
        
        @JsonProperty("duration")
        private String duration;
        
        @JsonProperty("departure_stop")
        private BusStop departureStop;
        
        @JsonProperty("arrival_stop")
        private BusStop arrivalStop;
        
        @JsonProperty("via_stops")
        private List<Object> viaStops;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusStop {
        @JsonProperty("name")
        private String name;
    }

    // ====================== 距离测量返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DistanceResult {
        @JsonProperty("results")
        private List<DistanceItem> results;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DistanceItem {
        @JsonProperty("origin_id")
        private String originId;
        
        @JsonProperty("dest_id")
        private String destId;
        
        @JsonProperty("distance")
        private String distance;
        
        @JsonProperty("duration")
        private String duration;
    }

    // ====================== 天气查询返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherResult {
        @JsonProperty("city")
        private String city;
        
        @JsonProperty("forecasts")
        private List<WeatherForecast> forecasts;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherForecast {
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

    // ====================== IP定位返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IpLocationResult {
        @JsonProperty("province")
        private String province;
        
        @JsonProperty("city")
        private String city;
        
        @JsonProperty("adcode")
        private String adcode;
        
        @JsonProperty("rectangle")
        private String rectangle;
    }

    // ====================== Schema返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SchemaResult {
        @JsonProperty("uri")
        private String uri;
    }

    // ====================== 个人地图Schema返回模型 ======================
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalMapRequest {
        @JsonProperty("orgName")
        private String orgName;
        
        @JsonProperty("lineList")
        private List<LineItem> lineList;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineItem {
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("pointInfoList")
        private List<PointInfo> pointInfoList;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PointInfo {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("lon")
        private Double lon;
        
        @JsonProperty("lat")
        private Double lat;
        
        @JsonProperty("poiId")
        private String poiId;
    }
}