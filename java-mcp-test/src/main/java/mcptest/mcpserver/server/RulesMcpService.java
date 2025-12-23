package mcptest.mcpserver.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RulesMcpService {

    @Tool(name = "checkRule", description = "执行指定名称的业务规则")
    public Map<String, Object> checkRule(
            @ToolParam(description = "规则名称") String ruleName,
            @ToolParam(description = "输入参数") Map<String, Object> inputParams) {

        Map<String, Object> result = new HashMap<>();
        result.put("ruleName", ruleName);
        result.put("input", inputParams);
        result.put("result", evaluateRule(ruleName, inputParams));
        return result;
    }

    private boolean evaluateRule(String ruleName, Map<String, Object> params) {
        switch (ruleName) {
            case "user_age_check":
                Integer age = (Integer) params.get("age");
                return age != null && age >= 18;
            case "order_amount_check":
                Double amount = (Double) params.get("amount");
                return amount != null && amount > 0 && amount <= 10000;
            default:
                return false;
        }
    }

    @Tool(name = "listAvailableRules", description = "列出所有可用的规则")
    public List<String> listAvailableRules() {
        return List.of("user_age_check", "order_amount_check", "payment_method_check");
    }
}