package network.radicle.tools.github.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreadAction extends Action {
    @JsonProperty("action")
    public Map<String, String> action;

    public ThreadAction(Map<String, String> action) {
        this.type = "thread";
        this.action = action;
    }

    public ThreadAction() {
    }

    @Override
    public String toString() {
        return "ThreadAction{" +
                "type='" + type + '\'' +
                ", action=" + action +
                '}';
    }
}
