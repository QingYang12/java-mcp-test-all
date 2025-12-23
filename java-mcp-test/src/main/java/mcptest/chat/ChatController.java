package mcptest.chat;


import mcptest.mcpserver.util.DashScopeClientUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    // ✅ 使用你自己封装的客户端
    private final DashScopeClientUtil ds = new DashScopeClientUtil();


    @GetMapping("/search")
    public String ask(@RequestParam String question) {
        String SYSTEM_TEMPLATE = """
        你是一个智能助手，可以使用工具获取实时信息。
        如果用户的问题涉及实时数据（如新闻、股价、天气、赛事等），请调用 search_web 工具进行联网搜索。
        否则，直接回答。
        """;
        String USER_TEMPLATE = """
            用户问题：
            ---
            %s
            ---
            """.formatted(question);

        try {
            String result = ds.chatMCP(SYSTEM_TEMPLATE, USER_TEMPLATE);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return "[ERROR] 调用失败: " + e.getMessage();
        }


    }
}