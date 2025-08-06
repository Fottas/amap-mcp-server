# 高德地图API工具文档 (amap-amap-sse)

## 工具列表

| 工具名 | 功能 | 主要入参 | 主要出参 |
|--------|------|----------|----------|
| maps_geo | 地址转坐标 | address, city | location, country, province, city |
| maps_regeocode | 坐标转地址 | location | country, province, district |
| maps_text_search | 关键字搜索POI | keywords, city | pois数组 |
| maps_around_search | 周边搜索POI | keywords, location, radius | pois数组 |
| maps_search_detail | POI详情查询 | id | 详细信息 |
| maps_direction_driving | 驾车路径规划 | origin, destination | paths数组 |
| maps_direction_walking | 步行路径规划 | origin, destination | route对象 |
| maps_direction_transit_integrated | 公交路径规划 | origin, destination, city, cityd | transits数组 |
| maps_direction_bicycling | 骑行路径规划 | origin, destination | paths数组 |
| maps_distance | 距离测量 | origins, destination, type | results数组 |
| maps_weather | 天气查询 | city | forecasts数组 |
| maps_ip_location | IP定位 | ip | province, city, adcode |
| maps_schema_navi | 导航URI | lon, lat | amapuri://navi... |
| maps_schema_take_taxi | 打车URI | slon, slat, sname, dlon, dlat, dname | amapuri://drive/takeTaxi... |
| maps_schema_personal_map | 个人地图URI | orgName, lineList | amapuri://workInAmap... |

## 详细参数说明

### 坐标格式
所有坐标参数格式均为: "经度,纬度"

### 距离单位
- 距离: 米
- 时间: 秒

### 常用字段说明
- location: 经纬度坐标
- distance: 距离(米)
- duration: 时间(秒)
- steps: 路径步骤
- pois: 兴趣点数组 