package mcptest.mcpserver.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SmdMcpService {

        @Autowired
        private RestTemplate restTemplate;

        @Value("${smd.service.url}")
        private String smdServiceUrl;//调用其他服务的url

        @Tool(name = "getSmdInfo", description = "获取表结构信息")
        public String getSmdInfo(
                @ToolParam(description = "业务系统") String businessSystem,
                @ToolParam(description = "表名列表") List<String> tableNames) {

                Map<String, Object> params = new HashMap<>();
                params.put("businessSystem", businessSystem);
                params.put("tableNames", tableNames);

                ResponseEntity<String> response = restTemplate.postForEntity(
                        smdServiceUrl + "/mcp/api/getSmdInfo",
                        params,
                        String.class);

                return response.getBody();
        }

        @Tool(name = "getCRUDCode", description = "根据表名生成增删改查代码")
        public List<Map<String, Object>> getCRUDByTable(
                @ToolParam(description = "业务系统") String businessSystem,
                @ToolParam(description = "表名列表") List<String> tableNames,
                @ToolParam(description = "模块名，非必填") String moduleName) {

                Map<String, Object> params = new HashMap<>();
                params.put("businessSystem", businessSystem);
                params.put("tableNames", tableNames);
                params.put("moduleName", moduleName);
                params.put("author", "smd-mcp");

                HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params);
                ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                        smdServiceUrl + "/mcp/api/crud",
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {});

                return response.getBody();
        }
}