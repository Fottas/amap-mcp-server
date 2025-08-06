package com.fottas.amapmcpserver.service;

import com.fottas.amapmcpserver.config.AmapConfigProperties;
import com.fottas.amapmcpserver.model.AmapApiModels;
import com.fottas.amapmcpserver.model.AmapOtherModels;
import com.fottas.amapmcpserver.model.PoiModels;
import com.fottas.amapmcpserver.model.RouteModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 高德地图API服务类 - 基于真实API文档优化版
 * 严格按照高德API规范：https://amap.apifox.cn/
 */
@Service
public class AmapApiService {

    private static final Logger logger = LoggerFactory.getLogger(AmapApiService.class);

    private final WebClient webClient;
    private final AmapConfigProperties configProperties;
    private final Retry amapApiRetry;

    public AmapApiService(@Qualifier("amapWebClient") WebClient webClient,
                          AmapConfigProperties configProperties,
                          Retry amapApiRetry) {
        this.webClient = webClient;
        this.configProperties = configProperties;
        this.amapApiRetry = amapApiRetry;
    }

    // ====================== 地理编码相关 ======================

    @Cacheable(value = "amapCache", key = "'geocoding:' + #request.address + ':' + T(java.util.Objects).toString(#request.city, '')")
    public Mono<AmapApiModels.GeocodingResponse> geocoding(AmapApiModels.GeocodingRequest request) {
        return callAmapApi("/v3/geocode/geo", request, AmapApiModels.GeocodingResponse.class, "地理编码", request.getAddress());
    }

    @Cacheable(value = "amapCache", key = "'reverse_geocoding:' + #request.location + ':' + T(java.util.Objects).toString(#request.radius, '1000')")
    public Mono<AmapApiModels.ReverseGeocodingResponse> reverseGeocoding(AmapApiModels.ReverseGeocodingRequest request) {
        return callAmapApi("/v3/geocode/regeo", request, AmapApiModels.ReverseGeocodingResponse.class, "逆地理编码", request.getLocation());
    }

    // ====================== POI搜索相关 ======================

    @Cacheable(value = "amapCache", key = "'poi_text_search:' + #request.keywords + ':' + T(java.util.Objects).toString(#request.region, '') + ':' + #request.page_num")
    public Mono<PoiModels.PoiResponse> poiTextSearch(PoiModels.PoiTextSearchRequest request) {
        return callAmapApi("/v5/place/text", request, PoiModels.PoiResponse.class, "POI关键字搜索", request.getKeywords());
    }

    @Cacheable(value = "amapCache", key = "'poi_around_search:' + #request.location + ':' + T(java.util.Objects).toString(#request.keywords, '') + ':' + #request.radius + ':' + #request.page_num")
    public Mono<PoiModels.PoiResponse> poiAroundSearch(PoiModels.PoiAroundSearchRequest request) {
        return callAmapApi("/v5/place/around", request, PoiModels.PoiResponse.class, "POI周边搜索", request.getLocation());
    }

    @Cacheable(value = "amapCache", key = "'poi_polygon_search:' + #request.polygon + ':' + T(java.util.Objects).toString(#request.keywords, '') + ':' + #request.page_num")
    public Mono<PoiModels.PoiResponse> poiPolygonSearch(PoiModels.PoiPolygonSearchRequest request) {
        return callAmapApi("/v5/place/polygon", request, PoiModels.PoiResponse.class, "POI多边形搜索", request.getPolygon());
    }

    @Cacheable(value = "amapCache", key = "'poi_detail:' + #request.id")
    public Mono<PoiModels.PoiResponse> poiDetail(PoiModels.PoiDetailRequest request) {

        return callAmapApi("/v5/place/detail", request, PoiModels.PoiResponse.class, "POI详情查询", request.getId());
    }

    // ====================== 路线规划相关 ======================

    // 驾车路线规划
    @Cacheable(value = "amapCache", key = "'driving_route:' + #request.origin + ':' + #request.destination + ':' + T(java.util.Objects).toString(#request.strategy, '0')")
    public Mono<RouteModels.RouteResponse> drivingRoute(RouteModels.DrivingRouteRequest request) {
        return callAmapApi("/v5/direction/driving", request, RouteModels.RouteResponse.class, "驾车路线规划", request.getOrigin() + " -> " + request.getDestination());
    }

    // 步行路线规划
    @Cacheable(value = "amapCache", key = "'walking_route:' + #request.origin + ':' + #request.destination")
    public Mono<RouteModels.RouteResponse> walkingRoute(RouteModels.WalkingRouteRequest request) {
        return callAmapApi("/v5/direction/walking", request, RouteModels.RouteResponse.class, "步行路线规划", request.getOrigin() + " -> " + request.getDestination());
    }

    // 骑行路线规划
    @Cacheable(value = "amapCache", key = "'bicycling_route:' + #request.origin + ':' + #request.destination + ':' + T(java.util.Objects).toString(#request.riding_type, '0')")
    public Mono<RouteModels.RouteResponse> bicyclingRoute(RouteModels.BicyclingRouteRequest request) {
        return callAmapApi("/v5/direction/bicycling", request, RouteModels.RouteResponse.class, "骑行路线规划", request.getOrigin() + " -> " + request.getDestination());
    }

    // 公交路线规划
    @Cacheable(value = "amapCache", key = "'transit_route:' + #request.origin + ':' + #request.destination + ':' + #request.city + ':' + T(java.util.Objects).toString(#request.strategy, '0')")
    public Mono<RouteModels.TransitRouteResponse> transitRoute(RouteModels.TransitRouteRequest request) {
        return callAmapApi("/v5/direction/transit/integrated", request, RouteModels.TransitRouteResponse.class, "公交路线规划", request.getOrigin() + " -> " + request.getDestination());
    }

    @Cacheable(value = "amapCache", key = "'distance:' + #request.origins + ':' + #request.destination + ':' + T(java.util.Objects).toString(#request.type, '0')")
    public Mono<AmapOtherModels.DistanceResponse> distance(AmapOtherModels.DistanceRequest request) {
        return callAmapApi("/v3/distance", request, AmapOtherModels.DistanceResponse.class, "距离测量", request.getOrigins() + " -> " + request.getDestination());
    }

    // ====================== 其他API相关 ======================

    @Cacheable(value = "amapCache", key = "'current_weather:' + #request.city")
    public Mono<AmapOtherModels.WeatherResponse> getCurrentWeather(AmapOtherModels.WeatherRequest request) {
        request.setExtensions("base");
        return callAmapApi("/v3/weather/weatherInfo", request, AmapOtherModels.WeatherResponse.class, "实时天气查询", request.getCity());
    }

    @Cacheable(value = "amapCache", key = "'forecast_weather:' + #request.city")
    public Mono<AmapOtherModels.WeatherResponse> getWeatherForecast(AmapOtherModels.WeatherRequest request) {
        request.setExtensions("all");
        return callAmapApi("/v3/weather/weatherInfo", request, AmapOtherModels.WeatherResponse.class, "天气预报查询", request.getCity());
    }

    @Cacheable(value = "amapCache", key = "'ip_location:' + T(java.util.Objects).toString(#request.ip, 'auto')")
    public Mono<AmapOtherModels.IpLocationResponse> ipLocation(AmapOtherModels.IpLocationRequest request) {
        return callAmapApi("/v3/ip", request, AmapOtherModels.IpLocationResponse.class, "IP定位查询", request.getIp());
    }

    // ====================== 私有方法 ======================

    /**
     * 统一高德API调用方法
     */
    private <T extends AmapApiModels.ApiResult<?>> Mono<T> callAmapApi(String endpoint, Object request,
                                                                       Class<T> responseType, String apiName, String key) {
        Map<String, String> params = buildRequestParams(request);

        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path(endpoint);
                    params.forEach(builder::queryParam);
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(amapApiRetry)
                .doOnSuccess(response -> logApiResult(response, apiName, key, true))
                .doOnError(error -> logApiResult(null, apiName, key, false, error))
                .onErrorMap(WebClientResponseException.class,
                        ex -> new RuntimeException(apiName + "失败: " + ex.getMessage(), ex));
    }

    /**
     * 构建请求参数 - 支持继承字段和特殊类型处理
     */
    private Map<String, String> buildRequestParams(Object request) {
        Map<String, String> params = new HashMap<>();
        params.put("key", configProperties.getKey());

        Class<?> clazz = request.getClass();
        while (clazz != null && !clazz.equals(Object.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(request);
                    if (value != null) {
                        String strValue = convertFieldValue(field, value);
                        if (!strValue.isEmpty()) {
                            params.put(field.getName(), strValue);
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.warn("访问字段失败: {} in {}", field.getName(), clazz.getSimpleName());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return params;
    }

    /**
     * 转换字段值为字符串 - 处理特殊类型
     */
    private String convertFieldValue(Field field, Object value) {
        if (value instanceof Boolean) {
            return value.toString().toLowerCase();
        }
        String strValue = value.toString().trim();
        return "null".equals(strValue) ? "" : strValue;
    }

    /**
     * 记录API调用结果
     */
    private void logApiResult(AmapApiModels.ApiResult<?> response, String apiName, String key,
                              boolean isSuccess, Throwable error) {
        if (isSuccess && response != null) {
            if (response.isSuccess()) {
                logger.debug("{}成功，关键信息: {}", apiName, key);
            } else {
                logger.warn("{}失败，关键信息: {}, 错误: {}", apiName, key, response.getMessage());
            }
        } else {
            if (error instanceof WebClientResponseException) {
                WebClientResponseException ex = (WebClientResponseException) error;
                logger.error("{}API调用失败，关键信息: {}, 状态码: {}, 错误: {}",
                        apiName, key, ex.getStatusCode(), ex.getResponseBodyAsString());
            } else {
                logger.error("{}执行失败，关键信息: {}", apiName, key, error);
            }
        }
    }

    private void logApiResult(AmapApiModels.ApiResult<?> response, String apiName, String key, boolean isSuccess) {
        logApiResult(response, apiName, key, isSuccess, null);
    }
}