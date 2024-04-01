package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Commit {

    @JsonProperty("url")
    public String url;
    @JsonProperty("sha")
    public String sha;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("comments_url")
    public String commentsUrl;
    @JsonProperty("commit")
    public CommitMetadata metadata;
    @JsonProperty("author")
    public User author;
    @JsonProperty("committer")
    public User committer;

    @Override
    public String toString() {
        return "Commit{" +
                "url='" + url + '\'' +
                ", sha='" + sha + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", commentsUrl='" + commentsUrl + '\'' +
                ", metadata=" + metadata +
                ", author=" + author +
                ", committer=" + committer +
                '}';
    }
}
