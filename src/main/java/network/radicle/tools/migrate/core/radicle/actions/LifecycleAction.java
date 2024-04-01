package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.radicle.State;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LifecycleAction extends Action {
    @JsonProperty("state")
    public State state;

    public LifecycleAction(State state) {
        this.type = "lifecycle";
        this.state = state;
    }

    @Override
    public String toString() {
        return "Lifecycle{" +
                "type='" + type + '\'' +
                ", state=" + state +
                '}';
    }
}
