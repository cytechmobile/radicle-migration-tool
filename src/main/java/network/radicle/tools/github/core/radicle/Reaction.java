package network.radicle.tools.github.core.radicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reaction {
    @JsonProperty("emoji")
    public String emoji;

    @Override
    public String toString() {
        return "Reaction{" +
                "emoji='" + emoji + '\'' +
                '}';
    }
}
