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
 * 路线规划相关数据模型 - 优化版
 * 基于高德API文档：https://amap.apifox.cn/
 */
public class RouteModels {

    // ====================== 路线规划请求基类 - 优化版 ======================

    @Data
    public static abstract class BaseRouteRequest {
        @NotBlank(message = "起点坐标不能为空")
        @Pattern(regexp = "^\\d+\\.\\d+,\\d+\\.\\d+$", message = "坐标格式错误，应为：经度,纬度")
        private String origin;

        @NotBlank(message = "终点坐标不能为空")
        @Pattern(regexp = "^\\d+\\.\\d+,\\d+\\.\\d+$", message = "坐标格式错误，应为：经度,纬度")
        private String destination;

        private String extensions = "base";
        private String output = "json";

        public BaseRouteRequest() {}

        public BaseRouteRequest(String origin, String destination) {
            this.origin = origin;
            this.destination = destination;
        }
    }

    // ====================== 驾车路线规划 - 优化版 ======================

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class DrivingRouteRequest extends BaseRouteRequest {
        private String waypoints;  // 途经点坐标，支持最多16个途经点
        private String avoidpolygons;  // 避让区域
        private String avoidroad;  // 避让道路名
        private String plate;  // 车牌号，用于限行规避
        private String strategy = "0";  // 路径规划策略：0-速度优先（默认）；1-费用优先；2-距离优先；3-不走高速
        private String ferry = "1";  // 是否使用轮渡：0-不使用，1-使用（默认）
        private String cartype = "0";  // 车辆类型：0-小型车（默认）；1-货车

        public DrivingRouteRequest() {}

        public DrivingRouteRequest(String origin, String destination) {
            super(origin, destination);
        }

        public DrivingRouteRequest(String origin, String destination, String strategy) {
            super(origin, destination);
            this.strategy = strategy;
        }
    }

    // ====================== 步行路线规划 - 优化版 ======================

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WalkingRouteRequest extends BaseRouteRequest {
        private String isindoor = "0";  // 是否室内：0-室外（默认）；1-室内

        public WalkingRouteRequest() {}

        public WalkingRouteRequest(String origin, String destination) {
            super(origin, destination);
        }
    }

    // ====================== 骑行路线规划 - 优化版 ======================

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class BicyclingRouteRequest extends BaseRouteRequest {
        private String alternative_route = "0";  // 是否返回备选路线：0-只返回一条路线（默认）；1-返回备选路线
        private String riding_type = "0";  // 骑行类型：0-普通自行车（默认）；1-电动自行车

        public BicyclingRouteRequest() {}

        public BicyclingRouteRequest(String origin, String destination) {
            super(origin, destination);
        }
    }

    // ====================== 公交路线规划 - 优化版 ======================

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TransitRouteRequest extends BaseRouteRequest {
        @NotBlank(message = "城市不能为空")
        private String city;  // 城市名称或城市编码

        private String cityd;  // 跨城市时的目的地城市
        private String strategy = "0";  // 公交策略：0-最快捷；1-最经济；2-最少换乘；3-最少步行；5-不乘地铁
        private String nightflag = "0";  // 是否计算夜班车：0-不计算（默认）；1-计算
        private String date;  // 出发时间，格式：yyyy-MM-dd
        private String time;  // 出发时间，格式：HH:mm

        public TransitRouteRequest() {}

        public TransitRouteRequest(String origin, String destination, String city) {
            super(origin, destination);
            this.city = city;
        }
    }

    // ====================== 电动车路线规划 - 优化版 ======================

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ElectricBikeRouteRequest extends BaseRouteRequest {
        private String alternative_route = "0";  // 是否返回备选路线

        public ElectricBikeRouteRequest() {}

        public ElectricBikeRouteRequest(String origin, String destination) {
            super(origin, destination);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class RouteResponse implements AmapApiModels.ApiResult<RouteResponse.RouteData> {
        @JsonProperty("status")
        private String status;

        @JsonProperty("info")
        private String info;

        @JsonProperty("infocode")
        private String infocode;

        @JsonProperty("count")
        private String count;

        @JsonProperty("route")
        private RouteData route;

        @Override
        public boolean isSuccess() {
            return Objects.equals(this.status, "1");
        }

        @Override
        public String getMessage() {
            return this.info;
        }

        @Override
        public RouteData getData() {
            return this.route;
        }

        // 通用路线数据结构，用于驾车、步行、骑行
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RouteData {
            @JsonProperty("origin")
            private String origin;

            @JsonProperty("destination")
            private String destination;

            @JsonProperty("taxi_cost")
            private String taxiCost;

            @JsonProperty("paths")
            private List<Path> paths;
        }
    }

// ====================== 公交路线规划专用响应 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TransitRouteResponse implements AmapApiModels.ApiResult<TransitRouteResponse.TransitRouteData> {
        @JsonProperty("status")
        private String status;

        @JsonProperty("info")
        private String info;

        @JsonProperty("infocode")
        private String infocode;

        @JsonProperty("count")
        private String count;

        @JsonProperty("route")
        private TransitRouteData route;

        @Override
        public boolean isSuccess() {
            return Objects.equals(this.status, "1");
        }

        @Override
        public String getMessage() {
            return this.info;
        }

        @Override
        public TransitRouteData getData() {
            return this.route;
        }

        // 公交路线数据结构
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TransitRouteData {
            @JsonProperty("origin")
            private String origin;

            @JsonProperty("destination")
            private String destination;

            @JsonProperty("distance")
            private String distance;

            @JsonProperty("cost")
            private TransitCost cost;

            @JsonProperty("transits")
            private List<Transit> transits;
        }
    }

// ====================== 通用路径数据结构 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Path {
        @JsonProperty("distance")
        private String distance;

        @JsonProperty("restriction")
        private String restriction;

        @JsonProperty("cost")
        private Cost cost;

        @JsonProperty("steps")
        private List<Step> steps;
    }

// ====================== 通用费用数据结构 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Cost {
        @JsonProperty("duration")
        private String duration;

        @JsonProperty("tolls")
        private String tolls;

        @JsonProperty("toll_distance")
        private String tollDistance;

        @JsonProperty("traffic_lights")
        private String trafficLights;
    }

// ====================== 通用步骤数据结构 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Step {
        @JsonProperty("instruction")
        private String instruction;

        @JsonProperty("orientation")
        private String orientation;

        @JsonProperty("road_name")
        private String roadName;

        @JsonProperty("step_distance")
        private String stepDistance;

        @JsonProperty("cost")
        private Cost cost;

        @JsonProperty("polyline")
        private String polyline;

        @JsonProperty("action")
        private String action;

        @JsonProperty("assistant_action")
        private String assistantAction;

        @JsonProperty("cities")
        private List<City> cities;

        @JsonProperty("tmcs")
        private List<Tmc> tmcs;
    }

// ====================== 公交相关数据结构 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TransitCost {
        @JsonProperty("duration")
        private String duration;

        @JsonProperty("transit_fee")
        private String transitFee;

        @JsonProperty("taxi_cost")
        private String taxiCost;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Transit {
        @JsonProperty("distance")
        private String distance;

        @JsonProperty("walking_distance")
        private String walkingDistance;

        @JsonProperty("nightflag")
        private String nightflag;

        @JsonProperty("cost")
        private TransitCost cost;

        @JsonProperty("segments")
        private List<Segment> segments;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Segment {
        @JsonProperty("walking")
        private WalkingSegment walking;

        @JsonProperty("bus")
        private BusSegment bus;

        @JsonProperty("taxi")
        private TaxiSegment taxi;

        @JsonProperty("entrance")
        private Entrance entrance;

        @JsonProperty("exit")
        private Exit exit;

        @JsonProperty("railway")
        private Railway railway;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WalkingSegment {
        @JsonProperty("destination")
        private String destination;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("origin")
        private String origin;

        @JsonProperty("cost")
        private Cost cost;

        @JsonProperty("steps")
        private List<WalkingStep> steps;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class WalkingStep {
        @JsonProperty("instruction")
        private String instruction;

        @JsonProperty("road")
        private String road;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("polyline")
        private String polyline;

        @JsonProperty("action")
        private String action;

        @JsonProperty("assistant_action")
        private String assistantAction;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BusSegment {
        @JsonProperty("buslines")
        private List<BusLine> buslines;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
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
        private List<BusStop> viaStops;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BusStop {
        @JsonProperty("name")
        private String name;

        @JsonProperty("location")
        private String location;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TaxiSegment {
        @JsonProperty("distance")
        private String distance;

        @JsonProperty("price")
        private String price;

        @JsonProperty("drivetime")
        private String drivetime;

        @JsonProperty("polyline")
        private String polyline;

        @JsonProperty("startpoint")
        private String startpoint;

        @JsonProperty("startname")
        private String startname;

        @JsonProperty("endpoint")
        private String endpoint;

        @JsonProperty("endname")
        private String endname;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Entrance {
        @JsonProperty("name")
        private String name;

        @JsonProperty("location")
        private String location;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Exit {
        @JsonProperty("name")
        private String name;

        @JsonProperty("location")
        private String location;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Railway {
        @JsonProperty("name")
        private String name;

        @JsonProperty("trip")
        private String trip;
    }

// ====================== 城市和路况信息 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class City {
        @JsonProperty("name")
        private String name;

        @JsonProperty("citycode")
        private String citycode;

        @JsonProperty("adcode")
        private String adcode;

        @JsonProperty("districts")
        private List<District> districts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class District {
        @JsonProperty("name")
        private String name;

        @JsonProperty("adcode")
        private String adcode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Tmc {
        @JsonProperty("status")
        private String status;

        @JsonProperty("distance")
        private String distance;

        @JsonProperty("lcodes")
        private String lcodes;

        @JsonProperty("polyline")
        private String polyline;
    }
}