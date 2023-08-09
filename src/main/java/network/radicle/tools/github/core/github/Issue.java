package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.github.core.radicle.State;
import network.radicle.tools.github.utils.Markdown;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static network.radicle.tools.github.core.github.Timeline.DTF;
import static network.radicle.tools.github.utils.Markdown.escape;
import static network.radicle.tools.github.utils.Markdown.link;

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

    public network.radicle.tools.github.core.radicle.Issue toRadicle() {
        var issue = new network.radicle.tools.github.core.radicle.Issue();

        issue.title = this.title;
        var meta = getMeta();
        issue.description = Strings.isNullOrEmpty(meta) ?
                Strings.nullToEmpty(this.body) :
                meta + "<br/>" + "\n\n" +  Strings.nullToEmpty(this.body);
        issue.labels = this.labels != null ?
                this.labels.stream().map(l -> l.name).collect(Collectors.toList()) :
                List.of();

        if (this.milestone != null) {
            var rLabels = new ArrayList<>(issue.labels);
            rLabels.add(this.milestone.title);
            issue.labels = rLabels;
        }

        var reason = "";
        if (STATE_OPEN.equalsIgnoreCase(this.state)) {
            reason = null;
        } else {
            reason = STATE_COMPLETED.equalsIgnoreCase(this.stateReason) ? STATE_SOLVED : STATE_OTHER;
        }
        issue.state = new State(this.state, reason);
        issue.assignees = List.of();

        return issue;
    }

    public String getMeta() {
        var metadata = new Markdown().openDropDown(METADATA_TITLE);

        var headers = new ArrayList<>();
        headers.add("Number");
        headers.add("Created On");
        headers.add("Created By");

        var hasAssignees = this.assignees != null && !this.assignees.isEmpty();
        if (hasAssignees) {
            headers.add("Assignees");
        }
        var hasMilestone = this.milestone != null;
        if (hasMilestone) {
            headers.add("Milestone");
            headers.add("Due By");
        }
        metadata.openTable(headers.toArray());

        var rows = new ArrayList<>();
        rows.add(link("#" + this.number, this.htmlUrl));
        rows.add(escape(DTF.format(this.createdAt)));
        rows.add(link(this.user.login, this.user.htmlUrl));
        if (hasAssignees) {
            rows.add(this.assignees.stream()
                    .map(a -> link(a.login, a.htmlUrl))
                    .collect(Collectors.joining(", ")));
        }
        if (hasMilestone) {
            rows.add(link(this.milestone.title, this.milestone.htmlUrl));
            if (this.milestone.dueOn != null) {
                rows.add(escape(DTF.format(Instant.parse(this.milestone.dueOn))));
            } else {
                rows.add("-");
            }
        }
        metadata.addTableRow(rows.toArray());
        metadata.closeTable().closeDropDown();

        return metadata.build();
    }

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
