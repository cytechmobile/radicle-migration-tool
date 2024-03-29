package network.radicle.tools.migrate.core.radicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {
    @JsonProperty("id")
    public String id;

    @Override
    public String toString() {
        return "Author{" +
                "id='" + id + '\'' +
                '}';
    }
}
