package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Source {

    @JsonProperty("type")
    public String type;

    @JsonProperty("issue")
    public Issue issue;

    @Override
    public String toString() {
        return "Source{" +
                "type='" + type + '\'' +
                ", issue=" + issue +
                '}';
    }
}
