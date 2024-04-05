package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.Timeline;

import java.time.Instant;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabComment extends Timeline {

    @JsonProperty("id")
    public Long id;
    @JsonProperty("body")
    public String body;
    @JsonProperty("attachment")
    public String attachment;
    @JsonProperty("author")
    public GitLabUser author;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("system")
    public Boolean system;
    @JsonProperty("noteable_id")
    public Long noteableId;
    @JsonProperty("noteable_type")
    public String noteableType;
    @JsonProperty("project_id")
    public Long projectId;
    @JsonProperty("noteable_iid")
    public Long noteableIid;
    @JsonProperty("resolvable")
    public Boolean resolvable;
    @JsonProperty("confidential")
    public Boolean confidential;
    @JsonProperty("internal")
    public Boolean internal;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", attachment='" + attachment + '\'' +
                ", author=" + author +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", system=" + system +
                ", noteableId=" + noteableId +
                ", noteableType='" + noteableType + '\'' +
                ", projectId=" + projectId +
                ", noteableIid=" + noteableIid +
                ", resolvable=" + resolvable +
                ", confidential=" + confidential +
                ", internal=" + internal +
                '}';
    }

    @Override
    public String getType() {
        return "comment";
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getBody() {
        return this.body;
    }
}
