
package network.radicle.tools.github.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubIssue {
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
    public GitHubUser user;
    @JsonProperty("labels")
    public List<GitHubLabel> labels;
    @JsonProperty("assignee")
    public GitHubUser assignee;
    @JsonProperty("assignees")
    public List<GitHubUser> assignees;
    @JsonProperty("milestone")
    public GitHubMilestone milestone;
    @JsonProperty("locked")
    public Boolean locked;
    @JsonProperty("active_lock_reason")
    public String activeLockReason;
    @JsonProperty("comments")
    public Long comments;
    @JsonProperty("pull_request")
    public GitHubPullRequest pullRequest;
    @JsonProperty("closed_at")
    public Instant closedAt;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("closed_by")
    public GitHubUser closedBy;
    @JsonProperty("author_association")
    public String authorAssociation;
    @JsonProperty("state_reason")
    public String stateReason;

}
