package mcptest.chatclient;


import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//@Component
public class OfficialWebSearchClient {
    private static final String SSE_URL = "https://dashscope.aliyuncs.com/api/v1/mcps/WebSearch/sse";
    private static final String API_KEY = "sk-5a839dbb64074a62a1a78e9cb6502bef"; // 替换为您的真实 KEY

    private final WebClient webClient = WebClient.builder()
            .defaultHeader("Authorization", "Bearer " + API_KEY)
            .build();
    public void start() {
        System.out.println("🚀 连接阿里官方 WebSearch MCP 服务...");

        webClient.get()
                .uri(SSE_URL)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(300))
                .onErrorResume(error -> {
                    System.err.println("❌ SSE 连接错误: " + error.getMessage());
                    return Mono.empty();
                })

                // ✅ Step 1: 按空行分段，每个 window 是一个完整事件的所有行
                .windowUntil(String::isBlank, true) // isBlank 包括空和纯空白

                // ✅ Step 2: 处理每个 window（一个事件块）
                .flatMap(window -> window
                        .takeWhile(line -> !line.isBlank()) // 只取非空行
                        .collectList() // 收集成 List<String>
                        .filter(list -> !list.isEmpty())
                        .map(this::parseEvent) // 使用您已有的 parseEvent(List<String>)
                        .onErrorResume(e -> {
                            System.err.println("❌ 解析事件失败: " + e.getMessage());
                            return Mono.empty();
                        })
                )

                // ✅ Step 3: 处理事件
                .subscribe(event -> {
                    if ("endpoint".equals(event.event())) {
                        System.out.println("🎯 发现工具端点: " + event.data());
                        sendToolCall(); // 触发搜索
                    } else if ("tool_result".equals(event.event())) {
                        System.out.println("✅ 搜索结果: \n" + event.data());
                    } else if ("error".equals(event.event())) {
                        System.err.println("❌ 工具错误: " + event.data());
                    } else if (event.event() == null) {
                        System.out.println("💡 心跳/注释: " + event.data());
                    }
                }, error -> {
                    System.err.println("❌ 流终止: " + error.getMessage());
                });
    }
    private ServerSentEvent<String> parseEventBlock(String block) {
        StringBuilder data = new StringBuilder();
        String event = null;
        String id = null;

        String[] lines = block.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith(":")) {
                // 心跳注释
                return ServerSentEvent.<String>builder()
                        .event(null)
                        .data(line)
                        .build();
            }

            if (line.startsWith("id:")) {
                id = line.substring(3).trim();
            } else if (line.startsWith("event:")) {
                event = line.substring(6).trim();
            } else if (line.startsWith("data:")) {
                data.append(line.substring(5).trim()).append("\n");
            }
        }

        // 构建最终事件
        return ServerSentEvent.<String>builder()
                .id(id)
                .event(event)
                .data(data.toString().trim())
                .build();
    }
    // 手动解析一个 SSE 事件块
    private ServerSentEvent<String> parseEvent(List<String> lines) {
        StringBuilder data = new StringBuilder();
        String event = null;
        String id = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith(":")) {
                return ServerSentEvent.<String>builder().event(null).data(line).build();
            }

            if (line.startsWith("id:")) {
                id = line.substring(3).trim();
            } else if (line.startsWith("event:")) {
                event = line.substring(6).trim();
            } else if (line.startsWith("data:")) {
                data.append(line.substring(5).trim()).append("\n");
            }
        }

        return ServerSentEvent.<String>builder()
                .id(id)
                .event(event)
                .data(data.toString().trim())
                .build();
    }
    private void sendToolCall() {
        Map<String, Object> request = Map.of(
                "type", "tool_call",
                "name", "web_search",
                "parameters", Map.of("query", "阿里巴巴最新新闻")
        );

        webClient.post()
                .uri("https://dashscope.aliyuncs.com/api/v1/mcps/WebSearch/sse") // ✅ 完整 URL
                .header("Authorization", "Bearer " + API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        success -> System.out.println("📤 tool_call 已发送"),
                        error -> System.err.println("❌ 发送失败: " + error.getMessage())
                );
    }
}
