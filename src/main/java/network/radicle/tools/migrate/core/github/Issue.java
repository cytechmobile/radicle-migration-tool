package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Issue {
    public static final String METADATA_TITLE = "GitHub Metadata";
    public static final String STATE_OPEN = "open";
    public static final String STATE_CLOSED = "closed";
    public static final String STATE_COMPLETED = "completed";
    public static final String STATE_SOLVED = "solved";
    public static final String STATE_OTHER = "other";

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
    public User assignee;
    @JsonProperty("assignees")
    public List<User> assignees;
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
    public Instant closedAt;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("closed_by")
    public User closedBy;
    @JsonProperty("author_association")
    public String authorAssociation;
    @JsonProperty("state_reason")
    public String stateReason;

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", url='" + url + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", labelsUrl='" + labelsUrl + '\'' +
                ", commentsUrl='" + commentsUrl + '\'' +
                ", eventsUrl='" + eventsUrl + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", number=" + number +
                ", state='" + state + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                ", labels=" + labels +
                ", assignee=" + assignee +
                ", assignees=" + assignees +
                ", milestone=" + milestone +
                ", locked=" + locked +
                ", activeLockReason='" + activeLockReason + '\'' +
                ", comments=" + comments +
                ", pullRequest=" + pullRequest +
                ", closedAt=" + closedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", closedBy=" + closedBy +
                ", authorAssociation='" + authorAssociation + '\'' +
                ", stateReason='" + stateReason + '\'' +
                '}';
    }
}
