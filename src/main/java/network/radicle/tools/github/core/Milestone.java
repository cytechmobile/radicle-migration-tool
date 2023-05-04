
package network.radicle.tools.github.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Milestone {
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("labels_url")
    public String labelsUrl;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("number")
    public Integer number;
    @JsonProperty("state")
    public String state;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("creator")
    public Creator creator;
    @JsonProperty("open_issues")
    public Integer openIssues;
    @JsonProperty("closed_issues")
    public Integer closedIssues;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
    @JsonProperty("closed_at")
    public String closedAt;
    @JsonProperty("due_on")
    public String dueOn;

}
