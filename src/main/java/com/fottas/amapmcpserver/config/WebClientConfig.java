package com.fottas.amapmcpserver.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient配置类
 * 配置用于调用高德地图API的HTTP客户端
 */
@Configuration
@EnableCaching
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    private final AmapConfigProperties amapConfigProperties;

    public WebClientConfig(AmapConfigProperties amapConfigProperties) {
        this.amapConfigProperties = amapConfigProperties;
    }

    /**
     * 配置用于高德地图API的WebClient
     *
     * @return 配置好的WebClient实例
     */
    @Bean("amapWebClient")
    public WebClient amapWebClient() {
        // 创建连接池
        ConnectionProvider connectionProvider = ConnectionProvider.builder("amap-connection-pool")
                .maxConnections(amapConfigProperties.getHttpClient().getMaxConnections())
                .maxIdleTime(amapConfigProperties.getHttpClient().getIdleTimeout())
                .maxLifeTime(Duration.ofMinutes(30))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        // 配置HttpClient
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 
                        (int) amapConfigProperties.getHttpClient().getConnectionTimeout().toMillis())
                .responseTimeout(amapConfigProperties.getHttpClient().getReadTimeout())
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(
                                amapConfigProperties.getHttpClient().getReadTimeout().toSeconds(), TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(
                                amapConfigProperties.getHttpClient().getReadTimeout().toSeconds(), TimeUnit.SECONDS)))
                .compress(true);

        // 如果启用HTTP/2
        if (amapConfigProperties.getHttpClient().isHttp2Enabled()) {
            httpClient = httpClient.protocol(reactor.netty.http.HttpProtocol.H2C, reactor.netty.http.HttpProtocol.HTTP11);
        }

        // 配置ExchangeStrategies以处理大响应
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();

        return WebClient.builder()
                .baseUrl(amapConfigProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, amapConfigProperties.getHttpClient().getUserAgent())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .filter(loggingExchangeFilterFunction())
                .filter(retryExchangeFilterFunction())
                .filter(errorHandlingExchangeFilterFunction())
                .build();
    }

    /**
     * 请求日志过滤器
     *
     * @return 日志过滤器
     */
    @Bean
    public ExchangeFilterFunction loggingExchangeFilterFunction() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                clientRequest.headers().forEach((name, values) -> 
                    logger.debug("{}={}", name, values));
            }
            return Mono.just(clientRequest);
        });
    }

    /**
     * 重试过滤器
     *
     * @return 重试过滤器
     */
    @Bean
    public ExchangeFilterFunction retryExchangeFilterFunction() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            logger.warn("Server error received, will retry. Status: {}, Body: {}", 
                                    clientResponse.statusCode(), errorBody);
                            return Mono.error(new RuntimeException("Server error: " + clientResponse.statusCode()));
                        });
            }
            return Mono.just(clientResponse);
        });
    }

    /**
     * 错误处理过滤器
     *
     * @return 错误处理过滤器
     */
    @Bean
    public ExchangeFilterFunction errorHandlingExchangeFilterFunction() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            logger.error("API call failed. Status: {}, Body: {}", 
                                    clientResponse.statusCode(), errorBody);
                            
                            if (clientResponse.statusCode().is4xxClientError()) {
                                return Mono.error(new IllegalArgumentException(
                                        "Client error: " + clientResponse.statusCode() + " - " + errorBody));
                            } else {
                                return Mono.error(new RuntimeException(
                                        "Server error: " + clientResponse.statusCode() + " - " + errorBody));
                            }
                        });
            }
            return Mono.just(clientResponse);
        });
    }

    /**
     * 创建重试规范
     *
     * @return Retry规范
     */
    @Bean
    public Retry amapApiRetry() {
        return Retry.backoff(
                        amapConfigProperties.getRetry().getMaxAttempts(),
                        amapConfigProperties.getRetry().getDelay())
                .maxBackoff(amapConfigProperties.getRetry().getMaxDelay())
                .multiplier(amapConfigProperties.getRetry().getMultiplier())
                .filter(throwable -> {
                    // 只对特定异常进行重试
                    return throwable instanceof RuntimeException && 
                           !throwable.getMessage().contains("Client error");
                })
                .doBeforeRetry(retrySignal -> 
                        logger.warn("Retrying API call, attempt: {}, exception: {}", 
                                retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    logger.error("Retry exhausted after {} attempts", retrySignal.totalRetries());
                    return new RuntimeException("API call failed after " + retrySignal.totalRetries() + " retries", 
                            retrySignal.failure());
                });
    }

    /**
     * 通用WebClient Bean，可用于其他HTTP调用
     *
     * @return 通用WebClient实例
     */
    @Bean("genericWebClient")
    public WebClient genericWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("generic-connection-pool")
                .maxConnections(50)
                .maxIdleTime(Duration.ofMinutes(2))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(30))
                .compress(true);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "AmapMcpServer/1.0")
                .filter(loggingExchangeFilterFunction())
                .build();
    }
}