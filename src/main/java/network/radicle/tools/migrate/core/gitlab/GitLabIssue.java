
package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabIssue {

    public static final String METADATA_TITLE = "GitLab Metadata";
    public static final String STATE_OPEN = "open";
    public static final String STATE_OPENED = "opened";
    public static final String STATE_CLOSED = "closed";
    public static final String STATE_SOLVED = "solved";

    @JsonProperty("state")
    public String state;
    @JsonProperty("description")
    public String description;
    @JsonProperty("author")
    public GitLabUser author;
    @JsonProperty("milestone")
    public GitLabMilestone milestone;
    @JsonProperty("project_id")
    public Long projectId;
    @JsonProperty("assignees")
    public List<GitLabUser> assignees;
    @JsonProperty("assignee")
    public GitLabUser assignee;
    @JsonProperty("type")
    public String type;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("closed_at")
    public Instant closedAt;
    @JsonProperty("closed_by")
    public GitLabUser closedBy;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("iid")
    public Long iid;
    @JsonProperty("labels")
    public List<String> labels;
    @JsonProperty("user_notes_count")
    public Long userNotesCount;
    @JsonProperty("due_date")
    public LocalDate dueDate;
    @JsonProperty("web_url")
    public String webUrl;
    @JsonProperty("has_tasks")
    public Boolean hasTasks;
    @JsonProperty("task_status")
    public String taskStatus;
    @JsonProperty("issue_type")
    public String issueType;
    @JsonProperty("severity")
    public String severity;
    @JsonProperty("_links")
    public GitLabLinks links;

    @Override
    public String toString() {
        return "Issue{" +
                "state='" + state + '\'' +
                ", description='" + description + '\'' +
                ", author=" + author +
                ", milestone=" + milestone +
                ", projectId=" + projectId +
                ", assignees=" + assignees +
                ", assignee=" + assignee +
                ", type='" + type + '\'' +
                ", updatedAt=" + updatedAt +
                ", closedAt=" + closedAt +
                ", closedBy=" + closedBy +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", iid=" + iid +
                ", labels=" + labels +
                ", userNotesCount=" + userNotesCount +
                ", dueDate=" + dueDate +
                ", webUrl='" + webUrl + '\'' +
                ", hasTasks=" + hasTasks +
                ", taskStatus='" + taskStatus + '\'' +
                ", issueType='" + issueType + '\'' +
                ", severity='" + severity + '\'' +
                ", links=" + links +
                '}';
    }
}
