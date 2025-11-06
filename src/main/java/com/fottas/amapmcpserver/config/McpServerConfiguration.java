package com.fottas.amapmcpserver.config;

import com.fottas.amapmcpserver.tools.AmapMcpTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP服务器配置类
 * 配置Spring AI MCP服务器，注册高德地图API工具
 */
@Configuration
public class McpServerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(McpServerConfiguration.class);

    /**
     * 注册MCP工具回调提供者
     * 将高德地图API封装的工具注册到MCP服务器
     */
    @Bean
    public ToolCallbackProvider amapMcpToolCallbackProvider(AmapMcpTools amapMcpTools) {
        logger.info("注册高德地图MCP工具回调提供者");

        // 使用MethodToolCallbackProvider自动扫描@Tool注解的方法
        ToolCallbackProvider provider = MethodToolCallbackProvider.builder()
                .toolObjects(amapMcpTools)
                .build();

        logger.info("成功注册高德地图MCP工具");
        return provider;
    }
}