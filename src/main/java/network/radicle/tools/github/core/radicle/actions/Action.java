package network.radicle.tools.github.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Action {
    @JsonProperty("type")
    public String type;

    @Override
    public String toString() {
        return "Action{" +
                "type='" + type + '\'' +
                '}';
    }
}
