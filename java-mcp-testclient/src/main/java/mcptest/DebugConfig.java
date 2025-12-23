package mcptest;


import com.alibaba.nacos.common.packagescan.resource.ClassPathResource;
import com.alibaba.nacos.common.packagescan.resource.Resource;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class DebugConfig {
    @PostConstruct
    public void checkMcpConfig() {
        try {
            Resource resource = new ClassPathResource("mcp-servers-config.json");
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            System.out.println("✅ 加载 mcp-servers.json 成功：\n" + content);
        } catch (Exception e) {
            System.err.println("❌ 加载 mcp-servers.json 失败：" + e.getMessage());
        }
    }
}
