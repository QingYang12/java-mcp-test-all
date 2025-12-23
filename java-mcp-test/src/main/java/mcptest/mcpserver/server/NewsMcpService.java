package mcptest.mcpserver.server;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Service
public class NewsMcpService {

        @Autowired
        private RestTemplate restTemplate;


        @Autowired
        private ObjectMapper objectMapper;

        @Value("${mcp.websearch.base-url}")
        private String mcpWebSearchBaseUrl;

        @Value("${mcp.websearch.api-key}")
        private String mcpApiKey;

        // ==================== 新增：调用 MCP WebSearch 的工具 ====================




        //@Tool(name = "search_web", description = "通过阿里云百炼 MCP WebSearch 进行联网搜索，获取实时信息")
        public String searchWeb(
                @ToolParam(description = "要搜索的查询语句") String query) {

            System.out.println("MCP WebSearch--------------------------: " + query);

            if (query == null || query.trim().isEmpty()) {
                return "搜索查询不能为空。";
            }

            String url = mcpWebSearchBaseUrl;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(mcpApiKey); // 使用 Bearer Token
            //headers.set("Authorization", "Bearer sk-5a839dbb64074a62a1a78e9cb6502bef");
            /** 构建请求体
            Map<String, Object> body = new HashMap<>();
            body.put("query", query);
            body.put("count", 1); // 示例参数

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);**/
            HttpEntity<String> entity = new HttpEntity<>(headers);
            try {
                // 使用 RestTemplate 发起同步请求
                //ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
                // 使用 exchange 方法发送 GET 请求并传递请求头
                ResponseEntity<String> response = restTemplate.exchange(
                        url + "?query={query}&count={count}",
                        HttpMethod.GET,
                        entity,
                        String.class,
                        query, 1
                );
                // 检查 HTTP 状态码
                if (response.getStatusCode() != HttpStatus.OK) {
                    return "搜索请求失败，HTTP 状态码: " + response.getStatusCodeValue();
                }

                // 解析 JSON 响应（需根据实际 API 文档调整）
                List<String> snippets = extractSnippetsFromJson(response.getBody());
                return String.join("\n", snippets);

            } catch (RestClientException e) {
                return "搜索请求失败: " + e.getMessage();
            }
        }

    private List<String> extractSnippetsFromJson(String json) {
        try {
            JsonNode rootNode = new ObjectMapper().readTree(json);
            JsonNode resultNode = rootNode.path("result");

            List<String> snippets = new ArrayList<>();
            for (JsonNode item : resultNode) {
                String title = item.path("title").asText();
                String content = item.path("content").asText();
                snippets.add("【" + title + "】\n" + content);
            }
            return snippets;
        } catch (Exception e) {
            return Collections.singletonList("无法解析 JSON 响应: " + e.getMessage());
        }
    }

    @Tool(name = "search_web", description = "通过阿里云百炼 MCP WebSearch 进行联网搜索，获取实时信息")
    public List<String> searchWeb2(
            @ToolParam(description = "要搜索的查询语句") String query) throws NoApiKeyException, InputRequiredException {

        System.out.println("MCP WebSearch--------------------------: " + query);
        List<String> resultTags=new ArrayList<String>();
        try{
            if (query == null || query.trim().isEmpty()) {
                return resultTags;
            }
            ApplicationParam param = ApplicationParam.builder()
                    // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                    .apiKey(mcpApiKey)
                    .appId("07baa4d64261423c9ea111e383cf25dd")
                    .prompt(query)
                    .build();

            Application application = new Application();
            ApplicationResult result = application.call(param);

            System.out.printf("text: %s\n",
                    result.getOutput().getText());
            resultTags=parseTextToList(result);
        }catch (Exception e){
            e.printStackTrace();
        }

       return resultTags;
    }
    public static List<String> parseTextToList(ApplicationResult result) throws IOException {
        String json = result.getOutput().getText();
        ObjectMapper mapper = new ObjectMapper();

        // 去除可能的 Markdown 代码块标记（如 ```json 和 ```）
        json = json.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();
        return mapper.readValue(json, new TypeReference<List<String>>() {});
    }

}