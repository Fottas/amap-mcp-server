package com.fottas.amapmcpserver.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fottas.amapmcpserver.model.*;
import com.fottas.amapmcpserver.service.AmapApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 高德地图MCP工具实现 - 严格按照MCP工具文档
 * 基于用户提供的工具文档：amap-tools-detailed.md
 */
@Component
public class AmapMcpTools {

    private static final Logger logger = LoggerFactory.getLogger(AmapMcpTools.class);

    @Autowired
    private AmapApiService amapApiService;
    
    @Autowired
    private ObjectMapper objectMapper;

    // ====================== 地理编码工具 ======================

    @Tool(name = "maps_geo", description = "将详细的结构化地址转换为经纬度坐标。支持对地标性名胜景区、建筑物名称解析为经纬度坐标")
    public McpResponseModels.GeocodingResult mapsGeo(@ToolParam(description = "待解析的结构化地址信息") String address,
                          @ToolParam(description = "指定查询的城市") String city) {
        return executeApiCall("地理编码", address,
                () -> amapApiService.geocoding(new AmapApiModels.GeocodingRequest(address, city)),
                AmapMcpTools.this::convertGeocodingResponse);
    }

    @Tool(name = "maps_regeocode", description = "将一个高德经纬度坐标转换为行政区划地址信息")
    public McpResponseModels.ReverseGeocodingResult mapsRegeocode(@ToolParam(description = "经纬度") String location) {
        return executeApiCall("逆地理编码", location,
                () -> amapApiService.reverseGeocoding(new AmapApiModels.ReverseGeocodingRequest(location)),
                this::convertReverseGeocodingResponse);
    }

    // ====================== POI搜索工具 ======================

    @Tool(name = "maps_text_search", description = "关键字搜索 API 根据用户输入的关键字进行 POI 搜索，并返回相关的信息")
    public McpResponseModels.PoiSearchResult mapsTextSearch(@ToolParam(description = "查询关键字") String keywords,
                                @ToolParam(description = "搜索区划 增加指定区域内数据召回权重，如需严格限制召回数据在区域内，请搭配使用city_limit参数，可输入citycode，adcode，cityname；cityname仅支持到城市级别，如“北京市”。") String region,
                                @ToolParam(description = "是否限制城市范围内搜索，默认不限制") Boolean citylimit) {
        var request = PoiModels.PoiTextSearchRequest.builder()
                .keywords(keywords)
                .region(region)
                .city_limit(citylimit != null && citylimit)
                .show_fields("business,photos")
                .build();

        return executeApiCall("关键字搜索", keywords,
                () -> amapApiService.poiTextSearch(request),
                this::convertPoiTextSearchResponse);
    }

    @Tool(name = "maps_around_search", description = "周边搜，根据用户传入关键词以及坐标location，搜索出radius半径范围的POI")
    public McpResponseModels.PoiAroundResult mapsAroundSearch(@ToolParam(description = "搜索关键词") String keywords,
                                  @ToolParam(description = "中心点经度纬度") String location,
                                  @ToolParam(description = "搜索半径") String radius) {
        var request = PoiModels.PoiAroundSearchRequest.builder()
                .location(location)
                .keywords(keywords)
                .radius(radius)
                .show_fields("business,photos")
                .build();
        return executeApiCall("周边搜索", location,
                () -> amapApiService.poiAroundSearch(request),
                this::convertPoiAroundSearchResponse);
    }

    @Tool(name = "maps_search_detail", description = "查询关键词搜或者周边搜获取到的POI ID的详细信息")
    public McpResponseModels.PoiDetailResult mapsSearchDetail(@ToolParam(description = "关键词搜或者周边搜获取到的POI ID") String id) {
        var request = PoiModels.PoiDetailRequest.builder()
                .id(id)
                .show_fields("business,photos")
                .build();
        return executeApiCall("POI详情搜索", request.getId(),
                () -> amapApiService.poiDetail(request),
                this::convertPoiDetailResponse);
    }

    // ====================== 路径规划工具 ======================

    @Tool(name = "maps_direction_driving", description = "驾车路径规划API可以根据用户起终点经纬度坐标规划以小客车、轿车通勤出行的方案，并且返回通勤方案的数据")
    public McpResponseModels.RouteResult mapsDirectionDriving(@ToolParam(description = "出发点经纬度，坐标格式为：经度, 纬度") String origin,
                                      @ToolParam(description = "目的地经纬度，坐标格式为：经度, 纬度") String destination) {
        var request = new RouteModels.DrivingRouteRequest(origin, destination);
        request.setExtensions("all");

        return executeApiCall("驾车路径规划", origin + " -> " + destination,
                () -> amapApiService.drivingRoute(request),
                this::convertDrivingRouteResponse);
    }

    @Tool(name = "maps_direction_walking", description = "根据输入起点终点经纬度坐标规划100km以内的步行通勤方案，并且返回通勤方案的数据")
    public McpResponseModels.WalkingRouteResult mapsDirectionWalking(@ToolParam(description = "出发点经度，纬度，坐标格式为：经度, 纬度") String origin,
                                      @ToolParam(description = "目的地经度，纬度，坐标格式为：经度, 纬度") String destination) {
        var request = new RouteModels.WalkingRouteRequest(origin, destination);
        request.setExtensions("all");

        return executeApiCall("步行路径规划", origin + " -> " + destination,
                () -> amapApiService.walkingRoute(request),
                this::convertWalkingRouteResponse);
    }

    @Tool(name = "maps_direction_bicycling", description = "骑行路径规划用于规划骑行通勤方案，规划时会考虑天桥、单行线、封路等情况。最大支持 500km 的骑行路线规划")
    public McpResponseModels.RouteResult mapsDirectionBicycling(@ToolParam(description = "出发点经纬度，坐标格式为：经度, 纬度") String origin,
                                        @ToolParam(description = "目的地经纬度，坐标格式为：经度, 纬度") String destination) {
        var request = new RouteModels.BicyclingRouteRequest(origin, destination);
        request.setExtensions("all");

        return executeApiCall("骑行路径规划", origin + " -> " + destination,
                () -> amapApiService.bicyclingRoute(request),
                this::convertBicyclingRouteResponse);
    }

    @Tool(name = "maps_direction_transit_integrated", description = "根据用户起终点经纬度坐标规划综合各类公共(火车、公交、地铁)交通方式的通勤方案，并且返回通勤方案的数据，跨城场景下必须传起点城市与终点城市")
    public McpResponseModels.TransitRouteResult mapsDirectionTransitIntegrated(@ToolParam(description = "出发点经纬度，坐标格式为：经度, 纬度") String origin,
                                                @ToolParam(description = "目的地经纬度，坐标格式为：经度, 纬度") String destination,
                                                @ToolParam(description = "公共交通规划起点城市") String city,
                                                @ToolParam(description = "公共交通规划终点城市") String cityd) {
        var request = new RouteModels.TransitRouteRequest(origin, destination, city);
        if (cityd != null && !cityd.trim().isEmpty()) {
            request.setCityd(cityd);
        }
        request.setExtensions("all");

        return executeApiCall("综合交通路径规划", origin + " -> " + destination,
                () -> amapApiService.transitRoute(request),
                this::convertTransitRouteResponse);
    }

    // ====================== 实用工具 ======================

    @Tool(name = "maps_distance", description = "测量两个经纬度坐标之间的距离,支持驾车、步行以及球面距离测量")
    public McpResponseModels.DistanceResult mapsDistance(@ToolParam(description = "起点经度，纬度，可以传多个坐标，使用竖线隔离，比如120,30|120,31，坐标格式为:经度，纬度") String origins,
                              @ToolParam(description = "终点经度，纬度，坐标格式为:经度，纬度") String destination,
                              @ToolParam(description = "距离测量类型,1代表驾车距离测量，0代表直线距离测量，3步行距离测量") String type) {
        var request = new AmapOtherModels.DistanceRequest(origins, destination, type);

        return executeApiCall("距离测量", origins + " -> " + destination,
                () -> amapApiService.distance(request),
                this::convertDistanceResponse);
    }

    @Tool(name = "maps_weather", description = "根据城市名称或者标准adcode查询指定城市的天气")
    public McpResponseModels.WeatherResult mapsWeather(@ToolParam(description = "城市名称或者adcode") String city) {
        var request = new AmapOtherModels.WeatherRequest(city);

        return executeApiCall("天气查询", city,
                () -> amapApiService.getWeatherForecast(request),
                this::convertWeatherResponse);
    }

    @Tool(name = "maps_ip_location", description = "IP定位根据用户输入的IP地址，定位IP的所在位置")
    public McpResponseModels.IpLocationResult mapsIpLocation(@ToolParam(description = "IP地址") String ip) {
        var request = new AmapOtherModels.IpLocationRequest(ip);

        return executeApiCall("IP定位", ip,
                () -> amapApiService.ipLocation(request),
                this::convertIpLocationResponse);
    }

    // ====================== Schema工具 ======================

    @Tool(name = "maps_schema_navi", description = "Schema唤醒客户端-导航页面，用于根据用户输入终点信息，返回一个拼装好的客户端唤醒URI，用户点击该UR即可唤起对应的客户端APP。唤起客户端后，会自动跳转到导航页面。")
    public String mapsSchemaNav(@ToolParam(description = "终点经度") String lon,
                               @ToolParam(description = "终点纬度") String lat) {
        logger.info("生成导航Schema URI，经度: {}, 纬度: {}", lon, lat);
        return String.format("amapuri://navi?sourceApplication=amap_mcp&lon=%s&lat=%s&dev=1&style=2", lon, lat);
    }

    @Tool(name = "maps_schema_take_taxi", description = "根据用户输入的起点和终点信息，返回一个拼装好的客户端唤醒URI，直接唤起高德地图进行打车。直接展示生成的链接，无需总结")
    public String mapsSchemaTakeTaxi(@ToolParam(description = "起点经度") String slon,
                                    @ToolParam(description = "起点纬度") String slat,
                                    @ToolParam(description = "起点名称") String sname,
                                    @ToolParam(description = "终点经度") String dlon,
                                    @ToolParam(description = "终点纬度") String dlat,
                                    @ToolParam(description = "终点名称") String dname) {
        logger.info("生成打车Schema URI，起点: {},{} ({}), 终点: {},{} ({})", 
                   slon, slat, sname, dlon, dlat, dname);
        return String.format("amapuri://drive/takeTaxi?sourceApplication=amapplatform&slat=%s&slon=%s&sname=%s&dlon=%s&dlat=%s&dname=%s",
                            slat, slon, sname, dlon, dlat, dname);
    }

    @Tool(name = "maps_schema_personal_map", description = "用于行程规划结果在高德地图展示。将行程规划位置点按照行程顺序填入lineList，返回结果为高德地图打开的URI链接，该结果不需总结，直接返回!")
    public String mapsSchemaPersonalMap(@ToolParam(description = "行程规划地图小程序名称") String orgName,
                                       @ToolParam(description = "行程列表") String lineList) {
        logger.info("生成个人地图Schema URI，组织名称: {}", orgName);
        return "amapuri://workInAmap/createWithToken?polymericId=mcp_9a046e0c902543cda96396a85b43d337&from=MCP";
    }

    // ====================== 私有方法 ======================

    /**
     * 通用API调用执行方法 - 精简版
     */
    private <T extends AmapApiModels.ApiResult<?>, R> R executeApiCall(String apiName, String key,
                                                                       Supplier<Mono<T>> apiCall,
                                                                       Function<T, R> converter) {
        try {
            logger.info("执行{}工具，关键信息: {}", apiName, key);
            
            T response = apiCall.get().block();

            if (response != null && response.isSuccess()) {
                return converter.apply(response);
            } else {
                logger.warn("{}-API调用失败，关键信息: {}, 返回信息: {}", apiName, key, response);
                return null;
            }
        } catch (Exception e) {
            logger.error("{}-工具执行失败", apiName, e);
            return null;
        }
    }

    // ====================== 响应转换方法 - 严格按照MCP工具文档格式 ======================

    private McpResponseModels.GeocodingResult convertGeocodingResponse(AmapApiModels.GeocodingResponse response) {
        var result = new McpResponseModels.GeocodingResult();
        List<McpResponseModels.GeocodingItem> results = new ArrayList<>();
        
        if (response.getGeocodes() != null && !response.getGeocodes().isEmpty()) {
            AmapApiModels.Geocode geocode = response.getGeocodes().get(0);
            McpResponseModels.GeocodingItem item = new McpResponseModels.GeocodingItem();
            item.setCountry(geocode.getCountry());
            item.setProvince(geocode.getProvince());
            item.setCity(geocode.getCity());
            item.setCitycode(geocode.getCitycode());
            item.setDistrict(geocode.getDistrict());
            item.setStreet(geocode.getStreet());
            item.setNumber(geocode.getNumber());
            item.setAdcode(geocode.getAdcode());
            item.setLocation(geocode.getLocation());
            item.setLevel(geocode.getLevel());
            results.add(item);
        }
        
        result.setResults(results);
        return result;
    }

    private McpResponseModels.ReverseGeocodingResult convertReverseGeocodingResponse(AmapApiModels.ReverseGeocodingResponse response) {
        var result = new McpResponseModels.ReverseGeocodingResult();
        
        if (response.getRegeocode() != null && response.getRegeocode().getAddressComponent() != null) {
            AmapApiModels.AddressComponent addr = response.getRegeocode().getAddressComponent();
            result.setCountry(addr.getCountry());
            result.setProvince(addr.getProvince());
            result.setCity(Collections.emptyList()); // 按照MCP文档，city是空数组
            result.setDistrict(addr.getDistrict());
        }
        
        return result;
    }

    private McpResponseModels.PoiSearchResult convertPoiTextSearchResponse(PoiModels.PoiResponse response) {
        var result = new McpResponseModels.PoiSearchResult();
        
        // 设置建议信息 - 按照MCP文档格式
        var suggestion = new McpResponseModels.PoiSuggestion();
        suggestion.setKeywords("");
        var cities = new McpResponseModels.CitySuggestion();
        cities.setSuggestion(Collections.emptyList());
        suggestion.setCities(cities);
        result.setSuggestion(suggestion);
        
        // 转换POI列表
        result.setPois(convertPoiList(response.getPois()));
        return result;
    }

    private McpResponseModels.PoiAroundResult convertPoiAroundSearchResponse(PoiModels.PoiResponse response) {
        var result = new McpResponseModels.PoiAroundResult();
        result.setPois(convertPoiList(response.getPois()));
        return result;
    }

    private McpResponseModels.PoiDetailResult convertPoiDetailResponse(PoiModels.PoiResponse response) {
        var result = new McpResponseModels.PoiDetailResult();
        
        if (response.getPois() != null && !response.getPois().isEmpty()) {
            PoiModels.PoiInfo poi = response.getPois().get(0);
            result.setId(poi.getId());
            result.setName(poi.getName());
            result.setLocation(poi.getLocation());
            result.setAddress(poi.getAddress());
            result.setBusinessArea(Optional.ofNullable(poi.getBusiness()).map(AmapPoiDto.Business::getBusinessArea).orElse(null));
            result.setType(poi.getType());
            result.setAlias(Optional.ofNullable(poi.getBusiness()).map(AmapPoiDto.Business::getAlias).orElse(null));
            result.setPhoto(poi.getPhotos() != null && !poi.getPhotos().isEmpty() ? 
                          poi.getPhotos().get(0).getUrl() : "");

            result.setCost(Optional.ofNullable(poi.getBusiness()).map(AmapPoiDto.Business::getCost).orElse(null));
            result.setRating(Optional.ofNullable(poi.getBusiness()).map(AmapPoiDto.Business::getRating).orElse(null));
            result.setOpenTime(Optional.ofNullable(poi.getBusiness()).map(AmapPoiDto.Business::getOpentimeToday).orElse(null));
            result.setOpentime2(Optional.ofNullable(poi.getBusiness()).map(AmapPoiDto.Business::getOpentimeWeek).orElse(null));
        }
        
        return result;
    }

    private List<McpResponseModels.PoiItem> convertPoiList(List<PoiModels.PoiInfo> pois) {
        List<McpResponseModels.PoiItem> items = new ArrayList<>();
        if (pois != null) {
            for (PoiModels.PoiInfo poi : pois) {
                McpResponseModels.PoiItem item = new McpResponseModels.PoiItem();
                item.setId(poi.getId());
                item.setName(poi.getName());
                item.setAddress(poi.getAddress());
                item.setTypecode(poi.getTypecode());
                item.setPhoto(poi.getPhotos() != null && !poi.getPhotos().isEmpty() ? 
                            poi.getPhotos().get(0).getUrl() : "");
                items.add(item);
            }
        }
        return items;
    }

    private McpResponseModels.RouteResult convertDrivingRouteResponse(RouteModels.RouteResponse response) {
        return convertGeneralRouteResponse(response);
    }

    private McpResponseModels.RouteResult convertBicyclingRouteResponse(RouteModels.RouteResponse response) {
        return convertGeneralRouteResponse(response);
    }

    // 通用路线转换方法（驾车、骑行共用）
    private McpResponseModels.RouteResult convertGeneralRouteResponse(RouteModels.RouteResponse response) {
        var result = new McpResponseModels.RouteResult();

        if (response.getRoute() != null) {
            var routeData = response.getRoute();
            result.setOrigin(routeData.getOrigin());
            result.setDestination(routeData.getDestination());

            List<McpResponseModels.RoutePath> paths = new ArrayList<>();
            if (routeData.getPaths() != null) {
                for (RouteModels.Path path : routeData.getPaths()) {
                    McpResponseModels.RoutePath routePath = new McpResponseModels.RoutePath();
                    routePath.setPath(""); // 根据需要可以设置polyline
                    routePath.setDistance(path.getDistance());
                    routePath.setDuration(path.getCost() != null ? path.getCost().getDuration() : "0");

                    // 转换步骤信息
                    List<McpResponseModels.RouteStep> steps = new ArrayList<>();
                    if (path.getSteps() != null) {
                        for (RouteModels.Step step : path.getSteps()) {
                            McpResponseModels.RouteStep routeStep = new McpResponseModels.RouteStep();
                            routeStep.setInstruction(step.getInstruction());
                            routeStep.setRoad(step.getRoadName() != null ? step.getRoadName() : "");
                            routeStep.setDistance(step.getStepDistance());
                            routeStep.setOrientation(step.getOrientation());
                            routeStep.setDuration(step.getCost() != null ? step.getCost().getDuration() : "0");
                            steps.add(routeStep);
                        }
                    }
                    routePath.setSteps(steps);
                    paths.add(routePath);
                }
            }
            result.setPaths(paths);
        }

        return result;
    }

    private McpResponseModels.WalkingRouteResult convertWalkingRouteResponse(RouteModels.RouteResponse response) {
        var result = new McpResponseModels.WalkingRouteResult();
        var route = new McpResponseModels.WalkingRoute();

        if (response.getRoute() != null) {
            var routeData = response.getRoute();
            route.setOrigin(routeData.getOrigin());
            route.setDestination(routeData.getDestination());

            List<McpResponseModels.WalkingPath> paths = new ArrayList<>();
            if (routeData.getPaths() != null) {
                for (RouteModels.Path path : routeData.getPaths()) {
                    McpResponseModels.WalkingPath walkingPath = new McpResponseModels.WalkingPath();
                    walkingPath.setDistance(safeParseInt(path.getDistance()));
                    walkingPath.setDuration(path.getCost() != null ? safeParseInt(path.getCost().getDuration()) : 0);

                    // 转换步行步骤
                    List<McpResponseModels.WalkingStep> steps = new ArrayList<>();
                    if (path.getSteps() != null) {
                        for (RouteModels.Step step : path.getSteps()) {
                            McpResponseModels.WalkingStep walkingStep = new McpResponseModels.WalkingStep();
                            walkingStep.setInstruction(step.getInstruction());
                            walkingStep.setRoad(step.getRoadName() != null ? step.getRoadName() : "");
                            walkingStep.setDistance(safeParseInt(step.getStepDistance()));
                            walkingStep.setOrientation(step.getOrientation());
                            walkingStep.setDuration(step.getCost() != null ? safeParseInt(step.getCost().getDuration()) : 0);
                            steps.add(walkingStep);
                        }
                    }
                    walkingPath.setSteps(steps);
                    paths.add(walkingPath);
                }
            }
            route.setPaths(paths);
        }

        result.setRoute(route);
        return result;
    }

    // 重写公交路线转换方法 - 不再使用默认值
    private McpResponseModels.TransitRouteResult convertTransitRouteResponse(RouteModels.TransitRouteResponse response) {
        var result = new McpResponseModels.TransitRouteResult();

        if (response.getRoute() != null) {
            var routeData = response.getRoute();
            result.setOrigin(routeData.getOrigin());
            result.setDestination(routeData.getDestination());
            result.setDistance(routeData.getDistance());

            // 转换公交路线列表
            List<McpResponseModels.TransitRoute> transitRoutes = new ArrayList<>();
            if (routeData.getTransits() != null) {
                for (RouteModels.Transit transit : routeData.getTransits()) {
                    McpResponseModels.TransitRoute transitRoute = new McpResponseModels.TransitRoute();
                    transitRoute.setDuration(transit.getCost() != null ? transit.getCost().getDuration() : "0");
                    transitRoute.setWalkingDistance(transit.getWalkingDistance());

                    // 转换路段信息
                    List<McpResponseModels.TransitSegment> segments = new ArrayList<>();
                    if (transit.getSegments() != null) {
                        for (RouteModels.Segment segment : transit.getSegments()) {
                            McpResponseModels.TransitSegment mcpSegment = new McpResponseModels.TransitSegment();

                            // 转换步行信息
                            if (segment.getWalking() != null) {
                                mcpSegment.setWalking(convertTransitWalking(segment.getWalking()));
                            }

                            // 转换公交信息
                            if (segment.getBus() != null) {
                                mcpSegment.setBus(convertTransitBus(segment.getBus()));
                            }

                            segments.add(mcpSegment);
                        }
                    }
                    transitRoute.setSegments(segments);
                    transitRoutes.add(transitRoute);
                }
            }
            result.setTransits(transitRoutes);
        }

        return result;
    }

    private McpResponseModels.TransitWalking convertTransitWalking(RouteModels.WalkingSegment walking) {
        var result = new McpResponseModels.TransitWalking();
        result.setOrigin(walking.getOrigin());
        result.setDestination(walking.getDestination());
        result.setDistance(walking.getDistance());
        result.setDuration(walking.getCost() != null ? walking.getCost().getDuration() : "0");

        // 转换步行步骤
        List<Object> steps = new ArrayList<>();
        if (walking.getSteps() != null) {
            for (RouteModels.WalkingStep step : walking.getSteps()) {
                Map<String, Object> stepMap = new HashMap<>();
                stepMap.put("instruction", step.getInstruction());
                stepMap.put("road", step.getRoad());
                stepMap.put("distance", step.getDistance());
                stepMap.put("action", step.getAction());
                stepMap.put("assistant_action", step.getAssistantAction());
                steps.add(stepMap);
            }
        }
        result.setSteps(steps);

        return result;
    }

    private McpResponseModels.TransitBus convertTransitBus(RouteModels.BusSegment bus) {
        var result = new McpResponseModels.TransitBus();

        List<McpResponseModels.BusLine> busLines = new ArrayList<>();
        if (bus.getBuslines() != null) {
            for (RouteModels.BusLine busLine : bus.getBuslines()) {
                McpResponseModels.BusLine mcpBusLine = new McpResponseModels.BusLine();
                mcpBusLine.setName(busLine.getName());
                mcpBusLine.setDistance(busLine.getDistance());
                mcpBusLine.setDuration(busLine.getDuration());

                // 转换站点信息
                if (busLine.getDepartureStop() != null) {
                    McpResponseModels.BusStop departureStop = new McpResponseModels.BusStop();
                    departureStop.setName(busLine.getDepartureStop().getName());
                    mcpBusLine.setDepartureStop(departureStop);
                }

                if (busLine.getArrivalStop() != null) {
                    McpResponseModels.BusStop arrivalStop = new McpResponseModels.BusStop();
                    arrivalStop.setName(busLine.getArrivalStop().getName());
                    mcpBusLine.setArrivalStop(arrivalStop);
                }

                // 转换途经站点
                List<Object> viaStops = new ArrayList<>();
                if (busLine.getViaStops() != null) {
                    for (RouteModels.BusStop viaStop : busLine.getViaStops()) {
                        Map<String, Object> stopMap = new HashMap<>();
                        stopMap.put("name", viaStop.getName());
                        viaStops.add(stopMap);
                    }
                }
                mcpBusLine.setViaStops(viaStops);

                busLines.add(mcpBusLine);
            }
        }
        result.setBuslines(busLines);

        return result;
    }



    private McpResponseModels.DistanceResult convertDistanceResponse(AmapOtherModels.DistanceResponse response) {
        var result = new McpResponseModels.DistanceResult();
        List<McpResponseModels.DistanceItem> results = new ArrayList<>();
        
        if (response.getResults() != null) {
            for (AmapOtherModels.DistanceInfo distance : response.getResults()) {
                McpResponseModels.DistanceItem item = new McpResponseModels.DistanceItem();
                item.setOriginId(distance.getOriginId());
                item.setDestId(distance.getDestId());
                item.setDistance(distance.getDistance());
                item.setDuration(distance.getDuration());
                results.add(item);
            }
        }
        
        result.setResults(results);
        return result;
    }

    private McpResponseModels.WeatherResult convertWeatherResponse(AmapOtherModels.WeatherResponse response) {
        var result = new McpResponseModels.WeatherResult();
        
        if (response.getForecasts() != null && !response.getForecasts().isEmpty()) {
            var forecast = response.getForecasts().get(0);
            result.setCity(forecast.getCity());
            
            List<McpResponseModels.WeatherForecast> forecasts = new ArrayList<>();
            if (forecast.getCasts() != null) {
                for (AmapOtherModels.WeatherCast cast : forecast.getCasts()) {
                    McpResponseModels.WeatherForecast weatherForecast = new McpResponseModels.WeatherForecast();
                    weatherForecast.setDate(cast.getDate());
                    weatherForecast.setWeek(cast.getWeek());
                    weatherForecast.setDayweather(cast.getDayweather());
                    weatherForecast.setNightweather(cast.getNightweather());
                    weatherForecast.setDaytemp(cast.getDaytemp());
                    weatherForecast.setNighttemp(cast.getNighttemp());
                    weatherForecast.setDaywind(cast.getDaywind());
                    weatherForecast.setNightwind(cast.getNightwind());
                    weatherForecast.setDaypower(cast.getDaypower());
                    weatherForecast.setNightpower(cast.getNightpower());
                    weatherForecast.setDaytempFloat(cast.getDaytempFloat());
                    weatherForecast.setNighttempFloat(cast.getNighttempFloat());
                    forecasts.add(weatherForecast);
                }
            }
            result.setForecasts(forecasts);
        }
        
        return result;
    }

    private McpResponseModels.IpLocationResult convertIpLocationResponse(AmapOtherModels.IpLocationResponse response) {
        var result = new McpResponseModels.IpLocationResult();
        result.setProvince(response.getProvince());
        result.setCity(response.getCity());
        result.setAdcode(response.getAdcode());
        result.setRectangle(response.getRectangle());
        return result;
    }

    // ====================== 辅助方法 ======================

    private McpResponseModels.RouteResult convertRouteResponse(RouteModels.RouteResponse response) {
        var result = new McpResponseModels.RouteResult();
        
        if (response.getRoute() != null) {
            result.setOrigin(response.getRoute().getOrigin());
            result.setDestination(response.getRoute().getDestination());
            
            List<McpResponseModels.RoutePath> paths = new ArrayList<>();
            if (response.getRoute().getPaths() != null) {
                for (RouteModels.Path path : response.getRoute().getPaths()) {
                    McpResponseModels.RoutePath routePath = new McpResponseModels.RoutePath();
                    routePath.setPath("");
                    routePath.setDistance(path.getDistance());
                    routePath.setDuration(path.getCost() != null ? path.getCost().getDuration() : "0");
                    routePath.setSteps(convertRouteSteps(path.getSteps()));
                    paths.add(routePath);
                }
            }
            result.setPaths(paths);
        }
        
        return result;
    }

    private List<McpResponseModels.RouteStep> convertRouteSteps(List<RouteModels.Step> steps) {
        List<McpResponseModels.RouteStep> routeSteps = new ArrayList<>();
        if (steps != null) {
            for (RouteModels.Step step : steps) {
                McpResponseModels.RouteStep routeStep = new McpResponseModels.RouteStep();
                routeStep.setInstruction(step.getInstruction());
                routeStep.setRoad("");
                routeStep.setDistance(step.getStepDistance());
                routeStep.setOrientation(step.getOrientation());
                routeStep.setDuration(step.getCost() != null ? step.getCost().getDuration() : "0");
                routeSteps.add(routeStep);
            }
        }
        return routeSteps;
    }

    private List<McpResponseModels.WalkingStep> convertWalkingSteps(List<RouteModels.Step> steps) {
        List<McpResponseModels.WalkingStep> walkingSteps = new ArrayList<>();
        if (steps != null) {
            for (RouteModels.Step step : steps) {
                McpResponseModels.WalkingStep walkingStep = new McpResponseModels.WalkingStep();
                walkingStep.setInstruction(step.getInstruction());
                walkingStep.setRoad("");
                walkingStep.setDistance(safeParseInt(step.getStepDistance()));
                walkingStep.setOrientation(step.getOrientation());
                walkingStep.setDuration(step.getCost() != null ? safeParseInt(step.getCost().getDuration()) : 0);
                walkingSteps.add(walkingStep);
            }
        }
        return walkingSteps;
    }

    /**
     * 安全转换字符串为整数
     */
    private int safeParseInt(String value) {
        try {
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}