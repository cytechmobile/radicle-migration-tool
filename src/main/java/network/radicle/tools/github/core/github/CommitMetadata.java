package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommitMetadata {

    @JsonProperty("url")
    public String url;
    @JsonProperty("author")
    public User author;
    @JsonProperty("committer")
    public User committer;
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
