package mcptest.chatclient;



import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.Buffer;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.*;

public class DashScopeClientUtil {

    @Value("${qwenkey}")
    private String API_KEY="sk-5a839dbb64074a62a1a78e9cb6502bef";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    /**
     * 聊天 调用大模型
     * @param systemPrompt 系统提示
     * @param userPrompt 用户输入
     * @return 响应结果
     * @throws IOException
     */
    public String chat(String systemPrompt, String userPrompt) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "qwen-plus");
        requestMap.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        // 自动转义并生成紧凑 JSON
        String json = mapper.writeValueAsString(requestMap);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        log(request);
        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            // 简单解析 JSON（生产环境建议用 Jackson/Gson）
            System.out.println("Response: " + responseBody);
            return responseBody;
        }
    }
    /**
     * 聊天 调用大模型MCP
     * @param systemPrompt 系统提示
     * @param userPrompt 用户输入
     * @return 响应结果
     * @throws IOException
     */
    public String chatMCP(String systemPrompt, String userPrompt) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "qwen-plus");
        requestMap.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        // 自动转义并生成紧凑 JSON
        String json = mapper.writeValueAsString(requestMap);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        log(request);
        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            // 简单解析 JSON（生产环境建议用 Jackson/Gson）
            System.out.println("Response: " + responseBody);
            return responseBody;
        }
    }


    public void log(Request request){
        // ==================== 【添加以下代码】====================
        // 打印请求信息（调试用）
        System.out.println("======== 请求信息调试 ========");

        // 1. 打印 URL
        System.out.println("👉 URL: " + request.url());

        // 2. 打印 Headers
        System.out.println("👉 Headers:");
        for (int i = 0; i < request.headers().size(); i++) {
            System.out.println("    " + request.headers().name(i) + ": " + request.headers().value(i));
        }

        // 3. 打印 Body（关键！）
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            try {
                request.body().writeTo(buffer);
                System.out.println("👉 请求体 (Body): " + buffer.readUtf8());
            } catch (IOException e) {
                System.err.println("❌ 无法读取请求体: " + e.getMessage());
            }
        }
        System.out.println("==============================");

        // ==================== 【添加代码结束】====================
    }

    public static void main(String[] args) throws IOException {
        DashScopeClientUtil ds = new DashScopeClientUtil();

        String system = "你是一个助手";
        String user = "你好，你是谁？";

        String result = ds.chat(system, user);
        System.out.println(result);
    }
}