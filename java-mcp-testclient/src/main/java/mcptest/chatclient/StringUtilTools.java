package mcptest.chatclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtilTools {

    public static List<String> parseTags(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            String content = root.at("/choices/0/message/content").asText();

            // 把 content 当作 JSON 数组解析
            return Arrays.asList(mapper.readValue(content, String[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return  new ArrayList<>();
        }
    }

}
