package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignAction extends Action {

    public List<String> assignees;

    public AssignAction(List<String> assignees) {
        this.type = "assign";
        this.assignees = assignees;
    }

    public AssignAction() {
    }

    @Override
    public String toString() {
        return "AssignAction{" +
                "assignees=" + assignees +
                ", type='" + type + '\'' +
                '}';
    }
}
