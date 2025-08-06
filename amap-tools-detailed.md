# 高德地图API工具详细文档 (amap-amap-sse)

## 1. 地理编码 (maps_geo)

**工具名**: `mcp_amap-amap-sse_maps_geo`

**功能**: 将详细的结构化地址转换为经纬度坐标

### 入参
- `address` (string, 必需): 待解析的结构化地址信息
- `city` (string, 可选): 指定查询的城市

### 出参格式
```json
{
  "results": [
    {
      "country": "中国",
      "province": "北京市",
      "city": "北京市",
      "citycode": "010",
      "district": "朝阳区",
      "street": "阜通东大街",
      "number": "6号",
      "adcode": "110105",
      "location": "116.482086,39.990496",
      "level": "门址"
    }
  ]
}
```

---

## 2. 逆地理编码 (maps_regeocode)

**工具名**: `mcp_amap-amap-sse_maps_regeocode`

**功能**: 将一个高德经纬度坐标转换为行政区划地址信息

### 入参
- `location` (string, 必需): 经纬度坐标，格式为"经度,纬度"

### 出参格式
```json
{
  "country": "中国",
  "province": "北京市",
  "city": [],
  "district": "朝阳区"
}
```

---

## 3. 关键字搜索 (maps_text_search)

**工具名**: `mcp_amap-amap-sse_maps_text_search`

**功能**: 根据用户输入的关键字进行POI搜索，并返回相关的信息

### 入参
- `keywords` (string, 必需): 查询关键字
- `city` (string, 可选): 查询城市
- `citylimit` (boolean, 可选): 是否限制城市范围内搜索，默认false

### 出参格式
```json
{
  "suggestion": {
    "keywords": "",
    "ciytes": {
      "suggestion": []
    }
  },
  "pois": [
    {
      "id": "B0FFFFXR8F",
      "name": "星巴克(北京新世界店)",
      "address": "崇文门外大街3号新世界商场一期首层",
      "typecode": "050501",
      "photo": "http://store.is.autonavi.com/showpic/..."
    }
  ]
}
```

---

## 4. 周边搜索 (maps_around_search)

**工具名**: `mcp_amap-amap-sse_maps_around_search`

**功能**: 根据用户传入关键词以及坐标location，搜索出radius半径范围的POI

### 入参
- `keywords` (string, 必需): 搜索关键词
- `location` (string, 必需): 中心点经度纬度，格式为"经度,纬度"
- `radius` (string, 可选): 搜索半径

### 出参格式
```json
{
  "pois": [
    {
      "id": "B0IA3XKVYY",
      "name": "北京方恒假日酒店聚味轩中餐厅",
      "address": "望京阜通东大街6号院3号楼",
      "typecode": "050100",
      "photo": "https://aos-comment.amap.com/..."
    }
  ]
}
```

---

## 5. POI详情搜索 (maps_search_detail)

**工具名**: `mcp_amap-amap-sse_maps_search_detail`

**功能**: 查询关键词搜或者周边搜获取到的POI ID的详细信息

### 入参
- `id` (string, 必需): 关键词搜或者周边搜获取到的POI ID

### 出参格式
```json
{
  "id": "B0FFFFXR8F",
  "name": "星巴克(北京新世界店)",
  "location": "116.418084,39.898363",
  "address": "崇文门外大街3号新世界商场一期首层",
  "business_area": "花市",
  "city": "北京市",
  "type": "餐饮服务;咖啡厅;星巴克咖啡",
  "alias": "",
  "photo": "http://store.is.autonavi.com/showpic/...",
  "cost": "",
  "opentime2": "周一至周日 07:00-22:00",
  "rating": "4.4",
  "open_time": "07:00-22:00",
  "meal_ordering": "0"
}
```

---

## 6. 驾车路径规划 (maps_direction_driving)

**工具名**: `mcp_amap-amap-sse_maps_direction_driving`

**功能**: 驾车路径规划API可以根据用户起终点经纬度坐标规划以小客车、轿车通勤出行的方案

### 入参
- `origin` (string, 必需): 出发点经纬度，坐标格式为"经度,纬度"
- `destination` (string, 必需): 目的地经纬度，坐标格式为"经度,纬度"

### 出参格式
```json
{
  "origin": "116.482086,39.990496",
  "destination": "116.418084,39.898363",
  "paths": [
    {
      "path": "",
      "distance": "16097",
      "duration": "1522",
      "steps": [
        {
          "instruction": "向西南行驶100米向左前方行驶",
          "road": "",
          "distance": "100",
          "orientation": "西南",
          "duration": "32"
        }
      ]
    }
  ]
}
```

---

## 7. 步行路径规划 (maps_direction_walking)

**工具名**: `mcp_amap-amap-sse_maps_direction_walking`

**功能**: 根据输入起点终点经纬度坐标规划100km以内的步行通勤方案

### 入参
- `origin` (string, 必需): 出发点经度，纬度，坐标格式为"经度,纬度"
- `destination` (string, 必需): 目的地经度，纬度，坐标格式为"经度,纬度"

### 出参格式
```json
{
  "route": {
    "origin": "116.482086,39.990496",
    "destination": "116.418084,39.898363",
    "paths": [
      {
        "distance": 13430,
        "duration": 10744,
        "steps": [
          {
            "instruction": "向西南步行181米左转",
            "road": "",
            "distance": 181,
            "orientation": "西南",
            "duration": 145
          }
        ]
      }
    ]
  }
}
```

---

## 8. 综合交通路径规划 (maps_direction_transit_integrated)

**工具名**: `mcp_amap-amap-sse_maps_direction_transit_integrated`

**功能**: 根据用户起终点经纬度坐标规划综合各类公共（火车、公交、地铁）交通方式的通勤方案

### 入参
- `origin` (string, 必需): 出发点经纬度，坐标格式为"经度,纬度"
- `destination` (string, 必需): 目的地经纬度，坐标格式为"经度,纬度"
- `city` (string, 必需): 公共交通规划起点城市
- `cityd` (string, 必需): 公共交通规划终点城市

### 出参格式
```json
{"origin":"116.482086,39.990496","destination":"116.418084,39.898363","distance":"17166","transits":[{"duration":"3556","walking_distance":"1736","segments":[{"walking":{"origin":"116.481827,39.990654","destination":"116.481880,39.984669","distance":"1056","duration":"905","steps":[{"instruction":"步行147米右转","road":"","distance":"147","action":"右转","assistant_action":""},{"instruction":"步行50米左转","road":"","distance":"50","action":"左转","assistant_action":""},{"instruction":"步行47米向左前方行走","road":"","distance":"47","action":"向左前方行走","assistant_action":""},{"instruction":"步行259米左转","road":"","distance":"259","action":"左转","assistant_action":""},{"instruction":"沿广顺南大街步行263米左转","road":"广顺南大街","distance":"263","action":"左转","assistant_action":""},{"instruction":"步行24米右转","road":"","distance":"24","action":"右转","assistant_action":""},{"instruction":"步行1米","road":"","distance":"1","action":"","assistant_action":""},{"instruction":"步行266米到达望京南","road":"","distance":"266","action":"","assistant_action":"到达望京南"}]},"bus":{"buslines":[{"name":"地铁14号线(善各庄--张郭庄)","distance":"6790","duration":"870","departure_stop":{"name":"望京南"},"arrival_stop":{"name":"朝阳公园"},"via_stops":[{"name":"将台"},{"name":"东风北桥"},{"name":"枣营"}]}]},"entrance":{"name":"A西北口"},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.478287,39.933491","destination":"116.480095,39.933727","distance":"187","duration":"160","steps":[{"instruction":"步行187米到达朝阳公园","road":"","distance":"187","action":"","assistant_action":"到达朝阳公园"}]},"bus":{"buslines":[{"name":"地铁3号线(东坝北--东四十条)","distance":"3923","duration":"570","departure_stop":{"name":"朝阳公园"},"arrival_stop":{"name":"东四十条"},"via_stops":[{"name":"团结湖"},{"name":"工人体育场"}]}]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"","destination":"","distance":"","duration":"","steps":[]},"bus":{"buslines":[{"name":"地铁2号线内环(积水潭--积水潭)","distance":"4808","duration":"630","departure_stop":{"name":"东四十条"},"arrival_stop":{"name":"崇文门"},"via_stops":[{"name":"朝阳门"},{"name":"建国门"},{"name":"北京站"}]}]},"entrance":{"name":""},"exit":{"name":"H西南口"},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.416908,39.901081","destination":"116.418221,39.898365","distance":"492","duration":"421","steps":[{"instruction":"步行328米往后走","road":"","distance":"328","action":"往后走","assistant_action":""},{"instruction":"步行1米向左前方行走","road":"","distance":"1","action":"向左前方行走","assistant_action":""},{"instruction":"步行10米右转","road":"","distance":"10","action":"右转","assistant_action":""},{"instruction":"沿崇文门外大街辅路步行41米向右前方行走","road":"崇文门外大街辅路","distance":"41","action":"向右前方行走","assistant_action":""},{"instruction":"步行113米","road":"","distance":"113","action":"","assistant_action":""}]},"bus":{"buslines":[]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}}]},{"duration":"3832","walking_distance":"1987","segments":[{"walking":{"origin":"116.481827,39.990654","destination":"116.481880,39.984669","distance":"1056","duration":"905","steps":[{"instruction":"步行147米右转","road":"","distance":"147","action":"右转","assistant_action":""},{"instruction":"步行50米左转","road":"","distance":"50","action":"左转","assistant_action":""},{"instruction":"步行47米向左前方行走","road":"","distance":"47","action":"向左前方行走","assistant_action":""},{"instruction":"步行259米左转","road":"","distance":"259","action":"左转","assistant_action":""},{"instruction":"沿广顺南大街步行263米左转","road":"广顺南大街","distance":"263","action":"左转","assistant_action":""},{"instruction":"步行24米右转","road":"","distance":"24","action":"右转","assistant_action":""},{"instruction":"步行1米","road":"","distance":"1","action":"","assistant_action":""},{"instruction":"步行266米到达望京南","road":"","distance":"266","action":"","assistant_action":"到达望京南"}]},"bus":{"buslines":[{"name":"地铁14号线(善各庄--张郭庄)","distance":"11254","duration":"1350","departure_stop":{"name":"望京南"},"arrival_stop":{"name":"九龙山"},"via_stops":[{"name":"将台"},{"name":"东风北桥"},{"name":"枣营"},{"name":"朝阳公园"},{"name":"金台路"},{"name":"大望路"}]}]},"entrance":{"name":"A西北口"},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.477486,39.893394","destination":"116.478874,39.893208","distance":"137","duration":"117","steps":[{"instruction":"步行137米到达九龙山","road":"","distance":"137","action":"","assistant_action":"到达九龙山"}]},"bus":{"buslines":[{"name":"地铁7号线(环球度假区--北京西站)","distance":"5030","duration":"780","departure_stop":{"name":"九龙山"},"arrival_stop":{"name":"磁器口"},"via_stops":[{"name":"双井"},{"name":"广渠门外"},{"name":"广渠门内"}]}]},"entrance":{"name":""},"exit":{"name":"A西北口"},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.419937,39.893169","destination":"116.418213,39.898361","distance":"794","duration":"680","steps":[{"instruction":"步行194米往后走","road":"","distance":"194","action":"往后走","assistant_action":""},{"instruction":"步行1米右转","road":"","distance":"1","action":"右转","assistant_action":""},{"instruction":"步行10米右转","road":"","distance":"10","action":"右转","assistant_action":""},{"instruction":"步行40米左转","road":"","distance":"40","action":"左转","assistant_action":""},{"instruction":"沿崇文门外大街辅路步行412米向左前方行走","road":"崇文门外大街辅路","distance":"412","action":"向左前方行走","assistant_action":""},{"instruction":"步行26米右转","road":"","distance":"26","action":"右转","assistant_action":""},{"instruction":"步行112米","road":"","distance":"112","action":"","assistant_action":""}]},"bus":{"buslines":[]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}}]},{"duration":"4131","walking_distance":"1495","segments":[{"walking":{"origin":"116.481827,39.990654","destination":"116.481880,39.984669","distance":"1056","duration":"905","steps":[{"instruction":"步行147米右转","road":"","distance":"147","action":"右转","assistant_action":""},{"instruction":"步行50米左转","road":"","distance":"50","action":"左转","assistant_action":""},{"instruction":"步行47米向左前方行走","road":"","distance":"47","action":"向左前方行走","assistant_action":""},{"instruction":"步行259米左转","road":"","distance":"259","action":"左转","assistant_action":""},{"instruction":"沿广顺南大街步行263米左转","road":"广顺南大街","distance":"263","action":"左转","assistant_action":""},{"instruction":"步行24米右转","road":"","distance":"24","action":"右转","assistant_action":""},{"instruction":"步行1米","road":"","distance":"1","action":"","assistant_action":""},{"instruction":"步行266米到达望京南","road":"","distance":"266","action":"","assistant_action":"到达望京南"}]},"bus":{"buslines":[{"name":"地铁14号线(善各庄--张郭庄)","distance":"18813","duration":"2190","departure_stop":{"name":"望京南"},"arrival_stop":{"name":"蒲黄榆"},"via_stops":[{"name":"将台"},{"name":"东风北桥"},{"name":"枣营"},{"name":"朝阳公园"},{"name":"金台路"},{"name":"大望路"},{"name":"九龙山"},{"name":"平乐园"},{"name":"北工大西门"},{"name":"十里河"},{"name":"方庄"}]}]},"entrance":{"name":"A西北口"},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.423088,39.865582","destination":"116.421669,39.865639","distance":"131","duration":"112","steps":[{"instruction":"步行131米到达蒲黄榆","road":"","distance":"131","action":"","assistant_action":"到达蒲黄榆"}]},"bus":{"buslines":[{"name":"地铁5号线(宋家庄--天通苑北)","distance":"3930","duration":"660","departure_stop":{"name":"蒲黄榆"},"arrival_stop":{"name":"崇文门"},"via_stops":[{"name":"天坛东门"},{"name":"磁器口"}]}]},"entrance":{"name":""},"exit":{"name":"H西南口"},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.418587,39.900730","destination":"116.418221,39.898365","distance":"308","duration":"264","steps":[{"instruction":"步行144米往后走","road":"","distance":"144","action":"往后走","assistant_action":""},{"instruction":"步行1米向左前方行走","road":"","distance":"1","action":"向左前方行走","assistant_action":""},{"instruction":"步行10米右转","road":"","distance":"10","action":"右转","assistant_action":""},{"instruction":"沿崇文门外大街辅路步行41米向右前方行走","road":"崇文门外大街辅路","distance":"41","action":"向右前方行走","assistant_action":""},{"instruction":"步行113米","road":"","distance":"113","action":"","assistant_action":""}]},"bus":{"buslines":[]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}}]},{"duration":"3834","walking_distance":"1639","segments":[{"walking":{"origin":"116.481827,39.990654","destination":"116.481880,39.984669","distance":"1056","duration":"905","steps":[{"instruction":"步行147米右转","road":"","distance":"147","action":"右转","assistant_action":""},{"instruction":"步行50米左转","road":"","distance":"50","action":"左转","assistant_action":""},{"instruction":"步行47米向左前方行走","road":"","distance":"47","action":"向左前方行走","assistant_action":""},{"instruction":"步行259米左转","road":"","distance":"259","action":"左转","assistant_action":""},{"instruction":"沿广顺南大街步行263米左转","road":"广顺南大街","distance":"263","action":"左转","assistant_action":""},{"instruction":"步行24米右转","road":"","distance":"24","action":"右转","assistant_action":""},{"instruction":"步行1米","road":"","distance":"1","action":"","assistant_action":""},{"instruction":"步行266米到达望京南","road":"","distance":"266","action":"","assistant_action":"到达望京南"}]},"bus":{"buslines":[{"name":"地铁14号线(善各庄--张郭庄)","distance":"7896","duration":"1050","departure_stop":{"name":"望京南"},"arrival_stop":{"name":"金台路"},"via_stops":[{"name":"将台"},{"name":"东风北桥"},{"name":"枣营"},{"name":"朝阳公园"}]}]},"entrance":{"name":"A西北口"},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.478111,39.923553","destination":"116.478416,39.922901","distance":"98","duration":"84","steps":[{"instruction":"步行98米到达金台路","road":"","distance":"98","action":"","assistant_action":"到达金台路"}]},"bus":{"buslines":[{"name":"地铁6号线(潞城--金安桥)","distance":"5360","duration":"870","departure_stop":{"name":"金台路"},"arrival_stop":{"name":"东四"},"via_stops":[{"name":"呼家楼"},{"name":"东大桥"},{"name":"朝阳门"}]}]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.415955,39.924309","destination":"116.417488,39.924366","distance":"177","duration":"151","steps":[{"instruction":"步行177米到达东四","road":"","distance":"177","action":"","assistant_action":"到达东四"}]},"bus":{"buslines":[{"name":"地铁5号线(天通苑北--宋家庄)","distance":"2640","duration":"510","departure_stop":{"name":"东四"},"arrival_stop":{"name":"崇文门"},"via_stops":[{"name":"灯市口"},{"name":"东单"}]}]},"entrance":{"name":""},"exit":{"name":"H西南口"},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.418587,39.900730","destination":"116.418221,39.898365","distance":"308","duration":"264","steps":[{"instruction":"步行144米往后走","road":"","distance":"144","action":"往后走","assistant_action":""},{"instruction":"步行1米向左前方行走","road":"","distance":"1","action":"向左前方行走","assistant_action":""},{"instruction":"步行10米右转","road":"","distance":"10","action":"右转","assistant_action":""},{"instruction":"沿崇文门外大街辅路步行41米向右前方行走","road":"崇文门外大街辅路","distance":"41","action":"向右前方行走","assistant_action":""},{"instruction":"步行113米","road":"","distance":"113","action":"","assistant_action":""}]},"bus":{"buslines":[]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}}]},{"duration":"5592","walking_distance":"1268","segments":[{"walking":{"origin":"116.482269,39.990372","destination":"116.481705,39.983803","distance":"1010","duration":"865","steps":[{"instruction":"步行181米左转","road":"","distance":"181","action":"左转","assistant_action":""},{"instruction":"步行291米右转","road":"","distance":"291","action":"右转","assistant_action":""},{"instruction":"沿望京东路辅路步行286米左转","road":"望京东路辅路","distance":"286","action":"左转","assistant_action":""},{"instruction":"沿广顺南大街步行141米右转","road":"广顺南大街","distance":"141","action":"右转","assistant_action":""},{"instruction":"沿京密路步行111米到达西八间房","road":"京密路","distance":"111","action":"","assistant_action":"到达西八间房"}]},"bus":{"buslines":[{"name":"403路(环行铁道--北京站东街)","distance":"8476","duration":"2601","departure_stop":{"name":"西八间房"},"arrival_stop":{"name":"东大桥路口北"},"via_stops":[{"name":"京密路丽都饭店"},{"name":"四元桥东"},{"name":"四元桥西"},{"name":"三元桥东站"},{"name":"三元桥南"},{"name":"三元东桥西"},{"name":"新源街"},{"name":"新源南路西口"},{"name":"塔园村"},{"name":"幸福三村"},{"name":"工人体育场"},{"name":"朝阳医院"}]}]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"","destination":"","distance":"","duration":"","steps":[]},"bus":{"buslines":[{"name":"39路(团结湖--双庙)","distance":"6432","duration":"1906","departure_stop":{"name":"东大桥路口北"},"arrival_stop":{"name":"花市路口南"},"via_stops":[{"name":"芳草地"},{"name":"芳草地南"},{"name":"永安里路口西"},{"name":"日坛路"},{"name":"建国门南"},{"name":"东便门"},{"name":"崇文门东"}]}]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}},{"walking":{"origin":"116.418510,39.896107","destination":"116.418213,39.898361","distance":"257","duration":"220","steps":[{"instruction":"沿崇文门外大街辅路步行119米向左前方行走","road":"崇文门外大街辅路","distance":"119","action":"向左前方行走","assistant_action":""},{"instruction":"步行26米右转","road":"","distance":"26","action":"右转","assistant_action":""},{"instruction":"步行112米","road":"","distance":"112","action":"","assistant_action":""}]},"bus":{"buslines":[]},"entrance":{"name":""},"exit":{"name":""},"railway":{"name":"","trip":""}}]}]}
```

---

## 9. 骑行路径规划 (maps_direction_bicycling)

**工具名**: `mcp_amap-amap-sse_maps_direction_bicycling`

**功能**: 骑行路径规划用于规划骑行通勤方案，规划时会考虑天桥、单行线、封路等情况

### 入参
- `origin` (string, 必需): 出发点经纬度，坐标格式为"经度,纬度"
- `destination` (string, 必需): 目的地经纬度，坐标格式为"经度,纬度"

### 出参格式
```json
{
  "origin": "116.482086,39.990496",
  "destination": "116.482086,39.990496",
  "paths": [
    {
      "distance": 14016,
      "duration": 3364,
      "steps": [
        {
          "instruction": "向西南骑行181米右转",
          "road": "",
          "distance": 181,
          "orientation": "西南",
          "duration": 43
        }
      ]
    }
  ]
}
```

---

## 10. 距离测量 (maps_distance)

**工具名**: `mcp_amap-amap-sse_maps_distance`

**功能**: 测量两个经纬度坐标之间的距离,支持驾车、步行以及球面距离测量

### 入参
- `origins` (string, 必需): 起点经度，纬度，可以传多个坐标，使用竖线隔离，比如"120,30|120,31"
- `destination` (string, 必需): 终点经度，纬度，坐标格式为"经度,纬度"
- `type` (string, 可选): 距离测量类型,1代表驾车距离测量，0代表直线距离测量，3步行距离测量

### 出参格式
```json
{
  "results": [
    {
      "origin_id": "1",
      "dest_id": "1",
      "distance": "16064",
      "duration": "1518"
    }
  ]
}
```

---

## 11. 天气查询 (maps_weather)

**工具名**: `mcp_amap-amap-sse_maps_weather`

**功能**: 根据城市名称或者标准adcode查询指定城市的天气

### 入参
- `city` (string, 必需): 城市名称或者adcode

### 出参格式
```json
{
  "city": "北京市",
  "forecasts": [
    {
      "date": "2025-07-31",
      "week": "4",
      "dayweather": "多云",
      "nightweather": "多云",
      "daytemp": "30",
      "nighttemp": "23",
      "daywind": "南",
      "nightwind": "南",
      "daypower": "1-3",
      "nightpower": "1-3",
      "daytemp_float": "30.0",
      "nighttemp_float": "23.0"
    }
  ]
}
```

---

## 12. IP定位 (maps_ip_location)

**工具名**: `mcp_amap-amap-sse_maps_ip_location`

**功能**: IP定位根据用户输入的IP地址，定位IP的所在位置

### 入参
- `ip` (string, 必需): IP地址

### 出参格式
```json
{
  "province": "",
  "city": "",
  "adcode": "",
  "rectangle": ""
}
```

---

## 13. 导航Schema (maps_schema_navi)

**工具名**: `mcp_amap-amap-sse_maps_schema_navi`

**功能**: Schema唤醒客户端-导航页面，用于根据用户输入终点信息，返回一个拼装好的客户端唤醒URI

### 入参
- `lon` (string, 必需): 终点经度
- `lat` (string, 必需): 终点纬度

### 出参格式
```
amapuri://navi?sourceApplication=amap_mcp&lon=116.418084&lat=39.898363&dev=1&style=2
```

---

## 14. 打车Schema (maps_schema_take_taxi)

**工具名**: `mcp_amap-amap-sse_maps_schema_take_taxi`

**功能**: 根据用户输入的起点和终点信息，返回一个拼装好的客户端唤醒URI，直接唤起高德地图进行打车

### 入参
- `slon` (string, 必需): 起点经度
- `slat` (string, 必需): 起点纬度
- `sname` (string, 必需): 起点名称
- `dlon` (string, 必需): 终点经度
- `dlat` (string, 必需): 终点纬度
- `dname` (string, 必需): 终点名称

### 出参格式
```
amapuri://drive/takeTaxi?sourceApplication=amapplatform&slat=39.990496&slon=116.482086&sname=望京SOHO&dlon=116.418084&dlat=39.898363&dname=星巴克(北京新世界店)
```

---

## 15. 个人地图Schema (maps_schema_personal_map)

**工具名**: `mcp_amap-amap-sse_maps_schema_personal_map`

**功能**: 用于行程规划结果在高德地图展示。将行程规划位置点按照行程顺序填入lineList，返回结果为高德地图打开的URI链接

### 入参
- `orgName` (string, 必需): 行程规划地图小程序名称
- `lineList` (array, 必需): 行程列表
  - `title` (string, 必需): 行程名称描述（按行程顺序）
  - `pointInfoList` (array, 必需): 行程目标位置点描述
    - `name` (string, 必需): 行程目标位置点名称
    - `lon` (number, 必需): 行程目标位置点经度
    - `lat` (number, 必需): 行程目标位置点纬度
    - `poiId` (string, 必需): 行程目标位置点POIID
```json
{
  "orgName": "我的行程",
  "lineList": [
    {
      "title": "北京一日游",
      "pointInfoList": [
        {
          "name": "天安门广场",
          "lon": 116.397128,
          "lat": 39.916527,
          "poiId": "B000A7XW2W"
        },
        {
          "name": "故宫博物院",
          "lon": 116.397026,
          "lat": 39.916527,
          "poiId": "B000A7XW2X"
        },
        {
          "name": "景山公园",
          "lon": 116.397026,
          "lat": 39.916527,
          "poiId": "B000A7XW2Y"
        }
      ]
    }
  ]
}
```

### 出参格式
```
amapuri://workInAmap/createWithToken?polymericId=mcp_9a046e0c902543cda96396a85b43d337&from=MCP
```

---

## 工具分类总结

### 地理编码类
- maps_geo: 地址转坐标
- maps_regeocode: 坐标转地址

### POI搜索类
- maps_text_search: 关键字搜索
- maps_around_search: 周边搜索
- maps_search_detail: POI详情查询

### 路径规划类
- maps_direction_driving: 驾车路径
- maps_direction_walking: 步行路径
- maps_direction_transit_integrated: 公交路径
- maps_direction_bicycling: 骑行路径

### 实用工具类
- maps_distance: 距离测量
- maps_weather: 天气查询
- maps_ip_location: IP定位

### Schema工具类
- maps_schema_navi: 导航URI
- maps_schema_take_taxi: 打车URI
- maps_schema_personal_map: 个人地图URI

---

## 注意事项

1. 所有坐标格式均为"经度,纬度"
2. 距离单位通常为米
3. 时间单位通常为秒
4. Schema工具返回的URI可直接用于唤醒高德地图客户端
5. 部分工具可能需要有效的API密钥才能正常使用 