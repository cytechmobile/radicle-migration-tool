package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubRename {
    @JsonProperty("from")
    public String from;
    @JsonProperty("to")
    public String to;

    @Override
    public String toString() {
        return "Rename{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
