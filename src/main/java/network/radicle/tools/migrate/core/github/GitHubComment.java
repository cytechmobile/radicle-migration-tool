package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.Timeline;

import java.time.Instant;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubComment extends Timeline {

    @JsonProperty("id")
    public Long id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("body")
    public String body;
    @JsonProperty("user")
    public GitHubUser user;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("issue_url")
    public String issueUrl;
    @JsonProperty("author_association")
    public String authorAssociation;

    public String getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", issueUrl='" + issueUrl + '\'' +
                ", authorAssociation='" + authorAssociation + '\'' +
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
}
