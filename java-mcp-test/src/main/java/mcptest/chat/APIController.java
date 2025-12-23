package mcptest.chat;
import mcptest.mcpserver.util.DashScopeClientUtil;
import mcptest.mcpserver.util.StringUtilTools;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mcpApi")
public class APIController {

    // ✅ 使用你自己封装的客户端
    private final DashScopeClientUtil ds = new DashScopeClientUtil();


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