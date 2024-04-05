
package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabMilestone {
    @JsonProperty("project_id")
    public Long projectId;
    @JsonProperty("description")
    public String description;
    @JsonProperty("state")
    public String state;
    @JsonProperty("due_date")
    public String dueDate;
    @JsonProperty("iid")
    public Long iid;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("title")
    public String title;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("web_url")
    public String webUrl;

    @Override
    public String toString() {
        return "Milestone{" +
                "projectId=" + projectId +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                ", dueDate=" + dueDate +
                ", iid=" + iid +
                ", createdAt='" + createdAt + '\'' +
                ", title='" + title + '\'' +
                ", id=" + id +
                ", updatedAt=" + updatedAt +
                ", webUrl=" + webUrl +
                '}';
    }
}
