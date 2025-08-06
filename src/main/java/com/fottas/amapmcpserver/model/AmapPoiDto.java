package com.fottas.amapmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 高德搜索poi信息
 *
 * @author yinh
 */
@Data
public class AmapPoiDto {
    /**
     * poi 唯一标识
     */
    private String id;

    private String parent;
    /**
     * poi 详细地址
     */
    private String address;

    private Business business;

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

    @Data
    public static class Business {
        /**
         * poi 所属商圈
         */
        @JsonProperty("business_area")
        private String businessArea;

        /**
         * poi 今日营业时间，如 08:30-17:30 08:30-09:00 12:00-13:30 09:00-13:00
         */
        @JsonProperty("opentime_today")
        private String opentimeToday;

        /**
         * poi 营业时间描述，如 周一至周五:08:30-17:30(延时服务时间:08:30-09:00；12:00-13:30)；周六延时服务时间:09:00-13:00(法定节假日除外)
         */
        @JsonProperty("opentime_week")
        private String opentimeWeek;

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
        @JsonProperty("parking_type")
        private String parkingType;

        /**
         * 别名
         */
        private String alias;
        /**
         * poi 标识，用于确认poi信息类型
         */
        private String keytag;

        /**
         * 用于再次确认信息类型
         */
        private String rectag;
    }

    @Data
    public static class Photo {
        /**
         * poi 的图片介绍
         */
        private String title;
        /**
         * poi 图片的下载链接
         */
        private String url;
    }
}
