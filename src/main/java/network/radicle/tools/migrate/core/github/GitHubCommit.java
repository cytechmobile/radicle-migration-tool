package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubCommit {

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
    public GitHubUser author;
    @JsonProperty("committer")
    public GitHubUser committer;

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
