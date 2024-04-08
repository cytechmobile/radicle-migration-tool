package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditAction extends Action {

    public String title;

    public EditAction(String title) {
        this.type = "edit";
        this.title = title;
    }

    public EditAction() {
    }

    @Override
    public String toString() {
        return "EditAction{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
