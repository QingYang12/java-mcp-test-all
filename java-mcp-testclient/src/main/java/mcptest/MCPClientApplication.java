package mcptest;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication(exclude = {
        com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAutoConfiguration.class
})
public class MCPClientApplication { //implements CommandLineRunner

   // @Autowired
   // private McpClient mcpClient;

   // @Autowired
   // private OfficialWebSearchClient officialWebSearchClient;

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void checkConfig() {
        String enabled = environment.getProperty("spring.ai.dashscope.enabled");
        System.out.println("👉 spring.ai.dashscope.enabled = " + enabled);
        System.out.println("👉 spring.ai.alibaba.qwen.api-key = " + environment.getProperty("spring.ai.alibaba.qwen.api-key"));
    }


    public static void main(String[] args) {
        SpringApplication.run(MCPClientApplication.class, args);
    }

   /** @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 MCP Client 启动，连接本地 MCP Server...");
       // mcpClient.listenToSse(); // 开始监听 SSE 并自动发送 tool_call
        officialWebSearchClient.start();
    }**/
}
