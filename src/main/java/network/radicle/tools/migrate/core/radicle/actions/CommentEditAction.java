package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.radicle.Embed;

import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentEditAction extends Action {

    public String id;
    public String body;
    public List<Embed> embeds;

    public CommentEditAction(String id, String body, List<Embed> embeds) {
        this.type = "comment.edit";
        this.id = id;
        this.body = body;
        this.embeds = embeds;
    }

    public CommentEditAction() {
    }

    @Override
    public String toString() {
        return "CommentEditAction{" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                ", embeds=" + embeds +
                ", type='" + type + '\'' +
                '}';
    }
}
