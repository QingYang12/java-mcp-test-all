package mcptest.chatclient;

import java.util.List;

public class NewsItem {
    private String title;
    private String summary;
    private String source;
    private String time;
    private String link;
    private List<String> tags;

    // 必须提供无参构造函数（Jackson需要）
    public NewsItem() {}

    // 为每个字段提供getter和setter方法
    // ... (省略getter和setter，实际代码中需要写上)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
