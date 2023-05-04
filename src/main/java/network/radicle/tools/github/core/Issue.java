
package network.radicle.tools.github.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Issue {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("url")
    public String url;
    @JsonProperty("repository_url")
    public String repositoryUrl;
    @JsonProperty("labels_url")
    public String labelsUrl;
    @JsonProperty("comments_url")
    public String commentsUrl;
    @JsonProperty("events_url")
    public String eventsUrl;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("number")
    public Long number;
    @JsonProperty("state")
    public String state;
    @JsonProperty("title")
    public String title;
    @JsonProperty("body")
    public String body;
    @JsonProperty("user")
    public User user;
    @JsonProperty("labels")
    public List<Label> labels;
    @JsonProperty("assignee")
    public Assignee assignee;
    @JsonProperty("assignees")
    public List<Assignee> assignees;
    @JsonProperty("milestone")
    public Milestone milestone;
    @JsonProperty("locked")
    public Boolean locked;
    @JsonProperty("active_lock_reason")
    public String activeLockReason;
    @JsonProperty("comments")
    public Long comments;
    @JsonProperty("pull_request")
    public PullRequest pullRequest;
    @JsonProperty("closed_at")
    public Object closedAt;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
    @JsonProperty("closed_by")
    public ClosedBy closedBy;
    @JsonProperty("author_association")
    public String authorAssociation;
    @JsonProperty("state_reason")
    public String stateReason;

}
