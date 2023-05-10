package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {

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
    public User user;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("issue_url")
    public String issueUrl;
    @JsonProperty("author_association")
    public String authorAssociation;

    public String getMeta() {
        return "> github comment: commented on " + this.updatedAt.toString() + " by " + this.user.login;
    }

    public String getBodyWithMeta() {
        return this.getMeta() + "\n\n" + this.body;
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
}
