package mcptest.chatclient;


import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
public class OfficialWebSearchClient2 {
    private static final String API_KEY = "sk-5a839dbb64074a62a1a78e9cb6502bef"; // 替换为您的真实 KEY
    public List<NewsItem> searchWeb2(
            @ToolParam(description = "要搜索的查询语句") String query) throws NoApiKeyException, InputRequiredException {

        System.out.println("MCP WebSearch--------------------------: " + query);
        List<NewsItem> resultTags=new ArrayList<NewsItem>();
        String resultStr="";
        try{
            if (query == null || query.trim().isEmpty()) {
                return resultTags;
            }
            // 1. 构建 bizParams
            JsonObject bizParams = new JsonObject();
            bizParams.addProperty("max_tokens", 16384);  // ⭐ 设置最大输出 token 数
            bizParams.addProperty("temperature", 0.7);
            bizParams.addProperty("top_p", 0.9);
            ApplicationParam param = ApplicationParam.builder()
                    // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                    .apiKey(API_KEY)
                    .appId("07baa4d64261423c9ea111e383cf25dd")
                    .prompt(query)
                    .bizParams(bizParams)
                    .build();

            Application application = new Application();
            ApplicationResult result = application.call(param);
            resultStr=result.getOutput().getText();
            System.out.printf("text: %s\n",
                    resultStr);
            resultTags=parseTextToList(result);
        }catch (Exception e){
            e.printStackTrace();
        }

        return resultTags;
    }
    public static List<NewsItem> parseTextToList(ApplicationResult result) throws IOException {
        String json = result.getOutput().getText();
        ObjectMapper mapper = new ObjectMapper();

        // 去除可能的 Markdown 代码块标记（如 ```json 和 ```）
        json = json.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();
        List<NewsItem> newsList = mapper.readValue(json, new TypeReference<List<NewsItem>>() {});
        return newsList;
    }


}
