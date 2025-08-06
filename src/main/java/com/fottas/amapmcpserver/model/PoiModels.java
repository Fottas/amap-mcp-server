package com.fottas.amapmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.util.List;
import java.util.Objects;

/**
 * 高德地图POI搜索2.0和其他API数据模型 - 优化版
 * 基于高德API文档：https://amap.apifox.cn/
 */
public class PoiModels {

    // ====================== POI搜索2.0相关 - 优化版 ======================

    /**
     * POI文本搜索请求 - 优化版
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoiTextSearchRequest {
        @NotBlank(message = "搜索关键字不能为空")
        private String keywords;

        private String types;  // POI分类编码
        /**
         * 搜索区划 增加指定区域内数据召回权重，如需严格限制召回数据在区域内，请搭配使用city_limit参数，可输入citycode，adcode，cityname；cityname仅支持到城市级别，如“北京市”。
         */
        private String region;

        /**
         * 指定城市数据召回限制 可选值：true/false 为true时，仅召回region对应区域内数据。
         */
        private Boolean city_limit;

        /**
         * 返回结果控制 show_fields用来筛选response结果中可选字段。show_fields的使用需要遵循如下规则：
         * 1、具体可指定返回的字段类请见下方返回结果说明中的“show_fields”内字段类型；
         * 2、多个字段间采用“,”进行分割；
         * 3、show_fields未设置时，只返回基础信息类内字段。
         */
        private String show_fields;

        private String page_size;

        private String page_num;

        private String output;
    }

    /**
     * POI周边搜索请求 - 优化版
     */
    @Data
    @Builder
    public static class PoiAroundSearchRequest {
        @NotBlank(message = "中心点坐标不能为空")
        private String location;

        private String keywords;
        private String types;

        @Min(value = 0, message = "搜索半径最小为0米")
        @Max(value = 50000, message = "搜索半径最大为50000米")
        private String radius;  // 搜索半径，默认3000米

        private String sortrule;  // 排序规则：distance-距离排序，weight-权重排序
        private String page_size;
        private String page_num;
        private String show_fields;
        private String output;
    }

    /**
     * POI多边形搜索请求 - 优化版
     */
    @Data
    @Builder
    public static class PoiPolygonSearchRequest {
        @NotBlank(message = "多边形坐标不能为空")
        private String polygon;  // 多边形坐标点序列

        private String keywords;
        private String types;
        private String page_size;
        private String page_num;
        private String show_fields;
        private String output;

    }

    /**
     * POI详情查询请求 - 新增
     */
    @Data
    @Builder
    public static class PoiDetailRequest {
        @NotBlank(message = "POI ID不能为空")
        private String id;
        private String show_fields;
        private String output = "json";

    }

    // ====================== POI响应模型 ======================

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class PoiResponse implements AmapApiModels.ApiResult<List<PoiModels.PoiInfo>> {
        @JsonProperty("status")
        private String status;

        @JsonProperty("info")
        private String info;

        @JsonProperty("infocode")
        private String infocode;

        @JsonProperty("count")
        private String count;

        @JsonProperty("pois")
        private List<PoiInfo> pois;

        @Override
        public boolean isSuccess() {
            return Objects.equals(this.status, "1");
        }

        @Override
        public String getMessage() {
            return this.info;
        }

        @Override
        public List<PoiInfo> getData() {
            return this.pois;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class PoiInfo {
        /**
         * poi 唯一标识
         */
        private String id;

        private String parent;
        /**
         * poi 详细地址
         */
        private String address;

        private AmapPoiDto.Business business;

        private String distance;
        /**
         * poi 所属省份编码
         */
        private String pcode;
        /**
         * poi 所属区域编码
         */
        private String adcode;

        private String pname;
        /**
         * poi 所属城市
         */

        private String cityname;
        /**
         * poi 所属类型
         */
        private String type;

        private List<Photo> photos;
        /**
         * poi 分类编码
         */
        private String typecode;
        /**
         * poi 所属区县
         */
        private String adname;
        /**
         * poi 所属城市编码
         */
        private String citycode;
        /**
         * poi 名称
         */
        private String name;

        /**
         * poi 经纬度
         */
        private String location;

        private ChildPoi children;

        private IndoorData indoor;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ChildPoi {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("address")
        private String address;

        @JsonProperty("location")
        private String location;

        @JsonProperty("tel")
        private String tel;
    }

    @Data
    public static class Business {
        /**
         * poi 所属商圈
         */
        private String business_area;

        /**
         * poi 今日营业时间，如 08:30-17:30 08:30-09:00 12:00-13:30 09:00-13:00
         */
        private String opentime_today;

        /**
         * poi 营业时间描述，如 周一至周五:08:30-17:30(延时服务时间:08:30-09:00；12:00-13:30)；周六延时服务时间:09:00-13:00(法定节假日除外)
         */
        private String opentime_week;

        /**
         * poi 的联系电话
         */
        private String tel;
        /**
         * poi 特色内容，目前仅在美食 poi 下返回
         */
        private String tag;

        /**
         * poi 评分，目前仅在餐饮、酒店、景点、影院类 POI 下返回
         */
        private String rating;


        /**
         * poi 人均消费，目前仅在餐饮、酒店、景点、影院类 POI 下返回
         */
        private String cost;

        /**
         * 停车场类型（地下、地面、路边），目前仅在停车场类 POI 下返回
         */
        private String parking_type;
        /**
         * poi 标识，用于确认poi信息类型
         */
        private String keytag;

        /**
         * 用于再次确认信息类型
         */
        private String rectag;
    }

    /**
     * 室内相关信息
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class IndoorData {
        @JsonProperty("indoor_map")
        private String indoorMap;

        @JsonProperty("cpid")
        private String cpid;

        @JsonProperty("floor")
        private String floor;

        @JsonProperty("truefloor")
        private String truefloor;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Navi {
        @JsonProperty("navi_poiid")
        private String naviPoiId;

        @JsonProperty("entr_location")
        private String entrLocation;
        @JsonProperty("exit_location")
        private String exitLocation;

        @JsonProperty("gridcode")
        private String gridCode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Photo {
        @JsonProperty("title")
        private String title;

        @JsonProperty("url")
        private String url;
    }
}