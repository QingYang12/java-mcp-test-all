package mcptest.chatclient;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client")
public class APIController {

    // ✅ 使用你自己封装的客户端
    private final DashScopeClientUtil ds = new DashScopeClientUtil();
    private  ChatClient chatClient;

    private  OfficialWebSearchClient webSearchClient;

    public APIController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
        // 🔍 打印 MCP 工具（FunctionCallback）信息
        FunctionCallback[] callbacks = tools.getToolCallbacks();
        System.out.println("🔧 MCP 工具数量: " + callbacks.length);
        for (FunctionCallback callback : callbacks) {
            System.out.println("🛠️  工具名: " + callback.getName());
            System.out.println("📝 描述: " + callback.getDescription());
        }
        this.chatClient = chatClientBuilder
                .defaultTools(tools) // ✅ 注入 MCP 工具
                .build();
    }



   /** public APIController(OfficialWebSearchClient webSearchClient) {
        this.webSearchClient = webSearchClient;
    }**/
    @GetMapping("/search")
    public void search(@RequestParam String question) {
        webSearchClient.start(); // 每次访问就触发一次
        System.out.println("已发送搜索请求，请查看控制台输出结果...");
    }
    @GetMapping("/search3")
    public List<NewsItem> search3(@RequestParam String question) throws NoApiKeyException, InputRequiredException {
        OfficialWebSearchClient2 officialWebSearchClient2=new OfficialWebSearchClient2();
        List<NewsItem> result = officialWebSearchClient2.searchWeb2(question);
        return result;
    }

    @GetMapping("/search2")
    public String search2(@RequestParam String question) {
        String userInput = "请调用 search_web 工具，搜索并回答："+question;
        userInput+=" 限制： 调用工具后返回的数组请直接返回不要进行加工，将查询结果以数组的形式返回，例如：`[\"1. 俄罗斯难以赢得战争\", \"2. 阵亡士兵遗体交换\", \"和平前景升温\"]`";
        Map<String, Object> toolContext=new HashMap<>();
        toolContext.put("query", question);
        toolContext.put("count", 5);
       return chatClient.prompt(userInput).call().content();
    }
    @GetMapping("/splitNews")
    public List<String> splitNews(@RequestParam String question) {
        // ✅ 系统提示
        String systemPrompt = """
            你是一个新闻分析专家。
            请从用户提供的新闻内容中，提炼出最核心的 3 个要点标签。
            要求：
            - 每个标签不超过 6 个汉字
            - 只输出一个 JSON 数组，格式：["标签1", "标签2", "标签3"]
            - 不要解释、不要额外内容
            """;

        // ✅ 用户提示
        String userPrompt = """
            请分析以下新闻内容，并生成 3 个要点标签：
            ---
            %s
            ---
            """.formatted(question);

        try {
            // ✅ 调用你自己的客户端
            String result = ds.chat(systemPrompt, userPrompt);
            List<String>  resultTags=StringUtilTools.parseTags(result);
            return resultTags;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}