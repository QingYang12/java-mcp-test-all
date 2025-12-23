package mcptest.mcpserver.config;

import lombok.extern.slf4j.Slf4j;
import mcptest.mcpserver.server.NewsMcpService;
import mcptest.mcpserver.server.RulesMcpService;
import mcptest.mcpserver.server.SmdMcpService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class McpConfig {

    @Bean
    public ToolCallbackProvider smdToolCallbackProvider(SmdMcpService smdMcpService, NewsMcpService newsMcpService, RulesMcpService rulesMcpService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(smdMcpService, newsMcpService,rulesMcpService)
                .build();
    };
}
