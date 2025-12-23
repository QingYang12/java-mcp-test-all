package mcptest.chatclient;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
//@Component
public class McpClient {
    private final WebClient webClient = WebClient.create("http://localhost:8089");

    // 用于接收事件流
    public void listenToSse() {
        Flux<ServerSentEvent> eventStream = webClient.get()
                .uri("/sse")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(ServerSentEvent.class)
                .timeout(Duration.ofSeconds(300))
                .onErrorResume(e -> {
                    System.err.println("SSE 连接失败: " + e.getMessage());
                    return Mono.empty();
                });

        eventStream.subscribe(event -> {
            if ("endpoint".equals(event.event())) {
                System.out.println("发现 MCP 工具端点: " + event.data());
                // 可以在这里触发 tool_call
                sendToolCall();
            } else if ("ping".equals(event.event())) {
                System.out.println("心跳: " + event.data());
            }
        });
    }

    // 发送 tool_call 请求
    public void sendToolCall() {
        Map<String, Object> request = new HashMap<>();
        request.put("type", "tool_call");
        request.put("name", "search_web");  // 请确保这是您 @Tool 的 name
        Map<String, String> params = new HashMap<>();
        params.put("query", "阿里巴巴最新新闻");
        request.put("parameters", params);

        webClient.post()
                .uri("/mcp/message")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ServerSentEvent.class)
                .timeout(Duration.ofSeconds(300))
                .subscribe(result -> {
                    if ("tool_result".equals(result.event())) {
                        System.out.println("✅ 工具执行结果: " + result.data());
                    } else if ("error".equals(result.event())) {
                        System.err.println("❌ 工具执行失败: " + result.data());
                    }
                }, error -> {
                    System.err.println("调用失败: " + error.getMessage());
                });
    }
}
