package com.fottas.amapmcpserver;

import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;

/**
 * @author Christian Tzolov
 */

public class SampleClient {

	private final McpClientTransport transport;

	public SampleClient(McpClientTransport transport) {
		this.transport = transport;
	}

	public void run() {

		var client = McpClient.sync(this.transport).build();

		client.initialize();

		client.ping();

		// List and demonstrate tools
		ListToolsResult toolsList = client.listTools();
		System.out.println("Available Tools = " + toolsList);

//		CallToolResult weatherResult = client.callTool(new CallToolRequest("weather_query",
//				Map.of("city", "北京市", "type", "forecast")));
//		System.out.println("Weather Forcast: " + weatherResult);

		CallToolResult routeResult = client.callTool(new CallToolRequest("maps_text_search",
				Map.of("keywords", "滇池",
				"city", "昆明",
				"citylimit", false)));
		System.out.println("routeResult: " + routeResult);

		client.closeGracefully();

	}

}