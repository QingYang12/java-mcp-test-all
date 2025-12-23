
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


public class Test1 {



    @Autowired
    private ChatClient chatClient;

    String port = "8089";
    // ✅ 测试 1：直接调用控制器方法（推荐，轻量）
    public void testSplitNews_DirectCall() {



        String question="";

        // ✅ 系统提示：定义任务角色和输出格式
        String SYSTEM_TEMPLATE = """
            你是一个新闻分析专家。
            请从用户提供的新闻内容中，提炼出最核心的 3 个要点标签。
            要求：
            - 每个标签不超过 6 个汉字
            - 只输出一个 JSON 数组，格式：["标签1", "标签2", "标签3"]
            - 不要解释、不要额外内容
            """;

        // ✅ 用户提示模板
        String USER_TEMPLATE = """
            请分析以下新闻内容，并生成 3 个要点标签：
            ---
            {question}
            ---
            """;

        // ✅ 1. 构建系统消息（使用 content()，不是 getText() 或 getContent()）
        PromptTemplate systemPrompt = new PromptTemplate(SYSTEM_TEMPLATE);
        SystemMessage systemMessage = new SystemMessage(systemPrompt.render(Map.of()));

        // ✅ 2. 构建用户消息
        PromptTemplate userPrompt = new PromptTemplate(USER_TEMPLATE);
        UserMessage userMessage = new UserMessage(userPrompt.render(Map.of("question", question)));

        // ✅ 3. 调用 LLM（使用 ChatClient DSL）
         chatClient.prompt()
                .system(systemMessage.getText())  // ✅ 正确使用 content()
                .user(userMessage.getText())      // ✅ 正确使用 content()
                .call()
                .content();                      // ✅ 返回 JSON 数组字符串



    }

    // ✅ 测试 2：通过 HTTP 调用接口（完整端到端测试）

    public void testSplitNews_HttpRequest() throws Exception {
        String news = "特斯拉发布了新款无人驾驶汽车，支持城市道路自动驾驶。";

        // 1. 对 URL 进行编码
        String encodedQuestion = java.net.URLEncoder.encode(news, java.nio.charset.StandardCharsets.UTF_8);
        String url = "http://localhost:" + port + "/splitNews?question=" + encodedQuestion;

        // 2. 创建 HttpClient（Java 11+ 内置）
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

        // 3. 构建 HTTP 请求
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        // 4. 发送请求并获取响应
        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

        // 5. 获取响应体
        String result = response.body();

        System.out.println("HTTP Request Result: " + result);

    }



    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
