package mcptest;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import okhttp3.OkHttpClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class AiConfig {
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public DashScopeApi dashScopeApi() {
        return new DashScopeApi(apiKey);
    }

    @Bean
    public ChatModel chatModel(DashScopeApi dashScopeApi) {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel("qwen-plus")
                .build();
        return new DashScopeChatModel(dashScopeApi, options);
    }
}
