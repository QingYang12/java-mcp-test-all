package mcptest.chat;


import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class MCPController {
    @PostMapping("/mcp/message")
    public Flux<ServerSentEvent<String>> handleMessage(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");
        if ("tool_call".equals(type)) {
            String toolName = (String) request.get("name");
            Map<String, Object> params = (Map<String, Object>) request.get("parameters");

            String result;
            if ("search_web2".equals(toolName)) {
              //  result = newsMcpService.searchWeb((String) params.get("query"));
                result = "";
            } else {
                result = "错误：未知工具 " + toolName;
            }

            return Flux.just(
                    ServerSentEvent.<String>builder()
                            .event("tool_result")
                            .data(result)
                            .build()
            );
        }
        return Flux.just(
                ServerSentEvent.<String>builder()
                        .event("error")
                        .data("无效请求")
                        .build()
        );
    }

}
