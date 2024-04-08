package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelAction extends Action {

    public List<String> labels;

    public LabelAction(List<String> labels) {
        this.type = "label";
        this.labels = labels;
    }

    public LabelAction() {
    }

    @Override
    public String toString() {
        return "LabelAction{" +
                "labels=" + labels +
                ", type='" + type + '\'' +
                '}';
    }
}
