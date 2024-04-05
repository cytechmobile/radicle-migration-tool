package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommitMetadata {

    @JsonProperty("url")
    public String url;
    @JsonProperty("author")
    public GitHubUser author;
    @JsonProperty("committer")
    public GitHubUser committer;
    @JsonProperty("message")
    public String message;

    @Override
    public String toString() {
        return "CommitMetadata{" +
                "url='" + url + '\'' +
                ", author=" + author +
                ", committer=" + committer +
                ", message='" + message + '\'' +
                '}';
    }
}
